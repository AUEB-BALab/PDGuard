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
import forms.Order;
import forms.OrderInfo;
import forms.Product;
import forms.Transaction;
import models.DataSubject;
import models.PlacedOrder;
import models.ProductCart;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;
import security.CryptographyController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a request for the view of order's history of customer.
 *
 * @author Thodoris Sotiropoulos, Thodoris Mavrikis
 */
public class OrderHistory implements EshopView {
    /** Id of customer. */
    private String customer;

    /** List of orders of customer. */
    private List<Transaction> orders = new LinkedList<>();

    /**
     * Creates a new request for the view of order's history of customer.
     *
     * @param customer Id of customer.
     */
    public OrderHistory(final String customer) {
        this.customer = customer;
    }

    /**
     * This method loads all information associated with the history of order
     * of customer.
     *
     * @param orders Retrieved orders.
     */
    void loadOrders(List<PlacedOrder> orders) {
        for (PlacedOrder placedOrder: orders) {
            List<Product> products = new LinkedList<>();
            for (ProductCart productCart: placedOrder.getCart().getProducts()) {
                models.Product retrievedProduct = productCart.getProduct();
                products.add(new Product(retrievedProduct.getProductKey(),
                        retrievedProduct.getManufacturer(),
                        retrievedProduct.getName(), retrievedProduct.getPrice(),
                        retrievedProduct.getDescription(),
                        productCart.getQuantity()));
            }
            try {
                this.orders.add(new Order(placedOrder.getOrderId(),
                        products, new OrderInfo(new String(
                        placedOrder.getStreetAddress(), "UTF-8"),
                        new String(placedOrder.getCity(), "UTF-8"),
                        new String(placedOrder.getCountry(), "UTF-8"),
                        new String(placedOrder.getCardNumber(), "UTF-8"))));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Get history of orders of customer and decrypt all required information
     * about them.
     *
     * @throws EshopException is thrown when customer has not made any order
     * yet.
     */
    void getHistoryOfOrders() throws EshopException {
        List<PlacedOrder> retrievedOrders = PlacedOrder.getOrders(customer);
        if (retrievedOrders.size() == 0)
            throw new EshopException("There is no any order which has been made yet.");
        DataSubject dataSubject = retrievedOrders.get(0).getCart().getCustomer()
                .getDataSubject();
        ClientCredentials clientCredentials = new ClientCredentials(
                dataSubject.getClientId(), dataSubject.getSecret());
        loadOrders(retrievedOrders);
        CryptographyController decryptor = new CryptographyController(
                dataSubject.getEagent(), clientCredentials);
        decryptOrder(decryptor);
    }

    /**
     * Decrypts infromation associated with order e.g. street address
     *
     * @param decryptor Object responsible for the decryption of data.
     */
    public void decryptOrder(CryptographyController decryptor) {
        for (Transaction order1 : orders) {
            Order order = (Order) order1;
            try {
                order.getOrderInfo().setStreetAddress(new String(decryptor.decrypt(
                        order.getOrderInfo().getStreetAddress().getBytes("UTF-8"),
                        DataType.HOME_STREET_ADDRESS, DataUse.REPORT,
                        InteractionPurpose.INFORMATIVE), "UTF-8"));
            } catch (EscrowAgentErrorResponseException | IOException e) {
                order.getOrderInfo().setStreetAddress(e.getMessage());
            }
            try {
                order.getOrderInfo().setCity(new String(decryptor.decrypt(
                        order.getOrderInfo().getCity().getBytes("UTF-8"),
                        DataType.HOME_CITY, DataUse.REPORT,
                        InteractionPurpose.INFORMATIVE), "UTF-8"));
            } catch (EscrowAgentErrorResponseException | IOException e) {
                order.getOrderInfo().setCity(e.getMessage());
            }
        }
    }

    /**
     * Get list of orders of customer.
     *
     * @return List of orders.
     */
    @Override
    public List<Transaction> view() {
        return orders;
    }

    /**
     * Loads all customer's orders.
     *
     * @throws EshopException is thrown when customer has not made any orders
     * yet.
     */
    @Override
    public void process() throws EshopException {
        getHistoryOfOrders();
    }
}
