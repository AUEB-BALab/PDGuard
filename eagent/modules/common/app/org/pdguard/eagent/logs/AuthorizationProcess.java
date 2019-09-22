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
 * This enumeration defines the methods where an authorization process is based
 * on.
 *
 * @author Thodoris Sotiropoulos
 */
public enum AuthorizationProcess {
    /**
     * Authorization process is based on the default default policy of escrow
     * agent and it's common for all data subjects and data controllers.
     */
    DEFAULT,

    /**
     * Authorization process is based on the predefined authorization rules from
     * data subject.
     */
    DATA_SUBJECT_RULES
}
