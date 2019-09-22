/*-
 * Copyright 2014-2016 Thodoris Mavrikis, Thodoris Sotiropoulos
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

import play.data.validation.Constraints;

/**
 * This class defines a product entry to a customer's cart.
 *
 * @author Thodoris Sotiropoulos
 */
public class ProductEntry {
    /** Id of product to be added to cart. This field is required. */
    @Constraints.Required
    private int productId;

    /** Quantity of product to be added to cart. This field is required. */
    @Constraints.Min(value = 1)
    @Constraints.Required
    private int quantity;

    /** Default Constructor. */
    public ProductEntry() { }

    /**
     * Creates a new product entry.
     *
     * @param quantity Quantity of product to be added to cart.
     * @param productId Id of product to be added to cart.
     */
    public ProductEntry(final int quantity, final int productId) {
        this.quantity = quantity;
        this.productId = productId;
    }

    /**
     * Getter of productId field.
     *
     * @return Id of product to be added to cart.
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Setter of productId field.
     *
     * @param productId Id of product to be added to cart.
     */
    public void setProductId(final int productId) {
        this.productId = productId;
    }

    /**
     * Getter of quantity field.
     *
     * @return Quantity of product to be added to cart.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Setter of quantity field.
     *
     * @param quantity Quantity of product to be added to cart.
     */
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }
}
