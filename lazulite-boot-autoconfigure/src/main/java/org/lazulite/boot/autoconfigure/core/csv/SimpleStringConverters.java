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

package org.lazulite.boot.autoconfigure.core.csv;

/**
 * Created by junfu on 2016/5/6.
 */
abstract class SimpleStringConverters {
    SimpleStringConverters() {
    }

    static String toString(String valueAsString) {
        return valueAsString;
    }

    static Integer toInteger(String valueAsString) {
        valueAsString = valueAsString.trim();
        return valueAsString.isEmpty()?null:Integer.valueOf(Integer.parseInt(valueAsString));
    }

    static Integer toInt(String valueAsString) {
        Integer result = toInteger(valueAsString);
        if(result == null) {
            throw new RuntimeException(new RuntimeException("String value for type \'int\' cannot be empty."));
        } else {
            return result;
        }
    }

    static Boolean toBoolean(String valueAsString) {
        valueAsString = valueAsString.trim();
        if(valueAsString.isEmpty()) {
            return null;
        } else if(!valueAsString.equalsIgnoreCase("true") && !valueAsString.equalsIgnoreCase("false")) {
            throw new RuntimeException("Cannot parse boolean from string: " + valueAsString);
        } else {
            return Boolean.valueOf(valueAsString);
        }
    }

    static Boolean toBool(String valueAsString) {
        Boolean result = toBoolean(valueAsString);
        if(result == null) {
            throw new RuntimeException(new RuntimeException("String value for type \'boolean\' cannot be empty."));
        } else {
            return result;
        }
    }
}
