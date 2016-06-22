/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

/**
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.lazulite.boot.autoconfigure.osaam.shiro.sys.group.service;


import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.lazulite.boot.autoconfigure.osaam.shiro.base.BaseService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.group.entity.GroupRelation;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.group.repository.GroupRelationRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>User: 
 * <p>Date: 13-2-4 下午3:01
 * <p>Version: 1.0
 */
@Service
public class GroupRelationService extends BaseService<GroupRelation, Long> {

    private GroupRelationRepository getGroupRelationRepository() {
        return (GroupRelationRepository) baseRepository;
    }


    public void appendRelation(Long groupId, Long[] organizationIds) {
        if (ArrayUtils.isEmpty(organizationIds)) {
            return;
        }
        for (Long organizationId : organizationIds) {
            if (organizationId == null) {
                continue;
            }
            GroupRelation r = getGroupRelationRepository().findByGroupIdAndOrganizationId(groupId, organizationId);
            if (r == null) {
                r = new GroupRelation();
                r.setGroupId(groupId);
                r.setOrganizationId(organizationId);
                save(r);
            }
        }
    }

    public void appendRelation(Long groupId, Long[] userIds, Long[] startUserIds, Long[] endUserIds) {
        if (ArrayUtils.isEmpty(userIds) && ArrayUtils.isEmpty(startUserIds)) {
            return;
        }
        if (!ArrayUtils.isEmpty(userIds)) {
            for (Long userId : userIds) {
                if (userId == null) {
                    continue;
                }
                GroupRelation r = getGroupRelationRepository().findByGroupIdAndUserId(groupId, userId);
                if (r == null) {
                    r = new GroupRelation();
                    r.setGroupId(groupId);
                    r.setUserId(userId);
                    save(r);
                }
            }
        }

        if (!ArrayUtils.isEmpty(startUserIds)) {
            for (int i = 0, l = startUserIds.length; i < l; i++) {
                Long startUserId = startUserIds[i];
                Long endUserId = endUserIds[i];
                //范围查 如果在指定范围内 就没必要再新增一个 如当前是[10,20] 如果数据库有[9,21]
                GroupRelation r = getGroupRelationRepository().findByGroupIdAndStartUserIdLessThanEqualAndEndUserIdGreaterThanEqual(groupId, startUserId, endUserId);

                if (r == null) {
                    //删除范围内的
                    getGroupRelationRepository().deleteInRange(startUserId, endUserId);
                    r = new GroupRelation();
                    r.setGroupId(groupId);
                    r.setStartUserId(startUserId);
                    r.setEndUserId(endUserId);
                    save(r);
                }

            }
        }
    }

    public Set<Long> findGroupIds(Long userId, Set<Long> organizationIds) {
        if (organizationIds.isEmpty()) {
            return Sets.newHashSet(getGroupRelationRepository().findGroupIds(userId));
        }

        return Sets.newHashSet(getGroupRelationRepository().findGroupIds(userId, organizationIds));
    }

}
