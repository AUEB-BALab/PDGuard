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

import models.Customer;
import play.data.validation.Constraints;

/**
 * @author Thodoris Sotiropoulos
 */
public class Registration {
    /** The first name of the new customer. This field is required. */
    @Constraints.Required
    private String firstName;

    /** The last name of our customer. This field is required. */
    @Constraints.Required
    private String surname;

    /** Customer's password to enter e-shop. This field is required. */
    @Constraints.Required
    @Constraints.MinLength(value = 6)
    private String password;

    /** User's email, used as the username. This field is required. */
    @Constraints.Required
    @Constraints.Email
    private String email;

    /**
     * Password confirmation. This field's value has to be equal to password's
     * value.
     */
    private String repeatedPassword;

    /** Trusted escrow agent's name. This field is required. */
    @Constraints.Required
    private String eagent;

    /** Id of customer on escrow agent's database. This field is required. */
    @Constraints.Required
    private String dataSubjectId;

    /** Default Constructor. */
    public Registration() { }

    public Registration(final String firstName, final String surname,
            final String email, final String password,
            final String repeatedPassword, final String eagent,
            final String dataSubjectId) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.eagent = eagent;
        this.dataSubjectId = dataSubjectId;
    }

    /**
     * This method checks if password is equal to the value user gave on the
     * password confirmation field of registration form.
     *
     * @return true if two passwords are equal; false otherwise.
     */
    public boolean checkPassword() {
        return password.equals(repeatedPassword);
    }

    /**
     * This function tests if there is a registered user with the same ID
     * with current user.
     *
     * @return True if there is a registered user with the same ID with the
     * current user; false otherwise.
     */
    public final boolean isDataSubjectIdAvailable() {
        return Customer.isDataSubjectIdAvailable(dataSubjectId);
    }

    /**
     * Getter of email field.
     *
     * @return Customer's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter of email field.
     *
     * @param email Customer's email.
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Getter of firstName field.
     *
     * @return The first name of the new customer.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter of firstName field.
     *
     * @param firstName The first name of the new customer.
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter of surname field.
     *
     * @return The surname of the new customer.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Setter of surname field.
     *
     * @param surname The surname of the new customer.
     */
    public void setSurname(final String surname) {
        this.surname = surname;
    }

    /**
     * Getter of eagent field.
     *
     * @return Trusted escrow agent's name.
     */
    public String getEagent() {
        return eagent;
    }

    /**
     * Setter of eagent field.
     *
     * @param eagent Trusted escrow agent's name.
     */
    public void setEagent(final String eagent) {
        this.eagent = eagent;
    }

    /**
     * Getter of password field.
     *
     * @return Customer's password to enter e-shop.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter of password field.
     *
     * @param password Customer's password to enter e-shop.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Getter of repeatedPassword field.
     *
     * @return Password's confirmation.
     */
    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    /**
     * Setter of repeatedPassword field.
     *
     * @param repeatedPassword Password's confirmation.
     */
    public void setRepeatedPassword(final String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    /**
     * Getter of dataSubjectId field.
     *
     * @return Id of customer on escrow agent's database.
     */
    public String getDataSubjectId() {
        return dataSubjectId;
    }

    /**
     * Setter of dataSubjectId field.
     *
     * @param dataSubjectId Id of customer on escrow agent's database.
     */
    public void setDataSubjectId(String dataSubjectId) {
        this.dataSubjectId = dataSubjectId;
    }
}
