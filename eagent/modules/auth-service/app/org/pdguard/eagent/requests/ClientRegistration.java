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
import org.pdguard.api.exceptions.RegistrationFailedException;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.utils.ResponseParser;
import org.pdguard.eagent.forms.RegistrationBundle;
import org.pdguard.eagent.models.AuthorizedApplication;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.pdguard.eagent.utils.CredentialsGenerator;
import org.pdguard.eagent.utils.KeyManager;
import play.i18n.Messages;

import static org.apache.commons.codec.binary.Hex.encodeHex;

/**
 * This class is responsible for the registration of a client to escrow agent.
 *
 * Client registration requires the data controller id, data subject id and
 * application id in order escrow agent can make the association between them.
 *
 * Escrow agent also generates random credentials for that purpose in order to
 * verify PDGuards requests made by this client.
 *
 * @author Thodoris Sotiropoulos
 */
public class ClientRegistration implements PDGuardRequest {
    /** Bundle of parameters of client registration request. */
    private RegistrationBundle regBundle;

    /** Data subject to whom personal data belongs. */
    private DataSubject dataSubject;

    /** Data controller organization which stores personal data. */
    private DataController dataController;

    /** Data controller's application which processes personal data. */
    private AuthorizedApplication authApp;

    /** Default Constructor. */
    public ClientRegistration() { }

    /**
     * Creates a new client registration request according to the given bundle
     * of parameters.
     *
     * @param regBundle Bundle of parameters od client registration request.
     */
    public ClientRegistration(final RegistrationBundle regBundle) {
        this.regBundle = regBundle;
    }

    /**
     * This method validates client registration request.
     *
     * Method checks if they are data subject, data controller and authorized
     * application with data subject id, data controller id and application id
     * defined by the parameters of registration request respectively.
     *
     * @throws RegistrationFailedException is thrown when client registration
     * cannot be completed.
     */
    public void validateRequest() throws RegistrationFailedException {
        dataSubject = DataSubject.authenticateById(
                regBundle.getDataSubjectId());
        dataController = DataController.authenticate(
                regBundle.getDataControllerId());
        authApp = AuthorizedApplication.authenticate(regBundle.getAuthAppId(),
                regBundle.getDataControllerId());
        if (dataSubject == null)
            throw new RegistrationFailedException(Messages
                    .get("client.data_subject"));
        if (dataController == null)
            throw new RegistrationFailedException(Messages
                    .get("client.data_controller"));
        if (authApp == null)
            throw new RegistrationFailedException(Messages
                    .get("client.auth_app"));
        if (!Client.isUnique(regBundle.getDataSubjectId(),
                regBundle.getDataControllerId(), regBundle.getAuthAppId()))
            throw new RegistrationFailedException(Messages
                    .get("client.reg_failed"));
    }

    /**
     * This method processes client registration request by generating
     * client credentials and registering client to escrow agent.
     *
     * If client is added, it is considered registered to the system and now
     * data controller can issue PDGuard requests via the registered application
     * to obtain access to data of data subject defined by this client.
     *
     * Then, it generates client' credentials and adds it the escrow agent's
     * database.
     *
     * @return Client credentials which escrow agent generated.
     */
    public ClientCredentials registerClient() {
        String clientId = CredentialsGenerator.generateUniqueID();
        byte[] clientSecret = CredentialsGenerator.generateHmacSHA1Secret();
        KeyManager.addEncryptionKey(dataSubject, dataController);
        addClient(clientId, clientSecret);
        char[] hex = encodeHex(clientSecret);
        String secret = String.valueOf(hex);
        return new ClientCredentials(clientId, secret);
    }

    /**
     * This method adds a new client to the escrow agent's database.
     *
     * @param clientId Generated client id.
     * @param clientSecret Generated client secret.
     */
    void addClient(final String clientId, final byte[] clientSecret) {
        new Client(clientId, clientSecret, dataSubject, dataController, authApp)
                .addClient();
    }

    /**
     * This method validates client registration request.
     *
     * Method checks if they are data subject, data controller and authorized
     * application with data subject id, data controller id and application id
     * defined by the parameters of registration request respectively.
     *
     * @throws RegistrationFailedException is thrown when client registration
     * cannot be completed.
     */
    @Override
    public void validate() throws EscrowAgentErrorResponseException {
        validateRequest();
    }

    /**
     * This method processes client registration request by generating
     * client credentials and registering client to escrow agent.
     *
     * If client is added, it is considered registered to the system and now
     * data controller can issue PDGuard requests via the registered application
     * to obtain access to data of data subject defined by this client.
     *
     * Then, it generates client' credentials and sends them back in a URL
     * encoded format.
     *
     * @return Client credentials in a URL encoded format.
     */
    @Override
    public String process() throws EscrowAgentErrorResponseException {
        return ResponseParser.toURLEncoded(ClientCredentials.class,
                registerClient());
    }
}
