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

import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;

/**
 * This interface defines class that are responsible for the validation and
 * processing of PDGuard requests from data controller's applications to this
 * current escrow agent.
 *
 * @author Thodoris Sotiropoulos
 */
public interface PDGuardRequest {
    /**
     * This method validates a PDGuard request.
     *
     * Typically, a data controller's application is requesting for some
     * resources required during the authorization protocol. Escrow agent has to
     * validate each request before sends back the requested resources.
     *
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * cannot validate request.
     */
    void validate() throws EscrowAgentErrorResponseException;

    /**
     * This method processes a PDGuard request.
     *
     * Escrow agent after validates a PDGuard request, it sends back the
     * corresponding response in a URL encoded format e.g. client credentials.
     *
     * @return Response of a PDGuard request in a URL encoded format.
     *
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * cannot process request.
     */
    String process() throws EscrowAgentErrorResponseException;
}
