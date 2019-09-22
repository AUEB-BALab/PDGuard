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
 * This enumeration defines the purpose of an interaction, which requires
 * a data controller's use of the subject's personal data.
 *
 * @author Diomidis Spinellis
 */
public enum InteractionPurpose {
    /**
     * The data controller wants to send an advert to the data subject.
     *
     * Example: 30% off for tickets to Australia this month.
     */
    ADVERT,

    /**
     * The data controller wants to send an informative message to the data
     * subject.
     *
     * The message can aid the data subject, but it is not required for
     * interacting with it.
     *
     * Example: You can now check-in for your flight.
     */
    INFORMATIVE,

    /**
     * The data controller wants to send an important message to the data
     * subject.
     *
     * The delivery of the message can significantly aid the data subject.
     *
     * Example: Your flight departure will be delayed by 30 minutes.
     */
    IMPORTANT,

    /**
     * The data controller wants to interact with the data subject in a way
     * that is required to fullfill an already established contractual
     * relationship between them.
     *
     * Example: Enclosed find the book you ordered.
     */
    CONTRACTUAL,

    /**
     * The data controller requires access to the personal data in order to
     * fullfill its obligations to regulatory authorities.
     *
     * Example: The customer paid into his bank account $10,001 in cash.
     */
    REGULATORY,

    /**
     * The data controller wants to send a critical message to the data
     * subject.
     *
     * The non-delivery of the message can significantly affect the data
     * subject.
     *
     * Example:  Your credit card has been suspended.
     */
    CRITICAL,

    /**
     * The data controller wants to send to the data subject an urgent
     * alert message.
     *
     * Failure of timely delivery of the message can significantly affect
     * the data subject.
     *
     * Example: You are requested to board immediately.
     */
    ALERT,

    /**
     * The data controller wants to send an emergency message to the data
     * subject.
     *
     * The non-delivery of the message can lead to loss of life or injury.
     *
     * Example: Fire: evacuate the building now!
     *
     * Note: PDGuard is not designed or implemented as a safety-critical
     * system, and should not be used as such.  Safety-critical operations
     * should use other suitable systems as their primary agents.
     */
    EMERGENCY,
}
