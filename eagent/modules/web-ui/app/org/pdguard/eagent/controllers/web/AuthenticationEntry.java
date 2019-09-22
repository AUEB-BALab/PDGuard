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

import org.pdguard.eagent.exceptions.EmailNotAvailableException;
import org.pdguard.eagent.exceptions.UsernameNotAvailableException;
import org.pdguard.eagent.userstate.DataSubjectNew;
import org.pdguard.eagent.userstate.DataSubjectNotAuthenticated;
import org.pdguard.eagent.userstate.DataSubject;
import org.pdguard.eagent.exceptions.PasswordsNotMatchException;

import org.pdguard.api.utils.DataType;
import play.data.Form;

import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

import org.pdguard.eagent.views.html.index;
import org.pdguard.eagent.views.html.mainpage;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Stack;

/**
 * This class extends {@link play.mvc.Controller} class and contains public
 * static methods which represent an action.
 * Usually, an action method does not include a return statement. The method
 * exit is done by the invocation of a result method.
 *
 * Actions which are included in this class represent requests for the logging
 * of a user into the escrow agent web UI as well as registration of a user to
 * escrow agent services.
 *
 * @author Thodoris Sotiropoulos
 */
public class AuthenticationEntry extends Controller {
    /** An empty registration form. */
    private static Form<DataSubjectNew> emptyRegistrationForm = Form
            .form(DataSubjectNew.class);

    /** An empty login form. */
    private static Form<DataSubjectNotAuthenticated> emptyLoginForm = Form
            .form(DataSubjectNotAuthenticated.class);

    /**
     * This method reads the parameters of a form for the login operation of a
     * user to the escrow agent web UI.
     *
     * It takes two parameters. First one is user email and the second one is
     * password. Then it searches database if a user with the given credentials
     * exists. If user exists, go to the main page of application, otherwise go
     * to the page with the login form and a message that wrong credentials
     * were given is returned.
     *
     * @return Page to forward if user exists, else go back to the fist page
     * with the appropriate wrong message.
     */
    public static Result authenticateDataSubject() {
        Form<DataSubjectNotAuthenticated> signInForm = Form.form(
                DataSubjectNotAuthenticated.class).bindFromRequest();
        if (signInForm.hasErrors())
            return badRequest(index.render(signInForm, emptyRegistrationForm));
        DataSubjectNotAuthenticated registeredUser = signInForm.get();
        if (registeredUser.isAuthenticated()) {
            session("user", registeredUser.getEmail());
            return redirect(routes.AuthenticationEntry.mainpage());
        }
        signInForm.reject("password", Messages.get("web.login"));
        return badRequest(index.render(signInForm, emptyRegistrationForm));
    }

    /**
     * Go to the page when a user who is not connected or registered to the
     * system first visit. Contains login and registration forms to enter
     * system.
     *
     * @return Initial page of escrow agent web UI.
     */
    public static Result index() {
        return ok(index.render(emptyLoginForm, emptyRegistrationForm));
    }

    /**
     * Go to the mainpage of escrow agent web UI for connected users.
     *
     * @return Mainpage of escrow agent web UI for connected users.
     */
    public static Result mainpage() {
        EnumSet<DataType> dataTypes = EnumSet.allOf(DataType.class);
        Iterator<DataType> iter = dataTypes.iterator();
        Stack<DataType> categories = new Stack<>();
        DataType initial = iter.next();
        String content = putDataTypeHierarchically(initial, initial,
                new StringBuilder("<ul>"), iter, categories);
        if (session().get("user") == null)
            return redirect(routes.AuthenticationEntry.index());
        return ok(mainpage.render(content + "</ul>", session()));
    }

    /**
     * This method reads data from a completed registration form.
     *
     * If all fields of registration form were completed successfully, then it
     * adds user to the escrow agent's database. Therefore, user can enter
     * system or a data controller can use their personal data
     * (encryption/decryption requests).
     *
     * If fields were not completed successfully, then go back to registration
     * form with the appropriate explanatory messages.
     *
     * @return Initial page of escrow agent web UI.
     */
    public static Result registration() {
        Form<DataSubjectNew> registrationForm = Form.form(DataSubjectNew.class)
                .bindFromRequest();
        if (registrationForm.hasErrors())
            return badRequest(index.render(emptyLoginForm, registrationForm));
        DataSubjectNew newUser = registrationForm.get();
        try {
            newUser.addUser();
        } catch (EmailNotAvailableException e) {
            registrationForm.reject("email", e.getMessage());
            return badRequest(index.render(emptyLoginForm, registrationForm));
        } catch (UsernameNotAvailableException e) {
            registrationForm.reject("username", e.getMessage());
            return badRequest(index.render(emptyLoginForm, registrationForm));
        } catch (PasswordsNotMatchException e) {
            registrationForm.reject("repeatedPassword", e.getMessage());
            return badRequest(index.render(emptyLoginForm, registrationForm));
        }
        return redirect(routes.AuthenticationEntry.index());
    }

    /**
     * This methods checks if given email is available for user.
     *
     * It takes user's email as parameters and it looks if a user with the email
     * given as parameter exists exists, then returns message to user
     * accordingly.
     *
     * @param email Email to check if it already exists.
     *
     * @return Message to user, if email is accepted or not.
     */
    public static Result checkEmailAvailability(final String email) {
        DataSubject dataSubject = new DataSubject(email);
        if (dataSubject.isEmailAvailable())
            return ok(Messages.get("web.email_unavailable"));
        else
            return ok(Messages.get("web.email_exists"));
    }

    /**
     * This method is used for the disconnection of a user from the Escrow
     * Agent's web UI.
     *
     * @return Initial page of escrow agent web UI.
     */
    public static Result logout() {
        session().clear();
        return redirect(routes.AuthenticationEntry.index());
    }

    /**
     * This recursive method creates the HTML content with all types of data
     * presented in a hierarchically form.
     *
     * @param previous Previous type of data based on the iterator.
     * @param current Current type of data based on the iterator.
     * @param content HTML content.
     * @param iter Iterator of data types.
     * @param categories Stack contains the branch data categories.
     *
     * @return HTML content representing data types in tree-based format.
     */
    public static String putDataTypeHierarchically(DataType previous,
            DataType current, StringBuilder content, Iterator<DataType> iter,
            Stack<DataType> categories) {
        String prefix = "";
        if (current.getParent() == null)
            categories.push(current);
        if (current.getParent() == previous) {
            if (!categories.contains(previous))
                categories.push(previous);
            prefix = "<ul>";
        } else if (current.getParent() == categories.peek())
            prefix = "";
        else if (current.getParent() != null || current != DataType.PERSONAL) {
            if (current == categories.peek())
                categories.pop();
            categories.pop();
            if (current.getParent() != null || categories.empty())
                prefix = "</ul>";
            else {
                categories.pop();
                prefix = "</ul></ul>";
            }
        }
        content.append(prefix).append("<li id=\"").append(current).append("\">")
                .append(current.toString().replace("_", " "));
        if (iter.hasNext())
            putDataTypeHierarchically(current, iter.next(), content, iter,
                    categories);
        return content.toString();
    }
}
