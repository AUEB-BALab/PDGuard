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
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.eagent.logs.AuthorizationProcess;
import org.pdguard.eagent.logs.AuthorizationRequestType;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.models.AuthorizationLog;
import org.pdguard.eagent.models.Client;

import java.util.Date;

/**
 * This class is responsible for the addition of history associated with the
 * authorization procedure of PDGuard requests.
 *
 * @author Thodoris Sotiropoulos
 */
public class AuthorizationLogApiImpl implements AuthorizationLogApi {
    /** Client who requested authorization. */
    private Client client;

    /**
     * Initialize object with the required field.
     *
     * @param client Client who requested authorization.
     */
    public AuthorizationLogApiImpl(final Client client) {
        this.client = client;
    }

    /**
     * This method adds all information associated with the authorization
     * procedure of a decryption PDGuard request based on the predefined
     * guidelines of data subject.
     *
     * @param auth Authorization procedure.
     */
    @Override
    public void addAuthorizationLog(DecryptionAuthorization auth) {
        if (auth.getAuthResult() == null)
            throw new RuntimeException();
        addDecryptionAuthorizationLog(auth.getAuthBundle(),
                auth.getAuthResult(), AuthorizationProcess.DATA_SUBJECT_RULES);
    }

    /**
     * This method adds all information associated with the authorization
     * procedure of an encryption PDGuard request based on the predefined
     * guidelines of data subject.
     *
     * @param auth Authorization procedure.
     */
    @Override
    public void addAuthorizationLog(EncryptionAuthorization auth) {
        if (auth.getAuthResult() == null)
            throw new RuntimeException();
        addEncryptionAuthorizationLog(auth.getAuthBundle(),
                auth.getAuthResult(), AuthorizationProcess.DATA_SUBJECT_RULES);
    }

    /**
     * This method adds all information associated with the authorization
     * procedure of a decryption PDGuard request based on the default
     * authorization rules of escrow agent.
     *
     * @param auth Authorization procedure.
     */
    @Override
    public void addAuthorizationLog(DefaultDecryptionAuthorization auth) {
        addDecryptionAuthorizationLog(auth.getAuthBundle(),
                auth.getAuthResult(), AuthorizationProcess.DEFAULT);
    }

    /**
     * This method adds all information associated with the authorization
     * procedure of a encryption PDGuard request based on the default
     * authorization rules of escrow agent.
     *
     * @param auth Authorization procedure.
     */
    @Override
    public void addAuthorizationLog(DefaultEncryptionAuthorization auth) {
        addEncryptionAuthorizationLog(auth.getAuthBundle(),
                auth.getAuthResult(), AuthorizationProcess.DEFAULT);
    }

    /**
     * Add authorization log associated with a decryption PDGuard request.
     *
     * @param authBundle Authorization bundle of parameters in which escrow
     * agent would grant authorization.
     * @param authResult Result of authorization procedure.
     * @param authProcess Method in which authorization process based on.
     */
    public void addDecryptionAuthorizationLog(
            final DecryptionBundle authBundle,
            final AuthorizationResult authResult,
            final AuthorizationProcess authProcess) {
        AuthorizationLog authLog = new AuthorizationLog(authResult,
                AuthorizationRequestType.READ, authProcess, new Date(),
                authBundle.getDataType(), client);
        authLog.setDataUse(authBundle.getDataUse());
        authLog.setInteractionPurpose(authBundle.getInteractionPurpose());
        authLog.addAuthorizationLog();
    }

    /**
     * Add authorization log associated with an encryption PDGuard request.
     *
     * @param authBundle Authorization bundle of parameters in which escrow
     * agent would grant authorization.
     * @param authResult Result of authorization procedure.
     * @param authProcess Method in which authorization process based on.
     */
    public void addEncryptionAuthorizationLog(
            final EncryptionBundle authBundle,
            final AuthorizationResult authResult,
            final AuthorizationProcess authProcess) {
        AuthorizationLog authLog = new AuthorizationLog(authResult,
                AuthorizationRequestType.UPDATE, authProcess, new Date(),
                authBundle.getDataType(), client);
        authLog.setDataProvenance(authBundle.getDataProvenance());
        authLog.setUpdateField(authBundle.isUpdate());
        authLog.addAuthorizationLog();
    }
}
