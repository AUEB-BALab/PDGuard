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
 * This enumeration defines how a data controller's application will
 * use the data subject's data.
 *
 * @author Diomidis Spinellis
 */
public enum DataUse {
    /** Data will be used to compose an email to be sent to the subject. */
    COMPOSE_EMAIL_TO_SUBJECT,
    /**
     * Data will be used to compose an a physical mail to be sent
     * to the subject.
     */
    COMPOSE_LETTER_TO_SUBJECT,
    /** Data will be used to send a package to the subject. */
    SEND_PACKAGE_TO_SUBJECT,
    /** Data will be used to send a phone short message to the subject. */
    SEND_SMS_TO_SUBJECT,
    /** Data will be used to visit the subject. */
    VISIT_SUBJECT,
    /** Data will be used to interact with the subject via a phone call. */
    INTERACT_WITH_SUBJECT_OVER_PHONE,
    /** Data will be used to interact with the subject via an online chat. */
    INTERACT_WITH_SUBJECT_OVER_CHAT,
    /**
     * Data will be used to send a query to a third party regarding the
     * subject.
     */
    QUERY_THIRD_PARTY,
    /**
     * Data will be used to send a notification to a third party regarding the
     * subject.
     */
    NOTIFY_THIRD_PARTY,
    /**
     * Data will be used to update data of a third party regarding the subject.
     */
    UPDATE_THIRD_PARTY_DATA,
    /** Data will be posted to the data controller's intranet. */
    INTRANET,
    /** Data will be posted to the world wide web. */
    WWW,
    /**
     * Data will be physically published in material circulating within the
     * data controller.
     */
    PUBLISH_INTERNAL,
    /**
     * Data will be physically published in widely available material.
     */
    PUBLISH_WORLD,
    /** Data will be used for voice broadcasting. */
    BROADCAST_VOICE,
    /** Data will be used for video broadcasting. */
    BROADCAST_VIDEO,
    /** Data will be aggregated for reporting purposes. */
    REPORT,
    /** Data will be used for analytical processing. */
    ANALYTICS,
    /** Data will be used in a desktop application. */
    APPLICATION_DESKTOP,
    /** Data will be used in a remotelly-accessible application. */
    APPLICATION_REMOTE,
    /** Data will be used for another purpose. */
    OTHER,
}
