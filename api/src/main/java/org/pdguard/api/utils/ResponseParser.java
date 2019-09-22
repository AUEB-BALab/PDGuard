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

package org.pdguard.api.utils;

import com.google.common.base.CaseFormat;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * This is a utility class that is responsible for the parsing of an escrow
 * agent's response either to an object (e.g. request token object, encryption
 * key object, etc.) from a url encoded response or to a url encoded response
 * from an existing object.
 *
 * @author Thodoris Sotiropoulos
 */
public final class ResponseParser {
    /** Hide constructor of utility class. */
    private ResponseParser() { }

    /**
     * This method converts an object such as Request Token object, or Client
     * Credentials object to a url encoded string.
     *
     * @param givenClass Class of given object.
     * @param obj Object to turn into a url encoded string.
     *
     * @return URL encoded string specified by the given object.
     *
     * @throws RuntimeException is thrown when cannot extract URL encoded
     * string from the given object.
     */
    public static String toURLEncoded(final Class givenClass,
            final Object obj) {
        Field[] fields = givenClass.getDeclaredFields();
        StringBuilder response = new StringBuilder();
        boolean firstLoop = true;
        for (Field field: fields) {
            String parameterPair;
            try {
                if (field.isAccessible())
                    parameterPair = publicField(obj, field);
                else
                    parameterPair = nonPublicField(givenClass, obj, field);
                if (firstLoop) {
                    firstLoop = false;
                    response.append(parameterPair);
                } else
                    response.append("&").append(parameterPair);
            } catch (InvocationTargetException | NoSuchMethodException
                    | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException("Unexpected error on response"
                        + " parsing");
            }
        }
        return response.toString();
    }

    /**
     * This method creates a key value url encoded string from an existing
     * public field of the given class.
     *
     * @param obj Object to turn into a URL encoded string.
     * @param field Public field used to generate url encoded key value string.
     *
     * @return URL encoded key value string of a public class field.
     *
     * @throws IllegalAccessException is thrown when an application tries to
     * reflectively create an instance (other than an array), set or get a
     * field, or invoke a method, but the currently executing method does not
     * have access to the definition of the specified class, field, method or
     * constructor.
     */
    public static String publicField(final Object obj, final Field field)
            throws IllegalAccessException {
        String parameterPair = null;
        try {
            parameterPair = URLEncoder.encode(CaseFormat.UPPER_CAMEL.to(
                    CaseFormat.LOWER_UNDERSCORE, field.getName()), "UTF-8")
                + "=" + URLEncoder.encode(String.valueOf(field.get(obj)),
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assert parameterPair != null;
        return parameterPair.replace("+", "%20");
    }

    /**
     * This method creates a key value url encoded string from an existing
     * non public field of the given class.
     *
     * Method gets the value of this field using its getter method. So it's
     * prerequisite for given class to have getter of this field; otherwise
     * exception is thrown.
     *
     * @param givenClass Class of the given object.
     * @param obj Object to turn into a URL encoded string.
     * @param field Non public field used to generate a url encoded key value
     * string.
     *
     * @return URL encoded key value string of a non public class field.
     *
     * @throws NoSuchMethodException is thrown when there is no such method in
     * the given class.
     * @throws InvocationTargetException exception thrown by an invoked method
     * or constructor.
     * @throws IllegalAccessException is thrown when an application tries to
     * reflectively create an instance (other than an array), set or get a
     * field, or invoke a method, but the currently executing method does not
     * have access to the definition of the specified class, field, method or
     * constructor.
     */
    public static String nonPublicField(final Class givenClass,
            final Object obj, final Field field) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        String fieldName = null;
        String fieldValue = null;
        try {
            fieldName = URLEncoder.encode(field.getName(), "UTF-8");
            String capitalizedField = fieldName.substring(0, 1).toUpperCase(
                    Locale.ENGLISH) + fieldName.substring(1);
            String getter = field.getType().equals(boolean.class)
                    || field.getType().equals(Boolean.class)
                    ? "is" + capitalizedField : "get" + capitalizedField;
            fieldValue = URLEncoder.encode(String.valueOf(
                    givenClass.getDeclaredMethod(getter).invoke(obj)), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assert fieldName != null && fieldValue != null;
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                fieldName + "=" + fieldValue).replace("+", "%20");
    }

    /**
     * This method sets value of a public field of a class according to the
     * URL encoded key value string.
     *
     * @param parameterName Parameter name corresponding to the class field.
     * Should be on camel case.
     * @param parameterValue Value of parameter corresponding to the class
     * field.
     * @param givenClass Class of the returned object.
     * @param obj Object to set values according to the URL encoded key value
     * string.
     *
     * @throws NoSuchFieldException is thrown when there is no such field in the
     * given class.
     * @throws UnsupportedEncodingException is thrown when the Character
     * Encoding is not supported.
     * @throws IllegalAccessException is thrown when an application tries to
     * reflectively create an instance (other than an array), set or get a
     * field, or invoke a method, but the currently executing method does not
     * have access to the definition of the specified class, field, method or
     * constructor.
     */
    public static void publicField(final String parameterName,
            final String parameterValue, final Class givenClass,
            final Object obj) throws NoSuchFieldException,
            UnsupportedEncodingException, IllegalAccessException {
        Field field = givenClass.getDeclaredField(parameterName);
        Class fieldType = field.getType();
        if (fieldType.equals(boolean.class))
            field.set(obj, Boolean.valueOf(URLDecoder.decode(parameterValue,
                    "UTF-8")));
        else
            field.set(obj, URLDecoder.decode(parameterValue, "UTF-8"));
    }

    /**
     * This method sets value of a non public field of a class according to the
     * URL encoded key value string.
     *
     * Method sets the value to this field using its setter method. So it's
     * prerequisite for given class to have setter method of this field;
     * otherwise exception is thrown.
     *
     * @param parameterName Parameter name corresponding to the class field.
     * Should be on camel case.
     * @param parameterValue Value of parameter corresponding to the class
     * field.
     * @param givenClass Class of the returned object.
     * @param obj Object to set values according to the URL encoded key value
     * string.
     *
     * @throws NoSuchFieldException is thrown when there is no such field in the
     * given class.
     * @throws NoSuchMethodException is thrown when there is no such method in
     * the given class.
     * @throws UnsupportedEncodingException is thrown when the Character
     * Encoding is not supported.
     * @throws InvocationTargetException exception thrown by an invoked method
     * or constructor.
     * @throws IllegalAccessException is thrown when an application tries to
     * reflectively create an instance (other than an array), set or get a
     * field, or invoke a method, but the currently executing method does not
     * have access to the definition of the specified class, field, method or
     * constructor.
     */
    public static void nonPublicField(final String parameterName,
            final String parameterValue, final Class givenClass,
            final Object obj) throws NoSuchFieldException,
            NoSuchMethodException, UnsupportedEncodingException,
            InvocationTargetException, IllegalAccessException {
        String capitalizedField = parameterName.substring(0, 1).toUpperCase(
                Locale.ENGLISH) + parameterName.substring(1);
        String setter = "set" + capitalizedField;
        Class fieldType = givenClass.getDeclaredField(parameterName).getType();
        if (fieldType == boolean.class)
            givenClass.getMethod(setter, fieldType).invoke(obj,
                    Boolean.valueOf(parameterValue));
        else
            givenClass.getMethod(setter, fieldType).invoke(obj, parameterValue);
    }

    /**
     * This method converts a URL encoded string to an object such as Request
     * Token object or Encryption Key object.
     *
     * @param response URL encoded response received from escrow agent.
     * @param givenClass Class of the returned object.
     *
     * @return Object being represented by the given URL encoded string.
     *
     * @throws RuntimeException is thrown when cannot create object from the
     * given URL encoced string.
     */
    public static Object toObject(final String response,
            final Class givenClass) {
        String[] parameterPairs = response.split("&");
        Object obj;
        try {
            obj = givenClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot create object from this"
                    + " response");
        }
        for (String pair:  parameterPairs) {
            try {
                String fieldName = URLDecoder.decode(CaseFormat.UPPER_UNDERSCORE
                        .to(CaseFormat.LOWER_CAMEL, pair.split("=")[0]),
                        "UTF-8");
                String parameterValue = URLDecoder.decode(pair.split("=")[1],
                        "UTF-8");
                if (givenClass.getDeclaredField(fieldName).isAccessible())
                    publicField(fieldName, parameterValue, givenClass, obj);
                else
                    nonPublicField(fieldName, parameterValue, givenClass, obj);
            } catch (IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException | UnsupportedEncodingException
                    | NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot create object from this"
                        + " response");
            }
        }
        return obj;
    }

    /**
     * This method creates a URL encoded string based on the name and value of
     * one parameter.
     *
     * @param parameterName Name of the parameter to encode.
     * @param parameterValue Value of the parameter to encode.
     *
     * @return URL encoded string representing one parameter.
     */
    public static String encodeParameter(final String parameterName,
            final String parameterValue) {
        String name = null;
        String value = null;
        try {
            name = URLEncoder.encode(parameterName, "UTF-8");
            value = URLEncoder.encode(parameterValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assert name != null && value != null;
        return name + "=" + value.replace("+", "%20");
    }
}
