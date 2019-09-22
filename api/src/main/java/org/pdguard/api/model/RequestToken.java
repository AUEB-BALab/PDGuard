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

/**
 * This class represents a request token which is used by data controller in
 * order to obtain an encryption key for the encryption/decryption of data
 * subject's block of data.
 *
 * @author Thodoris Sotiropoulos
 */
public class RequestToken {
    /**
     * Value of request token. It is generated randomly and uniquely from
     * escrow agent.
     */
    private String requestToken;

    /** Request token's secret. */
    private String tokenSecret;

    /**
     * True if request token is authorized and ready for exchange with
     * encryption key; false otherwise.
     */
    private boolean authorized = false;

    /** Default constructor. */
    public RequestToken() { }

    /**
     * Creates a new request token with the given token's credentials.
     *
     * @param requestToken Value of request token. It is generated randomly and
     * uniquely from escrow agent.
     * @param tokenSecret Request token's secret.
     */
    public RequestToken(final String requestToken, final String tokenSecret) {
        this.requestToken = requestToken;
        this.tokenSecret = tokenSecret;
    }

    /**
     * Check that class fields are not null. If null, then this methods throws
     * a NullPointerException.
     *
     * @throws NullPointerException is thrown when class fields are null.
     */
    public final void checkNotNull() {
        if (requestToken == null || tokenSecret == null)
            throw new NullPointerException("Request token credentials must not"
                    + " be null");
    }

    /**
     * Getter of requestToken field.
     *
     * @return Value of request token. It is generated randomly and uniquely
     * from escrow agent.
     */
    public final String getRequestToken() {
        return requestToken;
    }

    /**
     * Getter of tokenSecret.
     *
     * @return Request token's secret.
     */
    public final String getTokenSecret() {
        return tokenSecret;
    }

    /**
     * Getter of authorized field.
     *
     * @return True if request token is authorized and ready for exchange with
     * encryption key; false otherwise.
     */
    public final boolean isAuthorized() {
        return authorized;
    }

    /**
     * Setter of authorized field.
     *
     * @param authorized True if request token is authorized; false otherwise.
     */
    public final void setAuthorized(final boolean authorized) {
        this.authorized = authorized;
    }

    /**
     * Setter of requestToken field.
     *
     * @param requestToken Value of request token. It is generated randomly and
     * uniquely from escrow agent.
     */
    public final void setRequestToken(final String requestToken) {
        this.requestToken = requestToken;
    }

    /**
     * Setter of tokenSecret field.
     *
     * @param tokenSecret Request token's secret.
     */
    public final void setTokenSecret(final String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
}
