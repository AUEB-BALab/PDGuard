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

package org.pdguard.eagent.controllers.authservice;

import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.InteractionPurpose;
import org.pdguard.api.utils.ResponseParser;
import org.pdguard.eagent.auth.AuthorizationApi;
import org.pdguard.eagent.auth.DecryptionAuthorization;
import org.pdguard.eagent.auth.DefaultDecryptionAuthorization;
import org.pdguard.eagent.auth.DefaultEncryptionAuthorization;
import org.pdguard.eagent.auth.EncryptionAuthorization;

import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.requests.PDGuardRequest;
import org.pdguard.eagent.requests.PDGuardRequestProcessor;
import org.pdguard.eagent.requests.RequestTokenAuthorization;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashMap;

/**
 * This class extends {@link play.mvc.Controller} class and contains public
 * static methods which represent an action.
 * Usually, an action method does not include a return statement. The method
 * exit is done by the invocation of a result method.
 *
 * This contains request handler methods for the authorization requests for
 * the decryption and encryption of data.
 *
 * @author Thodoris Sotiropoulos
 */
public class AuthorizationService extends Controller {
    /** Content type of response. */
    private static final String CONT_TYPE = "application/x-www-form-urlencoded;"
            + " charset=utf-8";

    /**
     * Gets request from a data controller and then checks if he has the
     * authorization to access encrypted data. If yes, then authorizes given
     * request token and sends it back.
     *
     * Authorization processing is issued according to the authorization rules
     * defined by the data subjects themselves via the escrow agent's web UI.
     *
     * @param dataType The type of the data to decrypt.
     * @param dataUse The intended use of the decrypted data.
     * @param interactionPurpose The purpose of the requested interaction.
     * @param clientId ID of client who 's requesting authorization.
     * @param requestToken ID of request token which is going to be authorized.
     */
    public static Result decryptService(final String dataType,
            final String dataUse, final String interactionPurpose,
            final String clientId, final String requestToken) {
        Client client = Client.authenticate(clientId);
        if (client == null)
            return unauthorized(ResponseParser.encodeParameter(
                    "authentication_error", "Unauthenticated client"))
                    .as(CONT_TYPE);
        AuthorizationApi authApi = new DecryptionAuthorization(
                new DecryptionBundle(DataType.valueOf(dataType),
                        DataUse.valueOf(dataUse),
                        InteractionPurpose.valueOf(interactionPurpose)),
                        client.getDataSubject().getId(),
                        client.getDataController().getId());
        return processRequest(new RequestTokenAuthorization(client, authApi,
                requestToken));
    }

    /**
     * Gets request from a data controller and then checks if he has the
     * authorization to access encrypted data. If yes, then authorizes given
     * request token and sends it back.
     *
     * Authorization processing is issued according to the default authorization
     * rules based on the escrow agent's policy.
     *
     * @param dataType The type of the data to decrypt.
     * @param dataUse The intended use of the decrypted data.
     * @param interactionPurpose The purpose of the requested interaction.
     * @param clientId ID of client who 's requesting authorization.
     * @param requestToken ID of request token which is going to be authorized.
     */
    public static Result decryptServiceDefaultAuth(final String dataType,
            final String dataUse, final String interactionPurpose,
            final String clientId, final String requestToken) {
        Client client = Client.authenticate(clientId);
        if (client == null)
            return unauthorized(ResponseParser.encodeParameter(
                    "authentication_error", "Unauthenticated client"))
                    .as(CONT_TYPE);
        AuthorizationApi authApi = new DefaultDecryptionAuthorization(
                new DecryptionBundle(DataType.valueOf(dataType),
                        DataUse.valueOf(dataUse),
                        InteractionPurpose.valueOf(interactionPurpose)));
        return processRequest(new RequestTokenAuthorization(client, authApi,
                requestToken));
    }

    /**
     * Gets request from a data controller and then checks if he has the
     * authorization to access encrypt data of a data subject.
     * If yes, then authorizes given request token and sends it back.
     *
     * If data controller does not have the authorization to do such an action
     * escrow agent denies to authorize request token.
     *
     * @param dataType The type of the data to encrypt.
     * @param dataProvenance The data's provenance; where the data was obtained
     * from.
     * @param update True if an existing field is updated; false otherwise.
     * @param clientId ID of client who 's requesting authorization.
     * @param requestToken ID of request token which is going to be authorized.
     */
    public static Result encryptService(final String dataType,
            final String dataProvenance, final boolean update,
            final String clientId, final String requestToken) {
        Client client = Client.authenticate(clientId);
        if (client == null)
            return unauthorized(ResponseParser.encodeParameter(
                    "authentication_error", "Unauthenticated client"))
                    .as(CONT_TYPE);
        AuthorizationApi authApi = new EncryptionAuthorization(
                new EncryptionBundle(DataType.valueOf(dataType),
                        DataProvenance.valueOf(dataProvenance),
                        update), client.getDataSubject(),
                client.getDataController());
        return processRequest(new RequestTokenAuthorization(client, authApi,
                requestToken));
    }

    /**
     * Gets request from a data controller and then checks if he has the
     * authorization to access encrypt data of a data subject.
     * If yes, then authorizes given request token and sends it back.
     *
     * Authorization processing is issued according to the default authorization
     * rules based on the escrow agent's policy.
     *
     * If data controller does not have the authorization to do such an action
     * escrow agent denies to authorize request token.
     *
     * @param dataType The type of the data to encrypt.
     * @param dataProvenance The data's provenance; where the data was obtained
     * from.
     * @param update True if an existing field is updated; false otherwise.
     * @param clientId ID of client who 's requesting authorization.
     * @param requestToken ID of request token which is going to be authorized.
     */
    public static Result encryptServiceDefaultAuth(final String dataType,
            final String dataProvenance, final boolean update,
            final String clientId, final String requestToken) {
        Client client = Client.authenticate(clientId);
        if (client == null)
            return unauthorized(ResponseParser.encodeParameter(
                    "authentication_error", "Unauthenticated client"))
                    .as(CONT_TYPE);
        AuthorizationApi authApi = new DefaultEncryptionAuthorization(
                new EncryptionBundle(DataType.valueOf(dataType),
                        DataProvenance.valueOf(dataProvenance),
                        update), client.getDataSubject(),
                client.getDataController());
        return processRequest(new RequestTokenAuthorization(client, authApi,
                requestToken));
    }

    static Result processRequest(final PDGuardRequest pdGuardRequest) {
        PDGuardRequestProcessor processor = new PDGuardRequestProcessor(
                pdGuardRequest);
        processor.processRequest();
        return status(processor.getStatusCode(), processor.getResponse())
                .as(CONT_TYPE);
    }
}
