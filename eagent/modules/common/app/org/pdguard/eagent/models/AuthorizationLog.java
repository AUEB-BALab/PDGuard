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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.pdguard.eagent.logs.AuthorizationProcess;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.logs.AuthorizationRequestType;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;
import play.data.format.Formats;
import play.data.validation.Constraints;
import com.avaje.ebean.Model;
import play.libs.Json;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class defines the entity of AuthorizationLog for database schema of
 * this application using Ebean ORM.
 *
 * An authorization log defines all required information which is associated
 * with a request sent by a data controller via an authorized application.
 * Data controllers requests (using an application) escrow agent to either
 * encrypt or decrypt a block of data which belongs to a data subject. Then,
 * escrow agent checks if data controller has the authorization to have access
 * to these data.
 *
 * This entity records all information associated with these requests.
 * For example, it records data controller who sends the request, application
 * which is used by data controller, which type of data, data controller is
 * requesting to have access, result of request (escrow agents allows access, or
 * not), intended use of data, etc.
 *
 * For that purpose, there is an relation with the {@link Client}
 * class declaring as @ManyToOne. This means that each authorization request is
 * associated with one client and one client can be associated with
 * multiple authorization requests.
 *
 * @author Thodoris Sotiropoulos
 */
@Entity
public final class AuthorizationLog extends Model {
    /** Id of entity. Declared as a primary key. Generated by database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Data controller's request result. */
    @Constraints.Required
    @Column(nullable = false)
    private AuthorizationResult result;

    /**
     * Data controller's request type. For example, encryption or decryption
     * request. This field is required.
     */
    @Constraints.Required
    @Column(nullable = false)
    private AuthorizationRequestType requestType;

    /**
     * Method where authorization method was proceeded. This field is required.
     */
    @Constraints.Required
    @Column(nullable = false)
    private AuthorizationProcess authProcess;

    /** The purpose of a requested interaction. */
    @Column
    private InteractionPurpose interactionPurpose;

    /**
     * Date when authorization request is received by escrow agent.
     * This field is required.
     */
    @Constraints.Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private Date requestDate;

    /** The type of the data. This field is required. */
    @Constraints.Required
    @Column(nullable = false)
    private DataType dataType;

    /**
     * The allowable action which can be performed on data subject's data.
     */
    @Column
    private DataUse dataUse;

    /** The data's provenance; where the data was obtained from. */
    @Column
    private DataProvenance dataProvenance;

    /** True if an existing field is updated; false otherwise. */
    @Column
    private Boolean updateField;

    /**
     * Mapping with the {@link AuthorizedApplication} entity with as
     * ManyToOne relation. Each authorization rule is associated with one
     * authorized application which communicates with escrow agent.
     * This field is required.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<Integer, AuthorizationLog> finder = new Finder<>(
            AuthorizationLog.class);

    /** Default Constructor. */
    public AuthorizationLog() { }

    /**
     * Initializes information which are associated with a request sent by a
     * data controller.
     *
     * @param result Data controller's request result.
     * @param requestType Data controller's request type. For example,
     * encryption or decryption request.
     * @param authProcess Type of authorization procedure, e.g. encryption.
     * @param requestDate Date when authorization request is received by escrow
     * agent.
     * @param dataType The type of the data.
     * @param client Client related to the authorization procedure.
     */
    public AuthorizationLog(final AuthorizationResult result,
            final AuthorizationRequestType requestType,
            final AuthorizationProcess authProcess,
            final Date requestDate, final DataType dataType,
            final Client client) {
        this.result = result;
        this.requestType = requestType;
        this.requestDate = requestDate;
        this.authProcess = authProcess;
        this.dataType = dataType;
        this.client = client;
    }

    /**
     * This method documents all requests that escrow agent receives from data
     * controllers in order to have access or add data of a data subject.
     */
    public void addAuthorizationLog() {
        save();
    }

    /**
     * This method returns history of authorization requests associated with
     * a specific data subject.
     *
     * Given the email of data subject, and a time period
     * (@from and @to parameters)) this method searches for all authorization
     * requests which were associated with specific data of a subject and were
     * completed in time period defined by parameters and it stores
     * them into a list of json objects.
     *
     * @param dataSubjectEmail Email of data subject.
     * @param from Beginning date of time period.
     * @param to End date of time period.
     *
     * @return List of json objects containing the authorization logs.
     */
    public static List<ObjectNode> getDataSubjectAuthLogs(
            final String dataSubjectEmail, final Date from, final Date to) {
        DateFormat dateForm = new SimpleDateFormat("HH:mm yyyy-MM-dd");
        List<ObjectNode> jsonObjects = new LinkedList<>();
        List<AuthorizationLog> authLogs;
        if (from == null)
            authLogs = AuthorizationLog.finder.where()
                    .eq("client.dataSubject.dataSubjectEmail",
                            dataSubjectEmail).le("requestDate", to).findList();
        else
            authLogs = AuthorizationLog.finder.where()
                .eq("client.dataSubject.dataSubjectEmail", dataSubjectEmail)
                .between("requestDate", from, to).findList();
        for (AuthorizationLog authLog: authLogs) {
            ObjectNode json = Json.newObject();
            json.put("dataController", authLog.client.getDataController() == null
                    ? null : authLog.client.getDataController()
                    .getDataControllerName());
            json.put("app", authLog.getClient() == null
                    ? null : authLog.getClient().getAuthApp()
                    .getAuthorizedApplicationName());
            json.put("dataType", authLog.getDataType().toString());
            json.put("dataUse", authLog.getDataUse() == null
                    ? null : authLog.getDataUse().toString());
            json.put("interactionPurpose", (
                    authLog.getInteractionPurpose() == null
                    ? null : authLog.getInteractionPurpose().toString()));
            json.put("dataProvenance", authLog.dataProvenance == null
                    ? null : authLog.dataProvenance.toString());
            json.put("update", authLog.updateField == null
                    ? null : authLog.updateField);
            json.put("requestType", authLog.getRequestType().toString());
            json.put("requestDate", dateForm.format(authLog.getRequestDate()));
            json.put("requestResult", authLog.getResult().toString());
            jsonObjects.add(json);
        }
        return jsonObjects;
    }

    /**
     * Getter of id field.
     *
     * @return Id of entity.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Getter of result field.
     *
     * @return Data controller's request result.
     */
    public AuthorizationResult getResult() {
        return result;
    }

    /**
     * Getter of requestType field.
     *
     * @return Data controller's request type. For example, encryption or
     * decryption request.
     */
    public AuthorizationRequestType getRequestType() {
        return requestType;
    }

    /**
     * Getter of authProcess field.
     *
     * @return Method where authorization was proceeded.
     */
    public AuthorizationProcess getAuthProcess() {
        return authProcess;
    }

    /**
     * Getter of requestDate field.
     *
     * @return Date when authorization request is received by escrow agent.
     */
    public Date getRequestDate() {
        return requestDate;
    }

    /**
     * Getter of interactionPurpose field.
     *
     * @return The purpose of a requested interaction.
     */
    public InteractionPurpose getInteractionPurpose() {
        return interactionPurpose;
    }

    /**
     * Getter of dataType field.
     *
     * @return The type of the data.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Getter of dataUse field.
     *
         * @return The intended use of data.
     */
    public DataUse getDataUse() {
        return dataUse;
    }

    /**
     * Getter of client field.
     *
     * @return Authorized application which communicates with escrow agent.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Setter of dataUse field.
     *
     * @param dataUse The intended use of data.
     */
    public void setDataUse(final DataUse dataUse) {
        this.dataUse = dataUse;
    }

    /**
     * Setter of interactionPurpose field.
     *
     * @param interactionPurpose The purpose of a requested interaction.
     */
    public void setInteractionPurpose(
            final InteractionPurpose interactionPurpose) {
        this.interactionPurpose = interactionPurpose;
    }

    /**
     * Setter of update field.
     *
     * @param update True if existing field is updated; false otherwise.
     */
    public void setUpdateField(final Boolean update) {
        this.updateField = update;
    }

    /**
     * Setter of dataProvenance field.
     *
     * @param dataProvenance The data's provenance; where the data was obtained
     * from.
     */
    public void setDataProvenance(final DataProvenance dataProvenance) {
        this.dataProvenance = dataProvenance;
    }

    /**
     * Getter of finder field.
     *
     * @return It will be used to programatically make queries which return
     * object of this class.
     */
    public static Finder<Integer, AuthorizationLog> getFinder() {
        return finder;
    }
}
