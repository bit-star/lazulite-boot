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

package org.lazulite.boot.autoconfigure.core.plugin.entity;

import org.apache.commons.lang3.StringUtils;
import org.lazulite.boot.autoconfigure.core.entity.BaseEntity;

import javax.persistence.Transient;
import java.io.Serializable;

public abstract class TransientTreeable<ID extends Serializable> extends BaseEntity<ID> implements Treeable<ID> {

    /**
     * 标题
     */
    private String name;
    /**
     * 点击后前往的地址 菜单才有
     */
    private String url;

    /**
     * 父路径
     */

    private ID parentId;

    private String parentIds;

    private Integer weight;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否有叶子节点
     */
    // @Formula(value = "(select count(*) from sys_resource f_t where
    // f_t.parent_id = id)")
    private boolean hasChildren;

    /**
     * 是否显示
     */

    private Boolean show = Boolean.FALSE;

    private String diy;

    /**
     * 树枝节点默认图标 如果没有默认 空即可
     *
     * @return
     */
    @Transient
    @Override
    public String getBranchDefaultIcon() {
        return "ztree_branch";
    }

    public String getDiy() {
        return diy;
    }

    public void setDiy(String diy) {
        this.diy = diy;
    }

    public String getIcon() {
        if (!StringUtils.isEmpty(icon)) {
            return icon;
        }
        if (isRoot()) {
            return getRootDefaultIcon();
        }
        if (isLeaf()) {
            return getLeafDefaultIcon();
        }
        return getBranchDefaultIcon();
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 树叶节点默认图标 如果没有默认 空即可
     *
     * @return
     */
    @Transient
    @Override
    public String getLeafDefaultIcon() {
        return "ztree_file";
    }

    /**
     * 树枝节点默认图标 如果没有默认 空即可
     *
     * @return
     */
    @Transient
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public ID getParentId() {
        return parentId;
    }

    public void setParentId(ID parentId) {
        this.parentId = parentId;
    }

    @Transient
    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    /**
     * 根节点默认图标 如果没有默认 空即可
     *
     * @return
     */
    @Transient
    @Override
    public String getRootDefaultIcon() {
        return "ztree_root_open";
    }

    @Transient
    @Override
    public String getSeparator() {
        return "-";
    }

    @Transient
    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    @Transient
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Transient
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Transient
    @Override
    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    @Transient
    @Override
    public boolean isLeaf() {
        if (isRoot()) {
            return false;
        }
        if (isHasChildren()) {
            return false;
        }

        return true;
    }

    @Transient
    @Override
    public boolean isRoot() {
        if (getParentId() instanceof Number) {
            if (getParentId() != null && ((Number) getParentId()).longValue() == 0) {
                return true;
            }
        }
        return false;
    }

    @Transient
    @Override
    public String makeSelfAsNewParentIds() {
        return getParentIds() + getId() + getSeparator();
    }

}
