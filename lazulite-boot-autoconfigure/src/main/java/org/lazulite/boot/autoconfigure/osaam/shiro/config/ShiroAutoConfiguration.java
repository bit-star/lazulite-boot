package org.lazulite.boot.autoconfigure.osaam.shiro.config;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.lazulite.boot.autoconfigure.osaam.shiro.ShiroProperties;
import org.lazulite.boot.autoconfigure.osaam.shiro.ShiroSignInProperties;
import org.lazulite.boot.autoconfigure.osaam.shiro.annotation.EnableShiroWebSupport;
import org.lazulite.boot.autoconfigure.osaam.shiro.matcher.RetryLimitHashedCredentialsMatcher;
import org.lazulite.boot.autoconfigure.osaam.shiro.web.filter.authc.CustomFormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
@EnableShiroWebSupport
@ConditionalOnWebApplication
@ConditionalOnClass(ShiroFilter.class)
@Import(ShiroConfiguration.class)
@EnableConfigurationProperties({ShiroProperties.class, ShiroSignInProperties.class})
public class ShiroAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroAutoConfiguration.class);
    @Autowired
    private ShiroProperties properties;

    @Autowired
    private ShiroSignInProperties signInProperties;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Bean(name = "realm")
    @DependsOn("lifecycleBeanPostProcessor")
    @ConditionalOnMissingBean
    public Realm realm(CacheManager cacheManager) {
        Class<?> realmClass = properties.getRealm();
        Assert.notNull(realmClass,"必须为shiro配置一个Realm");
        Realm realm = (Realm) BeanUtils.instantiate(realmClass);
        if (realm instanceof AuthenticatingRealm) {
            AuthenticatingRealm authenticatingRealm=((AuthenticatingRealm) realm);
            authenticatingRealm.setCredentialsMatcher(credentialsMatcher());
            authenticatingRealm.setCacheManager(cacheManager);
        }

        return realm;
    }

    @Bean(name = "credentialsMatcher")
    @ConditionalOnMissingBean
    public CredentialsMatcher credentialsMatcher() {
        RetryLimitHashedCredentialsMatcher credentialsMatcher = new RetryLimitHashedCredentialsMatcher(cacheManager);
        credentialsMatcher.setHashAlgorithmName(properties.getHashAlgorithmName());
        credentialsMatcher.setHashIterations(properties.getHashIterations());
        credentialsMatcher.setStoredCredentialsHexEncoded(properties.isStoredCredentialsHexEncoded());
        credentialsMatcher.setRetryMax(properties.getRetryMax());
        return credentialsMatcher;
    }

    private ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultSecurityManager securityManager, Realm realm, FormAuthenticationFilter formAuthenticationFilter) {
        securityManager.setRealm(realm);

        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl(properties.getLoginUrl());
        shiroFilter.setSuccessUrl(properties.getSuccessUrl());
        shiroFilter.setUnauthorizedUrl(properties.getUnauthorizedUrl());

        Map<String, Filter> filterMap = new LinkedHashMap<String, Filter>();
        filterMap.put("authc", formAuthenticationFilter);
        filterMap.put("logout",logoutFilter());
        shiroFilter.setFilters(filterMap);
        shiroFilter.setFilterChainDefinitionMap(properties.getFilterChainDefinitions());
        return shiroFilter;
    }
    private LogoutFilter logoutFilter(){
        LogoutFilter logoutFilter=new LogoutFilter();
        return logoutFilter;
    }

    @Bean(name = "shiroFilter")
    @DependsOn("securityManager")
    @ConditionalOnMissingBean
    public FilterRegistrationBean filterRegistrationBean(DefaultSecurityManager securityManager, Realm realm, FormAuthenticationFilter formAuthenticationFilter) throws Exception {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        //该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setFilter((Filter) getShiroFilterFactoryBean(securityManager, realm,formAuthenticationFilter).getObject());
        filterRegistration.setEnabled(true);
        filterRegistration.setAsyncSupported(true);
        //过滤器起的作用的请求来源 默认REQUEST
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST);
        filterRegistration.addUrlPatterns("/*");
        return filterRegistration;
    }

    @Bean
    public CustomFormAuthenticationFilter formAuthenticationFilter() {
        CustomFormAuthenticationFilter filter = new CustomFormAuthenticationFilter();
        filter.setLoginUrl(properties.getLoginUrl());
        filter.setSuccessUrl(properties.getSuccessUrl());
        filter.setUsernameParam(signInProperties.getUserParam());
        filter.setPasswordParam(signInProperties.getPasswordParam());
        filter.setRememberMeParam(signInProperties.getRememberMeParam());
        return filter;
    }

}
