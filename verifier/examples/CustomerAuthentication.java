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

import exceptions.EshopException;
import forms.Login;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;
import security.CryptographyController;
import models.DataSubject;

import java.io.IOException;
import java.nio.charset.Charset;

////////////////////////////////////////
import java.io.*;
import java.util.*;
////////////////////////////////////////

/**
 * This class represents a request for customer authentication.
 *
 * User who is registered to the system he has to give their credentials
 * (ID and password) to enter system in order to have potential to
 * use operations offered by application.
 *
 * @author Thodoris Mavrikis, Thodoris Sotiropoulos
 */
public class CustomerAuthentication implements EshopRequest {
    /** Form with user's credentials. */
    private Login loginForm;

    /**
     * Initializes request with user's credentials.
     *
     * @param loginForm Form with user's credentials.
     */
    public CustomerAuthentication(final Login loginForm) {
        this.loginForm = loginForm;
    }

    /**
     * This method checks if there is a registered user with the credentials
     * (ID and password) which are specified in object of class.
     *
     * This method is for the login operation in order to validate given ID
     * and password. In this way user is authenticated.
     *
     * If user is authenticated then a request to escrow agent is issued to
     * have access to their email for informative reasons.
     *
     * @return true if user gives the right credentials, false otherwise.
     */
    public boolean isAuthenticated() {
        models.Customer authCustomer = models.Customer.authenticate(
                loginForm.getDataSubjectId(), loginForm.getPassword());
        if (authCustomer != null) {
            loginForm.setEmail(getCustomerEmail(authCustomer));
            return true;
        } else
            return false;
    }

    /**
     * This method gets the email of an authenticated customer.
     *
     * Customer's email is stored in an encrypted form so a request is issued
     * to customer's escrow agent to have access to their email for informative
     * reasons.
     *
     * @param customer Authenticated customer.
     *
     * @return Customer's email.
     */
    public String getCustomerEmail(final models.Customer customer) {
        DataSubject authDataSubject = customer.getDataSubject();

        ClientCredentials clientCredentials = new ClientCredentials(
                authDataSubject.getClientId(), authDataSubject.getSecret());
        CryptographyController decryptor = new CryptographyController(
                customer.getDataSubject().getEagent(), clientCredentials);
        try {
            return  new String(decryptor.decrypt(
                    customer.getEmail(), DataType.PERSONAL_EMAIL, DataUse.REPORT,
                    InteractionPurpose.INFORMATIVE), Charset.forName("UTF-8"));
        } catch (IOException | EscrowAgentErrorResponseException e) {
            return e.getMessage();
        }
    }

	public void verifierTest(){
		BufferedWriter out = new BufferedWriter(new FileWriter("test.txt"));
		String st = getCustomerEmail() + "foo";
    		out.write(st);  	// Writing out data from PDGuard to a text file 
		
  		out.close();
	}

    /**
     * This method processes the request for customer authentication.
     *
     * If customer is not authenticated, a corresponding exception is thrown
     * because request cannot be completed successfully.
     *
     * @throws EshopException is thrown when customer is not authenticated.
     */
    @Override
    public void process() throws EshopException {
        if (!isAuthenticated())
            throw new EshopException();
    }
}
