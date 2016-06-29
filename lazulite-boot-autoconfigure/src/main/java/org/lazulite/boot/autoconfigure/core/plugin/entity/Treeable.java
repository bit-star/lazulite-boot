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

import java.io.Serializable;

/**
 * <p>实体实现该接口表示想要实现树结构
 */
public interface Treeable<ID extends Serializable> {

    public String getName();

    public void setName(String name);

    /**
     * 显示的图标 大小为16×16
     *
     * @return
     */
    public String getIcon();

    public void setIcon(String icon);

    /**
     * 父路径
     *
     * @return
     */
    public ID getParentId();

    public void setParentId(ID parentId);

    /**
     * 所有父路径 如1,2,3,
     *
     * @return
     */
    public String getParentIds();

    public void setParentIds(String parentIds);

    /**
     * 获取 parentIds 之间的分隔符
     *
     * @return
     */
    public String getSeparator();

    /**
     * 把自己构造出新的父节点路径
     *
     * @return
     */
    public String makeSelfAsNewParentIds();

    /**
     * 权重 用于排序 越小越排在前边
     *
     * @return
     */
    public Integer getWeight();

    public void setWeight(Integer weight);

    /**
     * 是否是根节点
     *
     * @return
     */
    public boolean isRoot();

    /**
     * 是否是叶子节点
     *
     * @return
     */
    public boolean isLeaf();

    /**
     * 是否有孩子节点
     *
     * @return
     */
    public boolean isHasChildren();

    /**
     * 自定义属性
     *
     * @return
     */
    public String getDiy();

    public void setDiy(String diy);

    /**
     * 根节点默认图标 如果没有默认 空即可  大小为16×16
     */
    public String getRootDefaultIcon();

    /**
     * 树枝节点默认图标 如果没有默认 空即可  大小为16×16
     */
    public String getBranchDefaultIcon();

    /**
     * 树叶节点默认图标 如果没有默认 空即可  大小为16×16
     */
    public String getLeafDefaultIcon();


}
