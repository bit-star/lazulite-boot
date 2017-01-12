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
package org.lazulite.boot.autoconfigure.osaam.shiro.session.mgt.eis;


import org.lazulite.boot.autoconfigure.osaam.shiro.session.mgt.OnlineSessionFactory;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.entity.UserOnline;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.lazulite.boot.autoconfigure.osaam.shiro.ShiroConstants;
import org.lazulite.boot.autoconfigure.osaam.shiro.session.mgt.OnlineSession;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.service.UserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Date;

/**
 * 对于db的操作 考虑使用 异步+队列机制

 */
public class OnlineSessionDAO extends EnterpriseCacheSessionDAO {
    /**
     * 上次同步数据库的时间戳
     */
    private static final String LAST_SYNC_DB_TIMESTAMP =
            OnlineSessionDAO.class.getName() + "LAST_SYNC_DB_TIMESTAMP";

    @Autowired
    private UserOnlineService userOnlineService;

    @Autowired
    private OnlineSessionFactory onlineSessionFactory;

    /**
     * 同步session到数据库的周期 单位为毫秒（默认5分钟）
     */
    private long dbSyncPeriod = 5 * 60 * 1000;

    public void setDbSyncPeriod(long dbSyncPeriod) {
        this.dbSyncPeriod = dbSyncPeriod;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        UserOnline userOnline = userOnlineService.findOne(Long.valueOf(String.valueOf(sessionId)));
        if (userOnline == null) {
            return null;
        }
        return onlineSessionFactory.createSession(userOnline);
    }

    /**
     * 将会话同步到DB
     *
     * @param session
     */
    public void syncToDb(OnlineSession session) {

        Date lastSyncTimestamp = (Date) session.getAttribute(LAST_SYNC_DB_TIMESTAMP);

        //当会话中的属性改变时-->强制同步
        //如果对于属性丢失影响不大 可以考虑把这块功能去掉
        if (lastSyncTimestamp != null) {
            boolean needSync = true;
            long deltaTime = session.getLastAccessTime().getTime() - lastSyncTimestamp.getTime();
            if (deltaTime < dbSyncPeriod) { //时间差不足  无需同步
                needSync = false;
            }
            boolean isGuest = session.getUserId() == null || session.getUserId() == 0L;

            //如果不是游客 且session 数据变更了 同步
            if (isGuest == false && session.isAttributeChanged()) {
                needSync = true;
            }

            if (needSync == false) {
                return;
            }
        }

        session.setAttribute(LAST_SYNC_DB_TIMESTAMP, session.getLastAccessTime());

        //更新完后 重置标识
        if (session.isAttributeChanged()) {
            session.resetAttributeChanged();
        }

        userOnlineService.online(UserOnline.fromOnlineSession(session));

    }

    /**
     * 会话过期时 离线处理
     *
     * @param session
     */
    @Override
    protected void doDelete(Session session) {
        OnlineSession onlineSession = (OnlineSession) session;
        //定时任务删除的此时就不删除了
        if (onlineSession.getAttribute(ShiroConstants.ONLY_CLEAR_CACHE) == null) {
            try {
                userOnlineService.offline(Long.valueOf(String.valueOf(onlineSession.getId())));
            } catch (Exception e) {
                //即使删除失败也无所谓
            }
        }

    }


}
