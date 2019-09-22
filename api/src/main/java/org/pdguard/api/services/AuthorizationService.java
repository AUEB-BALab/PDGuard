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

package org.pdguard.api.services;

import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.AuthorizationBundle;
import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.RequestToken;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.SecureConnection;
import org.pdguard.api.utils.PDGuardRequestType;
import org.pdguard.api.utils.ResponseParser;

import java.io.IOException;
import java.util.TreeMap;

/**
 * This class defines a connection to a data subject's escrow agent.
 *
 * It is used for the obtainment of an authorized request token which is going
 * to be exchanged for an encryption key in order data controller can have
 * access to data subject's block of data.
 *
 * Actually, this is the WS where escrow agents grants authorization to data
 * controller to either encrypt or decrypt data of data subject based on the
 * defined authorization rules of data subject or the default authorization
 * rules according to the escrow agent's policy.
 *
 * @author Thodoris Sotiropoulos
 */
public class AuthorizationService extends EscrowAgentService {
    /** Client's credentials used by escrow agent to verify request. */
    private ClientCredentials clientCredentials;

    /** Request token which needs to be authorized. */
    private RequestToken requestToken;

    /** Bundle of parameters required by escrow agent to grant authorization. */
    private AuthorizationBundle authBundle;

    /** Type of PDGuard request. Either a encryption or decryption request. */
    private PDGuardRequestType requestType;

    /**
     * Create a new connection to a data subject's specified escrow agent's
     * service to obtain an authorized request token.
     *
     * @param clientCredentials Credentials of client who is requesting escrow
     * agent.
     * @param escrowAgent Trusted escrow agent by data subject.
     * @param con Connection configuration to establish an encrypted connection
     * with escrow agent.
     * @param authBundle  Bundle of parameters required by escrow agent to grant
     * authorization.
     * @param rtoken Request token which needs to be authorized.
     * @param requestType Type of PDGuard request. Either a encryption or
     * decryption request.
     */
    public AuthorizationService(final ClientCredentials clientCredentials,
            final EscrowAgent escrowAgent, final SecureConnection con,
            final AuthorizationBundle authBundle, final RequestToken rtoken,
            final PDGuardRequestType requestType) {
        super(escrowAgent, con);
        this.authBundle = authBundle;
        requestToken = rtoken;
        this.clientCredentials = clientCredentials;
        this.requestType = requestType;
    }

    /**
     * This method stores the base parameters of a request to a map with keys
     * the name of parameters.
     *
     * @return A map of request parameters with keys the name of parameters.
     */
    @Override
    public final TreeMap<String, String> getParameters() {
        TreeMap<String, String> parameters = new TreeMap<>();
        if (requestType.equals(PDGuardRequestType.ENCRYPTION)) {
            if (authBundle instanceof EncryptionBundle) {
                parameters.put("data_provenance", ((EncryptionBundle)
                        authBundle).getDataProvenance().toString());
                parameters.put("update", String.valueOf(((EncryptionBundle)
                        authBundle).isUpdate()));
            } else
                throw new RuntimeException();
        } else {
            if (authBundle instanceof DecryptionBundle) {
                parameters.put("data_use", ((DecryptionBundle) authBundle)
                        .getDataUse().toString());
                parameters.put("interaction_purpose", ((DecryptionBundle)
                        authBundle).getInteractionPurpose().toString());
            } else
                throw new RuntimeException();
        }
        parameters.put("client_id", clientCredentials.getClientId());
        parameters.put("timestamp", String.valueOf(getTimeStamp()));
        parameters.put("nonce", getNonce());
        parameters.put("request_token", requestToken.getRequestToken());
        parameters.put("data_type", authBundle.getDataType().toString());
        parameters.put("request_type", requestType.toString());
        return parameters;
    }

    /**
     * This method call the corresponding web service of escrow agent which
     * is responsible for granting authorization to data controller.
     *
     * It creates a signature base string from parts of HTTP request and then
     * it signs it with both the client's secret and request token's secret.
     *
     * @return An authorized request token.
     *
     * @throws IOException is thrown when the communication with the
     * escrow agent fails.
     * @throws org.pdguard.api.exceptions.UnverifiedRequestException is thrown
     * when escrow agent cannot verify request from client.
     * @throws org.pdguard.api.exceptions.AccessDeniedException when the data
     * subject denies access to the corresponding data.
     * @throws EscrowAgentErrorResponseException is thrown when there is an
     * error response by escrow agent.
     */
    public RequestToken authorizeRequestToken() throws IOException,
            EscrowAgentErrorResponseException {
        clientCredentials.checkNotNull();
        requestToken.checkNotNull();
        String url = "https://" + super.getEscrowAgent().getEscrowAgentName()
                + EscrowAgent.getAuthorizationService();
        TreeMap<String, String> parameters = getParameters();
        SignatureBase signatureBase = new SignatureBase(parameters,
                url, "POST");
        String parametersQuery = signRequest(signatureBase,
                clientCredentials.getClientSecret()
                        + "&" + requestToken.getTokenSecret());
        String response = callEscrowAgentService(parametersQuery, url);
        return (RequestToken) ResponseParser.toObject(response,
                RequestToken.class);
    }
}
