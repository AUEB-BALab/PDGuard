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

package org.pdguard.eagent.requests;

import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.RequestToken;
import org.pdguard.api.utils.ResponseParser;
import org.pdguard.eagent.forms.ParametersBundle;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.ReqToken;
import org.pdguard.eagent.utils.CredentialsGenerator;
import org.pdguard.eagent.utils.EscrowAgentInfo;
import org.pdguard.eagent.validation.RequestValidator;

import static org.apache.commons.codec.binary.Hex.encodeHex;

/**
 * This is class is responsible for the verification of a PDGuard request for
 * the generation of an unauthorized request token.
 *
 * @author Thodoris Sotiropoulos
 */
public class RequestTokenGeneration implements PDGuardRequest {
    /**
     * Bundle of parameters associated with the request token generation
     * request.
     */
    private ParametersBundle bundle;

    /** Client who's requesting request token generation. */
    private Client client;

    /**
     * Initializes a PDGuard request for an unauthorized request token
     * generation with all required parameters.
     *
     * @param bundle Bundle of parameters associated with the request token
     * generation request.
     */
    public RequestTokenGeneration(final ParametersBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Creates a new request validator based on the given bundle of request
     * parameters.
     *
     * @return Request validator.
     */
    RequestValidator initValidator() {
        return new RequestValidator(bundle);
    }

    /**
     * This method generates a new request token and its secret and then adds
     * it to the escrow agent database.
     *
     * @return Unauthorized request token.
     */
    public RequestToken generateRequestToken(){
        String requestTokenId = CredentialsGenerator.generateUniqueID();
        byte[] requestTokenSecret = CredentialsGenerator
                .generateHmacSHA1Secret();
        addRequestToken(requestTokenId, requestTokenSecret);
        return new RequestToken(requestTokenId,
                String.valueOf(encodeHex(requestTokenSecret)));
    }

    /**
     * Adds generated request token to the database of escrow agent.
     *
     * @param requestToken Request token's value.
     * @param tokenSecret Request token's secret.
     */
    void addRequestToken(final String requestToken, final byte[] tokenSecret) {
        new ReqToken(requestToken, tokenSecret, client).addRequestToken();
    }

    /**
     * This method verifies a request for the request token generation.
     *
     * Verification of request is associated with the signature verification
     * by recalculating signature using the corresponding secret of client
     * using HMAC-SHA1 algorithm. Also, it verifies timestamp and nonce.
     *
     * @throws UnverifiedRequestException is thrown when request cannot be
     * verified.
     */
    void validateRequest() throws EscrowAgentErrorResponseException {
        RequestValidator validator = initValidator();
        client = validator.validateClient();
        String secret = String.valueOf(encodeHex(client.getSecret()));
        String signatureKey = secret + "&";
        validator.validateRequest(EscrowAgentInfo.getUrl()
                        + EscrowAgent.getRequestTokenService(), signatureKey);
    }

    /**
     * This method validates request token generation request.
     *
     * Validation of request is associated with the signature verification
     * by recalculating signature using the corresponding secret of client
     * using HMAC-SHA1 algorithm. Also, it verifies timestamp and nonce.
     *
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * cannot validate request.
     */
    @Override
    public void validate() throws EscrowAgentErrorResponseException {
        validateRequest();
    }

    /**
     * This method processes a request token generation request.
     *
     * After a successful request validation escrow agent generates a new
     * request token and sends it back in a URL encoded format.
     *
     * @return Generated request token in a URL encoded format.
     *
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * cannot process request.
     */
    @Override
    public String process() throws EscrowAgentErrorResponseException {
        return ResponseParser.toURLEncoded(RequestToken.class,
                generateRequestToken());
    }
}
