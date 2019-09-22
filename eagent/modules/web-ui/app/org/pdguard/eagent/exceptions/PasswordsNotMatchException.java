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

package org.pdguard.eagent.exceptions;

/**
 * This exception is thrown when password and confirmation password of a user
 * who is requesting to register escrow agent's web UI, are not equal.
 *
 * @author Thodoris Sotiropoulos
 */
public class PasswordsNotMatchException extends Exception {
    /**
     * Construct a new PasswordsNotMatchException with the specified explanatory
     * message.
     *
     * @param message the detail message.
     */
    public PasswordsNotMatchException(final String message) {
        super(message);
    }
}
