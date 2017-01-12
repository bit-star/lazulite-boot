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

package org.lazulite.boot.autoconfigure.osaam.shiro.spring;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by junfu on 2016/7/4.
 */
public class ShiroAuditorAware implements AuditorAware<String> {
    private Logger logger= LoggerFactory.getLogger(ShiroAuditorAware.class);

    private final String ANONYMOUS_USER="anonymous";
    public ShiroAuditorAware(SecurityManager securityManager){
            this.securityManager=securityManager;
    }

    private SecurityManager securityManager;

    @Override
    public String getCurrentAuditor() {
        try{
            SecurityUtils.setSecurityManager(securityManager);
            if(SecurityUtils.getSubject()!=null&&SecurityUtils.getSubject().getPrincipal()!=null){
                Object principal=SecurityUtils.getSubject().getPrincipal();
                logger.trace("审计器获得当前subject ："+principal);
                return (String) principal;
            }else{
                return ANONYMOUS_USER;
            }
        }catch (Exception e){
            logger.error("获取当前 shiro subject 时出错",e);
        }
        return null;
    }






}
