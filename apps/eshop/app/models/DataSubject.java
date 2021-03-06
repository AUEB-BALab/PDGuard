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

import javax.persistence.OneToOne;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.avaje.ebean.Model;

/**
 * This class defines the entity of organization DataSubject who has registered to
 * the e-shop of organization.
 *
 * @author Thodoris Mavrikis, Thodoris Sotiropoulos
 */
@Entity
public class DataSubject extends Model {
    /** Data subject id as specified in the customer's escrow agent. */
    @Id
    private String id;

    /** Trusted escrow agent of data subject. This field is required. */
    @Column(nullable = false)
    private String eagent;

    /**
     * Client Id which corresponds to this customer and is used for PDGuard
     * requests and it's generated by escrow agent. This field is required and
     * unique for each customer.
     */
    @Column(nullable = false, unique = true)
    private String clientId;

    /**
     * Client secret which corresponds to this customer and is used for PDGuard
     * requests and it's generated by escrow agent. This field is required.
     */
    @Column(nullable = false)
    private String secret;

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<String, DataSubject> finder = new Finder<>(
            DataSubject.class);

    /**
     * This field describes the one to one relation between the customer
     * and the DataSubject entities.
     */
    @OneToOne(mappedBy = "dataSubject")
    private Customer customer;

    /** 
     * Initializes a DataSubject with their respective client id
     * and secret credentials.
     *
     * @param dataSubjectId Data subject id as specified on customer's escrow
     * agent.
     * @param eagent Trusted escrow agent of data subject.
     * @param clientId, the client id of the DataSubject in the Escrow Agent.
     * @param secret, which corresponds to this dataSubject, issued
     * by the Escrow agent.
     */
    public DataSubject(final String dataSubjectId, final String eagent,
            final String clientId, final String secret) {
        id = dataSubjectId;
        this.eagent = eagent;
        this.clientId = clientId;
        this.secret = secret;
    }

    /**
     * This method retrieves a DataSubject with a specified client id.
     *
     * This method runs a query in the database and returns
     * the corresponding DataSubject.
     *
     * @param dataSubjectId the id which is associated with this data subject.
     *
     * @return the DataSubject with the given id.
     */
    public static DataSubject getDataSubject(final String dataSubjectId) {
        return DataSubject.finder.where().eq("id", dataSubjectId)
                .findUnique();
    }

    /**
     * This method is used to determine if the DataSubject
     * with the given client id already exists in the database of
     * the organization.
     *
     * @param dataSubjectId, the id to check if there is data subject who is
     * related to it.
     *
     * @return true if the DataSubject exists.
     */
    public static boolean subjectExists(final String dataSubjectId) {
        return DataSubject.finder.where().eq("id", dataSubjectId)
                .findUnique() != null;
    }

    /**
     * This method saves the DataSubject to the
     * database.
     */
    public void addDataSubject() {
        this.save();
    }

    /**
     * Getter of id field.
     *
     * @return Data subject id as specified in the customer's escrow agent.
     */
    public String getId() {
        return id;
    }

    /**
     * Getter of clientId field.
     *
     * @return Client Id which corresponds to this customer and is used for
     * PDGuard requests and it's generated by escrow agent.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Getter of secret field.
     *
     * @return Client Id which corresponds to this customer and is used for
     * PDGuard requests and it's generated by escrow agent.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Setter method of the one to one relation with
     * the customer.
     *
     * @param customer, the customer related to the DataSubject.
     */
    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    /**
     * Getter method of the one to one relation
     * with the customer.
     *
     * @return the customer related to the DataSubject.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Getter of agent field.
     *
     * @return Trusted escrow agent of customer.
     */
    public String getEagent() {
        return eagent;
    }

    /**
     * Getter of finder field.
     *
     * @return This field will be used to programatically make queries which
     * returns object of this class.
     */
    public static Finder<String, DataSubject> getFinder() {
        return finder;
    }
}
