/*-
 * Copyright 2014-2015 Thodoris Sotiropoulos
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.pdguard.eagent.utils;

import com.google.common.base.CaseFormat;
import org.pdguard.eagent.forms.ParametersBundle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.List;
import java.util.TreeMap;

/**
 * This utility class is responsible for the creation of TreeMap object that
 * stores the parameters which are included in the signature of a PDGuard
 * request.
 *
 * @author Thodoris Sotiropoulos
 */
public final class ParametersMapper {
    /** Hide constructor. */
    private ParametersMapper() { }

    /**
     * This method creates a TreeMap object from a bundle of request parameters.
     *
     * It stores only the parameters that have been included in the signature
     * calculation of request.
     *
     * @param aclass Class of bundle of request parameters.
     * @param bundle Object of bundle of request parameters.
     *
     * @return Map that stores parameters used for the validation of signature.
     */
    public static TreeMap<String, String> mapObject(
            Class<? extends ParametersBundle> aclass, ParametersBundle bundle) {
        TreeMap<String, String> parameters = new TreeMap<>();
        List<Field> fields = new ArrayList<>(
                Arrays.asList(aclass.getDeclaredFields()));
        if (aclass.getSuperclass() != null)
            fields.addAll(Arrays.asList(aclass.getSuperclass()
                    .getDeclaredFields()));
        for (Field field: fields) {
            String fieldName = field.getName();
            if (!fieldName.equals("signature"))
                parameters.put(CaseFormat.UPPER_CAMEL.to(
                        CaseFormat.LOWER_UNDERSCORE, fieldName),
                        getFieldValue(aclass, bundle, fieldName));
        }
        return parameters;
    }

    /**
     * This method gets the value of a field via its getter.
     *
     * @param aclass Class of bundle of request parameters.
     * @param bundle Object of bundle of request parameters.
     * @param fieldName Field name.
     *
     * @return Value of field.
     *
     * @throws RuntimeException is thrown when an unxpected error occurs.
     */
    static String getFieldValue(Class<? extends ParametersBundle> aclass,
            ParametersBundle bundle, String fieldName) {
        String capitalizedField = fieldName.substring(0, 1).toUpperCase(
                Locale.ENGLISH) + fieldName.substring(1);
        String getter = "get" + capitalizedField;
        String fieldValue;
        try {
            fieldValue = String.valueOf(aclass.getMethod(getter)
                    .invoke(bundle));
        } catch (IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return fieldValue;
    }
}
