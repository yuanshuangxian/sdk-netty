/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package com.fqserver.core.utils;

import com.fqserver.core.utils.reflect.ClassReflection;
import com.fqserver.core.utils.reflect.Constructor;
import com.fqserver.core.utils.reflect.ReflectionException;




/**
 * Pool that creates new instances of a type using reflection. The type must
 * have a zero argument constructor. {@link Constructor#setAccessible(boolean)}
 * will be used if the class and/or constructor is not visible.
 * 
 * @author Nathan Sweet
 */
public class ReflectionPool<T> extends Pool<T> {
    private final Constructor constructor;

    public ReflectionPool(Class<T> type) {
        this(type, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int max) {
        super(max);
        constructor = findConstructor(type);
        if (constructor == null)
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): "
                                       + type.getName());
    }

    private Constructor findConstructor(Class<T> type) {
        try {
            return ClassReflection.getConstructor(type, (Class[]) null);
        }
        catch (Exception ex1) {
            try {
                Constructor constructor = ClassReflection.getDeclaredConstructor(type,
                                                                                 (Class[]) null);
                constructor.setAccessible(true);
                return constructor;
            }
            catch (ReflectionException ex2) {
                return null;
            }
        }
    }

    protected T newObject() {
        try {
            return (T) constructor.newInstance((Object[]) null);
        }
        catch (Exception ex) {
            throw new RuntimeException("Unable to create new instance: "
                                       + constructor.getDeclaringClass().getName(), ex);
        }
    }
}
