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

/**
 * This interface defines the classes that are responsible for the addition of
 * all the history associated with the authorization procedure of PDGuard requests.
 *
 * @author Thodoris Sotiropoulos
 */
public interface AuthorizationLogApi {
    /**
     * This method adds all information associated with the authorization
     * procedure of a decryption PDGuard request based on the predefined
     * guidelines of data subject.
     *
     * @param auth Authorization procedure.
     */
    void addAuthorizationLog(final DecryptionAuthorization auth);

    /**
     * This method adds all information associated with the authorization
     * procedure of an encryption PDGuard request based on the predefined
     * guidelines of data subject.
     *
     * @param auth Authorization procedure.
     */
    void addAuthorizationLog(final EncryptionAuthorization auth);

    /**
     * This method adds all information associated with the authorization
     * procedure of a decryption PDGuard request based on the default
     * authorization rules of escrow agent.
     *
     * @param auth Authorization procedure.
     */
    void addAuthorizationLog(final DefaultDecryptionAuthorization auth);

    /**
     * This method adds all information associated with the authorization
     * procedure of a encryption PDGuard request based on the default
     * authorization rules of escrow agent.
     *
     * @param auth Authorization procedure.
     */
    void addAuthorizationLog(final DefaultEncryptionAuthorization auth);
}
