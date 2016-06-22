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
package org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.service;


import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.lazulite.boot.autoconfigure.osaam.shiro.ShiroProperties;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.entity.User;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.exception.UserPasswordNotMatchException;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.exception.UserPasswordRetryLimitExceedException;
import org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.utils.UserLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PasswordService {

    @Autowired
    private CacheManager ehcacheManager;

    @Autowired
    private ShiroProperties shiroProperties;

    private Cache loginRecordCache;

    @Value(value = "${shiro.retry-max}")
    private int maxRetryCount = 10;

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @PostConstruct
    public void init() {
        loginRecordCache = ehcacheManager.getCache("loginRecordCache");
    }

    public void validate(User user, String password) {
        String username = user.getUsername();
        int retryCount = 0;
        Cache.ValueWrapper valueWrapper = loginRecordCache.get(username);
        if (valueWrapper != null) {
            retryCount = (Integer) valueWrapper.get();
            if (retryCount >= maxRetryCount) {
                UserLogUtils.log(
                        username,
                        "passwordError",
                        "password error, retry limit exceed! password: {},max retry count {}",
                        password, maxRetryCount);
                throw new UserPasswordRetryLimitExceedException(maxRetryCount);
            }
        }

        if (!matches(user, password)) {
            loginRecordCache.put(username, ++retryCount);
            UserLogUtils.log(
                    username,
                    "passwordError",
                    "password error! password: {} retry count: {}",
                    password, retryCount);
            throw new UserPasswordNotMatchException();
        } else {
            clearLoginRecordCache(username);
        }
    }

    public boolean matches(User user, String newPassword) {
        return user.getPassword().equals(encryptPassword(user.getUsername(), newPassword, user.getSalt()));
    }

    public void clearLoginRecordCache(String username) {
        loginRecordCache.evict(username);
    }


    public String encryptPassword(String username, String password, String salt) {
        Hash hash=new SimpleHash("MD5", password, ByteSource.Util.bytes(username+salt), shiroProperties.getHashIterations());
        String encodedPassword = hash.toHex();
        return encodedPassword;
    }


   /* public static void main(String[] args) {
        System.out.println(new PasswordService().encryptPassword("monitor", "123456", "iY71e4d123"));
    }*/
}
