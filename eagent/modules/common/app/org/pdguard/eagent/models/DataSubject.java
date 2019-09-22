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
import javax.persistence.OneToMany;
import javax.persistence.Id;

import java.util.List;
import java.util.Set;

/**
 * This class defines the entity of DataSubject for database schema of this
 * application using Ebean ORM.
 *
 * A data subject is the one to whom data belongs. A data subject can be a
 * customer of the entity which holds their data. Moreover, data subjects can
 * also have an account on a server of an escrow agent, so in this way, they
 * can have a review of their data and watch who had access to their data and
 * in which way.
 *
 * For example, they can define which actions are allowable for some specific
 * data such as their home address or their Facebook Id.
 *
 * For that purpose, there is an relation with the {@link AuthorizationRule}
 * class declaring as @OneToMany. This means that each data subject can define
 * many authorization rules and each authorization rule is defined by one data
 * subject.
 *
 * Apart from this, there is also an relation with the {@link Key}
 * class declaring as @OneToMany. This means that each data subject owns
 * many keys for decryption and encryption of his data, and one key belongs to
 * one data subject.
 *
 * @author Thodoris Sotiropoulos
 */
@Entity
public final class DataSubject extends Model {
    /** Id of entity. Declared as a primary key. */
    @Id
    private String id;

    /** Data Subject's full name. This field is required. */
    @Constraints.Required
    @Column(nullable = false)
    private String dataSubjectName;

    /**
     * Data subject's email. Required for the data subject's authentication
     * during his enter to escrow agent server.
     */
    @Column(unique = true)
    private String dataSubjectEmail;

    /**
     * Data subject's password. Required for the data subject's authentication
     * during his enter to escrow agent server.
     */
    @Column
    private String dataSubjectPassword;

    /**
     * Mapping with the {@link Key} entity with as OneToMany
     * relation. Each data subject owns many keys. One for each data controller.
     */
    @OneToMany(mappedBy = "dataSubject", cascade = CascadeType.ALL)
    private Set<Key> keys;

    /**
     * Mapping with the {@link AuthorizationRule} entity with as OneToMany
     * relation. Each data subject can define many authorization rules.
     */
    @OneToMany(mappedBy = "dataSubject", cascade = CascadeType.ALL)
    private List<AuthorizationRule> authorizationRules;

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<String, DataSubject> finder = new Finder<>(
            DataSubject.class);

    /**
     * Default Constructor.
     */
    public DataSubject() { }

    /**
     * Initializes a data subject with their information associated with their.
     * @param id ID of data subject.
     * @param dataSubjectName Data Subject's full name.
     * @param dataSubjectEmail Data Subject's email.
     * @param dataSubjectPassword Data Subject's password to enter escrow agent.
     */
    public DataSubject(final String id, final String dataSubjectName,
            final String dataSubjectEmail, final String dataSubjectPassword) {
        this.id = id;
        this.dataSubjectName = dataSubjectName;
        this.dataSubjectEmail = dataSubjectEmail;
        this.dataSubjectPassword = dataSubjectPassword;
    }

    /**
     * Checks if there any data subject stored in database of escrow agent with
     * id as the id given as parameter.
     *
     * @param dataSubjectId Search data subject with the given id.
     *
     * @return Data subject.
     */
    public static DataSubject authenticateById(final String dataSubjectId) {
        if (dataSubjectId == null)
            return null;
        return DataSubject.finder.where().eq("id", dataSubjectId).findUnique();
    }

    /**
     * Checks if there any data subject stored in database of escrow agent with
     * email as the email given as parameter.
     *
     * @param dataSubjectEmail Search data subject with the given email.
     *
     * @return Data subject.
     */
    public static DataSubject authenticateByEmail(
            final String dataSubjectEmail) {
        if (dataSubjectEmail == null)
            return null;
        return DataSubject.finder.where().eq("dataSubjectEmail",
                dataSubjectEmail).findUnique();
    }

    /**
     * This method checks if a user gave right credentials during login
     * operation.
     *
     * Checks database if a user with credentials specified in object given as
     * parameter exists. This method is for the login operation and it tests
     * the password and email of a user.
     * If there is no record in database with email and password which are
     * the same with the corresponding fields of user objects then false is
     * returned, true otherwise.
     *
     * @param dataSubjectEmail Data subject's email.
     * @param dataSubjectPassword Data subject's password.
     *
     * @return True if user gave right credentials; false otherwise.
     */
    public static boolean isAuthenticated(final String dataSubjectEmail,
            final String dataSubjectPassword) {
        return DataSubject.finder.where().eq("dataSubjectEmail",
                dataSubjectEmail).eq("dataSubjectPassword", dataSubjectPassword)
                .findUnique() != null;
    }

    /**
     * This method checks the availability of an email.
     *
     * It takes the value of an email and then search escrow agent's database
     * to find to check if there is already a registered data subject with the
     * same email.
     *
     * It is obvious that if there is already a data subject who uses the same
     * email with that given as parameter the email is not available for use.
     *
     * @param dataSubjectEmail A data subject's email.
     * @return True if the given email is available; false otherwise.
     */
    public static boolean isEmailAvailable(final String dataSubjectEmail) {
        return DataSubject.finder.where().eq("dataSubjectEmail",
                dataSubjectEmail).findUnique() != null;
    }

    /**
     * This method checks the availability of data subject's identifier.
     *
     * It takes the value of an identifier and then search escrow agent's
     * database to find to check if there is already a registered data subject
     * with the same identifier.
     *
     * It is obvious that if there is already a data subject who uses the same
     * identifier with that given as parameter the identifier is not available
     * for use.
     *
     * @param id A data subject's identifier.
     * @return True if the given email is available; false otherwise.
     */
    public static boolean isIdAvailable(final String id) {
        return DataSubject.finder.where().eq("id", id).findUnique() != null;
    }

    /** Saves current data subject to escrow agent database. */
    public void addDataSubject() {
        this.save();
    }

    /**
     * Getter of id field.
     *
     * @return Id of data subject.
     */
    public String getId() {
        return id;
    }

    /**
     * Getter of dataSubjectName field.
     *
     * @return Data Subject's full name.
     */
    public String getDataSubjectName() {
        return this.dataSubjectName;
    }

    /**
     * Setter of dataSubjectName field.
     *
     * @param dataSubjectName Data Subject's full name.
     */
    public void setDataSubjectName(final String dataSubjectName) {
        this.dataSubjectName = dataSubjectName;
    }

    /**
     * Getter of dataSubjectEmail field.
     *
     * @return Data Subject's email.
     */
    public String getDataSubjectEmail() {
        return this.dataSubjectEmail;
    }

    /**
     * Setter of dataSubjectEmail field.
     *
     * @param dataSubjectEmail Data Subject's full email.
     */
    public void setDataSubjectEmail(final String dataSubjectEmail) {
        this.dataSubjectEmail = dataSubjectEmail;
    }

    /**
     * Getter of dataSubjectPassword field.
     *
     * @return Data Subject's password to enter system.
     */
    public String getDataSubjectPassword() {
        return this.dataSubjectPassword;
    }

    /**
     * Setter of dataSubjectPassword field.
     *
     * @param dataSubjectPassword Data Subject's password to enter system.
     */
    public void setDataSubjectPassword(final String dataSubjectPassword) {
        this.dataSubjectPassword = dataSubjectPassword;
    }

    /**
     * Getter of authorizationRules field.
     *
     * @return List of authorization rules that data subject has defined.
     */
    public List<AuthorizationRule> getAuthorizationRules() {
        return authorizationRules;
    }

    /**
     * Getter of encryptionKeys field.
     *
     * @return Encryption key which is associated with the data subject.
     */
    public Set<Key> getKeys() {
        return keys;
    }

    /**
     * Getter of finder field.
     *
     * @return It will be used to programatically make queries which return
     * object of this class.
     */
    public static Finder<String, DataSubject> getFinder() {
        return finder;
    }
}
