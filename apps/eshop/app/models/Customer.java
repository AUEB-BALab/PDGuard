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

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

/**
 * This class defines the entity of organization customer who is registered to
 * the e-shop of organization.
 *
 * @author Thodoris Mavrikis, Thodoris Sotiropoulos
 */
@Entity
public class Customer extends Model {
    /**
     * ID of customer. It is the same with ID associated with their trusted
     * escrow agent. This field is required.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Email of customer in encrypted binary format. This field is required and
     * it's unique for each customer.
     */
    @Constraints.Required
    @Column(nullable = false, unique = true)
    private byte[] email;

    /**
     * First name of customer in encrypted binary format. This field is
     * required.
     */
    @Constraints.Required
    @Column(nullable = false)
    private byte[] firstName;

    @OneToMany
    private List<Cart> carts;

    /**
     * Surname of customer in encrypted binary format. This field is required.
     */
    @Constraints.Required
    @Column(nullable = false)
    public byte[] surname;

    /** Password of customer to enter e-shop. This field is required.*/
    @Constraints.Required
    @Column(nullable = false)
    private String password;

    /**
     * This field describes the one to one relation between the customer
     * and the DataSubject entities.
     */
    @OneToOne
    private DataSubject dataSubject;

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<String, Customer> finder = new Finder<>(
            Customer.class);

    /**
     * Initialize customer with all information associated with their.
     *
     * @param email Email of customer in encrypted binary format.
     * @param firstName First name of customer in encrypted binary format.
     * @param password Password of customer to enter e-shop.
     * @param surname Surname of customer in encrypted binary format.
     * @param dataSubject Data subject entity related with this customer.
     */
    public Customer(final byte[] email, final byte[] firstName,
            final String password, final byte[] surname,
            final DataSubject dataSubject) {
        this.email = email;
        this.firstName = firstName;
        this.password = password;
        this.surname = surname;
        this.dataSubject = dataSubject;
    }

    /** Saves current customer to app's database. */
    public void addCustomer() {
        this.save();
    }

    /**
     * Checks if there any customer stored in database of escrow agent with
     * ID as the one given as parameter.
     *
     * @param id Id to search customer.
     *
     * @return Customer.
     */
    public static Customer authenticate(final String id) {
        return Customer.finder.where().eq("dataSubject.id", id).findUnique();
    }

    /**
     * Checks if there any stored stored in database of escrow agent with
     * ID and password as the ID and password given as parameters.
     *
     * @param id Search data subject with the given id.
     * @param password Customer's password.
     *
     * @return Customer.
     */
    public static Customer authenticate(final String id, final String password) {
        return Customer.finder.where().eq("dataSubject.id", id)
                .eq("password", password).findUnique();
    }

    /**
     * This method checks the availability of an customer's Id associated with
     * the escrow agent.
     *
     * It takes the value of an ID and then search application's database
     * to find to check if there is already a registered user with the
     * same ID.
     *
     * It is obvious that if there is already a user who uses the same
     * ID with that given as parameter the ID is not available for use.
     *
     * @param dataSubjectId A customers's ID.
     * @return True if the given ID is available; false otherwise.
     */
    public static boolean isDataSubjectIdAvailable(
            final String dataSubjectId) {
        return Customer.finder.where().eq("dataSubject.id",
                dataSubjectId).findUnique() == null;
    }

    /**
     * Getter of id field.
     *
     * @return ID of customer. It is the same with ID associated with their
     * trusted escrow agent.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Getter of email field.
     *
     * @return Customer's email in encrypted binary format.
     */
    public byte[] getEmail() {
        return email;
    }

    /**
     * Getter of firstName field.
     *
     * @return Customer's first name in encrypted binary format.
     */
    public byte[] getFirstName() {
        return firstName;
    }

    /**
     * Getter of surname field.
     *
     * @return Customer's surname in encrypted binary format.
     */
    public byte[] getSurname() {
        return surname;
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
     * Getter of dataSubject field.
     *
     * @return Customer's info associated with escrow agent.
     */
    public DataSubject getDataSubject() {
        return dataSubject;
    }

    /**
     * Getter of finder field.
     *
     * @return This field will be used to programatically make queries which
     * returns object of this class.
     */
    public static Finder<String, Customer> getFinder() {
        return finder;
    }
}
