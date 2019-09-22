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
 * requests based on the predefined authorization rules of data subject.
 *
 * @author Thodoris Sotiropoulos
 */
public class DecryptionAuthorization implements AuthorizationApi {
    /**
     * Authorization bundle of parameters in which escrow agent would grant
     * authorization.
     */
    private DecryptionBundle authBundle;

    /** Result of authorization procedure. */
    private AuthorizationResult authResult;

    /** ID of data subject. */
    private String dataSubjectId;

    /** ID of data controller. */
    private String dataControllerId;

    /**
     * Creates a new instance with all required fields.
     *
     * @param authBundle Authorization bundle of parameters in which escrow
     * agent would grant authorization.
     * @param dataSubjectId Id of data subject.
     * @param dataControllerId Id of data controller.
     */
    public DecryptionAuthorization(final DecryptionBundle authBundle,
            final String dataSubjectId, final String dataControllerId) {
        this.authBundle = authBundle;
        this.dataSubjectId = dataSubjectId;
        this.dataControllerId = dataControllerId;
    }

    /**
     * This method processes the authorization of a decryption PDGuard request
     * based on the predefined guidelines of data subject.
     */
    @Override
    public AuthorizationResult processAuthorization() {
        boolean isAuthorized = AllowableAction.isAuthorized(dataSubjectId,
                dataControllerId, authBundle.getDataType(),
                authBundle.getDataUse());
        authResult = isAuthorized ? AuthorizationResult.ALLOWED :
                AuthorizationResult.DENIED_BY_DATA_SUBJECT;
        return authResult;
    }

    /**
     * This method adds all the information associated with each authorization
     * request to the escrow agent's database.
     *
     * In this way, there is a history with all the requests sent to
     * escrow agent by data controllers and their outcome.
     * Therefore, we can supervise if an authorization procedure
     * was completed successfully.
     *
     * @param authLogApi API responsible for the addition of authorization log.
     */
    @Override
    public void addAuthorizationLog(AuthorizationLogApi authLogApi) {
        authLogApi.addAuthorizationLog(this);
    }

    /**
     * Getter of the authBundle field.
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
