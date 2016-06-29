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
package org.lazulite.boot.autoconfigure.osaam.shiro.sys.auth.task;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.lazulite.boot.autoconfigure.core.utils.LogUtils;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.auth.entity.Auth;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.auth.service.AuthService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.group.service.GroupService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.organization.service.JobService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.organization.service.OrganizationService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.permission.entity.Role;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.permission.service.RoleService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
public class AuthRelationClearTask {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private JobService jobService;


    /**
     * 清除删除的角色对应的关系
     */
    public void clearDeletedAuthRelation() {

        Set<Long> allRoleIds = findAllRoleIds();

        final int PAGE_SIZE = 100;
        int pn = 0;

        Page<Auth> authPage = null;

        do {
            Pageable pageable = new PageRequest(pn++, PAGE_SIZE);
            authPage = authService.findAll(pageable);
            //开启新事物清除
            try {
                AuthRelationClearTask authRelationClearService = (AuthRelationClearTask) AopContext.currentProxy();
                authRelationClearService.doClear(authPage.getContent(), allRoleIds);
            } catch (Exception e) {
                //出异常也无所谓
                LogUtils.logError("clear auth relation error", e);
            }
            //清空会话
            //  RepositoryHelper.clear();
        } while (authPage.hasNext());
    }

    public void doClear(Collection<Auth> authColl, Set<Long> allRoleIds) {
        for (Auth auth : authColl) {
            switch (auth.getType()) {
                case user:
                    break;//因为用户是逻辑删除不用管
                case user_group:
                case organization_group:
                    if (!groupService.exists(auth.getGroupId())) {
                        authService.delete(auth);
                        continue;
                    }
                    break;
                case organization_job:
                    if (!organizationService.exists(auth.getOrganizationId())) {
                        auth.setOrganizationId(0L);
                    }
                    if (!jobService.exists(auth.getOrganizationId())) {
                        auth.setJobId(0L);
                    }
                    //如果组织机构/工作职务都为0L 那么可以删除
                    if (auth.getOrganizationId() == 0L && auth.getJobId() == 0L) {
                        authService.delete(auth);
                        continue;
                    }
                    break;
            }

            boolean hasRemovedAny = auth.getRoleIds().retainAll(allRoleIds);
            if (hasRemovedAny) {
                authService.update(auth);
            }
        }

    }

    private Set<Long> findAllRoleIds() {
        return Sets.newHashSet(Lists.transform(roleService.findAll(), new Function<Role, Long>() {
            @Override
            public Long apply(Role input) {
                return input.getId();
            }
        }));
    }


}
