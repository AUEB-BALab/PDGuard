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

package org.pdguard.api.model;

import org.pdguard.api.utils.RequestEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.TreeMap;

/**
 * This class represents the signature base which is a concatenation of several
 * of the HTTP request parts into a one a string.
 *
 * This is used as an input for the HMAC-SHA1 signature algorithm which is used
 * to this framework.
 *
 * @author Thodoris Sotiropoulos
 */
public final class SignatureBase {
    /** Encoded URI base string. */
    private String encodedUrl;

    /** Encoded string of the normalized parameters query. */
    private String encodedParametersString;

    /**
     * Signature base string which is consisted of the concatenation of the
     * HTTP request method, encoded URI base string and encoded string of
     * normalized parameters query.
     */
    private String signatureBaseString;

    /**
     * Initialize a new signature base string with the required parts of HTTP
     * request given as parameters.
     *
     * @param parameters Map of parameters with the name of parameters as keys.
     * @param url URL of escrow agent's web service.
     * @param requestMethod HTTP request method.
     *
     * @throws UnsupportedEncodingException When defined encoding does not
     * supported.
     */
    public SignatureBase(final TreeMap<String, String> parameters,
            final String url, final String requestMethod)
            throws UnsupportedEncodingException {
        encodedUrl = RequestEncoder.encodeBaseURI(url);
        encodedParametersString = RequestEncoder.normalizeParametersQuery(
                parameters);
        setSignatureBasicString(requestMethod);
    }

    /**
     * This method creates a new signature base string based on the HTTP request
     * method, encoded base URI string and the encoded normalized parameters
     * query string.
     *
     * @param requestMethod HTTP request method.
     */
    public void setSignatureBasicString(final String requestMethod) {
        signatureBaseString = requestMethod.toUpperCase(Locale.ENGLISH)
                + "&" + encodedUrl + "&" + encodedParametersString;
    }

    /**
     * Getter of encodedUrl field.
     *
     * @return Encoded URI base string.
     */
    public String getEncodedUrl() {
        return encodedUrl;
    }

    /**
     * Getter of encodedParametersString.
     *
     * @return Encoded string of the normalized parameters query.
     */
    public String getEncodedParametersString() {
        return encodedParametersString;
    }

    /**
     * Getter of signatureBaseString field.
     *
     * @return Signature base string which is consisted of the concatenation of
     * the HTTP request method, encoded URI base string and encoded string of
     * normalized parameters query.
     */
    public String getSignatureBaseString() {
        return signatureBaseString;
    }
}
