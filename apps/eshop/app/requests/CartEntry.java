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
import forms.ProductEntry;
import models.Cart;
import models.Customer;
import models.Product;
import models.ProductCart;

/**
 * This class is used to represent a request for the addition of a product
 * to a customer's cart.
 *
 * @author Thodoris Mavrikis, Thodoris Sotiropoulos
 */
public class CartEntry implements EshopRequest {
    /** Id of customer to whom cart belongs. */
    private String customer;

    /** Product entry to customer's cart. */
    private ProductEntry productEntry;

    /** Initializes request with all required parameters.
     *
     * @param customer Id of customer to whom cart belongs.
     * @param productEntry Product entry to customer's cart.
     */
    public CartEntry(final String customer,
            final ProductEntry productEntry) {
        this.customer = customer;
        this.productEntry = productEntry;
    }

    /**
     * This method is used to add a product to the cart
     * of the customer.
     *
     * @throws EshopException is thrown when customer and product with the given
     * id cannot be found.
     */
    public void addProduct() throws EshopException {
        Customer customer = Customer.authenticate(this.customer);
        if (customer == null)
            throw new EshopException("There is no customer with the given id");
        Product product = Product.authenticate(productEntry.getProductId());
        if (product == null)
            throw new EshopException("There is no product with the given id");
        Cart cart = Cart.authenticate(this.customer);
        if (cart == null)
            cart = new Cart(customer);
        ProductCart productCart = new ProductCart(product, cart,
                productEntry.getQuantity());
        cart.increaseTotalPrice(product.getPrice());
        productCart.addProductEntry();
    }

    /**
     * This method processes the request for the addition of a product to a
     * customer's cart.
     *
     * @throws EshopException is thrown when the addition of the product cannot
     * be completed successfully.
     */
    @Override
    public void process() throws EshopException {
        addProduct();
    }
}
