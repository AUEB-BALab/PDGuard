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

package org.pdguard.eagent.logs;

/**
 * This enumeration defines the different types of a request which a data
 * controller can forward to escrow agent.
 *
 * @author Thodoris Sotiropoulos
 */
public enum AuthorizationRequestType {
    /**
     * A request from a data controller to encrypt a block of a subject's data.
     */
    UPDATE,

    /**
     * A request from a data controller to decrypt a block of a subject's data.
     * The allowable use of data is defined by data subject via the web UI of
     * escrow agent.
     */
    READ
}
