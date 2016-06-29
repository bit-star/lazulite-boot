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


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.lazulite.boot.autoconfigure.core.csv.annotations.CsvColumn;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by junfu on 2016/5/6.
 */
public class StringArrayToObjectConverter<T> {
    private final Class<T> m_targetClass;
    private final Map<Integer, ValueSetter> m_valueSetters;
    private final StringArrayToObjectConverter.IValueSetter m_noOpConverter = new StringArrayToObjectConverter.NoOpValueSetter((StringArrayToObjectConverter.NoOpValueSetter)null);

    public static <T> StringArrayToObjectConverter<T> newConverter(Class<T> targetClass) throws Exception {
        return new StringArrayToObjectConverter(targetClass);
    }

    StringArrayToObjectConverter(Class<T> targetClass) throws Exception {
        this.m_targetClass = targetClass;
        this.m_valueSetters = buildFieldMap(targetClass);
    }

    public T convert(String[] line) throws Exception {
        try {
            Object e = this.m_targetClass.newInstance();

            for(int index = 0; index < line.length; ++index) {
                if(this.m_valueSetters.containsKey(Integer.valueOf(index))) {
                    StringArrayToObjectConverter.IValueSetter valueSetter = this.getValueSetter(index);
                    valueSetter.setFieldValue(e, line[index]);
                }
            }

            return (T) e;
        } catch (Exception var6) {
            throw new RuntimeException(var6);
        }
    }

    public void overrideFieldConverter(int fieldIndex, IStringConverter<?> converter) {
        if(!this.m_valueSetters.containsKey(Integer.valueOf(fieldIndex))) {
            throw new IndexOutOfBoundsException("Invalid field index for target class: field must be mapped from CSV column");
        } else {
            ((StringArrayToObjectConverter.ValueSetter)this.m_valueSetters.get(Integer.valueOf(fieldIndex))).setConverter(converter);
        }
    }

    private static Map<Integer, ValueSetter> buildFieldMap(Class<?> targetClass) {
        HashMap setterMap = Maps.newHashMap();

        try {
            Field[] var5;
            int var4 = (var5 = targetClass.getFields()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                Field e = var5[var3];
                CsvColumn column = (CsvColumn)e.getAnnotation(CsvColumn.class);
                if(column != null) {
                    int index = column.value();
                    if(setterMap.containsKey(Integer.valueOf(index))) {
                        throw new RuntimeException("Column [" + index + "] mapped to more one+ fields");
                    }

                    StringArrayToObjectConverter.ValueSetter valueSetter = new StringArrayToObjectConverter.ValueSetter(e, column.trim());
                    if(column.converter() != Object.class) {
                        valueSetter.setConverter((IStringConverter)column.converter().newInstance());
                    } else {
                        Class type = e.getType();
                        if(type == String.class) {
                            valueSetter.setConverter(new IStringConverter() {
                                public String convert(String rawField) {
                                    return SimpleStringConverters.toString(rawField);
                                }
                            });
                        } else if(type == Integer.class) {
                            valueSetter.setConverter(new IStringConverter() {
                                public Integer convert(String rawField) {
                                    return SimpleStringConverters.toInteger(rawField);
                                }
                            });
                        } else if(type == Integer.TYPE) {
                            valueSetter.setConverter(new IStringConverter() {
                                public Integer convert(String rawField) {
                                    return SimpleStringConverters.toInt(rawField);
                                }
                            });
                        } else if(type == Boolean.class) {
                            valueSetter.setConverter(new IStringConverter() {
                                public Boolean convert(String rawField) {
                                    return SimpleStringConverters.toBoolean(rawField);
                                }
                            });
                        } else if(type == Boolean.TYPE) {
                            valueSetter.setConverter(new IStringConverter() {
                                public Boolean convert(String rawField) {
                                    return SimpleStringConverters.toBool(rawField);
                                }
                            });
                        } else {
                            Method method = type.getMethod("fromString", new Class[]{String.class});
                            valueSetter.setConverter(new StringArrayToObjectConverter.FromStringConverter(method));
                        }
                    }

                    setterMap.put(Integer.valueOf(index), valueSetter);
                }
            }

            ImmutableMap.Builder var13 = new ImmutableMap.Builder();
            var13.putAll(setterMap);
            return var13.build();
        } catch (RuntimeException var11) {
            throw var11;
        } catch (Exception var12) {
            throw new RuntimeException(var12);
        }
    }

    private StringArrayToObjectConverter.IValueSetter getValueSetter(int index) {
        return this.m_valueSetters.containsKey(Integer.valueOf(index))?(StringArrayToObjectConverter.IValueSetter)this.m_valueSetters.get(Integer.valueOf(index)):this.m_noOpConverter;
    }

    private static class FromStringConverter implements IStringConverter<Object> {
        private final Method m_method;

        public FromStringConverter(Method method) {
            this.m_method = method;
        }

        public Object convert(String rawValue) {
            try {
                return this.m_method.invoke((Object)null, new Object[]{rawValue});
            } catch (Exception var3) {
                throw new RuntimeException(var3);
            }
        }
    }

    private interface IValueSetter {
        void setFieldValue(Object var1, String var2) throws Exception;
    }

    private static class NoOpValueSetter implements StringArrayToObjectConverter.IValueSetter {
        private NoOpValueSetter(NoOpValueSetter noOpValueSetter) {
        }

        public void setFieldValue(Object o, String rawField) throws Exception {
        }
    }

    private static class ValueSetter implements StringArrayToObjectConverter.IValueSetter {
        private final Field m_field;
        private final boolean m_trim;
        private IStringConverter<?> m_converter;

        public ValueSetter(Field field, boolean trim) {
            this.m_field = field;
            this.m_trim = trim;
        }

        public void setFieldValue(Object o, String rawValue) throws Exception {
            if(rawValue != null && this.m_trim) {
                rawValue = rawValue.trim();
            }

            this.m_field.set(o, this.m_converter.convert(rawValue));
        }

        void setConverter(IStringConverter<?> converter) {
            this.m_converter = converter;
        }
    }
}

