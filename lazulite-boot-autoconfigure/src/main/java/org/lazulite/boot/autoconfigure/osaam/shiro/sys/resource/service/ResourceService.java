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
package org.lazulite.boot.autoconfigure.osaam.shiro.sys.resource.service;


import org.lazulite.boot.autoconfigure.core.service.BaseService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.resource.entity.tmp.Menu;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.auth.service.UserAuthService;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.resource.entity.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class ResourceService extends BaseService<Resource, Long> {

    @Autowired
    private UserAuthService userAuthService;

    @SuppressWarnings("unchecked")
    public static List<Menu> convertToMenus(List<Resource> resources) {
        if (resources.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        Menu root = convertToMenu(resources.remove(resources.size() - 1));
        recursiveMenu(root, resources);
        List<Menu> menus = root.getSubmenu();
        removeNoLeafMenu(menus);
        root.setSubmenu(null);
        menus.add(0,root);
        return menus;
    }

    private static void removeNoLeafMenu(List<Menu> menus) {
        if (menus.size() == 0) {
            return;
        }
        for (int i = menus.size() - 1; i >= 0; i--) {
            Menu m = menus.get(i);
            removeNoLeafMenu(m.getSubmenu());
            if (!m.isHasChildren() && StringUtils.isEmpty(m.getSref())) {
                menus.remove(i);
            }
        }
    }

    private static void recursiveMenu(Menu menu, List<Resource> resources) {
        for (int i = resources.size() - 1; i >= 0; i--) {
            Resource resource = resources.get(i);
            if (resource.getParentId().equals(menu.getId())) {
                menu.getSubmenu().add(convertToMenu(resource));
                resources.remove(i);
            }
        }

        for (Menu subMenu : menu.getSubmenu()) {
            recursiveMenu(subMenu, resources);
        }
    }

    private static Menu convertToMenu(Resource resource) {
        return new Menu(resource.getId(), resource.getName(),resource.getIcon(),resource.getUrl(),resource.getId()==1?true:null,resource.getTranslate(),resource.getAlert(),resource.getLabel());
    }

    /**
     * 得到真实的资源标识  即 父亲:儿子
     *
     * @param resource
     * @return
     */
    public String findActualResourceIdentity(Resource resource) {

        if (resource == null) {
            return null;
        }

        StringBuilder s = new StringBuilder(resource.getIdentity() == null ? "" : resource.getIdentity());

        boolean hasResourceIdentity = !StringUtils.isEmpty(resource.getIdentity());

        Resource parent = findOne(resource.getParentId());
        while (parent != null) {
            if (!StringUtils.isEmpty(parent.getIdentity())) {
                s.insert(0, parent.getIdentity() + ":");
                hasResourceIdentity = true;
            }
            parent = findOne(parent.getParentId());
        }

        //如果用户没有声明 资源标识  且父也没有，那么就为空
        if (!hasResourceIdentity) {
            return "";
        }


        //如果最后一个字符是: 因为不需要，所以删除之
        int length = s.length();
        if (length > 0 && s.lastIndexOf(":") == length - 1) {
            s.deleteCharAt(length - 1);
        }

        //如果有儿子 最后拼一个*
        boolean hasChildren = false;
        for (Resource r : findAll()) {
            if (resource.getId().equals(r.getParentId())) {
                hasChildren = true;
                break;
            }
        }
        if (hasChildren) {
            s.append(":*");
        }

        return s.toString();
    }

    public List<Menu> findMenus(User user) {
//        Pageable pageable =
//                Pageable.newSearchable()
//                        .addSearchFilter("show", SearchOperator.eq, true)
//                        .addSort(new Sort(Sort.Direction.DESC, "parentId", "weight"));

        List<Resource> resources =  findAll(new Sort(Sort.Direction.DESC,"parentId","weight"));
        Set<String> userPermissions = userAuthService.findStringPermissions(user);
        Iterator<Resource> iter = resources.iterator();
        while (iter.hasNext()) {
            if (!hasPermission(iter.next(), userPermissions)) {
                iter.remove();
            }
        }
        return convertToMenus(resources);
    }

    private boolean hasPermission(Resource resource, Set<String> userPermissions) {
        String actualResourceIdentity = findActualResourceIdentity(resource);
        if (StringUtils.isEmpty(actualResourceIdentity)) {
            return true;
        }

        for (String permission : userPermissions) {
            if (hasPermission(permission, actualResourceIdentity)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermission(String permission, String actualResourceIdentity) {

        //得到权限字符串中的 资源部分，如a:b:create --->资源是a:b
        String permissionResourceIdentity = permission.substring(0, permission.lastIndexOf(":"));

        //如果权限字符串中的资源 是 以资源为前缀 则有权限 如a:b 具有a:b的权限
        if (permissionResourceIdentity.startsWith(actualResourceIdentity)) {
            return true;
        }


        //模式匹配
        WildcardPermission p1 = new WildcardPermission(permissionResourceIdentity);
        WildcardPermission p2 = new WildcardPermission(actualResourceIdentity);

        return p1.implies(p2) || p2.implies(p1);
    }

}
