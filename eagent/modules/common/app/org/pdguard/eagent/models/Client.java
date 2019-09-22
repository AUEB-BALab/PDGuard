/*-
 * Copyright 2014-2015 Thodoris Sotiropoulos
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

package org.pdguard.eagent.models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;
import javax.persistence.Table;

/**
 * This class defines the entity of Client for database schema of
 * this application using Ebean ORM.
 *
 * A client is an association between a data subject, a data controller, an
 * authorized application. Each client has their own credentials. In this way,
 * escrow agent can identify and verify PDGuard requests.
 *
 * For example, if escrow agents knows the credentials of a client, it can
 * verify that a specific application running on a specific data controller is
 * requesting access to data of a specific data subject.
 *
 * For that purpose, there is an relation with the {@link DataSubject}
 * class declaring as @ManyToOne. This means that each client is
 * associated with one data subject and one data subject can be associated with
 * multiple clients.
 *
 * Apart from this, there is also an relation with the {@link DataController}
 * class declaring as @ManyToOne. This means that each client is
 * associated with one data controller, and one data controller is associated
 * with multiple clients.
 *
 * There is also an relation with the {@link AuthorizedApplication}
 * class declaring as @ManyToOne. This means that each client
 * is associated with one authorized application, and one authorized application
 * is associated with many clients.
 *
 * Moreover, there must be a unique association with a data controller, data
 * subject and application.
 *
 * @author Thodoris Sotiropoulos
 */
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"data_subject_id", "data_controller_id", "auth_app_id"}))
@Entity
public class Client extends Model {
    /**
     * A String uniquely identifying the client with the specified
     * escrow agent.
     */
    @Id
    private String id;

    /** Client's secret. This field is required. */
    @Constraints.Required
    @Column(nullable = false)
    private byte[] secret;

    /**
     * Mapping with the {@link DataSubject} entity with as ManyToOne
     * relation. Each client is associated with one data subject. This field is
     * required.
     */
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private DataSubject dataSubject;

    /**
     * Mapping with the {@link DataController} entity with as ManyToOne
     * relation. Each client is associated with one data controller. This field
     * is required.
     */
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private DataController dataController;

    /**
     * Mapping with the {@link AuthorizedApplication} entity with as
     * ManyToOne relation. Each client is associated with one authorized
     * application. This field is required.
     */
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private AuthorizedApplication authApp;

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<String, Client> finder = new Finder<>(Client.class);

    /**
     * Creates a new client with all required field.
     *
     * @param id A String uniquely identifying the client with the
     * specified escrow agent.
     * @param secret Client's secret.
     * @param dataSubject Data subject who owns data.
     * @param dataController Data controller who sends request to escrow agent.
     * @param authApp Authorized application which communicates with escrow
     * agent.
     */
    public Client(final String id, final byte[] secret,
            final DataSubject dataSubject, final DataController dataController,
            final AuthorizedApplication authApp) {
        this.id = id;
        this.secret = secret;
        this.dataSubject = dataSubject;
        this.dataController = dataController;
        this.authApp = authApp;
    }

    /** This method registers a new client to the escrow agent's database. */
    public void addClient() {
        this.save();
    }

    /**
     * This method returns an existing client with the specified id given as
     * parameter.
     *
     * @param clientId ID of client.
     *
     * @return Client.
     */
    public static Client authenticate(final String clientId) {
        return Client.finder.where().eq("id", clientId).findUnique();
    }

    /**
     * This method checks that the association between a data subject, data
     * controller and application does not exist, therefore, it's unique.
     *
     * @param dataSubjectId ID of data subject.
     * @param dataControllerId ID of data controller.
     * @param authAppId ID of application.
     *
     * @return True if client association is unique; otherwise false.
     */
    public static boolean isUnique(final String dataSubjectId,
            final String dataControllerId, final String authAppId) {
        return Client.finder.where().eq("dataSubject.id", dataSubjectId)
                .eq("dataController.id", dataControllerId)
                .eq("authApp.id", authAppId).findList().size() == 0;
    }

    /**
     * Getter of secret field.
     *
     * @return Client's secret.
     */
    public byte[] getSecret() {
        return secret;
    }

    /**
     * Getter of id field.
     *
     * @return A String uniquely identifying the client with the specified
     * escrow agent.
     */
    public String getId() {
        return id;
    }

    /**
     * Getter of dataSubject field.
     *
     * @return Data subject who owns data.
     */
    public DataSubject getDataSubject() {
        return dataSubject;
    }

    /**
     * Getter of dataController field.
     *
     * @return Data controller who sends request to escrow agent.
     */
    public DataController getDataController() {
        return dataController;
    }

    /**
     * Getter of authApp field.
     *
     * @return Authorized application which communicates with escrow
     * agent.
     */
    public AuthorizedApplication getAuthApp() {
        return authApp;
    }

    /**
     * Getter of finder field.
     *
     * @return It will be used to programatically make queries which return
     * object of this class.
     */
    public static Finder<String, Client> getFinder() {
        return finder;
    }
}
