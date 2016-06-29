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

import java.io.Closeable;

/**
 * Created by junfu on 2016/5/6.
 */
public interface IProgressReporter extends Closeable {
    void setStatus(String var1);

    void setDetails(String var1);

    void addStepCount(int var1);

    void setStepCount(int var1);

    void incrementStep();

    void error(String var1);
}