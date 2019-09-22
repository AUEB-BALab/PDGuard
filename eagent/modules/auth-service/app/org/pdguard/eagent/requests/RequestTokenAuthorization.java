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

import org.pdguard.api.exceptions.AccessDeniedException;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.RequestToken;
import org.pdguard.api.utils.ResponseParser;
import org.pdguard.eagent.auth.AuthorizationApi;
import org.pdguard.eagent.auth.AuthorizationLogApiImpl;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.ReqToken;
import org.pdguard.eagent.validation.RequestTokenValidator;
import play.i18n.Messages;

import static org.apache.commons.codec.binary.Hex.encodeHex;

/**
 * This class is responsible for granting authorization by authorizing a given
 * request token based on the authorization api.
 *
 * @author Thodoris Sotiropoulos
 */
public class RequestTokenAuthorization implements PDGuardRequest,
        RequestTokenValidator {
    /** Client who's requesting authorization. */
    private Client client;

    /** API responsible for the authorization processing. */
    private AuthorizationApi authApi;

    /** Given request token which is going to be authorized. */
    private String requestToken;

    /**
     * Initializes a new procedure for the authorization grant.
     *
     * @param client Client who's requesting escrow agent.
     * @param authApi API responsible for the authorization processing.
     * @param requestToken Given request token which is going to be authorized.
     */
    public RequestTokenAuthorization(final Client client,
            final AuthorizationApi authApi, final String requestToken) {
        this.client = client;
        this.authApi = authApi;
        this.requestToken = requestToken;
    }

    /**
     * This method processes a request of a client for the authorization
     * of a given request token.
     *
     * Gets authorization and it returns a result accordingly. If authorization
     * is given, it returns a request token which is authorized and it can be
     * exchanged for an encryption key.
     *
     * If authorization is not given, it throws the corresponding exception
     * which is associated with the reason of failure.
     *
     * @throws AccessDeniedException is thrown when escrow agent cannot grant
     * authorization.
     */
    public void processAuthorization() throws AccessDeniedException {
        AuthorizationResult authResult = authApi.processAuthorization();
        authApi.addAuthorizationLog(new AuthorizationLogApiImpl(client));
        if (!authResult.equals(AuthorizationResult.ALLOWED))
            throw new AccessDeniedException(Messages.get("auth.access_denied"));
    }

    /**
     * This method authorizes a valid request token and returns it back to
     * the client.
     *
     * @return Authorized request token.
     *
     * @throws UnverifiedRequestException is thrown when the given request
     * token is invalid.
     */
    public RequestToken authorizeRequestToken()
            throws UnverifiedRequestException {
        ReqToken reqToken = ReqToken.authenticate(this.requestToken,
                client.getId());
        validateRequestToken(reqToken);
        reqToken.authorizeRequestToken();
        RequestToken authToken = new RequestToken(this.requestToken,
                String.valueOf(encodeHex(reqToken.getSecret())));
        authToken.setAuthorized(true);
        return authToken;
    }

    /**
     * This method validates a authorization request by checking if requested
     * party is authorized to use personal data as they define.
     *
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * cannot grant authorization.
     */
    @Override
    public void validate() throws EscrowAgentErrorResponseException {
        processAuthorization();
    }

    /**
     * This method processes authorization request.
     *
     * Authorizes a validated request token and sends it back in URL encoded
     * format.
     *
     * @return Authorized request token in a URL encoded format.
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * cannot validate given request token.
     */
    @Override
    public String process() throws EscrowAgentErrorResponseException {
        return ResponseParser.toURLEncoded(RequestToken.class,
                authorizeRequestToken());
    }

    /**
     * Getter of client field.
     *
     * @return Client who's requesting escrow agent.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Setter of client field.
     *
     * @param client Client who's requesting escrow agent.
     */
    public void setClient(final Client client) {
        this.client = client;
    }

    /**
     * Getter of request token field.
     *
     * @return Request token which is going to be authorized.
     */
    public String getRequestToken() {
        return requestToken;
    }

    /**
     * Setter of requestToken field.
     *
     * @param requestToken Request token which is going to be authorized.
     */
    public void setRequestToken(final String requestToken) {
        this.requestToken = requestToken;
    }
}
