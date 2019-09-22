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

import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.models.AllowableAction;

/**
 * This class implements the authorization processing of a decryption PDGuard
 * requests based on default authorization rules of escrow agent.
 *
 * @author Thodoris Sotiropoulos
 */
public class DefaultDecryptionAuthorization implements AuthorizationApi {
    /**
     * Authorization bundle of parameters in which escrow agent would grant
     * authorization.
     */
    private DecryptionBundle authBundle;

    /** Result of authorization procedure. */
    private AuthorizationResult authResult;

    /**
     * Creates a new instance with all required fields.
     *
     * @param authBundle Authorization bundle of parameters in which escrow
     * agent would grant authorization.
     */
    public DefaultDecryptionAuthorization(final DecryptionBundle authBundle) {
        this.authBundle = authBundle;
    }

    /**
     * This method processes the authorization of a decryption PDGuard request
     * based on the default authorization rules of escrow agent.
     */
    @Override
    public AuthorizationResult processAuthorization() {
        boolean isAuthorized = AllowableAction.isAuthorized(authBundle
                .getDataType(), authBundle.getDataUse());
        authResult = isAuthorized ? AuthorizationResult
                .ALLOWED : AuthorizationResult.DENIED_BY_DATA_SUBJECT;
        return authResult;
    }

    /**
     * This method adds all information associated with this authorization
     * request to escrow agent's database.
     *
     * In this way, there is a storage of all requests and their outcome sent to
     * escrow agent by data controllers. Therefore, it could be a supervision
     * that the authorization procedure is completed successfully.
     *
     * @param authLogApi API responsible for the addition of authorization log.
     */
    @Override
    public void addAuthorizationLog(AuthorizationLogApi authLogApi) {
        authLogApi.addAuthorizationLog(this);
    }

    /**
     * Getter of authBundle field.
     *
     * @return Authorization bundle of parameters in which escrow agent would
     * grant authorization.
     */
    public DecryptionBundle getAuthBundle() {
        return authBundle;
    }

    /**
     * Getter of authResult field.
     *
     * @return Result of authorization procedure.
     */
    public AuthorizationResult getAuthResult() {
        return authResult;
    }
}
