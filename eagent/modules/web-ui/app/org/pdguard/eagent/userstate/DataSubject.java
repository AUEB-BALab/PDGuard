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

package org.pdguard.eagent.userstate;

import play.data.validation.Constraints;

/**
 * This class represents a user of escrow agent's web UI.
 *
 * User is described only by a unique email. User of escrow agent's web UI may
 * be an already registered user or a new user who has to fill a registration
 * form in order to enter system to use operation of application.
 *
 * @author Thodoris Sotiropoulos
 */
public class DataSubject {
    /**
     * Email of a user. It is a required field. Every user of escrow agent's
     * web UI has to be described by a unique email. Moreover, its value has to
     * follow standar email's pattern in order to be valid.
     */
    @Constraints.Required
    @Constraints.Email
    private String email;

    /** Default Constructor. */
    public DataSubject() { }

    /**
     * Initialize a user object with a given email.
     *
     * @param email Email of a user. It is a required field. Every user of
     * escrow agent's web UI has to be described by a unique email. Moreover,
     * its value has to follow standard email's pattern in order to be valid.
     */
    public DataSubject(final String email) {
        this.email = email;
    }

    /**
     * This function tests if there is a registered user with the same email
     * with current user.
     *
     * @return True if there is a registered user with the same email with the
     * current user; false otherwise.
     */
    public boolean isEmailAvailable() {
        return !org.pdguard.eagent.models.DataSubject.isEmailAvailable(email);
    }

    /**
     * Getter of email field.
     *
     * @return Data subject's email.
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Setter of email field.
     *
     * @param email Data subject's email.
     */
    public final void setEmail(final String email) {
        this.email = email;
    }
}
