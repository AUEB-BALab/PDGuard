/*-
 * Copyright 2009-2014 Diomidis Spinellis
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

package org.pdguard.api.utils;

/**
 * This enumeration defines entities that can be authenticated when
 * issuing PDGuard requests.
 *
 * Typically requests will require an authenticated data controller,
 * host, and application.  Some requests may include an authenticated
 * data controller employer or data subject.
 *
 * @author Diomidis Spinellis
 */
public enum AuthenticatedEntityType {
    /** An authenticated data controller organization. */
    DATA_CONTROLLER,

    /** An authenticated data controller computing host. */
    DATA_CONTROLLER_HOST,

    /** An authenticated data controller application. */
    DATA_CONTROLLER_APPLICATION,

    /** An authenticated employee of the data controller organization. */
    DATA_CONTROLLER_EMPLOYEE,

    /** An authenticated contractor of the data controller organization. */
    DATA_CONTROLLER_CONTRACTOR,

    /**
     * An authenticated third party computing host.
     *
     * The host is not directly associated with the data controller or
     * the data subect.
     */
    THIRD_PARTY_HOST,

    /**
     * An authenticated third party application.
     *
     * The application is not directly associated with the data controller or
     * the data subect.
     */
    THIRD_PARTY_APPLICATION,

    /**
     * An authenticated third party employee.
     *
     * The person is not directly associated with the data controller or
     * the data subect.
     */
    THIRD_PARTY_EMPLOYEE,

    /**
     * An authenticated third party contractor.
     *
     * The person is not directly associated with the data controller or
     * the data subect.
     */
    THIRD_PARTY_CONTRACTOR,

    /** An authenticated data subject. */
    DATA_SUBJECT,
}
