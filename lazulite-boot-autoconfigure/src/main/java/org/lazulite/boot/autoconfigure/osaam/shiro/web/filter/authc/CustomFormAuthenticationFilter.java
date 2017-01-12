/*
 * Copyright 2016. junfu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lazulite.boot.autoconfigure.osaam.shiro.web.filter.authc;


import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.entity.User;
import org.lazulite.boot.autoconfigure.osaam.shiro.web.filter.authc.info.*;
import org.lazulite.boot.autoconfigure.osaam.shiro.web.filter.authc.token.Token;
import org.lazulite.boot.autoconfigure.osaam.shiro.web.filter.authc.token.UpToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.lazulite.boot.autoconfigure.core.utils.SpringUtils;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.auth.service.UserAuthService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.service.UserService;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 基于几点修改：
 * 1、onLoginFailure 时 把异常添加到request attribute中 而不是异常类名
 */
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return (!isLoginRequest(request, response) && isPermissive(mappedValue));
    }

    @Override
    protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
        request.setAttribute(getFailureKeyAttribute(), ae);
    }

//    @Override
//    protected void redirectToLogin(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
//        try {
//            HttpServletRequest request = (HttpServletRequest) servletRequest;
//            HttpServletResponse response = (HttpServletResponse) servletResponse;
//            String contentType = request.getContentType();
//            if (contentType != null && contentType.contains("application/json")) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setCharacterEncoding("UTF-8");
//                PrintWriter writer = response.getWriter();
//                writer.print("{\"error\":\"会话超时，请重新登录。\"}");
//            } else {
//                super.redirectToLogin(servletRequest, servletResponse);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            super.redirectToLogin(servletRequest, servletResponse);
//        }
//    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        if (request.getContentType().contains("application/json")) {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Token token = objectMapper.readValue(request.getInputStream(), UpToken.class).getToken();
                String host = getHost(request);
                return createToken(token.getPrincipal(), token.getCredentials(), token.getRememberMe(), host);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.createToken(request, response);
    }


    @Override
    protected boolean isRememberMe(ServletRequest request) {
        return super.isRememberMe(request);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        ObjectMapper objectMapper = new ObjectMapper();
        if (!request.getContentType().contains("application/json")) {
            // 不是ajax请求
            issueSuccessRedirect(request, response);
        } else {

            UserService userService = SpringUtils.getBean(UserService.class);
            httpServletResponse.setCharacterEncoding("UTF-8");
            PrintWriter out = httpServletResponse.getWriter();
            String username = (String) subject.getPrincipal();
            User user = userService.findByUsername(username);

            AuthenticationInfo authenticationInfo = new AuthenticationInfo();
            Info info = new Info();
            Authc authc = new Authc();
            Principal principal = new Principal();
            principal.setName(user.getUsername());
            principal.setLogin(user.getUsername());
            principal.setEmail(user.getEmail());
            Credentials credentials = new Credentials();
            credentials.setName(user.getUsername());
            credentials.setLogin(user.getUsername());
            credentials.setEmail(user.getEmail());
            authc.setCredentials(credentials);
            authc.setPrincipal(principal);
            Authz authz = new Authz();
            UserAuthService userAuthService = SpringUtils.getBean(UserAuthService.class);
            authz.setRoles(Lists.newArrayList(userAuthService.findStringRoles(user)));
            authz.setPermissions(Lists.newArrayList(userAuthService.findStringPermissions(user)));
            info.setAuthc(authc);
            info.setAuthz(authz);
            authenticationInfo.setInfo(info);
            out.println(objectMapper.writeValueAsString(authenticationInfo));
           /* out.println("{\n"
                    + "  \"info\": {\n"
                    + "    \"authc\": {\n"
                    + "      \"principal\": {\n"
                    + "        \"name\": \"Edgar Degas\",\n"
                    + "        \"login\": \"edegas\",\n"
                    + "        \"email\": \"edegas@mail.com\"\n"
                    + "      },\n"
                    + "      \"credentials\": {\n"
                    + "        \"name\": \"Edgar Degas\",\n"
                    + "        \"login\": \"edegas\",\n"
                    + "        \"email\": \"edegas@mail.com\"\n"
                    + "      }\n"
                    + "    },\n"
                    + "    \"authz\": {\n"
                    + "        \"roles\" : [\"ADMIN\"],\n"
                    + "        \"permissions\" : [\"address:view,create,edit,delete\"]\n"
                    + "    }\n" + "  }\n" + "}");*/
            out.flush();
            out.close();
        }
        return false;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        if (!request.getContentType().contains("application/json")) {// 不是ajax请求
            setFailureAttribute(request, e);
            return true;
        }
        try {

            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String message = e.getClass().getSimpleName();
            if ("IncorrectCredentialsException".equals(message)) {
                out.println("{success:false,message:'密码错误'}");
            } else if ("UnknownAccountException".equals(message)) {
                out.println("{success:false,message:'账号不存在'}");
            } else if ("LockedAccountException".equals(message)) {
                out.println("{success:false,message:'账号被锁定'}");
            } else {
                out.println("{success:false,message:'未知错误'}");
            }
            out.flush();
            out.close();
        } catch (IOException e1) {

            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return false;

        // return super.onLoginFailure(token, e, request, response);
    }


}
