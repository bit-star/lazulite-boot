/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package org.lazulite.boot.autoconfigure.osaam.shiro.web.filter.authc.info;

/**
 * Auto-generated: 2016-05-19 15:6:47
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class Authc {

    private Principal principal;
    private Credentials credentials;
    public void setPrincipal(Principal principal) {
         this.principal = principal;
     }
     public Principal getPrincipal() {
         return principal;
     }

    public void setCredentials(Credentials credentials) {
         this.credentials = credentials;
     }
     public Credentials getCredentials() {
         return credentials;
     }

}