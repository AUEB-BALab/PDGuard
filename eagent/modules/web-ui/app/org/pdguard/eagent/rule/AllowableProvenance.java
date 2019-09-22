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

import java.util.Date;

/**
 * This class represents an allowable provenance of data.
 *
 * This defines the allowable provenances where a data controller can obtain
 * data from in order to store them.
 *
 * For instance, an application running on a data controller can store credit
 * card number taken from data subject himself, but it cannot store it when it
 * taken from other provenance.
 *
 * @author Thodoris Sotiropoulos
 */
public final class AllowableProvenance {
    /** ID of authorization rule. */
    private int id;

    /** The allowable provenance where data can be obtained from. */
    private DataProvenance dataProvenance;

    /** Date when authorization rule is valid from. */
    private Date validFrom;

    /** Date when authorization rule validity expires. */
    private Date validTo;

    /** Default Constructor. */
    public AllowableProvenance() { }

    /** Define a new allowable provenance on data of a data subject.
     *
     * @param dataProvenance The allowable provenance where data can be obtained
     * from.
     * @param validFrom Date when authorization rule is valid from.
     * @param validTo Date when authorization rule validity expires.
     */
    public AllowableProvenance(final DataProvenance dataProvenance,
            final Date validFrom, final Date validTo) {
        this.dataProvenance = dataProvenance;
        this.validFrom = (Date) validFrom.clone();
        this.validTo = validTo == null ? null : (Date) validTo.clone();
    }

    /**
     * Getter of id field.
     *
     * @return ID of authorization rule.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter of dataUse field.
     *
     * @return The allowable provenance where data can be obtained from.
     */
    public DataProvenance getDataProvenance() {
        return dataProvenance;
    }

    /**
     * Getter of validFrom field.
     *
     * @return Date when authorization rule is valid from.
     */
    public Date getValidFrom() {
        return (Date) validFrom.clone();
    }

    /**
     * Getter of validTo field.
     *
     * @return Date when authorization rule expires.
     */
    public Date getValidTo() {
        return validTo == null ? null : (Date) validTo.clone();
    }

    /**
     * Setter of id field.
     *
     * @param id ID of authorization rule.
     */
    public void setId(final int id) {
        this.id = id;
    }
}
