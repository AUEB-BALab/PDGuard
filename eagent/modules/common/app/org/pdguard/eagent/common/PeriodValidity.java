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

package org.pdguard.eagent.common;

import java.util.Calendar;
import java.util.Date;

/**
 * This interface defines the policies for setting validity periods.
 *
 * Typically, authorization rules, encryption keys and request tokens have
 * their own validity periods.
 *
 * @author Thodoris Sotiropoulos
 */
public interface PeriodValidity {
    /**
     * This methods sets the expiration date of a validity period.
     *
     * Validity period can be associated with authorization rules, encryption
     * keys or request tokens.
     *
     * By default, expiration date of authorization rules and request tokens
     * is two years after the date when they are valid from.
     *
     * @param validFrom Date when validity period starts.
     *
     * @return Expiration date of authorization rule.
     */
    default Date getExpirationDate(final Date validFrom) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(validFrom);
        final int twoYearsAfter = 2;
        calendar.add(Calendar.YEAR, twoYearsAfter);
        return calendar.getTime();
    }

    /**
     * This method checks if the validity period has passed.
     *
     * @param validFrom Date when validity period starts.
     * @param validTo Date when validity period ends.
     *
     * @return True if the validity period expired, false otherwise.
     */
    default boolean isExpired(final Date validFrom, final Date validTo) {
        Date today = new Date();
        return !(today.equals(validFrom) || today.before(validTo));
    }
}
