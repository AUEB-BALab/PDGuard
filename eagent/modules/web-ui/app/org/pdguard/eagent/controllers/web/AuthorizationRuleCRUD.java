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

package org.pdguard.eagent.controllers.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.pdguard.api.utils.DataProvenance;
import play.i18n.Messages;
import play.mvc.BodyParser;
import org.pdguard.eagent.rule.DataSubjectAuthRule;
import org.pdguard.eagent.exceptions.DataNotFoundException;
import org.pdguard.eagent.userstate.DataSubjectAuthenticated;

import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class extends {@link play.mvc.Controller} class and contains public
 * static methods which represent an action.
 * Usually, an action method does not include a return statement. The method
 * exit is done by the invocation of a result method.
 *
 * Actions which are included in this class represent requests for reading,
 * deletion, creation, update of authorization rules of a data subject who is
 * logged into escrow agent's web UI.
 *
 * @author Thodoris Sotiropoulos
 */
public class AuthorizationRuleCRUD extends Controller {
    /**
     * This returns all authorization rules of a data subject.
     *
     * It uses the email of a data subject stored in session and then it
     * retrieves all authorization rules which were defined by this user.
     *
     * Returns authorization rules in JSON format.
     *
     * @return A JSON Array of authorization rules of a data subject.
     */
    public static Result getAuthorizationRules() {
        DataSubjectAuthenticated user = new DataSubjectAuthenticated(
                session("user"));
        user.retrieveAuthRules();
        JsonNode authRuleJson = Json.toJson(user.getAuthRules());
        return ok(authRuleJson);
    }

    /**
     * This method creates a new authorization rule defined by a data subject.
     *
     * Authorization rule which is going to be created is associated with a
     * type of data of data subject. Data subject defines the way they allow
     * a data controller to use their data of this specific type. Moreover,
     * they define the period of time which this authorization rule is valid,
     * by setting the expiration date.
     *
     * A JSON is taken a parameter should include:
     *  - An array of data types of a data subject.
     *  - The allowable action which can be performed on data subject's data.
     *  - Data controller's name.
     *  - Date when authorization rule expires in String format.
     *
     * @return JSON file which contains an explanatory message and the id in
     * case authorization rule was added successfully for each type of data.
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result createAuthorizationRule() {
        JsonNode json = request().body().asJson();
        DataUse dataUse = json.get("dataUse") != null
                ? DataUse.valueOf(json.get("dataUse").asText()) : null;
        DataProvenance dataProvenance = json.get("dataProvenance") != null
                ? DataProvenance.valueOf(json.get("dataProvenance").asText())
                : null;
        String dataController = json.get("dataController").asText();
        JsonNode validTo = json.get("validTo");
        Date expirationDate;
        ObjectNode result = Json.newObject();
        try {
            expirationDate = validateExpirationDate(validTo.asText());
        } catch (ParseException e) {
            result.put("message", Messages.get("web.misformatted_date"));
            return badRequest(result);
        }
        DataSubjectAuthenticated dataSubject = new DataSubjectAuthenticated(
                session("user"));
        for (JsonNode dataType : json.withArray("dataType")) {
            DataSubjectAuthRule authRule = new DataSubjectAuthRule(
                    DataType.valueOf(dataType.asText()), dataUse, dataProvenance,
                    dataController, new Date(), expirationDate);
            result.set(dataType.asText(), createAuthorizationRule(authRule,
                    dataSubject));
        }
        return ok(result);
    }

    /**
     * This method updates an existing authorization rule defined by a data
     * subject.
     *
     * Authorization rule which is going to be updated is associated with a
     * type of data of data subject. Data subject defines the way they allow
     * a data controller to use their data of this specific type. Moreover,
     * they define the period of time which this authorization rule is valid,
     * by setting the expiration date.
     *
     * @param dataType Type of data of a data subject.
     * @param dataUse The allowable action which can be performed on data
     * subject's data.
     * @param dataController Data controller's name.
     * @param validTo Date when authorization rule expires in String format
     * ("yyyy-MM-dd").
     * @param id The id of the authorization rule which is going to be updated.
     *
     * @return Explanatory message if update of authorization rule was
     * successful or not.
     */
    public static Result updateAllowableAction(final String dataType,
            final String dataUse, final String dataController,
            final String validTo, final Integer id) {
        try {
            Date expirationDate = validateExpirationDate(validTo);
            DataSubjectAuthRule authRule = new DataSubjectAuthRule(
                    DataType.valueOf(dataType), DataUse.valueOf(dataUse),
                    null, dataController, new Date(), expirationDate);
            DataSubjectAuthenticated dataSubject = new DataSubjectAuthenticated(
                    session("user"));
            int exitCode = dataSubject.updateAuthRule(authRule, id);
            if (exitCode == 0)
                return ok(Messages.get("web.update"));
            else
                return ok(Messages.get("web.defined_auth_rule"));
        } catch (DataNotFoundException e) {
            return ok(e.getMessage());
        } catch (ParseException e) {
            return badRequest(Messages.get("web.misformatted_date"));
        }
    }

    /**
     * This method updates an existing authorization rule defined by a data
     * subject.
     *
     * Authorization rule which is going to be updated is associated with a
     * type of data of data subject and its allowable provenance. Data subject
     * defines the provenance where a data controller can obtain their data of
     * this specific type to store them. Moreover, they define the period of
     * time which this authorization rule is valid, by setting the expiration
     * date.
     *
     * @param dataType Type of data of a data subject.
     * @param dataProvenance The allowable provenance where data can be
     * obtained from.
     * @param dataController Data controller's name.
     * @param validTo Date when authorization rule expires in String format
     * ("yyyy-MM-dd").
     * @param id The id of the authorization rule which is going to be updated.
     *
     * @return Explanatory message if update of authorization rule was
     * successful or not.
     */
    public static Result updateAllowableProvenance(final String dataType,
            final String dataProvenance, final String dataController,
            final String validTo, final Integer id) {
        try {
            Date expirationDate = validateExpirationDate(validTo);
            DataSubjectAuthRule authRule = new DataSubjectAuthRule(
                DataType.valueOf(dataType), null,
                DataProvenance.valueOf(dataProvenance), dataController,
                new Date(), expirationDate);
            DataSubjectAuthenticated dataSubject = new DataSubjectAuthenticated(
                session("user"));
            int exitCode = dataSubject.updateAllowableProvenance(authRule, id);
            if (exitCode == 0)
                return ok(Messages.get("web.update"));
            else
                return ok(Messages.get("web.defined_auth_rule"));
        } catch (DataNotFoundException e) {
            return ok(e.getMessage());
        } catch (ParseException e) {
            return badRequest(Messages.get("web.misformatted_date"));
        }
    }

    /**
     * This method deletes an existing authorization rule defined by a data
     * subject.
     *
     * Data subject deletes an authorization rule which they have previously
     * defined. There is one extreme case: If there is only one authorization
     * rule associated with a specific data type of a data subject and a data
     * controller, then deletion is completed only by locking these data to
     * data controller's access.
     *
     * @param id The id of the authorization rule which is going to be deleted.
     * @param removeAction True if an allowable action is going to be deleted;
     * false if an allowable provenance is going to be deleted.
     *
     * @return Explanatory message if deletion of authorization rule was
     * successful or not.
     */
    public static Result deleteAuthorizationRule(final Integer id,
            final Boolean removeAction) {
        DataSubjectAuthenticated dataSubject = new DataSubjectAuthenticated(
                session("user"));
        int exitCode = dataSubject.deleteAuthRule(id, removeAction);
        if (exitCode == 0)
            return ok(Messages.get("web.deletion"));
        else
            return ok(Messages.get("web.auth_rule_not_found"));
    }

    /**
     * This method locks personal data of a subject stored in a specific
     * organization (data controller).
     *
     * When a data subject locks specific data of them (for example, a data
     * subject locks their credit card number), then data controller who holds
     * these data, they cannot have any access to them. Data subject can permit
     * access to their data again by redefining the authorization rule.
     *
     * A JSON object specify the data controller and the types of data which
     * have to been locked.
     *
     * @return Explanatory message if lock of data was successful or not.
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result lockData() {
        DataSubjectAuthenticated dataSubject = new DataSubjectAuthenticated(
                session("user"));
        JsonNode json = request().body().asJson();
        String dataController = json.get("dataController").asText();
        ObjectNode response = Json.newObject();
        for (JsonNode dataType : json.withArray("dataTypes")) {
            try {
                dataSubject.lockData(DataType.valueOf(dataType.asText()),
                        dataController);
                response.put(dataType.asText(), Messages.get("web.lock"));
            } catch (DataNotFoundException e) {
                response.put(dataType.asText(), e.getMessage());
            }
        }
        return ok(response);
    }

    /**
     * This method checks that the input of expiration date string is in a valid
     * format.
     *
     * @param validTo Expiration date in a "yyyy-MM-dd" format.
     *
     * @return Date object of expiration date.
     * @throws ParseException is thrown when given input string does not follow
     * "yyyy-MM-dd" pattern.
     */
    static Date validateExpirationDate(final String validTo)
            throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!validTo.equals(""))
            return dateFormat.parse(validTo);
        else
            return null;
    }

    /**
     * This method adds given authorization rule associated with a specific
     * data subject, data controller and data type.
     *
     * @param authRule Authorization rule to be added.
     * @param dataSubject Data subject related to authorization rule.
     *
     * @return A json object with an explanatory message if authorization rule
     * was added successfully or not.
     */
    static JsonNode createAuthorizationRule(final DataSubjectAuthRule authRule,
            final DataSubjectAuthenticated dataSubject) {
        ObjectNode jsonObj = Json.newObject();
        try {
            int id = dataSubject.createAuthRule(authRule);
            if (id != 0) {
                jsonObj.put("message", Messages.get("web.addition"));
                jsonObj.put("id", id);
            } else
                jsonObj.put("message", Messages.get("web.defined_auth_rule"));
        } catch (DataNotFoundException e) {
            jsonObj.put("message", e.getMessage());
        }
        return jsonObj;
    }
}
