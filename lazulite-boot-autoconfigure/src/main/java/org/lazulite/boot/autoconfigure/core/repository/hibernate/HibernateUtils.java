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

package org.lazulite.boot.autoconfigure.core.repository.hibernate;

import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 根据 jpa api 获取hibernate相关api
 */
public class HibernateUtils {

    /**
     * 根据jpa EntityManager 获取 hibernate Session API
     *
     * @param em
     * @return
     */
    public static Session getSession(EntityManager em) {
        return (Session) em.getDelegate();
    }

    /**
     * 根据jpa EntityManager 获取 hibernate SessionFactory API
     *
     * @param em
     * @return
     * @see #getSessionFactory(EntityManagerFactory)
     */
    public static SessionFactory getSessionFactory(EntityManager em) {
        Session session = (Session) em.getDelegate();
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
        return sessionFactory;
    }

    /**
     * 根据jpa EntityManagerFactory 获取 hibernate SessionFactory API
     *
     * @param emf
     * @return
     */
    public static SessionFactory getSessionFactory(EntityManagerFactory emf) {
        HibernateEntityManagerFactory hmf = emf.unwrap(HibernateEntityManagerFactory.class);
        return hmf.getSessionFactory();
    }

    /**
     * 根据 jpa EntityManager 获取hibernate Cache API
     *
     * @param em
     * @return
     * @see #getCache(EntityManagerFactory)
     */
    public static Cache getCache(EntityManager em) {
        return getCache(em.getEntityManagerFactory());
    }

    /**
     * 根据jpa EntityManagerFactory 获取 hibernate Cache API
     *
     * @param emf
     * @return
     */
    public static Cache getCache(EntityManagerFactory emf) {
        return getSessionFactory(emf).getCache();
    }

    /**
     * 清空一级缓存
     *
     * @param em
     */
    public static void evictLevel1Cache(EntityManager em) {
        em.clear();
    }

    /**
     * 根据jpa EntityManager 清空二级缓存
     *
     * @param em
     * @see #evictLevel2Cache(EntityManagerFactory)
     */
    public static void evictLevel2Cache(EntityManager em) {
        evictLevel2Cache(em.getEntityManagerFactory());
    }

    /**
     * 根据jpa EntityManagerFactory 清空二级缓存 包括：
     * 1、实体缓存
     * 2、集合缓存
     * 3、查询缓存
     * 注意：
     * jpa Cache api 只能evict 实体缓存，其他缓存是删不掉的。。。
     */
    public static void evictLevel2Cache(EntityManagerFactory emf) {
        Cache cache = HibernateUtils.getCache(emf);
        cache.evictEntityRegions();
        cache.evictCollectionRegions();
        cache.evictDefaultQueryRegion();
        cache.evictQueryRegions();
        cache.evictNaturalIdRegions();
    }
}
