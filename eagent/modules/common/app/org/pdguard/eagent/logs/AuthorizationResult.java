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
 * This enumeration defines the outcome of an authorization request sent by
 * data controller.
 *
 * @author Thodoris Sotiropoulos
 */
public enum AuthorizationResult {
    /**
     * Escrow agent allows data controller to use data in the way defined in
     * request.
     */
    ALLOWED,

    /**
     * Escrow agent does not permit data controller to use data in the way
     * defined in request.
     */
    DENIED_BY_DATA_SUBJECT,
}
