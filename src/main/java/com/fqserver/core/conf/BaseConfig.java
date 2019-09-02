/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.fqserver.core.conf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Basic configuration class. Stores the configuration data but does not provide
 * any load or save functions. If you want to load your Configuration from a
 * file use PropertiesConfiguration or XmlConfiguration.
 *
 * This class extends normal Java properties by adding the possibility to use
 * the same key many times concatenating the value strings instead of
 * overwriting them.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:daveb@miceda-data">Dave Bryson</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:kjohnson@transparent.com">Kent Johnson</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:ipriha@surfeu.fi">Ilkka Priha</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:ksh@scand.com">Konstantin Shaposhnikov</a>
 * @version $Id: BaseConfiguration.java 1231721 2012-01-15 18:32:07Z oheger $
 */
public class BaseConfig extends AbstractConfiguration implements Cloneable {
    /** stores the configuration key-value pairs */
    private Map<String, Object> store = new LinkedHashMap<String, Object>();

    /**
     * Adds a key/value pair to the map. This routine does no magic morphing. It
     * ensures the keylist is maintained
     *
     * @param key
     *            key to use for mapping
     * @param value
     *            object to store
     */
    @Override
    protected void addPropertyDirect(String key, Object value) {
        Object previousValue = getProperty(key);

        if (previousValue == null) {
            getStore().put(key, value);
        } else if (previousValue instanceof List) {
            // safe to case because we have created the lists ourselves
            @SuppressWarnings("unchecked")
            List<Object> valueList = (List<Object>) previousValue;
            // the value is added to the existing list
            valueList.add(value);
        } else {
            // the previous value is replaced by a list containing the previous
            // value and the new value
            List<Object> list = new ArrayList<Object>();
            list.add(previousValue);
            list.add(value);

            getStore().put(key, list);
        }
    }

    /**
     * Read property from underlying map.
     *
     * @param key
     *            key to use for mapping
     *
     * @return object associated with the given configuration key.
     */
    public Object getProperty(String key) {
        return getStore().get(key);
    }

    /**
     * Check if the configuration is empty
     *
     * @return {@code true} if Configuration is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return getStore().isEmpty();
    }

    /**
     * check if the configuration contains the key
     *
     * @param key
     *            the configuration key
     *
     * @return {@code true} if Configuration contain given key, {@code false}
     *         otherwise.
     */
    public boolean containsKey(String key) {
        return getStore().containsKey(key);
    }

    /**
     * Clear a property in the configuration.
     *
     * @param key
     *            the key to remove along with corresponding value.
     */
    @Override
    protected void clearPropertyDirect(String key) {
        if (containsKey(key)) {
            getStore().remove(key);
        }
    }

    @Override
    public void clear() {
        fireEvent(EVENT_CLEAR, null, null, true);
        getStore().clear();
        fireEvent(EVENT_CLEAR, null, null, false);
    }

    /**
     * Get the list of the keys contained in the configuration repository.
     *
     * @return An Iterator.
     */
    public Iterator<String> getKeys() {
        return getStore().keySet().iterator();
    }

    /**
     * Creates a copy of this object. This implementation will create a deep
     * clone, i.e. the map that stores the properties is cloned, too. So changes
     * performed at the copy won't affect the original and vice versa.
     *
     * @return the copy
     * @since 1.3
     */
    @Override
    public Object clone() {
        try {
            BaseConfig copy = (BaseConfig) super.clone();
            // This is safe because the type of the map is known
            @SuppressWarnings("unchecked")
            Map<String, Object> clonedStore = (Map<String, Object>) clone(getStore());
            copy.setStore(clonedStore);

            // Handle collections in the map; they have to be cloned, too
            for (Map.Entry<String, Object> e : getStore().entrySet()) {
                if (e.getValue() instanceof Collection) {
                    // This is safe because the collections were created by
                    // ourselves
                    @SuppressWarnings("unchecked")
                    Collection<String> strList = (Collection<String>) e.getValue();
                    copy.getStore().put(e.getKey(), new ArrayList<String>(strList));
                }
            }

            return copy;
        }
        catch (CloneNotSupportedException cex) {
            // should not happen
            throw new ConfigurationRuntimeException(cex);
        }
    }

    /** Constant for the name of the clone() method. */
    private static final String METHOD_CLONE = "clone";

    /**
     * An internally used helper method for cloning objects. This implementation
     * is not very sophisticated nor efficient. Maybe it can be replaced by an
     * implementation from Commons Lang later. The method checks whether the
     * passed in object implements the {@code Cloneable} interface. If this is
     * the case, the {@code clone()} method is invoked by reflection. Errors
     * that occur during the cloning process are re-thrown as runtime
     * exceptions.
     *
     * @param obj
     *            the object to be cloned
     * @return the cloned object
     * @throws CloneNotSupportedException
     *             if the object cannot be cloned
     */
    public static Object clone(Object obj) throws CloneNotSupportedException {
        if (obj instanceof Cloneable) {
            try {
                Method m = obj.getClass().getMethod(METHOD_CLONE);
                return m.invoke(obj);
            }
            catch (NoSuchMethodException nmex) {
                throw new CloneNotSupportedException("No clone() method found for class"
                                                     + obj.getClass().getName());
            }
            catch (IllegalAccessException iaex) {
                throw new ConfigurationRuntimeException(iaex);
            }
            catch (InvocationTargetException itex) {
                throw new ConfigurationRuntimeException(itex);
            }
        } else {
            throw new CloneNotSupportedException(obj.getClass().getName()
                                                 + " does not implement Cloneable");
        }
    }

    public Map<String, Object> getStore() {
        return store;
    }

    public void setStore(Map<String, Object> store) {
        this.store = store;
    }
}
