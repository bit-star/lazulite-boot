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
package org.lazulite.boot.autoconfigure.osaam.shiro.web.filter.sync;


import org.lazulite.boot.autoconfigure.osaam.shiro.ShiroConstants;
import org.lazulite.boot.autoconfigure.osaam.shiro.session.mgt.OnlineSession;
import org.lazulite.boot.autoconfigure.osaam.shiro.session.mgt.eis.OnlineSessionDAO;
import org.apache.shiro.web.filter.PathMatchingFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 同步当前会话数据到数据库
 * <p/>
 * <p>User: 
 * <p>Date: 13-3-21 下午5:53
 * <p>Version: 1.0
 */
public class SyncOnlineSessionFilter extends PathMatchingFilter {

    private OnlineSessionDAO onlineSessionDAO;

    public void setOnlineSessionDAO(OnlineSessionDAO onlineSessionDAO) {
        this.onlineSessionDAO = onlineSessionDAO;
    }


    /**
     * 同步会话数据到DB 一次请求最多同步一次 防止过多处理  需要放到Shiro过滤器之前
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        OnlineSession session = (OnlineSession) request.getAttribute(ShiroConstants.ONLINE_SESSION);
        //如果session stop了 也不同步
        if (session != null && session.getStopTimestamp() == null) {
            onlineSessionDAO.syncToDb(session);
        }
        return true;
    }

}
