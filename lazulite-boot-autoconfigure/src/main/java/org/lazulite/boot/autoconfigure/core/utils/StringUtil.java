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

package org.lazulite.boot.autoconfigure.core.utils;

public class StringUtil {
    public static boolean isNull(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String getTopTenStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception in thread " + Thread.currentThread().getName() + " " + t.getMessage() + "\n");
        for (int i = 0; i < t.getStackTrace().length; i++) {
            if (i > 5) {
                return sb.toString();
            }
            StackTraceElement temp = t.getStackTrace()[i];
            sb.append("at " + temp.getClassName() + "." + temp.getMethodName() + "(" + temp.getFileName() + ":" + temp.getLineNumber() + ")\n");
        }
        return sb.toString();
    }

    public static String isNullToEmpty(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}
