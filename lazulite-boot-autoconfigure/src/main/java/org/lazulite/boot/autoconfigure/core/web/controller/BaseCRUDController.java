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

package org.lazulite.boot.autoconfigure.core.web.controller;


import org.apache.commons.lang3.StringUtils;
import org.lazulite.boot.autoconfigure.core.entity.BaseEntity;
import org.lazulite.boot.autoconfigure.core.service.BaseService;
import org.lazulite.boot.autoconfigure.core.web.controller.permission.PermissionList;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

public abstract class BaseCRUDController<M extends BaseEntity, ID extends Serializable> {

    protected BaseService<M, ID> baseService;

    protected PermissionList permissionList = null;

    /**
     * 设置基础service
     *
     * @param baseService
     */
    @Autowired
    public void setBaseService(BaseService<M, ID> baseService) {
        this.baseService = baseService;
    }

    public void setResourceIdentity(String resourceIdentity) {
        if (!StringUtils.isEmpty(resourceIdentity)) {
            permissionList = PermissionList.newPermissionList(resourceIdentity);
        }
    }


}
