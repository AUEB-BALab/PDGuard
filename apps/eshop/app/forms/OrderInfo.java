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
 * This class defines information associated with the order of a customer.
 *
 * @author Thodoris Mavrikis
 */
public class OrderInfo {
    /** The street address of Customer. This field is required.*/
    @Constraints.Required
    private String streetAddress;

    /** The city address of Customer. This field is required.*/
    @Constraints.Required
    private String city;

    /** The county address of Customer. This field is required.*/
    @Constraints.Required
    private String country;

    /** The card number of the customer. */
    @Constraints.Required
    private String cardNumber;

    /** Default constructor. */
    public OrderInfo() { }

    public OrderInfo(final String streetAddress, final String city,
            final String country, String cardNumber) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.country = country;
        this.cardNumber = cardNumber;
    }

    /**
     * Getter of streetAddress.
     *
     * @return The street address of Customer.
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Getter of city field.
     *
     * @return city of the customer.
     */
    public String getCity() {
        return city;
    }

    /**
     * Getter of country field.
     *
     * @return city of the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Getter of cardNumber field.
     *
     * @return the number of the customer's card.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Setter of streetAddress field.
     *
     * @param streetAddress the street address of the customer.
     */
    public void setStreetAddress(final String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * Setter of city field.
     *
     * @param city the city of the customer.
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * Setter of country field.
     *
     * @param country the country of the customer.
     */
    public void setCountry(final String country) {
        this.country = country;
    }


    /**
     * Setter of cardNumber.
     *
     * @param cardNumber the number of the customer's card.
     */
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
