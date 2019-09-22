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
 * This exception is thrown then escrow agent cannot verify the request of
 * the client.
 *
 * @author Thodoris Sotiropoulos
 */
public class UnverifiedRequestException
        extends EscrowAgentErrorResponseException {
    /**
     * Construct a new UnverifiedRequestException with the specified
     * explanatory message.
     *
     * @param message the detail message.
     */
    public UnverifiedRequestException(final String message) {
        super(message);
    }
}
