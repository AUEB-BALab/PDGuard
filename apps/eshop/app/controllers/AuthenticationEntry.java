/*-
 * Copyright 2014-2015 Thodoris Mavrikis, Thodoris Sotiropoulos
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

package controllers;

import requests.CustomerAuthentication;
import requests.CustomerRegistration;
import exceptions.EshopException;
import forms.Login;
import forms.Registration;
import play.mvc.Controller;
import play.data.Form;
import requests.EshopRequest;
import views.html.index;
import views.html.signin;
import views.html.signup;
import play.mvc.Result;

/**
 * This class extends {@link play.mvc.Controller} class and contains public
 * static methods which represent an action.
 * Usually, an action method does not include a return statement. The method
 * exit is done by the invocation of a result method.
 *
 * Actions which are included in this class represent requests for the logging
 * of a user into the e-shop application as well as registration of to app.
 *
 * @author Thodoris Mavrikis, Thodoris Sotiropoulos
 */
public class AuthenticationEntry extends Controller {
    /** An empty registration form. */
    private static Form<Registration> emptySignupForm = Form
            .form(Registration.class);

    /** An empty login form. */
    private static Form<Login> emptySigninForm = Form.form(Login.class);

    /**
     * Go to the page when a user who is not connected or registered to the
     * system first visit.
     *
     * @return Initial page of e-shop application.
     */

    public static Result index() {
        return ok(index.render());
    }

    /**
     * This method renders the sign up page which contains the registration
     * form.
     *
     * @return the rendered page.
     */
    public static Result signup() {
        return ok(signup.render(emptySignupForm));
    }

    /**
     * This method renders the sign in page which contains the login form for
     * a user to enter system.
     *
     * @return the rendered page.
     */
    public static Result signin() {
        return ok(signin.render(emptySigninForm));
    }

    /**
     * This method reads data from a completed registration form.
     *
     * If all fields of registration form were completed successfully, then it
     * adds user to the e-shop's database. Therefore, user can enter
     * system.
     *
     * If fields were not completed successfully, then go back to registration
     * form with the appropriate explanatory messages.
     *
     * @return Initial page of e-shop application.
     */
    public static Result registerCustomer() {
        Form<Registration> signUpForm = Form.form(Registration
                .class).bindFromRequest();
        if (signUpForm.hasErrors()) {
            return badRequest(signup.render(signUpForm));
        }
        Registration registration = signUpForm.get();
        if (!registration.checkPassword()) {
            signUpForm.reject("repeatedPassword",
                    "Your passwords do not match.");
            return badRequest(signup.render(signUpForm));
        }
        if (!registration.isDataSubjectIdAvailable()) {
            signUpForm.reject("dataSubjectId",
                    "Your ID is not available for use.");
            return badRequest(signup.render(signUpForm));
        }
        EshopRequest custReg = new CustomerRegistration(registration);
        try {
            custReg.process();
            session("customer", registration.getEmail());
            session("customerId", String.valueOf(
                    registration.getDataSubjectId()));
            return redirect(routes.StoreEntry.store());
        } catch (EshopException e) {
            signUpForm.reject("dataSubjectId", e.getMessage());
        }
        return badRequest(signup.render(signUpForm));
    }

    /**
     * This method reads the parameters of a form for the login operation of a
     * user to e-shop application.
     *
     * It takes two parameters. First one is user ID and the second one is
     * password. Then it searches database if a user with the given credentials
     * exists. If user exists, go to the main page of application, otherwise go
     * to the page with the login form and a message that wrong credentials
     * were given is returned.
     *
     * @return Page to forward if user exists, else go back to the fist page
     * with the appropriate wrong message.
     */
    public static Result authenticateCustomer() {
        Form<Login> signinForm = Form.form(Login.class)
                .bindFromRequest();
        if (signinForm.hasErrors())
            return badRequest(signin.render(signinForm));
        EshopRequest authCustomer = new CustomerAuthentication(signinForm.get());
        try {
            authCustomer.process();
            play.Logger.info(signinForm.get().getEmail());
            session("user", signinForm.get().getEmail());
            session("customerId", String.valueOf(signinForm.get()
                    .getDataSubjectId()));
            return redirect(routes.StoreEntry.store());
        } catch (EshopException e) {
            signinForm.reject("password", "Your id or password is"
                    + " incorrect.");
        }
        return badRequest(signin.render(signinForm));
    }

    /**
     * Go to the mainpage of e-shop application for authenticated users.
     *
     * @return Mainpage of e-shop application for authenticated users.
     *
    public static Result mainpage() {
        if (session().get("user") == null)
            return redirect(routes.AuthenticationEntry.index());
        return ok(mainpage.render(session().get("user")));
    }*/

    /**
     * This method is used for the disconnection of a user from the e-shop
     * application.
     *
     * @return Initial page of e-shop application.
     */
   public static Result logout() {
      session().clear();
      return redirect(routes.AuthenticationEntry.index());
   }
}
