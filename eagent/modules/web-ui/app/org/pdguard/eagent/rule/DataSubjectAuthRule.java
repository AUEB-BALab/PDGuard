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

package org.pdguard.eagent.rule;

import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an authorization rule of a data subject.
 *
 * It contains all required fields in order to be reviewed by data subject on
 *  the escrow agent's web UI.
 *
 * @author Thodoris Sotiropoulos
 */
public final class DataSubjectAuthRule {
    /** Type of data of a data subject. */
    private DataType dataType;

    /** Data controller's name. */
    private String dataController;

    /** A set of allowable uses of data. */
    private Set<AllowableUse> actions = new HashSet<>();

    /** A set of allowable provenances of data. */
    private Set<AllowableProvenance> provenances = new HashSet<>();

    /** Default Constructor. */
    public DataSubjectAuthRule() { }

    /**
     * Initializes an authorization rule with all required fiells.
     *
     * @param dataType Type of data of a data subject.
     * @param dataUse The allowable action which can be performed on data
     * subject's data.
     * @param dataController Data controller's name.
     * @param validFrom Date when authorization rule is valid from.
     * @param validTo Date when authorization rule expires.
     */
    public DataSubjectAuthRule(final DataType dataType, final DataUse
            dataUse, final DataProvenance dataProvenance,
            final String dataController, final Date validFrom,
            final Date validTo) {
        this.dataType = dataType;
        this.dataController = dataController;
        if (dataUse != null)
            actions.add(new AllowableUse(dataUse, validFrom, validTo));
        if (dataProvenance != null)
            provenances.add(new AllowableProvenance(dataProvenance, validFrom,
                    validTo));
    }

    /**
     * Getter of dataType field.
     *
     * @return Type of data of a data subject.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Getter of dataController field.
     *
     * @return Data controller's name.
     */
    public String getDataController() {
        return dataController;
    }

    /**
     * Setter of dataType field.
     *
     * @param dataType Type of data of a data subject.
     */
    public void setDataType(final DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Setter of dataController field.
     *
     * @param dataController Data controller's name.
     */
    public void setDataController(final String dataController) {
        this.dataController = dataController;
    }

    /**
     * Getter of rules field.
     *
     * @return A set of allowable uses of data.
     */
    public Set<AllowableUse> getActions() {
        return actions;
    }

    /**
     * Setter of rules field.
     *
     * @param actions A set of allowable uses of data.
     */
    public void setActions(final Set<AllowableUse> actions) {
        this.actions = actions;
    }

    /**
     * Getter of provenances field.
     *
     * @return A set of allowable provenances of data.
     */
    public Set<AllowableProvenance> getProvenances() {
        return provenances;
    }

    /**
     * Setter of provenances field.
     *
     * @param provenances A set of allowable provenances of data.
     */
    public void setProvenances(final Set<AllowableProvenance> provenances) {
        this.provenances = provenances;
    }
}
