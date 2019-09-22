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

import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.models.AuthorizationRule;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;

/**
 * This class is responsible for the authorization processing of an encryption
 * PDGuard request.
 *
 * @author Thodoris Sotiropoulos
 */
public abstract class EncryptionRequest implements AuthorizationApi {
    /**
     * Authorization bundle of parameters in which escrow agent would grant
     * authorization.
     */
    protected EncryptionBundle authBundle;

    /** Result of authorization procedure. */
    protected AuthorizationResult authResult;

    /** Person to whom data belongs. */
    protected DataSubject dataSubject;

    /** Organization which stores data. */
    protected DataController dataController;

    /**
     * Creates a new instance with all required fields.
     *
     * @param authBundle Authorization bundle of parameters in which escrow
     * agent would grant authorization.
     * @param dataSubject Data subject to whom data belongs.
     * @param dataController Data controller organization which stores data.
     */
    public EncryptionRequest(final EncryptionBundle authBundle,
            final DataSubject dataSubject,
            final DataController dataController) {
        this.dataSubject = dataSubject;
        this.dataController = dataController;
        this.authBundle = authBundle;
    }

    /**
     * This methods saves authorization rule to the escrow agent's database
     * with the default rule.
     *
     * Default rule does not define any way in which data controller can use
     * the personal data of data subject. Therefore, it is required data subject
     * set the allowable actions which can be performed to their data.
     */
    public void addDefaultAuthorizationRule() {
        AuthorizationRule authRule = AuthorizationRule.authenticate(
                dataSubject.getId(), dataController.getId(),
                authBundle.getDataType());
        if (authRule == null)
            new AuthorizationRule(dataSubject, dataController,
                    authBundle.getDataType()).addDefaultAuthRule();
    }
}
