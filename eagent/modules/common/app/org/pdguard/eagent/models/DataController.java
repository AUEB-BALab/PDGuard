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

import org.pdguard.api.utils.DataType;
import play.data.validation.Constraints;
import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.List;

/**
 * This class defines the entity of DataController for database schema of this
 * application using Ebean ORM.
 *
 * A data controller is the one who is requesting escrow agent to have access
 * to some specific data of a subject which are stored encrypted to database of
 * organization.
 *
 * For example, a data controller can be an employee of the organization who
 * wants to use data of a subject with a specific way. Alternatively, data
 * controller can be considered organization that stores data of a subject.
 *
 * For that purpose, there is an relation with the {@link AuthorizationRule}
 * class declaring as @OneToMany. This means that each data controller can use
 * data defined by many authorization rules and each authorization rule is
 * associated with one data subject. In this way, each data controller can use
 * data of a specific subject with ways which are described in each
 * authorization rule.
 *
 * Apart from this, there is also an relation with the {@link Key}
 * class declaring as @OneToMany. This means that each data controller uses
 * multiple keys for the encryption and decryption of a subject's data and one
 * key is associated with only one data controller.
 *
 * There is also an relation with the {@link AuthorizedApplication} class
 * declaring as @ManyToMany. This means that each data controller runs many
 * applications which are authorized by escrow agent, and one authorized
 * application is used by many data controllers.
 *
 * @author Thodoris Sotiropoulos
 */
@Entity
public final class DataController extends Model {
    /** Id of entity. Declared as a primary key. */
    @Id
    private String id;

    /** Name of data controller. This field is required. */
    @Constraints.Required
    @Column(nullable = false)
    private String dataControllerName;

    /**
     * Mapping with the {@link Key} entity with as OneToMany
     * relation. Each data controller uses multiple keys for encrypting and
     * decrypting data.
     */
    @OneToMany(mappedBy = "dataController")
    private List<Key> keys;

    /**
     * Mapping with the {@link AuthorizationRule} entity with as OneToMany
     * relation. Each data controller can use data as defined by many
     * authorization rules.
     */
    @OneToMany(mappedBy = "dataController", cascade = CascadeType.ALL)
    private List<AuthorizationRule> authorizationRules;

    /**
     * Mapping with the {@link AuthorizedApplication} entity with as
     * ManyToMany relation. Each data controller can use many application to
     * interact with escrow agent.
     */
    @ManyToMany(mappedBy = "dataControllers")
    private List<AuthorizedApplication> authorizedApplications;

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<Integer, DataController> finder = new Finder<>(
            DataController.class);

    /**
     * Default Constructor.
     */
    public DataController() { }

    /**
     * Initializes a data controller with their information associated with
     * their.
     *
     * @param id ID of data controller.
     * @param dataControllerName Name of data controller.
     */
    public DataController(final String id, final String dataControllerName) {
        this.id = id;
        this.dataControllerName = dataControllerName;
    }

    /**
     * Checks if there any data controller stored in database of escrow agent
     * with id as the id given as parameter.
     *
     * @param dataControllerId Search data controller with the given id.
     *
     * @return Data controller.
     */
    public static DataController authenticate(final String dataControllerId) {
        if (dataControllerId == null)
            return null;
        return DataController.finder.where().eq("id", dataControllerId)
                .findUnique();
    }

    /**
     * Checks if there any data controller stored in database of escrow agent
     * which is associated with authorization rules with data subject and their
     * data specified by the parameters.
     *
     * @param dataControllerName Name of data controller.
     * @param dataSubjectEmail Data subject's email.
     * @param dataType Type of data which belongs to data subject.
     *
     * @return Data controller.
     */
    public static DataController authenticate(final String dataControllerName,
            final String dataSubjectEmail, final DataType dataType) {
        if (dataSubjectEmail == null || dataType == null)
            return null;
        return DataController.finder.where().eq("authorizationRules.dataSubject"
                + ".dataSubjectEmail", dataSubjectEmail)
                .eq("authorizationRules.dataType", dataType)
                .eq("authorizationRules.dataController.dataControllerName",
                        dataControllerName).findUnique();
    }

    /**
     * This method checks if data controller stores the specified data which
     * belongs to person (data subject) described by the email given
     * as parameter.
     *
     * @param dataSubjectEmail Data subject's email.
     * @param dataType Type of data which belongs to data subject.
     *
     * @return True if data controller stores data of the defined data subject;
     * false otherwise.
     */
    public boolean storesData(final String dataSubjectEmail,
            final DataType dataType) {
        return DataController.finder.where().eq("authorizationRules.dataSubject"
                + ".dataSubjectEmail", dataSubjectEmail)
                .eq("authorizationRules.dataType", dataType)
                .eq("authorizationRules.dataController.dataControllerName",
                        dataControllerName).findUnique() != null;
    }

    /**
     * Getter of id field.
     *
     * @return Id of data controller.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter of id field.
     *
     * @param id Id of data controller.
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Getter of dataControllerName field.
     *
     * @return Name of data controller.
     */
    public String getDataControllerName() {
        return dataControllerName;
    }

    /**
     * Setter of dataControllerName field.
     *
     * @param dataControllerName Name of data controller.
     */
    public void setDataControllerName(final String dataControllerName) {
        this.dataControllerName = dataControllerName;
    }

    /**
     * Getter of encryptionKeys field.
     *
     * @return List of encryption keys that data controller uses.
     */
    public List<Key> getKeys() {
        return keys;
    }

    /**
     * Getter of authorizationRules field.
     *
     * @return List of authorization rules that defines how data controller
     * must uses certain data.
     */
    public List<AuthorizationRule> getAuthorizationRules() {
        return authorizationRules;
    }

    /**
     * Getter of authorizationApplications field.
     *
     * @return List of authorized applications that data controller uses.
     */
    public List<AuthorizedApplication> getAuthorizedApplications() {
        return authorizedApplications;
    }

    /**
     * Getter of finder field.
     *
     * @return It will be used to programatically make queries which return
     * object of this class.
     */
    public static Finder<Integer, DataController> getFinder() {
        return finder;
    }
}
