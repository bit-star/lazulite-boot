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

package org.lazulite.boot.autoconfigure.core.progress;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by junfu on 2016/5/6.
 */
public class ProgressReporter implements IProgressReporter, AutoCloseable {
    private Socket m_socket;
    private PrintWriter m_out;
    private BufferedReader m_in;

    public ProgressReporter(String host, int port) throws IOException {
        this.m_socket = new Socket(host, port);
        this.m_socket.setSoTimeout(2000);
        this.m_out = new PrintWriter(this.m_socket.getOutputStream(), true);
        this.m_in = new BufferedReader(new InputStreamReader(this.m_socket.getInputStream()));
    }

    private String readResponse() {
        try {
            return this.m_in.readLine();
        } catch (IOException var1) {
            return "";
        }
    }

    public void setStatus(String status) {
        this.m_out.println("status: " + status);
        this.readResponse();
    }

    public void setDetails(String details) {
        this.m_out.println("details: " + details);
        this.readResponse();
    }

    public void addStepCount(int count) {
        this.m_out.println("add-steps: " + count);
        this.readResponse();
    }

    public void setStepCount(int count) {
        this.m_out.println("steps: " + count);
        this.readResponse();
    }

    public void incrementStep() {
        this.m_out.println("step-increment");
        this.readResponse();
    }

    public void error(String message) {
        this.m_out.println("error: " + message);
        this.readResponse();
    }

    public void close() {
        try {
            this.m_out.println("bye");
        } catch (Exception var1) {
            ;
        }

        IOUtils.closeQuietly(this.m_in);
        IOUtils.closeQuietly(this.m_out);
        IOUtils.closeQuietly(this.m_socket);
    }
}

