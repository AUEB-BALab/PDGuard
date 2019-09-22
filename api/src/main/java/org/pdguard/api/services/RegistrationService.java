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
import org.pdguard.api.model.Client;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.security.SecureConnection;
import org.pdguard.api.utils.RequestEncoder;
import org.pdguard.api.utils.ResponseParser;

import java.io.IOException;
import java.util.TreeMap;


/**
 * This class defines a connection to a data subject's escrow agent.
 *
 * It is used for the registration of a client who is associated with one
 * data subject, one data controller and one application to the escrow agent.
 * In this way, after registration, escrow agent can identify client which is
 * issuing requests.
 *
 * @author Thodoris Sotiropoulos
 */
public class RegistrationService extends EscrowAgentService {
    /**
     * Create a new connection to a data subject's specified escrow agent's
     * registration service.
     *
     * @param client Client who is requesting escrow agent.
     * @param escrowAgent Trusted escrow agent by data subject.
     * @param con Connection configuration to establish an encrypted connection
     * with escrow agent.
     */
    public RegistrationService(final Client client,
            final EscrowAgent escrowAgent, final SecureConnection con) {
        super(client, escrowAgent, con);
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
        parameters.put("data_subject_id", super.getClient().getDataSubjectId());
        parameters.put("data_controller_id", super.getClient()
                .getDataControllerId());
        parameters.put("auth_app_id", super.getClient().getAppId());
        return parameters;
    }

    /**
     * This method call the corresponding web service of escrow agent which
     * is responsible for the registration of client.
     *
     * If registration is successful, client's credentials are expected as
     * escrow agent's response. A generated client ID and client secret by
     * escrow agent.
     *
     * @return Client credentials. A client ID, and client secret.
     *
     * @throws IOException is thrown when the communication with the
     * escrow agent fails.
     * @throws org.pdguard.api.exceptions.RegistrationFailedException is thrown
     * when client registration could not be completed.
     * @throws IllegalArgumentException is thrown when parameters which
     * describe a client are inappropriate.
     * @throws EscrowAgentErrorResponseException is thrown when there is an
     * error response by escrow agent.
     */
    public ClientCredentials register() throws IOException,
            EscrowAgentErrorResponseException {
        super.getClient().checkNotNull();
        String url = "https://" + super.getEscrowAgent().getEscrowAgentName()
                + EscrowAgent.getRegistrationService();
        TreeMap<String, String> parameters = getParameters();
        String response = callEscrowAgentService(RequestEncoder
                .normalizeParametersQuery(parameters), url);
        return (ClientCredentials) ResponseParser.toObject(response,
                ClientCredentials.class);
    }
}
