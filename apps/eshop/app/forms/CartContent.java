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
 * This class represents a cart of products created by a customer.
 *
 * @author Thodoris Sotiropoulos
 */
public class CartContent extends Transaction {
    /** Total value of cart. */
    private double totalPrice;

    /**
     * Creates a new cart.
     *
     * @param products List of products which are included in the cart.
     * @param totalPrice Total value of cart.
     */
    public CartContent(final List<Product> products, final double totalPrice) {
        this.products = products;
        this.totalPrice = totalPrice;
    }

    /**
     * Getter of totalPrice field.
     *
     * @return Total value of cart.
     */
    public double getTotalPrice() {
        return totalPrice;
    }
}
