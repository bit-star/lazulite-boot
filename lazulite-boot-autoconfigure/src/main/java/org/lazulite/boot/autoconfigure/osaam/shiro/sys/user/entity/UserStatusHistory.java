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
package org.lazulite.boot.autoconfigure.osaam.shiro.sys.user.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.lazulite.boot.autoconfigure.osaam.shiro.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * <p>User: 
 * <p>Date: 13-3-11 下午3:23
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_user_status_history")
public class UserStatusHistory extends BaseEntity<Long> {

    /**
     * 锁定的用户
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private User user;

    /**
     * 备注信息
     */
    private String reason;

    /**
     * 操作的状态
     */
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * 操作的管理员
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "op_user_id")
    private User opUser;

    /**
     * 操作时间
     */
    @Column(name = "op_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date opDate;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getOpUser() {
        return opUser;
    }

    public void setOpUser(User opUser) {
        this.opUser = opUser;
    }

    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }
}
