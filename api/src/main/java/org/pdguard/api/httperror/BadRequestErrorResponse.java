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

package org.pdguard.api.httperror;

import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.RegistrationFailedException;

/**
 * This class defines the error response from escrow agent associated with
 * the '400' (BAD REQUEST) status code.
 *
 * @author Thodoris Sotiropoulos
 */
public final class BadRequestErrorResponse implements EscrowAgentErrorResponse {
    /** BAD_REQUEST status code. */
    private static final int ERROR_CODE = 400;

    /**
     * This method returns the corresponding exception according to the status
     * code and the error response received from escrow agent.
     *
     * @param errorResponse Error response in a URL decoded format.
     *
     * @return Corresponding exception based on the status code and error
     * response.
     */
    @Override
    public EscrowAgentErrorResponseException getErrorResponseException(
            final String errorResponse) {
        if (errorResponse.split("registration_error=").length == 2)
            return new RegistrationFailedException(
                    errorResponse.split("registration_error=")[1]);
        return new EscrowAgentErrorResponseException(MESSAGE
                + " Status code: " + ERROR_CODE);
    }
}
