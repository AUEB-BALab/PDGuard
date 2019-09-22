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

package org.pdguard.eagent.validation;

import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.eagent.models.ReqToken;
import play.i18n.Messages;

/**
 * This interface defines the way a request token is validated by escrow agent
 * so it can be authorized or exchanged for an encryption key.
 *
 * @author Thodoris Sotiropoulos
 */
public interface RequestTokenValidator {
    /**
     * This method validates a request token.
     *
     * The default implementation of this method is associated with validation
     * of an unauthorized request token in order to be authorized by escrow
     * agent.
     *
     * @param reqToken Request token to be validated.
     * @throws UnverifiedRequestException is thrown when request token is
     * invalid.
     */
    default void validateRequestToken(final ReqToken reqToken)
            throws UnverifiedRequestException {
        String errorMessage = Messages.get("auth.invalid_req_token");
        if (reqToken == null)
            throw new UnverifiedRequestException(errorMessage);
        if (!reqToken.isValid()) {
            throw new UnverifiedRequestException(errorMessage);
        }
    }
}
