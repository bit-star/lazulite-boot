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
package org.lazulite.boot.autoconfigure.osaam.shiro.sys.resource.entity.tmp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * 界面是那个使用的菜单对象
 */
public class Menu implements Serializable {
    @JsonIgnore
    private Long id;
    private String text;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icon;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sref;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean heading;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String translate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alert;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String label;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Menu> submenu;

    public Menu(Long id, String text, String icon, String sref) {
        this.id = id;
        this.text = text;
        this.icon = icon;
        this.sref = sref;
    }

    public Menu(Long id, String text, String icon, String sref, Boolean heading, String translate, String alert, String label) {
        this.id = id;
        this.text = text;
        this.icon = icon;
        this.sref = sref;
        this.heading = heading;
        this.translate = translate;
        this.alert = alert;
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        if(!this.isHasChildren())return null;
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSref() {
        return sref;
    }

    public void setSref(String sref) {
        this.sref = sref;
    }

    public List<Menu> getSubmenu() {
        if (submenu == null) {
            submenu = Lists.newArrayList();
        }
        return submenu;
    }

    public void setSubmenu(List<Menu> submenu) {
        this.submenu = submenu;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isHasChildren() {
        return !getSubmenu().isEmpty();
    }


    public Boolean isHeading() {
        return heading;
    }

    public void setHeading(Boolean heading) {
        this.heading = heading;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", sref='" + sref + '\'' +
                ", heading=" + heading +
                ", translate='" + translate + '\'' +
                ", alert='" + alert + '\'' +
                ", label='" + label + '\'' +
                ", submenu=" + submenu +
                '}';
    }
}
