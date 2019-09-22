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

package requests;

import exceptions.EshopException;
import forms.CartContent;
import forms.Product;
import forms.Transaction;
import models.Cart;
import models.ProductCart;

import java.util.LinkedList;
import java.util.List;

/**
 * This class defines a request for the view of a customer's cart.
 *
 * @author Thodoris Sotiropoulos
 */
public class CartView implements EshopView {
    /** List of products which are placed on cart. */
    private List<Product> products = new LinkedList<>();

    /** Id of customer to whom cart belongs. */
    private String customer;

    /** The total value of cart. */
    private double totalPrice;

    /**
     * Creates a new request for the view of a customer's cart.
     *
     * @param customer The id of customer to whom cart belongs.
     */
    public CartView(final String customer) {
        this.customer = customer;
    }

    /**
     * This method loads customer's cart with all products which he has placed.
     *
     * @throws EshopException is thrown when cart is empty or it does not exist.
     */
    void loadCart() throws EshopException {
        Cart cart = Cart.authenticate(customer);
        if (cart == null)
            throw new EshopException();
        totalPrice = cart.getTotalPrice();
        List<models.ProductCart> products = cart.getProducts();
        if (products.size() == 0)
            throw new EshopException();
        for (ProductCart productCart : products) {
            models.Product product = productCart.getProduct();
            this.products.add(new Product(product.getProductKey(),
                    product.getManufacturer(), product.getName(),
                    product.getPrice(), product.getDescription(),
                    productCart.getQuantity()));
        }
    }

    /**
     * Get list of products which are placed to the customer's cart.
     *
     * @return List of products which are placed on cart.
     */
    @Override
    public List<Transaction> view() {
        List<Transaction> cart = new LinkedList<>();
        cart.add(new CartContent(products, totalPrice));
        return cart;
    }

    /**
     * This method loads customer's cart with all products which he has placed.
     *
     * @throws EshopException is thrown when cart is empty or it does not exist.
     */
    @Override
    public void process() throws EshopException {
        loadCart();
    }
}
