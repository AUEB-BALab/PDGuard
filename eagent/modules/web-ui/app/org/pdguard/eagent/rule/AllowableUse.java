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

import org.pdguard.api.utils.DataUse;

import java.util.Date;

/**
 * This class represents an allowable use of data.
 *
 * This defines how a data controller who stores some data which belongs to a
 * data subject, can use these data.
 *
 * For instance, an application running on a data controller can publish the
 * home address of a data subject but it cannot store it to a file.
 *
 * @author Thodoris Sotiropoulos
 */
public final class AllowableUse {
    /** ID of authorization rule. */
    private int id;

    /** The allowable action which can be performed on data subject's data. */
    private DataUse dataUse;

    /** Date when authorization rule is valid from. */
    private Date validFrom;

    /** Date when authorization rule validity expires. */
    private Date validTo;

    /** Default Constructor. */
    public AllowableUse() { }

    /** Define a new allowable use on data of a data subject.
     *
     * @param dataUse The allowable action which can be performed on data
     * subject's data.
     * @param validFrom Date when authorization rule is valid from.
     * @param validTo Date when authorization rule validity expires.
     */
    public AllowableUse(final DataUse dataUse, final Date validFrom,
            final Date validTo) {
        this.dataUse = dataUse;
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
     * @return The allowable action which should be performed on data subject's
     * data.
     */
    public DataUse getDataUse() {
        return dataUse;
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
