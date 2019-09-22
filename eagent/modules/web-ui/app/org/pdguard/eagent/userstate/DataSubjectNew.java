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

import org.pdguard.eagent.exceptions.EmailNotAvailableException;
import org.pdguard.eagent.exceptions.PasswordsNotMatchException;
import org.pdguard.eagent.exceptions.UsernameNotAvailableException;
import play.data.validation.Constraints;

/**
 * This class represents a user who is requesting to register to escrow agent's
 * web UI.
 *
 * User has to fill a registration form in order to register to escrow agent's
 * web UI. User has to fill registration form with their name (First name and
 * surname), their email and a password (which they have to confirm it by
 * retyping) to enter system when registration is completed.
 *
 * It extends {@link DataSubject} class
 *
 * @author Thodoris Sotiropoulos
 */
public class DataSubjectNew extends DataSubject {
    /** User's first name. This field is required. */
    @Constraints.Required
    private String firstName;

    /** User's surname. This field is required. */
    @Constraints.Required
    private String surname;

    /** Username of data subject. */
    @Constraints.Required
    private String username;

    /** User's password. This field must contain at least 6 character. */
    @Constraints.Required
    @Constraints.MinLength(value = 6)
    private String password;

    /**
     * Password confirmation. This field's value has to be equal to password's
     * value.
     */
    private String repeatedPassword;

    /** Default Constructor. */
    public DataSubjectNew() { }

    /**
     * Initialize a user who is requesting to register to escrow agent's web UI.
     *
     * @param firstName User's first name. This field is required.
     * @param surname User's surname. This field is required.
     * @param email Email of a user. It is a required field. Every user of
     * escrow agent's web UI has to be described by a unique email. Moreover,
     * its value has to follow standard email's pattern in order to be valid.
     * @param username Data subject's username.
     * @param password User's password. This field must contain at least 6
     * character.
     * @param repeatedPassword Password confirmation. This field's value has to
     * be equal to password's value.
     */
    public DataSubjectNew(final String firstName, final String surname,
        final String email, final String username, final String password,
        final String repeatedPassword) {
        super(email);
        this.firstName = firstName;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
    }

    /**
     * Add to escrow agent's web Ui.
     *
     * If user is added, it is considered registered to the system and now they
     * can enter system with the credentials given during his registration.
     * However, there are two scenarios that user's registration cannot be
     * completed.
     *
     * Firstly, registration cannot be completed when user is requesting to
     * register to escrow agent's web UI and their email is not available.
     * Secondly, registration fails when password and confirmation password of a
     * user who is requesting to register escrow agent's web UI, are not equal.
     *
     * @throws EmailNotAvailableException is thrown when user
     * is requesting to register to escrow agent's web UI and their email is not
     * available.
     * @throws PasswordsNotMatchException is thrown when
     * password and confirmation password of a user who is requesting to
     * register escrow agent's web UI, are not equal.
     * @throws UsernameNotAvailableException is thrown when user is requesting
     * to register to escrow agent's web UI and their username is not
     * available.
     */
    public void addUser() throws EmailNotAvailableException,
            PasswordsNotMatchException, UsernameNotAvailableException {
        if (!this.verifyPasswords())
            throw new PasswordsNotMatchException("Passwords do not match");
        if (!this.isEmailAvailable())
            throw new EmailNotAvailableException("Your email already exists");
        if (!this.isUsernameAvailable())
            throw new UsernameNotAvailableException("Your username already"
                    + " exists");
        org.pdguard.eagent.models.DataSubject dataSubject = new org.pdguard
                .eagent.models.DataSubject(username, firstName + " " + surname,
                super.getEmail(), password);
        dataSubject.addDataSubject();
    }

    /**
     * This function tests if there is a registered user with the same username
     * with current user.
     *
     * @return True if there is a registered user with the same username wituh
     * the current user; false otherwise.
     */
    public boolean isUsernameAvailable() {
        return !org.pdguard.eagent.models.DataSubject.isIdAvailable(
                username);
    }

    /**
     * This method checks if password is equal to the value user gave on the
     * password confirmation field of registration form.
     *
     * @return true if two passwords are equal; false otherwise.
     */
    public boolean verifyPasswords() {
        return password.equals(repeatedPassword);
    }

    /**
     * Getter of firstName field.
     *
     * @return User's first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter of firstName.
     *
     * @param firstName User's first name.
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter of surname field.
     *
     * @return User's surname.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Setter of surname field.
     *
     * @param surname User's surname.
     */
    public void setSurname(final String surname) {
        this.surname = surname;
    }

    /**
     * Getter of password field.
     *
     * @return User's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter of password field.
     *
     * @param password User's password.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Getter of repeatedPassword field.
     *
     * @return Password confirmation. This field's value has to be equal to
     * password's value.
     */
    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    /**
     * Setter of repeatedPassword field.
     *
     * @param repeatedPassword Password confirmation. This field's value has to
     * be equal to password's value.
     */
    public void setRepeatedPassword(final String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    /**
     * Getter of username field.
     *
     * @return Username of data subject to this current escrow agent.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter of username field.
     *
     * @param username Username of data subject to this current escrow agent.
     */
    public void setUsername(final String username) {
        this.username = username;
    }
}
