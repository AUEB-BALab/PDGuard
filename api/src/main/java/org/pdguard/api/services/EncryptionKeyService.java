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
import org.pdguard.api.exceptions.InvalidRequestTokenException;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.EncryptionKey;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.RequestToken;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.SecureConnection;
import org.pdguard.api.utils.ResponseParser;

import java.io.IOException;
import java.util.TreeMap;

/**
 * This class defines a connection to a data subject's escrow agent.
 *
 * It is used for the obtainment of an encryption key in order to either
 * encrypt or decrypt data subject's data.
 *
 * @author Thodoris Sotiropoulos
 */
public class EncryptionKeyService extends EscrowAgentService {
    /** Client's credentials used by escrow agent to verify request. */
    private ClientCredentials clientCredentials;

    /**
     * An authorized request token which is going to be exchanged with an
     * encryption key.
     */
    private RequestToken requestToken;

    /**
     * Create a new connection to a data subject's specified escrow agent's
     * service to obtain an encryption key.
     *
     * @param clientCredentials Credentials of client who is requesting escrow
     * agent.
     * @param escrowAgent Trusted escrow agent by data subject.
     * @param con Connection configuration to establish an encrypted connection
     * with escrow agent.
     * @param requestToken Authorized request token which is going to be
     * exchanged with an encryption key.
     */
    public EncryptionKeyService(final ClientCredentials clientCredentials,
            final EscrowAgent escrowAgent, final SecureConnection con,
            final RequestToken requestToken) {
        super(escrowAgent, con);
        this.clientCredentials = clientCredentials;
        this.requestToken = requestToken;
    }

    /**
     * This method stores the base parameters of a request to a map with keys
     * the name of parameters.
     *
     * @return A map of request parameters with keys the name of parameters.
     */
    @Override
    public TreeMap<String, String> getParameters() {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put("client_id", clientCredentials.getClientId());
        parameters.put("timestamp", String.valueOf(getTimeStamp()));
        parameters.put("nonce", getNonce());
        parameters.put("request_token", requestToken.getRequestToken());
        return parameters;
    }

    /**
     * This method call the corresponding web service of escrow agent which
     * is responsible for the exchange of an authorized request token with
     * an encryption key.
     *
     * It creates a signature base string from parts of HTTP request and then
     * it signs it with both the client's secret and request token's secret.
     *
     * @return An encryption key to encrypt or decrypt data.
     *
     * @throws IOException is thrown when the communication with the
     * escrow agent fails.
     * @throws InvalidRequestTokenException is thrown when request token is not
     * authorized yet, therefore it cannot be exchanged with an encryption key.
     * @throws org.pdguard.api.exceptions.UnretrievableKeyException is thrown
     * when the requested encryption key cannot be used because it cannot be
     * retrieved.
     * @throws EscrowAgentErrorResponseException is thrown when there is an
     * error response by escrow agent.
     */
    public EncryptionKey requestEncryptionKey() throws IOException,
            EscrowAgentErrorResponseException {
        if (!requestToken.isAuthorized())
            throw new InvalidRequestTokenException("Your request token cannot"
                    + " be exchanged for an encryption key");
        clientCredentials.checkNotNull();
        requestToken.checkNotNull();
        String url = "https://" + super.getEscrowAgent().getEscrowAgentName()
                + EscrowAgent.getEncryptionKeyService();
        TreeMap<String, String> parameters = getParameters();
        SignatureBase signatureBase = new SignatureBase(parameters,
                url, "POST");
        String parametersQuery = signRequest(signatureBase,
                clientCredentials.getClientSecret()
                        + "&" + requestToken.getTokenSecret());
        String response = callEscrowAgentService(parametersQuery, url);
        return (EncryptionKey) ResponseParser.toObject(response,
                EncryptionKey.class);
    }
}
