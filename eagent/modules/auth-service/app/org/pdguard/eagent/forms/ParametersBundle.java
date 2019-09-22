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

package org.pdguard.eagent.forms;

import play.data.validation.Constraints;

/**
 * This class represents a bundle of parameters of a request which is part of
 * authorization protocol of PDGuard.
 *
 * These parameters are required in every PDGuard request issued by a data
 * controller's application.
 *
 * @author Thodoris Sotiropoulos
 */
public class ParametersBundle {
    /** ID of client who's requesting escrow agent. This field is required. */
    @Constraints.Required
    private String clientId;

    /** Request signature. This field is required. */
    @Constraints.Required
    private String signature;

    /**
     * Nonce value; a unique randomly generated string. This field is required.
     */
    @Constraints.Required
    private String nonce;

    /** Timestamp of request. This field is required. */
    @Constraints.Required
    private Long timestamp;

    /** Default constructor. */
    public ParametersBundle() { }

    /**
     * Creates a new bundle of parameters.
     *
     * @param clientId ID of client who's requesting escrow agent.
     * @param signature Request signature.
     * @param nonce Nonce value; a unique randomly generated string.
     * @param timestamp Timestamp of request.
     */
    public ParametersBundle(final String clientId, final String signature,
            final String nonce, final Long timestamp) {
        this.clientId = clientId;
        this.signature = signature;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }

    /**
     * Getter of clientId field.
     *
     * @return ID of client who's requesting escrow agent.
     */
    public final String getClientId() {
        return clientId;
    }

    /**
     * Setter of clientId field.
     *
     * @param clientId ID of client who's requesting escrow agent.
     */
    public final void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    /**
     * Getter of signature field.
     *
     * @return Request signature.
     */
    public final String getSignature() {
        return signature;
    }

    /**
     * Setter of signature field.
     *
     * @param signature Request signature.
     */
    public final void setSignature(final String signature) {
        this.signature = signature;
    }

    /**
     * Getter of nonce field.
     *
     * @return Nonce value; a unique randomly generated string.
     */
    public final String getNonce() {
        return nonce;
    }

    /**
     * Setter of nonce field.
     *
     * @param nonce Nonce value; a unique randomly generated string.
     */
    public final void setNonce(final String nonce) {
        this.nonce = nonce;
    }

    /**
     * Getter of timestamp field.
     *
     * @return Timestamp of request.
     */
    public final Long getTimestamp() {
        return timestamp;
    }

    /**
     * Setter of timestamp field.
     *
     * @param timestamp Timestamp of request.
     */
    public final void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }
}
