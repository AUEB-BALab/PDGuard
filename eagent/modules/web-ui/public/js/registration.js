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

/**
 * This function creates an AJAX request in order to check the email availability.
 *
 * First, it checks if the input given by user matches pattern of an email
 * address. If yes, it sends an AJAX request to server side to check if given
 * email is available and there is no registered user with that email.
 *
 * Then user is informed with the appropriate explanatory message.
 *
 * @param element Form field which is filled with user's email.
 */
function checkEmail(element) {
    if (validateEmailValue(element))
        $.ajax({
            url: "checks",
            data: {"email": element.val()},
            success: function(data) {
                alertMessages(element, data);
            }
        });
    else
        alertMessages(element, "Given value is invalid.");
}

/**
 * Checks if the two passwords are same and have more than 6 characters.
 * If not it doesn't accept them, and false is returned; true otherwise.
 *
 * Moreover, user is informed with an explanatory message.
 *
 * @param password1 Form field which is filled by user's password.
 * @param password2 Form field which user retypes password.
 * @returns {boolean} If passwords do not match or password contains less than 6
 * character false is returned, true otherwise.
 */
function verifyPasswords(password1, password2) {
    var minChars = 6;
    var accepted = false;
    var message;
    if(password1.val() == password2.val())
        if (password1.val().length >= minChars) {
            accepted = true;
            message = "Passwords match";
        } else
            message = "Password must contain at least 6 characters.";
    else
        if (password1.val() == "")
            message = "Password field is required.";
        else
            message = "Passwords don't match.";
    alertMessages(password2, message);
    return accepted;
}

/**
 * This function checks if the given email by user matches the pattern that all
 * email addresses follow.
 *
 * Then user is informed with the appropriate explanatory message.
 *
 * @param element Form field which is filled by user's email.
 * @returns {boolean} True if given email matches pattern of all email addresses
 * , false otherwise.
 */
function validateEmailValue(element) {
    var pattern = "^([0-9a-zA-Z]+[-._+&amp;])*[0-9a-zA-Z]+@([-0-9a-zA-Z]+[.])+"
            + "[a-zA-Z]{2,6}$";
    var reg = new RegExp(pattern);
    return reg.test(element.val());
}