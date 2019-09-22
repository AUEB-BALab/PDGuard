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

package org.pdguard.eagent.requests;

import org.pdguard.api.exceptions.AccessDeniedException;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.RegistrationFailedException;
import org.pdguard.api.exceptions.UnretrievableKeyException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.utils.ResponseParser;
import static play.mvc.Http.Status;

/**
 * This class is responsible for the validation and processing of PDGuard
 * request issued by a data controller's application.
 *
 * @author Thodoris Sotiropoulos
 */
public class PDGuardRequestProcessor {
    /** PDGuard request to process. */
    private PDGuardRequest pdGuardRequest;

    /** Status code of response. */
    private int statusCode = Status.OK;

    /** Response to PDGuard request. */
    private String response;

    /**
     * Creates a new request processor for the given PDGuard request.
     *
     * @param pdGuardRequest PDGuard request to process.
     */
    public PDGuardRequestProcessor(final PDGuardRequest pdGuardRequest) {
        this.pdGuardRequest = pdGuardRequest;
    }

    /**
     * This method first validates a PDGuard request and the processes it.
     *
     * Method handles the possible exceptions and sets response to the request
     * accordingly.
     */
    public void processRequest() {
        try {
            pdGuardRequest.validate();
            response = pdGuardRequest.process();
        } catch (UnverifiedRequestException e) {
            response = ResponseParser.encodeParameter("authentication_error",
                    e.getMessage());
            statusCode = Status.UNAUTHORIZED;
        } catch (AccessDeniedException e) {
            response = ResponseParser.encodeParameter("authorization_error",
                    e.getMessage());
            statusCode = Status.UNAUTHORIZED;
        } catch (UnretrievableKeyException e) {
            response = ResponseParser.encodeParameter("key_error",
                    e.getMessage());
            statusCode = Status.UNAUTHORIZED;
        } catch (RegistrationFailedException e) {
            response = ResponseParser.encodeParameter("registration_error",
                    e.getMessage());
            statusCode = Status.BAD_REQUEST;
        } catch (EscrowAgentErrorResponseException e) {
            response = ResponseParser.encodeParameter("error", e.getMessage());
            statusCode = Status.UNAUTHORIZED;
        }
    }

    /**
     * Getter of statusCode field.
     *
     * @return StatusCode or response.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Getter of response field.
     *
     * @return Response to PDGuard request.
     */
    public String getResponse() {
        return response;
    }
}
