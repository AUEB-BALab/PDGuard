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
 * This class represents the client's credentials which Escrow agent use to
 * authenticate client. Credentials are generated by escrow agent as a response
 * of client registration service.
 *
 * @author Thodoris Sotiropoulos
 */
public final class ClientCredentials {
    /**
     * A String uniquely identifying the client with the specified
     * escrow agent.
     */
    private String clientId;

    /** The client secret provided by escrow agent. */
    private String clientSecret;

    /** Default Constructor. */
    public ClientCredentials() { }

    /**
     * Initializes client's credentials.
     *
     * @param clientId A String uniquely identifying the data subject with the
     * specified escrow agent.
     * @param clientSecret The client secret provided by escrow agent.
     */
    public ClientCredentials(final String clientId, final String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Check that class fields are not null. If null, then this methods throws
     * a NullPointerException.
     *
     * @throws NullPointerException is thrown when class fields are null.
     */
    public void checkNotNull() {
        if (clientId == null || clientSecret == null)
            throw new NullPointerException("Client credentials must not"
                    + " be null");
    }

    /**
     * Getter of clientSecret field.
     *
     * @return Client secret provided by escrow agent.
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Getter of clientId field.
     *
     * @return A String uniquely identifying the data subject with the specified
     * escrow agent.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Setter of clientId field.
     *
     * @param clientId A String uniquely identifying the client with the
     * specified escrow agent.
     */
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    /**
     * Setter of clientSecret field.
     *
     * @param clientSecret Client secret provided by escrow agent.
     */
    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
