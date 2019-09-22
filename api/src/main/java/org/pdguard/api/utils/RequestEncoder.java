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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is used for the encoding of the parameters query and the url of
 * a request.
 *
 * @author Thodoris Sotiropoulos
 */
public final class RequestEncoder {
    /** Default HTTP port. */
    private static final String HTTP_PORT = "80";

    /** Default HTTPS port. */
    private static final String HTTPS_PORT = "443";

    /** Hide constructor of utility class. */
    private RequestEncoder() { }
    /**
     * This method encodes the normalized parameters query of a request.
     *
     * @param parameters Map of parameters with the name of parameters as keys.
     * @return Encoded string of the normalized parameters query.
     *
     * @throws UnsupportedEncodingException When defined encoding does not
     * supported.
     */
    public static String normalizeParametersQuery(
            final TreeMap<String, String> parameters)
            throws UnsupportedEncodingException {
        boolean firstEntry = true;
        StringBuilder parametersQuery = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (firstEntry) {
                parametersQuery.append(URLEncoder
                        .encode(entry.getKey(), "UTF-8")).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                firstEntry = false;
            } else {
                parametersQuery.append("&")
                        .append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=").append(URLEncoder.encode(
                        entry.getValue(), "UTF-8"));
            }
        }
        return parametersQuery.toString().replace("+", "%20");
    }

    /**
     * This method encodes the base URI string.
     *
     * @param url URL of escrow agent's web service.
     *
     * @return Encoded URI base string.
     *
     * @throws UnsupportedEncodingException When defined encoding does not
     * supported.
     */
    public static String encodeBaseURI(final String url)
            throws UnsupportedEncodingException {
        if (url.startsWith("http") && url.contains(":" + HTTP_PORT))
            return URLEncoder.encode(url.toLowerCase(Locale.ENGLISH)
                    .replace(":" + HTTP_PORT, ""), "UTF-8");
        else if (url.startsWith("https") && url.contains(":" + HTTPS_PORT))
            return URLEncoder.encode(url.toLowerCase(Locale.ENGLISH)
                    .replace(":" + HTTPS_PORT, ""), "UTF-8");
        else
            return URLEncoder.encode(url.toLowerCase(Locale.ENGLISH), "UTF-8");
    }
}
