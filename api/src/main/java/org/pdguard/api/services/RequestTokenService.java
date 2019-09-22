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
 * It is used for the obtainment of an unauthorized request token which is going
 * to be exchanged for an encryption key in order data controller can have
 * access to data subject's block of data.
 *
 * @author Thodoris Sotiropoulos
 */
public class RequestTokenService extends EscrowAgentService {
    /** Client's credentials used by escrow agent to verify request. */
    private ClientCredentials clientCredentials;

    /**
     * Create a new connection to a data subject's specified escrow agent's
     * service to obtain an unauthorized request token.
     *
     * @param clientCredentials Credentials of client who is requesting escrow
     * agent.
     * @param escrowAgent Trusted escrow agent by data subject.
     * @param con Connection configuration to establish an encrypted connection
     * with escrow agent.
     */
    public RequestTokenService(final ClientCredentials clientCredentials,
            final EscrowAgent escrowAgent, final SecureConnection con) {
        super(escrowAgent, con);
        this.clientCredentials = clientCredentials;
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
        return parameters;
    }

    /**
     * This method call the corresponding web service of escrow agent which
     * is responsible for the generation of an unauthorized request token.
     *
     * It creates a signature base string from parts of HTTP request and then
     * it signs it with the client's secret.
     *
     * @return An authorized request token.
     *
     * @throws IOException is thrown when the communication with the
     * escrow agent fails.
     * @throws org.pdguard.api.exceptions.UnverifiedRequestException is thrown
     * when escrow agent cannot verify request from client.
     * @throws EscrowAgentErrorResponseException is thrown when there is an
     * error response by escrow agent.
     */
    public RequestToken getRequestToken() throws IOException,
            EscrowAgentErrorResponseException {
        clientCredentials.checkNotNull();
        String url = "https://" + super.getEscrowAgent().getEscrowAgentName()
                + EscrowAgent.getRequestTokenService();
        TreeMap<String, String> parameters = getParameters();
        SignatureBase signatureBase = new SignatureBase(parameters,
                url, "POST");
        String parametersQuery = signRequest(signatureBase,
                clientCredentials.getClientSecret() + "&");
        String response = callEscrowAgentService(parametersQuery, url);
        return (RequestToken) ResponseParser.toObject(response,
                RequestToken.class);
    }
}
