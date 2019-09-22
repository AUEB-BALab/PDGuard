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

/**
 * This class represents a registered user of escrow agent's web UI.
 *
 * User who is registered to the system he has to give their credentials
 * (email address and password) to enter system in order to have potential to
 * use operations offered by escrow agent.
 *
 * It extends {@link DataSubject} class.
 *
 * @author Thodoris Sotiropoulos
 */
public final class DataSubjectNotAuthenticated extends DataSubject {
    /** User's password to enter system. */
    private String password;

    /** Default Constructor. */
    public DataSubjectNotAuthenticated() { }

    /**
     * Initialize a registered user.
     *
     * They are described by a email and a password to enter system.
     *
     * @param email Email of a user. It is a required field. Every user of
     * escrow agent's web UI has to be described by a unique email. Moreover,
     * its value has to follow standard email's pattern in order to be valid.
     * @param password User's password to enter system.
     */
    public DataSubjectNotAuthenticated(final String email, final String
            password) {
        super(email);
        this.password = password;
    }

    /**
     * This method checks if there is a registered user with the credentials
     * (email address and password) which are specified in object of class.
     *
     * This method is for the login operation in order to validate given email
     * and password. In this way user is authenticated.
     *
     * @return true if user gives the right credentials, false otherwise.
     */
    public boolean isAuthenticated() {
        return org.pdguard.eagent.models.DataSubject.isAuthenticated(getEmail(),
                password);
    }

    /**
     * Getter of password field.
     *
     * @return User's password to enter system.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter of password field.
     *
     * @param password User's password to enter system.
     */
    public void setPassword(final String password) {
        this.password = password;
    }
}
