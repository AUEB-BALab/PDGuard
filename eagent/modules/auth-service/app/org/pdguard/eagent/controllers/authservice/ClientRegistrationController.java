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

import org.pdguard.api.utils.ResponseParser;
import org.pdguard.eagent.forms.RegistrationBundle;
import org.pdguard.eagent.requests.ClientRegistration;
import org.pdguard.eagent.requests.PDGuardRequest;
import org.pdguard.eagent.requests.PDGuardRequestProcessor;
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
 * This contains request handler methods for the registration of a client.
 *
 * @author Thodoris Sotiropoulos
 */
public class ClientRegistrationController extends Controller {
    /** Content type of response. */
    private static final String CONT_TYPE = "application/x-www-form-urlencoded;"
                + " charset=utf-8";
    /**
     * This method gets parameters describing a client and then adds their
     * on the escrow agent.
     *
     * If all fields of registration form were completed successfully, then it
     * adds client to the escrow agent's database and sends back client's
     * credentials. Therefore, data controller can issue PDGuard requests via
     * the registered application to obtain access to data of data subject
     * defined by this client.
     *
     * If fields were not completed successfully, then it sends back an error
     * response in URL encoded format.
     */
    public static Result registerClient() {
        DynamicForm df = play.data.Form.form().bindFromRequest();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("dataSubjectId", df.get("data_subject_id"));
        parameters.put("dataControllerId", df.get("data_controller_id"));
        parameters.put("authAppId", df.get("auth_app_id"));
        Form<RegistrationBundle> form = Form.form(RegistrationBundle.class).
                bind(parameters);
        if (form.hasErrors())
            return badRequest(ResponseParser.encodeParameter("error",
                    "Missing parameter")).as(CONT_TYPE);
        PDGuardRequest pdGuardRequest = new ClientRegistration(form.get());
        PDGuardRequestProcessor processor = new PDGuardRequestProcessor(
                pdGuardRequest);
        processor.processRequest();
        return status(processor.getStatusCode(), processor.getResponse())
                .as(CONT_TYPE);
    }
}
