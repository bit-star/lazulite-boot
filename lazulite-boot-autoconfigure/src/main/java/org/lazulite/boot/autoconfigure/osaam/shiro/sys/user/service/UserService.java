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

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.service;


import org.lazulite.boot.autoconfigure.core.service.BaseService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.entity.User;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.entity.UserStatus;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.exception.UserBlockedException;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.exception.UserNotExistsException;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.exception.UserPasswordNotMatchException;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.utils.UserLogUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.entity.UserOrganizationJob;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.repository.UserRepository;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService extends BaseService<User, Long> {

    @Autowired
    private UserStatusHistoryService userStatusHistoryService;
    @Autowired
    private PasswordService passwordService;

    private UserRepository getUserRepository() {
        return (UserRepository) baseRepository;
    }

    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public User save(User user) {
        user.randomSalt();
        user.setPassword(passwordService.encryptPassword(user.getUsername(), user.getPassword(), user.getSalt()));

        return super.save(user);
    }


    @Override
    public User update(User user) {

        List<UserOrganizationJob> localUserOrganizationJobs = user.getOrganizationJobs();
        for (int i = 0, l = localUserOrganizationJobs.size(); i < l; i++) {

            UserOrganizationJob localUserOrganizationJob = localUserOrganizationJobs.get(i);
            //设置关系 防止丢失 报 A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance
            localUserOrganizationJob.setUser(user);

            UserOrganizationJob dbUserOrganizationJob = findUserOrganizationJob(localUserOrganizationJob);
            if (dbUserOrganizationJob != null) {//出现在先删除再添加的情况
                dbUserOrganizationJob.setJobId(localUserOrganizationJob.getJobId());
                dbUserOrganizationJob.setOrganizationId(localUserOrganizationJob.getOrganizationId());
                dbUserOrganizationJob.setUser(localUserOrganizationJob.getUser());
                localUserOrganizationJobs.set(i, dbUserOrganizationJob);
            }
        }
        return super.update(user);
    }

    public UserOrganizationJob findUserOrganizationJob(UserOrganizationJob userOrganizationJob) {
        return getUserRepository().findUserOrganization(
                userOrganizationJob.getUser(),
                userOrganizationJob.getOrganizationId(),
                userOrganizationJob.getJobId());
    }

    public User findByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        return getUserRepository().findByUsername(username);
    }

    public User findByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        }
        return getUserRepository().findByEmail(email);
    }


    public User findByMobilePhoneNumber(String mobilePhoneNumber) {
        if (StringUtils.isEmpty(mobilePhoneNumber)) {
            return null;
        }
        return getUserRepository().findByMobilePhoneNumber(mobilePhoneNumber);
    }


    public User changePassword(User user, String newPassword) {
        user.randomSalt();
        user.setPassword(passwordService.encryptPassword(user.getUsername(), newPassword, user.getSalt()));
        update(user);
        return user;
    }

    public User changeStatus(User opUser, User user, UserStatus newStatus, String reason) {
        user.setStatus(newStatus);
        update(user);
        userStatusHistoryService.log(opUser, user, newStatus, reason);
        return user;
    }

    public User login(String username, String password) {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            UserLogUtils.log(
                    username,
                    "loginError",
                    "username is empty");
            throw new UserNotExistsException();
        }
        //密码如果不在指定范围内 肯定错误
        if (password.length() < User.PASSWORD_MIN_LENGTH || password.length() > User.PASSWORD_MAX_LENGTH) {
            UserLogUtils.log(
                    username,
                    "loginError",
                    "password length error! password is between {} and {}",
                    User.PASSWORD_MIN_LENGTH, User.PASSWORD_MAX_LENGTH);

            throw new UserPasswordNotMatchException();
        }

        User user = null;

        //此处需要走代理对象，目的是能走缓存切面

        UserService proxyUserService = (UserService) AopContext.currentProxy();
        if (maybeUsername(username)) {
            user = proxyUserService.findByUsername(username);
        }

        if (user == null && maybeEmail(username)) {
            user = proxyUserService.findByEmail(username);
        }

        if (user == null && maybeMobilePhoneNumber(username)) {
            user = proxyUserService.findByMobilePhoneNumber(username);
        }

        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            UserLogUtils.log(
                    username,
                    "loginError",
                    "user is not exists!");

            throw new UserNotExistsException();
        }

        passwordService.validate(user, password);

        if (user.getStatus() == UserStatus.blocked) {
            UserLogUtils.log(
                    username,
                    "loginError",
                    "user is blocked!");
            throw new UserBlockedException(userStatusHistoryService.getLastReason(user));
        }

        UserLogUtils.log(
                username,
                "loginSuccess",
                "");
        return user;
    }


    private boolean maybeUsername(String username) {
        if (!username.matches(User.USERNAME_PATTERN)) {
            return false;
        }
        //如果用户名不在指定范围内也是错误的
        if (username.length() < User.USERNAME_MIN_LENGTH || username.length() > User.USERNAME_MAX_LENGTH) {
            return false;
        }

        return true;
    }

    private boolean maybeEmail(String username) {
        if (!username.matches(User.EMAIL_PATTERN)) {
            return false;
        }
        return true;
    }

    private boolean maybeMobilePhoneNumber(String username) {
        if (!username.matches(User.MOBILE_PHONE_NUMBER_PATTERN)) {
            return false;
        }
        return true;
    }

    public void changePassword(User opUser, Long[] ids, String newPassword) {
        UserService proxyUserService = (UserService) AopContext.currentProxy();
        for (Long id : ids) {
            User user = findOne(id);
            proxyUserService.changePassword(user, newPassword);
            UserLogUtils.log(
                    user.getUsername(),
                    "changePassword",
                    "admin user {} change password!", opUser.getUsername());

        }
    }

    public void changeStatus(User opUser, Long[] ids, UserStatus newStatus, String reason) {
        UserService proxyUserService = (UserService) AopContext.currentProxy();
        for (Long id : ids) {
            User user = findOne(id);
            proxyUserService.changeStatus(opUser, user, newStatus, reason);
            UserLogUtils.log(
                    user.getUsername(),
                    "changeStatus",
                    "admin user {} change status!", opUser.getUsername());
        }
    }

    public Set<Map<String, Object>> findIdAndNames(Pageable pageable, String usernme) {

//        pageable.addSearchFilter("username", SearchOperator.like, usernme);
//        pageable.addSearchFilter("deleted", SearchOperator.eq, false);

        return Sets.newHashSet(
                Lists.transform(
                        findAll(pageable).getContent(),
                        new Function<User, Map<String, Object>>() {
                            @Override
                            public Map<String, Object> apply(User input) {
                                Map<String, Object> data = Maps.newHashMap();
                                data.put("label", input.getUsername());
                                data.put("value", input.getId());
                                return data;
                            }
                        }
                )
        );
    }


    /**
     * 获取那些在用户-组织机构/工作职务中存在 但在组织机构/工作职务中不存在的
     *
     * @param pageable
     * @return
     */
    public Page<UserOrganizationJob> findUserOrganizationJobOnNotExistsOrganizationOrJob(Pageable pageable) {
        return getUserRepository().findUserOrganizationJobOnNotExistsOrganizationOrJob(pageable);
    }

    /**
     * 删除用户不存在的情况的UserOrganizationJob（比如手工从数据库物理删除）。。
     *
     * @return
     */
    public void deleteUserOrganizationJobOnNotExistsUser() {
        getUserRepository().deleteUserOrganizationJobOnNotExistsUser();
    }
}
