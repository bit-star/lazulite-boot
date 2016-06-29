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

package org.lazulite.boot.autoconfigure.core;

/**
 * Message delivery class.
 *
 * @author Rugal Berntein
 */
public class Message {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAILURE";
    private String status = FAIL;
    private String message = null;
    private Object data = null;

    private Message() {
    }

    private Message(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private Message(String status, String message) {
        this(status, message, null);
    }

    public static Message failMessage(String message) {
        return new Message(FAIL, message);
    }

    public static Message successMessage(String message, Object data) {
        return new Message(SUCCESS, message, data);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
