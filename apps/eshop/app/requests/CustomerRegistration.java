/*-
 * Copyright 2014-2015 Thodoris Mavrikis, Thodoris Sotiropoulos
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

package requests;

import com.fasterxml.jackson.databind.JsonNode;
import exceptions.EshopException;
import forms.Registration;
import security.CryptographyController;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.model.Client;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.security.SecureConnection;
import org.pdguard.api.services.RegistrationService;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataProvenance;
import utils.OrganizationInfo;
import models.DataSubject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * This class represents request related to a new user registration.
 *
 * User has to fill a registration form in order to register to e-shop
 * application. User has to fill registration form with their name (First name
 * and surname), their email, their trusted escrow agent's name, their escrow
 * agent ID and a password (which they have to confirm it by retyping) to enter
 * system when registration is completed.
 *
 * @author Thodoris Mavrikis, Thodoris Sotiropoulos
 */
public class CustomerRegistration implements EshopRequest {
    /** Registration form. */
    private Registration registration;

    /**
     * Initialize all required fields for the customer registration.
     *
     * @param registration Registration form.
     */
    public CustomerRegistration(final Registration registration) {
        this.registration = registration;
    }

    /**
     * Add customer to e-shop.
     *
     * If user is added, it is considered registered to the system and now they
     * can enter system with the credentials given during his registration.
     *
     * Before user registration their data have to be encrypted (Their first
     * name, their surname, their personal email). For this reason a request
     * to escrow agent is sent to get back these data encrypted.
     *
     * @throws IOException when communication with escrow agent cannot
     * be established.
     * @throws EscrowAgentErrorResponseException is thrown when the escrow
     * agent sends back an error message during the PDGuard flow.
     */
    public void addCustomer() throws IOException,
            EscrowAgentErrorResponseException {
        DataSubject dataSubject = DataSubject.getDataSubject(
                registration.getDataSubjectId());
        ClientCredentials clientCredentials;
        if (dataSubject == null) {
            play.Logger.warn(registration.getDataSubjectId());
            clientCredentials = createPDGuardClient();
            dataSubject = new DataSubject(registration.getDataSubjectId(),
                    registration.getEagent(), clientCredentials.getClientId(),
                    clientCredentials.getClientSecret());
        } else
            clientCredentials = new ClientCredentials(dataSubject.getClientId(),
                    dataSubject.getSecret());
        CryptographyController encryptor = new CryptographyController(
                registration.getEagent(), clientCredentials);
        encyptData(encryptor);
        new models.Customer(registration.getEmail().getBytes(), registration
                .getFirstName().getBytes(), registration.getPassword(),
                registration.getSurname().getBytes(), dataSubject).addCustomer();
    }

    /**
     * Encrypt personal data of customer's registration form.
     *
     * Current e-shop application policies support the encryption of first name,
     * surname and email of a new customer.
     *
     * @param encryptor Object responsible for the encryption of data.
     *
     * @throws IOException is thrown when communication with escrow agent cannot
     * be completed.
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * sends back an error response during the interaction with this
     * application.
     */
    void encyptData(final CryptographyController encryptor) throws IOException,
            EscrowAgentErrorResponseException {
        try {
            registration.setSurname(new String(encryptor.encrypt(
                    registration.getSurname().getBytes(Charset.forName("UTF-8")),
                    DataType.SURNAME, DataProvenance.DATA_SUBJECT_EXPLICIT,
                    false), "UTF-8"));
            registration.setFirstName(new String(encryptor.encrypt(
                    registration.getFirstName().getBytes(Charset.forName("UTF-8")),
                    DataType.GIVEN_NAME, DataProvenance.DATA_SUBJECT_EXPLICIT,
                    false), "UTF-8"));
            registration.setEmail(new String(encryptor.encrypt(
                    registration.getEmail().getBytes(Charset.forName("UTF-8")),
                    DataType.PERSONAL_EMAIL,
                    DataProvenance.DATA_SUBJECT_EXPLICIT, false), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method requests escrow agent to register new client which
     * corresponds to this application, data controller who owns this
     * application and customer who is registering this application.
     *
     * If client registration is successful, escrow agent sends back
     * client credentials (client id, client secret) required so that this
     * application can issue PDGuard requests.
     *
     * @return Client credentials to issue PDGuard requests.
     *
     * @throws IOException is thrown when the communication with escrow agent
     * fails.
     * @throws EscrowAgentErrorResponseException is thrown when the client
     * registration cannot be completed and escrow agent sends back an error
     * message.
     */
    public ClientCredentials createPDGuardClient()
            throws IOException, EscrowAgentErrorResponseException {
        // Load organization information in a JSON format.
        JsonNode json = OrganizationInfo.loadOrganizationInfo();
        assert json != null;
        String dataControllerId = json.get("dataControllerId").asText();
        String appId = json.get("apps").get("e-shop").asText();
        EscrowAgent escrowAgent = new EscrowAgent(registration.getEagent());
        Client newClient = new Client(String.valueOf(
                registration.getDataSubjectId()), dataControllerId, appId);
        SecureConnection secureConnection = new SecureConnection();
        RegistrationService registration = new RegistrationService(
                newClient, escrowAgent, secureConnection);
        ClientCredentials clientCredentials = registration.register();
        play.Logger.warn(clientCredentials.getClientId());
        new DataSubject(this.registration.getDataSubjectId(),
                this.registration.getEagent(), clientCredentials.getClientId(),
                clientCredentials.getClientSecret()).addDataSubject();
        return clientCredentials;
    }

    /**
     * This method processes the request for customer registration
     *
     * If customer cannot be registered successfully, a corresponding exception
     * is thrown because request cannot be completed.
     *
     * @throws EshopException is thrown when customer cannot be registered.
     */
    @Override
    public void process() throws EshopException {
        try {
            addCustomer();
        } catch (IOException | EscrowAgentErrorResponseException e) {
            throw new EshopException(e.getMessage());
        }
    }

    /**
     * Getter of registration field.
     *
     * @return Registration form.
     */
    public Registration getRegistration() {
        return registration;
    }
}
