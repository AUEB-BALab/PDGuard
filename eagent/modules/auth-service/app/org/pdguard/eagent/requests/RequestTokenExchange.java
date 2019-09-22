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
import org.pdguard.api.exceptions.UnretrievableKeyException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.EncryptionKey;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.ResponseParser;
import org.pdguard.eagent.forms.ParametersBundleReq;
import org.pdguard.eagent.utils.KeyManager;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.ReqToken;
import org.pdguard.eagent.utils.EscrowAgentInfo;
import org.pdguard.eagent.validation.RequestTokenValidator;
import org.pdguard.eagent.validation.RequestValidator;
import play.i18n.Messages;

import static org.apache.commons.codec.binary.Hex.encodeHex;

/**
 * This is class is responsible for the verification of a PDGuard request for
 * the validation and the exchange of a request token with an encryption key.
 *
 * @author Thodoris Sotiropoulos
 */
public class RequestTokenExchange implements PDGuardRequest,
        RequestTokenValidator {
    /**
     * Bundle of parameters associated with the request token generation
     * request.
     */
    private ParametersBundleReq params;

    /**
     * Client who's requesting exchange of a request token with an encryption
     * key.
     */
    private Client client;

    /** Request token which is going to be exchanged with an encryption key. */
    private ReqToken reqToken;

    /**
     * Type of data which requested encryption key is going to encrypt/decrypt.
     */
    private DataType dataType;

    /**
     * Initializes a PDGuard request for the exchange of an authorized request
     * token with an encryption key.
     *
     * @param params Bundle of parameters associated with the request token
     * generation request.
     */
    public RequestTokenExchange(final ParametersBundleReq params,
            final DataType dataType) {
        this.params = params;
        this.dataType = dataType;
    }

    /**
     * Creates a new request validator based on the given bundle of request
     * parameters.
     *
     * @return Request validator.
     */
    RequestValidator initValidator() {
        return new RequestValidator(params);
    }

    /**
     * This method validates a request for the exchange of a request token
     * with an encryption key.
     *
     * Before escrow agent sends back encryption key, it verifies the request
     * and validates the given request token.
     *
     * Verification of request is associated with the signature verification
     * by recalculating signature using the corresponding secret of client and
     * request token using HMAC-SHA1 algorithm. Also, it verifies timestamp and
     * nonce.
     *
     * @throws UnverifiedRequestException
     */
    void validateRequest() throws UnverifiedRequestException {
        RequestValidator validator = initValidator();
        client = validator.validateClient();
        reqToken = ReqToken.authenticate(params.getRequestToken(),
                client.getId());
        validateRequestToken(reqToken);
        String clientSecret = String.valueOf(encodeHex(client.getSecret()));
        String tokenSecret = String.valueOf(encodeHex(reqToken
                .getSecret()));
        String signatureKey = clientSecret + "&" + tokenSecret;
        validator.validateRequest(EscrowAgentInfo.getUrl()
                + EscrowAgent.getEncryptionKeyService(), signatureKey);
    }

    /**
     * This method exchanges an authorized request token with an encryption
     * key.
     *
     * @return Encryption key.
     *
     * @throws UnretrievableKeyException is thrown when encryption cannot
     * be retrieved or cannot be used.
     */
    public EncryptionKey exchangeEncryptionKey()
            throws UnretrievableKeyException {
        EncryptionKey key = KeyManager.requestEncryptionKey(
                client.getDataSubject().getId(),
                client.getDataController().getId(), dataType);
        reqToken.revokeRequestToken(true);
        return key;
    }

    /**
     * This method validates a request token.
     *
     * This validation is required so that it can be proved that request token
     * can be exchanged for an encryption key. Request token needs to be
     * authorized, valid and it must exist.
     *
     * If request token cannot be validated, then it's revoked.
     *
     * @param reqToken Request token to be validated.
     * @throws UnverifiedRequestException is thrown when request token is
     * invalid.
     */
    @Override
    public void validateRequestToken(final ReqToken reqToken)
            throws UnverifiedRequestException {
        String errorMessage = Messages.get("auth.invalid_req_token");
        if (reqToken == null)
            throw new UnverifiedRequestException(errorMessage);
        if (!reqToken.isValid() || !reqToken.isAuthorized()) {
            // Revoke request token by deleting it.
            reqToken.revokeRequestToken(true);
            throw new UnverifiedRequestException(errorMessage);
        }
    }

    /**
     * This method validates request token exchange request.
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
     * This method processes a request token exchange request.
     *
     * After a successful request validation escrow agent requests the
     * corresponding encryption key using Master Key Hierarchy and revokes
     * the given request token so that it cannot be used in the future.
     *
     * @return Encryption key in a URL encoded format.
     *
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * cannot process request.
     */
    @Override
    public String process() throws EscrowAgentErrorResponseException {
        return ResponseParser.toURLEncoded(org.pdguard.api
                .model.EncryptionKey.class, exchangeEncryptionKey());
    }

    /**
     * Getter of params field.
     *
     * @return Bundle of parameters associated with the request token generation
     * request.
     */
    public ParametersBundleReq getParams() {
        return params;
    }

    /**
     * Getter of client field.
     *
     * @return Client who's requesting exchange of a request token with an
     * encryption key.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Setter of client field.
     *
     * @param client Client who's requesting exchange of a request token with an
     * encryption key.
     */
    public void setClient(final Client client) {
        this.client = client;
    }

    /**
     * Setter of reqToken field.
     *
     * @param reqToken Request token which is going to be exchanged with an
     * encryption key.
     */
    public void setReqToken(final ReqToken reqToken) {
        this.reqToken = reqToken;
    }
}
