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
import org.pdguard.api.model.AuthorizationBundle;
import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.utils.PDGuardRequestType;
import org.pdguard.eagent.forms.ParametersBundleReq;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.ReqToken;
import org.pdguard.eagent.utils.EscrowAgentInfo;
import org.pdguard.eagent.validation.RequestTokenValidator;
import org.pdguard.eagent.validation.RequestValidator;

import java.util.TreeMap;

import static org.apache.commons.codec.binary.Hex.encodeHex;

/**
 * This is class is responsible for the verification of a PDGuard request for
 * the validation of a request token.
 *
 * @author Thodoris Sotiropoulos
 */
public class RequestTokenValidation implements PDGuardRequest,
        RequestTokenValidator {
    /**
     * Bundle of parameters associated with the request token validation
     * request.
     */
    private ParametersBundleReq params;

    /**
     * Bundle of parameters associated with the authorization processing of
     * escrow agent.
     */
    private AuthorizationBundle authBundle;

    /** Type of request. Request for decrypting or encrypting data. */
    private PDGuardRequestType requestType;

    /**
     * Initializes a PDGuard request for the validation of an unauthorized
     * request token with all required parameters.
     *
     * @param params Bundle of parameters associated with the request token
     * validation request.
     * @param authBundle Bundle of parameters associated with the authorization
     * processing of escrow agent.
     * @param requestType Type of request. Request for decrypting or encrypting
     * data.
     */
    public RequestTokenValidation(final ParametersBundleReq params,
            final AuthorizationBundle authBundle,
            final PDGuardRequestType requestType) {
        this.params = params;
        this.authBundle = authBundle;
        this.requestType = requestType;
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
     * This method adds additional bundle of parameters required for the
     * authorization processing of escrow agent.
     *
     * These parameters as the parameters associated with the authentication
     * processing of a request are stored in a Java TreeMap object.
     *
     * @param parameters Map which stores parameters associated with the
     * authentication processing.
     */
    TreeMap<String, String> addAuthorizationParameters(
            final TreeMap<String, String> parameters) {
        parameters.put("data_type", authBundle.getDataType().toString());
        parameters.put("request_type", requestType.toString());
        if (requestType.equals(PDGuardRequestType.ENCRYPTION)) {
            parameters.put("data_provenance", ((EncryptionBundle)
                    authBundle).getDataProvenance().toString());
            parameters.put("update", String.valueOf(((EncryptionBundle)
                    authBundle).isUpdate()));
        } else {
            parameters.put("data_use", ((DecryptionBundle) authBundle)
                    .getDataUse().toString());
            parameters.put("interaction_purpose", ((DecryptionBundle)
                    authBundle).getInteractionPurpose().toString());
        }
        return parameters;
    }

    /**
     * This method validates an authorization request.
     *
     * This method is required for the verification of request for the
     * authorization processing where the given request token is authorized
     * according to the correctness of authorization parameters.
     *
     * Verification of request is associated with the signature verification
     * by recalculating signature using the corresponding secret of client and
     * request token using HMAC-SHA1 algorithm. Also, it verifies timestamp and
     * nonce.
     *
     * @throws UnverifiedRequestException is thrown when request cannot be
     * verified.
     */
    void validateAuthorizationRequest()
            throws UnverifiedRequestException {
        RequestValidator validator = initValidator();
        Client client = validator.validateClient();
        ReqToken reqToken = ReqToken.authenticate(params.getRequestToken(),
                client.getId());
        validateRequestToken(reqToken);
        String clientSecret = String.valueOf(encodeHex(client.getSecret()));
        String tokenSecret = String.valueOf(encodeHex(reqToken
                .getSecret()));
        validator.setParameters(addAuthorizationParameters(validator
                .getParameters()));
        String signatureKey = clientSecret + "&" + tokenSecret;
        validator.validateRequest(EscrowAgentInfo.getUrl()
                        + EscrowAgent.getAuthorizationService(), signatureKey);
    }

    /**
     * This method validates request token validation request.
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
        validateAuthorizationRequest();
    }

    /**
     * This method processes request token validation request.
     *
     * It sends back validated request token which is ready for
     * authorization.
     *
     * @return Validated unauthorized request token.
     *
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * cannot process request.
     */
    @Override
    public String process() throws EscrowAgentErrorResponseException {
        return params.getRequestToken();
    }

    /**
     * Getter of params field.
     *
     * @return Bundle of parameters associated with the request token validation
     * request.
     */
    public ParametersBundleReq getParams() {
        return params;
    }

    /**
     * Getter of authBundle field.
     *
     * @return Bundle of parameters associated with the authorization processing
     * of escrow agent.
     */
    public AuthorizationBundle getAuthBundle() {
        return authBundle;
    }
}
