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
import forms.OrderInfo;
import models.Cart;
import models.DataSubject;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import security.CryptographyController;

import java.io.UnsupportedEncodingException;

import java.io.IOException;

import java.util.Date;
import java.nio.charset.Charset;
import models.PlacedOrder;

/**
 * This class represent a request for an order by a customer.
 *
 * @author Thodoris Mavrikis, Thodoris Sotiropoulos
 *
 */
public class OrderEntry implements EshopRequest {
    /** Id of customer who is requesting order. */
    private String customer;

    /** Information of order. */
    private OrderInfo orderInfo;

    /**
     * Creates a new request for order.
     *
     * @param customer Id of customer who is requesting order.
     * @param orderInfo Information of order.
     */
    public OrderEntry(final String customer, final OrderInfo orderInfo) {
        this.customer = customer;
        this.orderInfo = orderInfo;
    }

    /**
     * This method adds order.
     *
     * @throws EshopException is thrown when there is no any cart available
     * for order associated with the customer specified.
     * @throws IOException is thrown when interaction with escrow agent cannot
     * be completed successfully.
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * sends back an error response during interaction with e-shop.
     */
    public void addOrder() throws EshopException,
            IOException, EscrowAgentErrorResponseException {
        Cart cart = Cart.authenticate(customer);
        if (cart == null)
            throw new EshopException();
        DataSubject dataSubject = cart.getCustomer().getDataSubject();
        ClientCredentials clientCredentials = new ClientCredentials(
                dataSubject.getClientId(), dataSubject.getSecret());
        CryptographyController encryptor = new CryptographyController(
                dataSubject.getEagent(), clientCredentials);
        encryptOrder(encryptor);
        new PlacedOrder(new Date(),
                orderInfo.getStreetAddress().getBytes("UTF-8"),
                orderInfo.getCardNumber().getBytes("UTF-8"),
                orderInfo.getCountry().getBytes("UTF-8"),
                orderInfo.getCity().getBytes("UTF-8"), cart).addOrder();
    }

    /**
     * This method encrypts the order tha the customer just issued by
     * establishing a connection with the escrow agent and sending the required
     * credentials.
     *
     * @throws IOException is thrown when interaction with escrow agent cannot
     * be completed successfully.
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * sends back an error response during interaction with e-shop.
     */
    void encryptOrder(CryptographyController encryptor)
        throws IOException, EscrowAgentErrorResponseException {
       try {
           orderInfo.setCity(new String(encryptor.encrypt(
                   orderInfo.getCity().getBytes(Charset.forName("UTF-8")),
                   DataType.SENSITIVE,
                   DataProvenance.DATA_SUBJECT_EXPLICIT, false), "UTF-8"));
           orderInfo.setStreetAddress(new String(encryptor.encrypt(
                   orderInfo.getStreetAddress().getBytes(
                           Charset.forName("UTF-8")), DataType.SENSITIVE,
                   DataProvenance.DATA_SUBJECT_EXPLICIT, false), "UTF-8"));
           orderInfo.setCountry(new String(encryptor.encrypt(
                   orderInfo.getCountry().getBytes(Charset.forName("UTF-8")),
                   DataType.SENSITIVE, DataProvenance.DATA_SUBJECT_EXPLICIT,
                   false), "UTF-8"));
           orderInfo.setCardNumber(new String(encryptor.encrypt(
                   orderInfo.getCardNumber().getBytes(Charset.forName("UTF-8")),
                   DataType.SENSITIVE, DataProvenance.DATA_SUBJECT_EXPLICIT,
                   false), "UTF-8"));
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
    }

    /**
     * This method processes the request for an order.
     *
     * @throws EshopException is thrown when addition of order cannot be
     * completed successfully.
     */
    @Override
    public void process() throws EshopException {
        try {
            addOrder();
        } catch (IOException | EscrowAgentErrorResponseException e) {
            throw new EshopException(e.getMessage());
        }
    }
}
