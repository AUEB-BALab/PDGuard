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

package org.pdguard.eagent.auth;

import org.pdguard.eagent.logs.AuthorizationResult;

/**
 * This interface defines classes that are responsible for the authorization
 * of a PDGuard request.
 *
 * Authorization can be associated with either an encryption or
 * decryption of a piece of data. PDGuard request and based on the default policies of escrow
 * agent or the predefined authorization rules of data subject.
 *
 * @author Thodoris Sotiropoulos
 */
public interface AuthorizationApi {
    /**
     * This method processes the authorization of a PDGuard request which can
     * be associated with the encryption or decryption of data and based on
     * the default authorization rule of escrow agent or the predefined
     * guidelines of data subject.
     */
    AuthorizationResult processAuthorization();

    /**
     * This method adds all the information associated with this authorization
     * request to escrow agent's database.
     *
     * In this way, there is a storage of all requests and their outcome sent to
     * escrow agent by data controllers. Therefore, it could be a supervision
     * that the authorization procedure is completed successfully.
     *
     * @param authLogApi API responsible for the addition of authorization log.
     */
    void addAuthorizationLog(final AuthorizationLogApi authLogApi);
}
