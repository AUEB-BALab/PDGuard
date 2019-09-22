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

package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import com.avaje.ebean.Model;

/**
 * This class defines the entity of organization placed orders
 * which are being issued by the customer.
 *
 * @author Thodoris Mavrikis
 */
@Entity
public class PlacedOrder extends Model {
    /** The id of the order. This field is required.*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    /** The date that the order was made. This field is required.*/
    @Column(nullable = false)
    private Date date;

    /** THe customer's chosen city for the order. This field is required.*/
    @Column(nullable = false)
    private byte[] city;

    /** THe customer's chosen country for the order. This field is required.*/
    @Column(nullable = false)
    private byte[] country;

    /**
     * The address of the customer encoded into a byte format.
     * This field is required.
     */
    @Column(nullable = false)
    private byte[] streetAddress;

    /**
     * The card number that was used for the transaction,
     * if the transaction was made with a card.
     * This field is not required.
     */
    @Column(nullable = false)
    private byte[] cardNumber;

    /** Creates an one to one relation with the cart which created the order. */
    @OneToOne
    @JoinColumn(name = "cartId")
    private Cart cart;

    /**
     * This field will be used to pragmatically make queries which return
     * object of this class.
     */
    private static Finder<Integer, PlacedOrder> finder = new Finder<>(
            PlacedOrder.class);

    /**
     * Initialize Product with all information associated with their.
     * First constructor without the CardNumber.
     *
     * @param date The date that the order was issued.
     * @param streetAddress The address of the Customer who made the order.
     * @param cardNumber The billing information of the transaction.
     * @param country Country where order is placed.
     * @param city City where order is placed.
     */
    public PlacedOrder(final Date date,
            final byte[] streetAddress, final byte[] cardNumber, 
            final byte[] country, final byte[] city, final Cart cart) {
        this.date = date;
        this.streetAddress = streetAddress;
        this.city = city;
        this.country = country;
        this.cardNumber = cardNumber;
        this.cart = cart;
    }

    /** Adds a new order. */
    public void addOrder() {
        cart.setOrdered(true);
        cart.update();
        this.save();
    }

    /**
     * Gets the history of orders associated with a customer.
     *
     * @param customerId Id of customer.
     *
     * @return List of orders.
     */
    public static List<PlacedOrder> getOrders(final String customerId) {
        return PlacedOrder.finder.where().eq("cart.customer.dataSubject.id",
                customerId).findList();
    }

    /**
     * Getter method for the Id of the Order.
     *
     * @return orderId the key of the order.
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Getter method for the date that the
     * Order was issued.
     *
     * @return date the date of the transaction.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Getter method of the address of
     * the customer who made the order.
     *
     * @return address of the Customer
     * in encrypted byte format.
     */
    public byte[] getStreetAddress() {
        return streetAddress;
    }

    /**
     * Getted method for the city
     * where the customer wishes to 
     * receive the order
     *
     * @return the city in encrypted byte form.
     */
    public byte[] getCity() {
        return city;
    }

    /**
     * Getter method for the country 
     * of the customer.
     *
     * @return the country of the customer.
     */
    public byte[] getCountry() {
         return country;
    }

    /**
     * Getter method of the CardNumber
     * of the customer used for the order.
     *
     * @return CardNumber of the customer
     * into an encrypted byte format.
     */
    public byte[] getCardNumber() {
        return cardNumber;
    }

    /**
     * Setter method that creates one to one the relation
     * of the cart with the new placed order.
     *
     * @param cart , the cart that the order relates to.
     */
    public void setCart(final Cart cart) {
        this.cart = cart;
    }

    /**
     * Getter method for the related cart of the order.
     *
     * @return the cart from which the order was issued.
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Getter of finder field.
     *
     * @return This field will be used to pragmatically make queries which
     * returns object of this class.
     */
    public static Finder<Integer, PlacedOrder> getFinder() {
        return finder;
    }
}
