package org.lazulite.boot.autoconfigure.osaam.shiro.config;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.lazulite.boot.autoconfigure.osaam.shiro.cache.spring.SpringCacheManagerWrapper;
import org.lazulite.boot.autoconfigure.osaam.shiro.session.mgt.OnlineSessionFactory;
import org.lazulite.boot.autoconfigure.osaam.shiro.session.mgt.eis.OnlineSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;


public class ShiroConfiguration {


    @Bean(name = "lifecycleBeanPostProcessor")
    @ConditionalOnMissingBean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @ConditionalOnMissingBean
    @Bean(name = "defaultAdvisorAutoProxyCreator")
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }


    @Bean(name = "securityManager")
    @ConditionalOnMissingBean
    public DefaultSecurityManager securityManager(CacheManager cacheManager) {
        DefaultSecurityManager sm = new DefaultWebSecurityManager();
        sm.setCacheManager(cacheManager);
        return sm;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager);
        return aasa;
    }

    @Bean
    public CacheManager springCacheManagerWrapper(org.springframework.cache.CacheManager cacheManager){
        SpringCacheManagerWrapper springCacheManagerWrapper=new SpringCacheManagerWrapper();
        springCacheManagerWrapper.setCacheManager(cacheManager);
        return springCacheManagerWrapper;
    }

    @Bean
    public OnlineSessionFactory onlineSessionFactory(){
        OnlineSessionFactory onlineSessionFactory=new OnlineSessionFactory();
        return onlineSessionFactory;
    }

    @Bean
    public OnlineSessionDAO onlineSessionDAO(){
        JavaUuidSessionIdGenerator javaUuidSessionIdGenerator=new JavaUuidSessionIdGenerator();

        OnlineSessionDAO onlineSessionDAO=new OnlineSessionDAO();
        onlineSessionDAO.setSessionIdGenerator(javaUuidSessionIdGenerator);
        onlineSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
        return onlineSessionDAO;
    }




}
