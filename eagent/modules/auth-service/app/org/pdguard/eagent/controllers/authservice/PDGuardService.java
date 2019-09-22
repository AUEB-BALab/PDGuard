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

import org.pdguard.api.model.AuthorizationBundle;
import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.InteractionPurpose;
import org.pdguard.api.utils.PDGuardRequestType;
import org.pdguard.api.utils.ResponseParser;
import org.pdguard.eagent.forms.ParametersBundle;
import org.pdguard.eagent.forms.ParametersBundleReq;
import org.pdguard.eagent.requests.PDGuardRequest;
import org.pdguard.eagent.requests.PDGuardRequestProcessor;
import org.pdguard.eagent.requests.RequestTokenExchange;
import org.pdguard.eagent.requests.RequestTokenGeneration;
import org.pdguard.eagent.requests.RequestTokenValidation;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * This class extends {@link play.mvc.Controller} class and contains public
 * static methods which represent an action.
 * Usually, an action method does not include a return statement. The method
 * exit is done by the invocation of a result method.
 *
 * This contains request handler methods for the escrow agent's PDGuard
 * services.
 *
 * @author Thodoris Sotiropoulos
 */
public class PDGuardService extends Controller {
    /** Content type of response. */
    private static final String CONT_TYPE = "application/x-www-form-urlencoded;"
            + " charset=utf-8";

    /**
     * A map which is going to be used to retrieve data type associated with
     * the request token used on PDGuard authorization protocol.
     */
    private static final HashMap<String, DataType> DATA_TYPES = new HashMap<>();

    /**
     * This method gets a request for the generation of an unauthorized
     * request token.
     *
     * Before escrow agent generates request token, it verifies request. If
     * verification of request fails, then returns UNAUTHORIZED (401) status
     * code with the corresponding error response in URL encoded format.
     *
     * If verification of request is successful, then it returns request token's
     * credentials to the client.
     */
    public static Result requestToken() {
        DynamicForm df = play.data.Form.form().bindFromRequest();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("clientId", df.get("client_id"));
        parameters.put("signature", df.get("signature"));
        parameters.put("nonce", df.get("nonce"));
        parameters.put("timestamp", df.get("timestamp"));
        Form<ParametersBundle> form = Form.form(ParametersBundle
                .class).bind(parameters);
        if (form.hasErrors())
            return badRequest(ResponseParser.encodeParameter("error",
                    "Missing parameter")).as(CONT_TYPE);
        PDGuardRequest request = new RequestTokenGeneration(form.get());
        PDGuardRequestProcessor processor = new PDGuardRequestProcessor(request);
        processor.processRequest();
        return status(processor.getStatusCode(), processor.getResponse())
                .as(CONT_TYPE);
    }

    /**
     * This method gets a request for the authorizing of a request token.
     *
     * Before escrow agent authorizes request token, it verifies request. If
     * verification of request fails, then returns UNAUTHORIZED (401) status
     * code with the corresponding error response in URL encoded format.
     *
     * If verification of request is successful, then it returns request token's
     * credentials to the client.
     */
    public static Result authorizeRequestToken() {
        DynamicForm df = play.data.Form.form().bindFromRequest();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("clientId", df.get("client_id"));
        parameters.put("signature", df.get("signature"));
        parameters.put("nonce", df.get("nonce"));
        parameters.put("timestamp", df.get("timestamp"));
        parameters.put("requestToken", df.get("request_token"));
        Form<ParametersBundleReq> form = Form.form(ParametersBundleReq
                .class).bind(parameters);
        if (form.hasErrors())
            return badRequest(ResponseParser.encodeParameter("error",
                    "Missing parameter")).as(CONT_TYPE);
        if (df.get("request_type") == null)
            return badRequest(ResponseParser.encodeParameter("error",
                    "Missing parameter")).as(CONT_TYPE);
        PDGuardRequestType requestType = PDGuardRequestType.valueOf(
                df.get("request_type"));
        AuthorizationBundle authBundle;
        try {
            authBundle = getAuthorizationBundle(df,
                requestType);
        } catch (IllegalArgumentException e) {
            return badRequest(ResponseParser.encodeParameter("error",
                    e.getMessage())).as(CONT_TYPE);
        }
        PDGuardRequest pdGuardRequest = new RequestTokenValidation(
                form.get(), authBundle, requestType);
        PDGuardRequestProcessor processor = new PDGuardRequestProcessor(
                pdGuardRequest);
        processor.processRequest();
        if (processor.getStatusCode() != OK)
            return status(processor.getStatusCode(), processor.getResponse())
                    .as(CONT_TYPE);
        DATA_TYPES.put(df.get("request_token"), authBundle.getDataType());
        return redirectAuthorizationServices(df, requestType);

    }

    /**
     * This method initializes the authorization bundle.
     *
     * Authorization bundle is a bundle of parameters required by escrow agent
     * to authorize a request token in order to be exchanged for an encryption
     * key.
     *
     * @param form Parameters of request.
     * @param requestType Type of request; Encryption or Decryption.
     *
     * @return Bundle of parameters.
     */
    public static AuthorizationBundle getAuthorizationBundle(
            final DynamicForm form, final PDGuardRequestType requestType) {
        String dataType = form.get("data_type");
        if (dataType == null)
            throw new IllegalArgumentException("Missing parameter");
        AuthorizationBundle authBundle;
        if (requestType.equals(PDGuardRequestType.ENCRYPTION)) {
            String dataProvenance = form.get("data_provenance");
            String update = form.get("update");
            if (dataProvenance == null || update == null)
                throw new IllegalArgumentException("Missing parameter");
            authBundle = new EncryptionBundle(DataType.valueOf(dataType),
                    DataProvenance.valueOf(dataProvenance),
                    Boolean.valueOf(update));
        } else {
            String dataUse = form.get("data_use");
            String interactionPurpose = form.get("interaction_purpose");
            if (dataUse == null || interactionPurpose == null)
                throw new IllegalArgumentException("Missing parameter");
            authBundle = new DecryptionBundle(DataType.valueOf(dataType),
                    DataUse.valueOf(dataUse),
                    InteractionPurpose.valueOf(interactionPurpose));
        }
        return authBundle;
    }

    /**
     * This method redirects to the corresponding authorization service of
     * escrow agent to authorize the given request token.
     *
     * @param form Parameters of request.
     * @param requestType requestType Type of request; Encryption or Decryption.
     */
    public static Result redirectAuthorizationServices(final DynamicForm form,
            final PDGuardRequestType requestType) {
        if (requestType.equals(PDGuardRequestType.ENCRYPTION))
            return redirect(routes.AuthorizationService.encryptService(
                    form.get("data_type"), form.get("data_provenance"),
                    Boolean.valueOf(form.get("update")),
                    form.get("client_id"), form.get("request_token")));
        else
            return redirect(routes.AuthorizationService.decryptService(
                    form.get("data_type"), form.get("data_use"),
                    form.get("interaction_purpose"), form.get("client_id"),
                    form.get("request_token")));
    }

    /**
     * This method gets a request for the exchange of an authorized request
     * token with an encryption key.
     *
     * Before escrow agent exchanges request token, it verifies request. If
     * verification of request fails, then returns UNAUTHORIZED (401) status
     * code with the corresponding error response in URL encoded format.
     *
     * Moreover, it checks request token to be valid and authorized.
     *
     * If verification of request is successful, then it returns encryption
     * key's content.
     */
    public static Result requestTokenExchange() {
        DynamicForm df = play.data.Form.form().bindFromRequest();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("clientId", df.get("client_id"));
        parameters.put("signature", df.get("signature"));
        parameters.put("nonce", df.get("nonce"));
        parameters.put("timestamp", df.get("timestamp"));
        parameters.put("requestToken", df.get("request_token"));
        Form<ParametersBundleReq> form = Form.form(ParametersBundleReq
                .class).bind(parameters);
        DataType dataType = DATA_TYPES.get(df.get("request_token"));
        if (form.hasErrors() || dataType == null)
            return badRequest(ResponseParser.encodeParameter("error",
                    "Missing parameter")).as(CONT_TYPE);
        PDGuardRequest pdGuardRequest = new RequestTokenExchange(form.get(),
                dataType);
        PDGuardRequestProcessor processor = new PDGuardRequestProcessor(
                pdGuardRequest);
        processor.processRequest();
        int statusCode = processor.getStatusCode();
        if (statusCode == OK)
            DATA_TYPES.remove(df.get("request_token"));
        return status(statusCode, processor.getResponse()).
                as(CONT_TYPE);
    }

    /**
     * Getter of DATA_TYPES field.
     *
     * @return A map which is going to be used to retrieve data type
     * associated with the request token used on PDGuard authorization protocol.
     */
    public static HashMap<String, DataType> getDataTypes() {
        return DATA_TYPES;
    }
}
