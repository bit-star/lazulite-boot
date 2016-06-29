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

package org.lazulite.boot.autoconfigure.core.web.bind.util;


import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.lazulite.boot.autoconfigure.core.entity.BaseEntity;
import org.lazulite.boot.autoconfigure.core.plugin.entity.Treeable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ZTreeUtils {
    public static <M extends BaseEntity<ID> & Treeable<ID>, ID extends Serializable> List<ZTree<ID>> convertToZtreeList(
            String contextPath, List<M> models, boolean async, boolean onlySelectLeaf) {
        List<ZTree<ID>> zTrees = Lists.newArrayList();

        if (models == null || models.isEmpty()) {
            return zTrees;
        }

        for (M m : models) {
            ZTree zTree = convertToZtree(m, !async, onlySelectLeaf);
            zTrees.add(zTree);
        }
        return zTrees;
    }

    public static <M extends BaseEntity<ID> & Treeable<ID>, ID extends Serializable> List<ZTree<ID>> convertToZtreeListIdTotal(
            String contextPath, List<M> models, boolean async, boolean onlySelectLeaf) {
        List<ZTree<ID>> zTrees = Lists.newArrayList();

        if (models == null || models.isEmpty()) {
            return zTrees;
        }

        for (M m : models) {
            ZTree zTree = convertToZtreeIdTotal(m, !async, onlySelectLeaf);
            zTrees.add(zTree);
        }
        return zTrees;
    }

    public static <M extends BaseEntity<ID> & Treeable<ID>, ID extends Serializable> ZTree<ID> convertToZtree(M m,
                                                                                                              boolean open, boolean onlyCheckLeaf) {
        ZTree<ID> zTree = new ZTree<ID>();
        zTree.setId(m.getId());
        zTree.setPid(m.getParentId());
        zTree.setName(m.getName());
        zTree.setIconSkin(m.getIcon());
        zTree.setOpen(open);
        zTree.setRoot(m.isRoot());
        zTree.setIsParent(m.isHasChildren());

        if (onlyCheckLeaf && zTree.isIsParent()) {
            zTree.setNocheck(true);
        } else {
            zTree.setNocheck(false);
        }

        return zTree;
    }

    /***
     * ZTree ID 用完整层级路径ids ZTree ID 输出 String 类型。 适合多表，多中类型的id。
     *
     * @param m
     * @param open
     * @param onlyCheckLeaf
     * @return
     */
    public static <M extends BaseEntity<ID> & Treeable<ID>, ID extends Serializable> ZTree<String> convertToZtreeIdTotal(
            M m, boolean open, boolean onlyCheckLeaf) {
        ZTree<String> zTree = new ZTree<String>();
        zTree.setId(m.getParentIds() + m.getId());

        zTree.setPid(StringUtils.removeEnd(m.getParentIds(), m.getSeparator()));

        zTree.setName(m.getName());
        zTree.setIconSkin(m.getIcon());
        zTree.setOpen(open);
        zTree.setRoot(m.isRoot());
        zTree.setIsParent(m.isHasChildren());
        zTree.setDiy(m.getDiy());
        if (onlyCheckLeaf && zTree.isIsParent()) {
            zTree.setNocheck(true);
        } else {
            zTree.setNocheck(false);
        }

        return zTree;
    }

    public static <M extends BaseEntity<ID> & Treeable<ID>, ID extends Serializable, P extends BaseEntity<ID> & Treeable<ID>> void relation(
            Collection<M> list, P parent) {
        for (M m : list) {
            m.setParentId(parent.getId());
            m.setParentIds(parent.makeSelfAsNewParentIds());
        }
    }

}
