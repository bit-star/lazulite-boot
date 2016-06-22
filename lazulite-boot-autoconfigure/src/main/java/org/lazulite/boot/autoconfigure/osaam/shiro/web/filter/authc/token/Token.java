/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package org.lazulite.boot.autoconfigure.osaam.shiro.web.filter.authc.token;

/**
 * Auto-generated: 2016-05-19 15:13:33
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class Token {

    private String principal;
    private String credentials;
    private Boolean rememberMe=Boolean.FALSE;
    public void setPrincipal(String principal) {
         this.principal = principal;
     }
     public String getPrincipal() {
         return principal;
     }

    public void setCredentials(String credentials) {
         this.credentials = credentials;
     }
     public String getCredentials() {
         return credentials;
     }

    public Boolean getRememberMe() {
        return rememberMe==null?Boolean.FALSE:rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}