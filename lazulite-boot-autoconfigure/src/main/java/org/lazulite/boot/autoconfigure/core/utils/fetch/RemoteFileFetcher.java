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
package org.lazulite.boot.autoconfigure.core.utils.fetch;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 定时抓取远程文件
 */
public class RemoteFileFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileFetcher.class);
    private static final ScheduledExecutorService scheduledExecutorService =
            Executors
                    .newSingleThreadScheduledExecutor(new ThreadFactory() {
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "RemoteFileFetcher_Schedule_Thread");
                        }
                    });
    private byte[] fileConent;
    private String url;
    private long lastModified;
    private int connectTimeout;
    private int readTimeout;
    private FileChangeListener listener;


    private RemoteFileFetcher(String url, int reloadInterval, FileChangeListener listener) {
        this.connectTimeout = 1000;
        this.readTimeout = 1000;

        this.url = url;
        this.listener = listener;
        if (reloadInterval > 0) {
            scheduledExecutorService.scheduleWithFixedDelay(
                    new Runnable() {
                        public void run() {
                            RemoteFileFetcher.this.doFetch();
                        }
                    },
                    reloadInterval, reloadInterval, TimeUnit.MILLISECONDS);
        }
        doFetch();
    }

    public static RemoteFileFetcher createPeriodFetcher(String url,
                                                        int reloadInterval,
                                                        FileChangeListener listener) {

        return new RemoteFileFetcher(url, reloadInterval, listener);

    }

    private void doFetch() {
        if (url == null) {
            return;
        }
        LOGGER.info("Begin fetch remote file... url = {}", this.url);
        try {
            URL target = new URL(this.url);
            this.fileConent = IOUtils.toByteArray(target);
            this.lastModified = System.currentTimeMillis();
            if (this.listener != null && this.fileConent != null) {
                this.listener.fileReloaded(this.fileConent);
            }
        } catch (Exception e) {
            LOGGER.error("read from url failed", e);
        }
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public byte[] getFileByteArray() {
        return this.fileConent;
    }

    public interface FileChangeListener {
        public abstract void fileReloaded(byte[] contentBytes) throws IOException;
    }
}
