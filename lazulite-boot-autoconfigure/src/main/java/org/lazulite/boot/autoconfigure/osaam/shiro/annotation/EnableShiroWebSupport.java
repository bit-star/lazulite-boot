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

package org.lazulite.boot.autoconfigure.osaam.shiro.annotation;

import org.lazulite.boot.autoconfigure.osaam.shiro.spring.ShiroWebMvcConfigurerAdapter;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.*;

/**
 * Annotation to automatically register the following beans for usage with Spring MVC.
 * <ul>
 * <li>
 * {@link com.lazulite.spring.boot.autoconfig.shiro.annotation.SessionUser}.
 * </li>
 * </ul>
 *
 * @author John Zhang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Inherited
@Import({EnableShiroWebSupport.ShiroWebMvcConfigurerAdapterImportSelector.class})
public @interface EnableShiroWebSupport {

    static class ShiroWebMvcConfigurerAdapterImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{ShiroWebMvcConfigurerAdapter.class.getName()};
        }

    }
}
