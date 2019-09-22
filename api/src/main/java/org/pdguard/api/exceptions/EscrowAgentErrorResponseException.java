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

package org.pdguard.api.exceptions;

/**
 * This exception is thrown when a PDGuard request to escrow agent is not
 * successful and there is an error response from escrow agent.
 *
 * @author Thodoris Sotiropoulos
 */
public class EscrowAgentErrorResponseException extends Exception {
    /**
     * Construct a new EscrowAgentErrorResponseException with the specified
     * explanatory message.
     *
     * @param message the detail message.
     */
    public EscrowAgentErrorResponseException(final String message) {
        super(message);
    }
}
