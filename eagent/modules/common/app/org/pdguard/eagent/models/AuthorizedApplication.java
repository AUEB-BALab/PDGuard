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

import play.data.validation.Constraints;
import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.LinkedList;
import java.util.List;

/**
 * This class defines the entity of AuthorizedApplication for database schema of
 * this application using Ebean ORM.
 *
 * An authorized application is the application which a data controller uses
 * to request escrow agent for an encryption key. It communicates with PDGuard
 * API which is the intermediary between data controller and escrow agent.
 *
 * A data controller has to use only an authorized application by escrow agent
 * to request it which runs on an authorized host.
 *
 * There is also an relation with the {@link DataController} class
 * declaring as @ManyToMany. This means that each data controller runs many
 * applications which are authorized by escrow agent, and one authorized
 * application is used by many data controllers.
 *
 * @author Thodoris Sotiropoulos
 */
@Entity
public final class AuthorizedApplication extends Model {
    /** Id of entity. Declared as a primary key. */
    @Id
    private String id;

    /** Name of authorized application. This field is required. */
    @Constraints.Required
    @Column(nullable = false)
    private String authorizedApplicationName;

    /**
     * Mapping with the {@link DataController} entity with as
     * ManyToMany relation. Each authorized application can used many data
     * controllers to interact with escrow agent.
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @Constraints.Required
    private List<DataController> dataControllers  = new LinkedList<>();

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<Integer, AuthorizedApplication> finder = new Finder<>(
            AuthorizedApplication.class);

    /**
     * Default constructor.
     */
    public AuthorizedApplication() { }

    /**
     * Initializes an authorized application with information associated with
     * it.
     *  @param id ID of authorized application.
     * @param dataController A data controller who uses this application.
     * @param authorizedApplicationName Name of application.
     */
    public AuthorizedApplication(final String id,
            final DataController dataController,
            final String authorizedApplicationName) {
        this.id = id;
        this.dataControllers.add(dataController);
        this.authorizedApplicationName = authorizedApplicationName;
    }

    /**
     * Initializes an authorized application with information associated with
     * it.
     *
     * @param dataController A data controller who uses this application.
     */
    public AuthorizedApplication(final DataController dataController) {
        this.dataControllers.add(dataController);
    }

    /**
     * Checks if there any authorized application stored in database of escrow
     * agent which is associated with data controller described by the id given
     * as parameter.
     *
     * @param authAppId Authorized application's id.
     * @param dataControllerId Data controller's id.
     *
     * @return Authorized application.
     */
    public static AuthorizedApplication authenticate(final String authAppId,
            final String dataControllerId) {
        return AuthorizedApplication.finder.where()
                .eq("dataControllers.id", dataControllerId)
                .eq("id", authAppId).findUnique();
    }

    /**
     * Checks if the application which a data controller who requests escrow
     * agent for access to specific data and it runs on a specific host, is in
     * the list of authorized application of escrow agent.
     *
     * @param dataControllerId Id of data controller who is requesting escrow
     * agent.
     *
     * @return True if application is authorized, false otherwise.
     */
    public boolean isAuthorized(final String dataControllerId) {
        return AuthorizedApplication.finder.where()
                .eq("dataControllers.id", dataControllerId)
                .eq("id", id).findUnique() != null;
    }

    /**
     * Getter of id field.
     *
     * @return Id of an authorized application.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter of authorizedApplicationName field.
     *
     * @return Name of application.
     */
    public String getAuthorizedApplicationName() {
        return authorizedApplicationName;
    }

    /**
     * Setter of authorizedApplicationName field.
     *
     * @param authorizedApplicationName Name of application.
     */
    public void setAuthorizedApplicationName(
            final String authorizedApplicationName) {
        this.authorizedApplicationName = authorizedApplicationName;
    }

    /**
     * Getter of dataControllers field.
     *
     * @return A set of data controller who uses this application to communicate
     * with escrow agent.
     */
    public List<DataController> getDataControllers() {
        return dataControllers;
    }

    /**
     * Getter of finder field.
     *
     * @return It will be used to programatically make queries which return
     * object of this class.
     */
    public static Finder<Integer, AuthorizedApplication> getFinder() {
        return finder;
    }
}
