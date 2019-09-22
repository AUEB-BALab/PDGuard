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

package org.pdguard.eagent.userstate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.pdguard.api.utils.DataType;
import org.pdguard.eagent.exceptions.DataNotFoundException;
import org.pdguard.eagent.rule.AllowableProvenance;
import org.pdguard.eagent.rule.AllowableUse;
import org.pdguard.eagent.rule.DataSubjectAuthRule;
import org.pdguard.eagent.models.AuthorizationLog;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.AuthorizationRule;
import org.pdguard.eagent.models.AllowableAction;
import play.libs.Json;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * This class represents a data subject who is connected to the escrow agent's
 * web UI.
 *
 * A connected user to the escrow agent's web UI can perform many actions which
 * are offered by the escrow agent services. For instance, they can define and
 * revoke authorization rules which are associated with their personal data or
 * view the performed actions by data controllers.
 *
 * This class extends {@link DataSubject} class.
 *
 * @author Thodoris Sotiropoulos
 */
public class DataSubjectAuthenticated extends DataSubject {
    /** List of authorization rules defined by data subject. */
    private List<DataSubjectAuthRule> authRules = new LinkedList<>();

    /**
     * Initialize a connected user of escrow agent's web UI.
     *
     * @param email Email of a user. It is a required field. Every user of
     * escrow agent's web UI has to be described by a unique email. Moreover,
     * its value has to follow standard email's pattern in order to be valid.
     */
    public DataSubjectAuthenticated(final String email) {
        super(email);
    }

    /**
     * This method retrieves all authorization rules of data subject.
     *
     * Authorization rule of a data subject is described by the following
     * fields.
     *
     * 1) Type of a data subject's data. For example, address, Facebook ID, etc
     * 2) Allowable action which can be performed on data subject's data.
     * For example, export address to a file.
     * 3) Data Controller's name. Name of organization which holds data
     * subject's data.
     */
    public void retrieveAuthRules() {
        List<ObjectNode> json = AuthorizationRule.getDataSubjectAuthRules(
                super.getEmail());
        for (ObjectNode jsonObject: json)
            authRules.add(Json.fromJson(jsonObject, DataSubjectAuthRule.class));
    }

    /**
     * This method creates a new authorization rule for a data subject.
     *
     * There are possible scenarios.
     * Firstly, creation of this authorization rule is successful and secondly
     * creation of this authorization rule cannot be completed. Authorization
     * rule cannot be created in case there is already a same authorization rule
     * defined by data subject (associated with the same data subject, data
     * controller, data type, data use, etc).
     *
     * @param authRule Authorization rule to be created.
     *
     * @return Id of authorization rule, 0 if authorization rule was not created
     * successfully.
     *
     * @throws DataNotFoundException is thrown when specific type which
     * belongs to a data subject are not stored encrypted to a data controller's
     * database
     */
    public int createAuthRule(final DataSubjectAuthRule authRule)
            throws DataNotFoundException {
        AuthorizationRule authorizationRule = getDataSubjectAuthRule(
                authRule.getDataController(), authRule.getDataType());
        if (authorizationRule == null)
            return 0;
        if (!authRule.getActions().isEmpty())
            return addAllowableAction(authorizationRule, authRule);
        else
            return addAllowableProvenance(authorizationRule, authRule);
    }

    /**
     * This method adds a new allowable action performed on data subject's data
     * by a data controller organization.
     *
     * @param authorizationRule Authorization rule which is going to be updated.
     * @param dataSubjectAuthRule Updated authorization rule defined by data
     * subject.
     *
     * @return Id of authorization rule, 0 if authorization rule was not created
     * successfully.
     */
    int addAllowableAction(final AuthorizationRule authorizationRule,
            final DataSubjectAuthRule dataSubjectAuthRule) {
        assert dataSubjectAuthRule.getActions().size() == 1;
        AllowableUse allowableUse = dataSubjectAuthRule.getActions().iterator()
                .next();
        int id = new AllowableAction(authorizationRule, allowableUse
                .getDataUse(), allowableUse.getValidFrom(),
                allowableUse.getValidTo()).addAllowableAction();
        if (id != 0) {
            dataSubjectAuthRule.getActions().iterator().next().setId(id);
            authRules.add(dataSubjectAuthRule);
        }
        return id;
    }

    /**
     * This method adds a new allowable provenance where data can be obtained
     * from.
     *
     * @param authorizationRule Authorization rule which is going to be updated.
     * @param dataSubjectAuthRule Updated authorization rule defined by data
     * subject.
     *
     * @return Id of authorization rule, 0 if authorization rule was not created
     * successfully.
     */
    int addAllowableProvenance(final AuthorizationRule authorizationRule,
            final DataSubjectAuthRule dataSubjectAuthRule) {
        assert dataSubjectAuthRule.getProvenances().size() == 1;
        AllowableProvenance allowableProvenance = dataSubjectAuthRule
                .getProvenances().iterator().next();
        int id = new org.pdguard.eagent.models.AllowableProvenance(
                authorizationRule, allowableProvenance.getDataProvenance(),
                allowableProvenance.getValidFrom(),
                allowableProvenance.getValidTo()).addAllowableProvenance();
        if (id != 0) {
            dataSubjectAuthRule.getProvenances().iterator().next().setId(id);
            authRules.add(dataSubjectAuthRule);
        }
        return id;
    }

    /**
     * This method updates the parameters which describes a rule related to the
     * allowable action of data.
     *
     * Data subject redefines their authorization rules by updating the
     * allowable action performed on specific data, time when authorization rule
     * is valid or the data controller.
     *
     * @param authRule Redefined authorization rule.
     * @param id The id of the authorization rule to update.
     *
     * @return Exit code, 0 if authorization rule is updated successfully; 1
     * otherwise.
     *
     * @throws DataNotFoundException is thrown when specific type which
     * belongs to a data subject are not stored encrypted to a data controller's
     * database.
     */
    public int updateAuthRule(final DataSubjectAuthRule authRule, final int id)
            throws DataNotFoundException {
        AuthorizationRule authorizationRule = getDataSubjectAuthRule(
                authRule.getDataController(), authRule.getDataType());
        if (authorizationRule == null)
            return 0;
        assert authRule.getActions().size() == 1;
        AllowableUse allowableUse = authRule.getActions().iterator().next();
        AllowableAction updatedAuthAllowableAction = new AllowableAction(
                authorizationRule, allowableUse.getDataUse(),
                allowableUse.getValidFrom(), allowableUse.getValidTo());
        return updatedAuthAllowableAction.updateAllowableAction(id);
    }

    /**
     * This method updates the parameters which describes a rule associated with
     * the allowable provenance of data.
     *
     * Data subject redefines their authorization rules by updating the
     * allowable provenance where data can be obtained from, time when
     * authorization rule is valid or the data controller.
     *
     * @param authRule Redefined authorization rule.
     * @param id The id of the authorization rule to update.
     *
     * @return Exit code, 0 if authorization rule is updated successfully; 1
     * otherwise.
     *
     * @throws DataNotFoundException is thrown when specific type which
     * belongs to a data subject are not stored encrypted to a data controller's
     * database.
     */
    public int updateAllowableProvenance(final DataSubjectAuthRule authRule,
            final int id) throws DataNotFoundException {
        AuthorizationRule authorizationRule = getDataSubjectAuthRule(
                authRule.getDataController(), authRule.getDataType());
        if (authorizationRule == null)
            return 0;
        assert authRule.getProvenances().size() == 1;
        AllowableProvenance allowableProvenance = authRule.getProvenances()
                .iterator().next();
        return new org.pdguard.eagent.models.AllowableProvenance(
                authorizationRule, allowableProvenance.getDataProvenance(),
                allowableProvenance.getValidFrom(), allowableProvenance
                .getValidTo()).updateAllowableProvenance(id);
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
     * @return Exit code, 0 if authorization rule is deleted successfully; 1
     * otherwise.
     */
    public int deleteAuthRule(final int id, final boolean removeAction) {
        if (removeAction)
            return AllowableAction.deleteAllowableAction(id);
        else
            return org.pdguard.eagent.models.AllowableProvenance
                    .deleteAllowableProvenance(id);
    }

    /**
     * This method returns history of authorization requests associated with
     * a specific time period.
     *
     * Time period described by @from and @to parameters and this method
     * retrieves all authorization requests completed in this time period.
     *
     * @param from Beginning date of time period.
     * @param to End date of time period.
     *
     * @return List of json objects containing the authorization logs.
     */
    public List<ObjectNode> retrieveAuthLogs(final Date from, final Date to) {
        return AuthorizationLog.getDataSubjectAuthLogs(super.getEmail(), from,
                to);
    }

    /**
     * This method locks personal data of a subject.
     *
     * When a data subject locks specific data of them (for example, a data
     * subject locks their credit card number), then data controller who holds
     * these data, they cannot have any access to them. Data subject can permit
     * access to their data again by redefining the authorization rule.
     *
     * @param dataType Type of data to lock.
     * @param dataControllerName Organization's name which hold data.
     *
     * @throws DataNotFoundException is thrown when specific type which
     * belongs to a data subject are not stored encrypted to a data controller's
     * database.
     */
    public void lockData(final DataType dataType,
            final String dataControllerName) throws DataNotFoundException {
        getDataSubjectAuthRule(dataControllerName, dataType).lockData();
    }

    /**
     * This method retrieves an authorization rule related to the given data
     * subject, data controller and data type.
     *
     * @param dataControllerName Name of data controller's organization.
     * @param dataType Type of data.
     *
     * @return Authorization rule associated with data subject, data controller,
     * data type
     * @throws DataNotFoundException is thrown when specific type which
     * belongs to a data subject are not stored encrypted to a data controller's
     * database.
     */
    AuthorizationRule getDataSubjectAuthRule(final String dataControllerName,
            final DataType dataType) throws DataNotFoundException {
        org.pdguard.eagent.models.DataSubject dataSubject = org.pdguard.eagent
                .models.DataSubject.authenticateByEmail(super.getEmail());
        DataController dataController = DataController.authenticate(
                dataControllerName, super.getEmail(), dataType);
        if (dataController == null)
            throw new DataNotFoundException("Data controller "
                    + dataControllerName + " does not hold data"
                    + " of type " + dataType.toString()
                    .replace("_", " ").toLowerCase(Locale.ENGLISH)
                    + " which belongs to you");
        return AuthorizationRule.authenticate(dataSubject.getId(),
                dataController.getId(),dataType);
    }

    /**
     * Getter of authRules field.
     *
     * @return List of authorization rules defined by data subject.
     */
    public List<DataSubjectAuthRule> getAuthRules() {
        return authRules;
    }
}
