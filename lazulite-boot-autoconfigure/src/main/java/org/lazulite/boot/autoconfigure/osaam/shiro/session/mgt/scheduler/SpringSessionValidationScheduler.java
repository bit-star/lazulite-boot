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
package org.lazulite.boot.autoconfigure.osaam.shiro.session.mgt.scheduler;

import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.session.mgt.ValidatingSessionManager;
import org.lazulite.boot.autoconfigure.osaam.shiro.web.session.mgt.OnlineWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 使用spring的任务调度器完成 session验证

 */
public class SpringSessionValidationScheduler implements SessionValidationScheduler {

    //TODO - complete JavaDoc

    /*--------------------------------------------
    |             C O N S T A N T S             |
    ============================================*/
    /**
     * The default interval at which sessions will be validated (1 hour);
     * This can be overridden by calling {@link #setSessionValidationInterval(long)}
     */
    public static final long DEFAULT_SESSION_VALIDATION_INTERVAL = DefaultSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL;

    /*--------------------------------------------
    |    I N S T A N C E   V A R I A B L E S    |
    ============================================*/
    private static final Logger log = LoggerFactory.getLogger(SpringSessionValidationScheduler.class);

    /**
     * spring任务调度器
     */
    private TaskScheduler scheduler;

    private volatile boolean enabled = false;

    /**
     * The session manager used to validate sessions.
     */
    @Autowired
    private ValidatingSessionManager sessionManager;
    /**
     * The session validation interval in milliseconds.
     */
    private long sessionValidationInterval = DEFAULT_SESSION_VALIDATION_INTERVAL;

    /**
     * Default constructor.
     */
    public SpringSessionValidationScheduler() {
    }

    /*--------------------------------------------
    |         C O N S T R U C T O R S           |
    ============================================*/

    /**
     * Constructor that specifies the session manager that should be used for validating sessions.
     *
     * @param sessionManager the <tt>SessionManager</tt> that should be used to validate sessions.
     */
    public SpringSessionValidationScheduler(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @PostConstruct
    public void init() {
        ((OnlineWebSessionManager) sessionManager).setSessionValidationScheduler(this);
    }

    /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(final TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }


    public void setSessionManager(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Specifies how frequently (in milliseconds) this Scheduler will call the
     * {@link ValidatingSessionManager#validateSessions() ValidatingSessionManager#validateSessions()} method.
     * <p>
     * <p>Unless this method is called, the default value is {@link #DEFAULT_SESSION_VALIDATION_INTERVAL}.
     *
     * @param sessionValidationInterval
     */
    public void setSessionValidationInterval(long sessionValidationInterval) {
        this.sessionValidationInterval = sessionValidationInterval;
    }

    /*--------------------------------------------
    |               M E T H O D S               |
    ============================================*/

    /**
     * Starts session validation by creating a spring PeriodicTrigger.
     */
    public void enableSessionValidation() {

        enabled = true;

        if (log.isDebugEnabled()) {
            log.debug("Scheduling session validation job using Spring Scheduler with " +
                    "session validation interval of [" + sessionValidationInterval + "]ms...");
        }

        try {

            PeriodicTrigger trigger = new PeriodicTrigger(sessionValidationInterval, TimeUnit.MILLISECONDS);
            trigger.setInitialDelay(sessionValidationInterval);

            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    if (enabled) {
                        sessionManager.validateSessions();
                    }
                }
            }, trigger);

            this.enabled = true;

            if (log.isDebugEnabled()) {
                log.debug("Session validation job successfully scheduled with Spring Scheduler.");
            }

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error starting the Spring Scheduler session validation job.  Session validation may not occur.", e);
            }
        }
    }

    public void disableSessionValidation() {
        if (log.isDebugEnabled()) {
            log.debug("Stopping Spring Scheduler session validation job...");
        }

        this.enabled = false;
    }
}
