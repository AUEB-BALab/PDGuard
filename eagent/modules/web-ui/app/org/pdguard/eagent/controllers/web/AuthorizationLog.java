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

package org.pdguard.eagent.controllers.web;

import com.fasterxml.jackson.databind.JsonNode;
import org.pdguard.eagent.userstate.DataSubjectAuthenticated;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class extends {@link play.mvc.Controller} class and contains public
 * static methods which represent an action.
 * Usually, an action method does not include a return statement. The method
 * exit is done by the invocation of a result method.
 *
 * Actions which are included in this class represent requests for reading,
 * history of authorization requests which are associated with data of a user
 * of a escrow agent's web UI.
 *
 * @author Thodoris Sotiropoulos
 */
public class AuthorizationLog extends Controller {
    /**
     * This returns all authorization logs of a data subject sent in a specific
     * time period.
     *
     * It uses the email of a data subject stored in session and then it
     * retrieves all authorization logs which were sent in a time period
     * defined by parameters.
     *
     * Returns authorization rules in JSON format.
     *
     * @param from Beginning date of time period.
     * @param to End date of time period.
     *
     * @return A JSON Array of authorization logs of a data subject.
     */
    public static Result getAuthorizationLogs(final String from,
            final String to) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate;
        try {
            if (to == null)
                toDate = new Date();
            else
                toDate = dateFormat.parse(to);
            if (from != null)
                fromDate = dateFormat.parse(from);
        } catch (ParseException e) {
            return badRequest(Messages.get("web.misformatted_date"));
        }
        DataSubjectAuthenticated dataSubject = new
                DataSubjectAuthenticated(session("user"));
        JsonNode authLogsJson = Json.toJson(dataSubject.retrieveAuthLogs(
                fromDate, toDate));
        return ok(authLogsJson);
    }
}
