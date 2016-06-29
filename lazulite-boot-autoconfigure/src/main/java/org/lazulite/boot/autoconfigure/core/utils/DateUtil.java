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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static String DEFALUT_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private static String SIMPLE_FORMAT = "yyyyMMdd";

    public static String DateToString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DEFALUT_FORMAT);
        return df.format(date);
    }

    /***
     * e.q 20150713
     *
     * @param date
     * @return
     */
    public static String DateToOnlyDateString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(SIMPLE_FORMAT);
        return df.format(date);
    }

    public static String DateToString(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static Date StringToDate(String date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(DateToOnlyDateString(new Date()));
    }
}
