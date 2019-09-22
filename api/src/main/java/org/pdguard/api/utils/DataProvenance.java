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
 * This enumeration defines the possible sources of the data subject's data.
 *
 * @author Diomidis Spinellis
 */
public enum DataProvenance {
    /**
     * The data was explicitly provided by the data subject for the particular
     * purpose.
     *
     * As an example, the data subject filled in a form to register for a gym.
     */
    DATA_SUBJECT_EXPLICIT,

    /**
     * The data was gathered from the data subject's behavior.
     *
     * This may include purchasing history, web clicks, or location data.
     */
    DATA_SUBJECT_BEHAVIOR,

    /**
     * The data controller already had the data.
     *
     * This covers cases where data is reused among different departments.
     */
    DATA_CONTROLLER,

    /** The data was obtained from a public registry. */
    PUBLIC_REGISTRY,

    /**
     * The data was obtained from unstructured publically available sources.
     *
     * Examples include the web, books, articles.
     */
    PUBLIC_DATA,

    /**
     * The data was obtained from a third party.
     *
     * This includes commercial data providers and state entities that
     * maintain non-public personal data.
     */
    THIRD_PARTY,

    /** Cases not covered by any of the definitions. */
    OTHER,
}
