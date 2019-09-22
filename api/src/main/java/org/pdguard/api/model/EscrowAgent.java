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
 * This class represents a trusted escrow agent by data subject. API will
 * call this escrow agent's services.
 *
 * @author Thodoris Sotiropoulos
 */
public final class EscrowAgent {
    /** The DNS name of the escrow agent's web service endpoint. */
    private String escrowAgentName;

    /** URI of the escrow agent's registration web service endpoint. */
    private static final String REGISTRATION_SERVICE = "/register";

    /** URI of the escrow agent's request token web service endpoint. */
    private static final String REQUEST_TOKEN_SERVICE = "/token";

    /** URI of the escrow agent's authorization web service endpoint. */
    private static final String AUTHORIZATION_SERVICE = "/authorize";

    /** URI of the escrow agent's encryption key web service endpoint. */
    private static final String ENCRYPTION_KEY_SERVICE = "/key";

    /**
     * Initializes escrow agent with its DNS name.
     *
     * @param escrowAgentName The DNS name of the escrow agent's web service
     * endpoint.
     */
    public EscrowAgent(final String escrowAgentName) {
        this.escrowAgentName = escrowAgentName;
    }

    /**
     * Getter of escrowAgentName field.
     *
     * @return The DNS name of the escrow agent's web service endpoint.
     */
    public String getEscrowAgentName() {
        return escrowAgentName;
    }

    /**
     * Getter of registrationService field.
     *
     * @return URI of the escrow agent's registration web service endpoint.
     */
    public static String getRegistrationService() {
        return REGISTRATION_SERVICE;
    }

    /**
     * Getter of REQUEST_TOKEN_SERVICE field.
     *
     * @return URI of the escrow agent's request token web service endpoint.
     */
    public static String getRequestTokenService() {
        return REQUEST_TOKEN_SERVICE;
    }

    /**
     * Getter of AUTHORIZATION_SERVICE field.
     *
     * @return URI of the escrow agent's authorization web service endpoint.
     */
    public static String getAuthorizationService() {
        return AUTHORIZATION_SERVICE;
    }

    /**
     * Getter of ENCRYPTION_KEY_SERVICE field.
     *
     * @return URI of the escrow agent's encryption key web service endpoint.
     */
    public static String getEncryptionKeyService() {
        return ENCRYPTION_KEY_SERVICE;
    }
}
