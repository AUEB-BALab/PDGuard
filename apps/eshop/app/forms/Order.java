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

package forms;

import java.util.List;

/**
 * This class represents an order which has been made by a customer.
 *
 * @author Thodoris Sotiropoulos
 */
public class Order extends Transaction {
    /** ID of order. */
    private int orderId;

    /** Order's information. */
    private OrderInfo orderInfo;

    /**
     * Creates a new order.
     *
     * @param orderId ID of order.
     * @param products List of products which customer ordered.
     * @param orderInfo Order's information.
     */
    public Order(final int orderId, final List<Product> products,
            final OrderInfo orderInfo) {
        this.orderId = orderId;
        this.products = products;
        this.orderInfo = orderInfo;
    }

    /**
     * Getter of orderInfo.
     *
     * @return Order's information.
     */
    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    /**
     * Getter of orderId field.
     *
     * @return ID of order.
     */
    public int getOrderId() {
        return orderId;
    }
}
