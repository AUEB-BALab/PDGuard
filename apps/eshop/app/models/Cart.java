/*-
 * Copyright 2014-2015 Thodoris Mavrikis
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
package models;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import java.util.LinkedList;
import java.util.List;
import com.avaje.ebean.Model;

/**
 * This class defines the cart that the e-shop of the organization
 * makes available to the user to store their desired purchases.
 *
 * @author Thodoris Mavrikis
 */
@Entity
public class Cart extends Model {
    /** The id of the Cart. This field is required. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;

    @Column(nullable = false)
    private boolean ordered;

    /** The total price of the Cart. This field is required. */
    @Column(nullable = false)
    private double totalPrice;

    /** Creates many to many relation with products. */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<ProductCart> products = new LinkedList<>();

    /** Creates one to one relation with a placed order. */
    @OneToOne(mappedBy = "cart")
    private PlacedOrder order;

    /** Creates an one to many relation with the customer who has the cart */
    @ManyToOne
    private Customer customer;

    /**
     * This field will be used to pragmatically make queries which return
     * object of this class.
     */
    private static Finder<Integer, Cart> finder = new Finder<>(Cart.class);

    /** Initializes a Cart. */
    public Cart(final Customer customer) {
        totalPrice = 0;
        ordered = false;
        this.customer = customer;
    }

    /**
     * Checks if there any cart stored in database of escrow agent associated
     * with customer id as the one given as parameter and it is not ordered.
     *
     * @param customer Id of customer.
     *
     * @return Cart related to the customer's id.
     */
    public static Cart authenticate(final String customer) {
        List<Cart> carts = Cart.finder.where().eq("customer.dataSubject.id",
                customer).findList();
        for (Cart cart: carts)
            if (!cart.isOrdered())
                return cart;
        return null;
    }

    /**
     * Private method used when adding a new product in
     * the cart to increase the total price accordingly.
     *
     * @param price , the price of the new selected product.
     */
    public void increaseTotalPrice(final double price) {
        totalPrice = totalPrice + price;
    }

    public Customer getCustomer() {
        return customer;
    }

    /**
     * Setter method for the Cart relation to it's respective customer.
     *
     * @param customer the owner customer of the cart.
     */
    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    /**
     * Getter of all the products related to this cart.
     *
     * @return the set of products that are into the cart.
     */
    public List<ProductCart> getProducts() {
        return products;
    }

    /**
     * Getter of the CartId of the Cart.
     *
     * @return the unique key of the Cart entity.
     */
    public Integer getCartId() {
        return cartId;
    }

    /**
     * Getter of finder field.
     *
     * @return This field will be used to pragmatically make queries which
     * returns object of this class.
     */
    public static Finder<Integer, Cart> getFinder() {
        return finder;
    }

    /**
     * Getter method for the new placed order from the cart.
     *
     * @return the issued order.
     */
    public PlacedOrder getOrder() {
        return order;
    }

    /**
     * Setter method which places a new order
     * for the products of the cart.
     *
     * @param order , the newly placed order.
     */
    public void setOrder(final PlacedOrder order) {
        this.order = order;
    }

    /**
     * Getter method for the total price of the cart.
     *
     * @return the total cost of the products in the cart.
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(final boolean ordered) {
        this.ordered = ordered;
    }
}
