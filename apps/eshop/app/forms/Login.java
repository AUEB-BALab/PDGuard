/*-
 * Copyright 2014-2016 Thodoris Mavrikis, Thodoris Sotiropoulos
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
package forms;

/**
 * This class represents a login form.
 *
 * @author Thodoris Sotiropoulos
 */
public class Login {
    /**
     * ID of customer. It is the same with the ID to their trusted escrow agent.
     */
    private String dataSubjectId;

    /** Customer's password to enter e-shop. */
    private String password;

    /** Customer's email. */
    private String email;

    /** Default Constructor. */
    public Login() { }

    /**
     * Creates a new Login form.
     *
     * @param dataSubjectId ID of customer. It is the same with the ID to their
     * trusted escrow agent.
     * @param password Customer's password to enter e-shop.
     */
    public Login(final String dataSubjectId, final String password) {
        this.dataSubjectId = dataSubjectId;
        this.password = password;
    }

    /**
     * Getter of dataSubjectId form.
     *
     * @return ID of customer. It is the same with the ID to their
     * trusted escrow agent.
     */
    public String getDataSubjectId() {
        return dataSubjectId;
    }

    /**
     * Setter of dataSubjectId field.
     *
     * @param dataSubjectId ID of customer. It is the same with the ID to their
     * trusted escrow agent.
     */
    public void setDataSubjectId(final String dataSubjectId) {
        this.dataSubjectId = dataSubjectId;
    }

    /**
     * Getter of password field.
     *
     * @return Customer's password to enter e-shop.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter of password field.
     *
     * @param password Customer's password to enter e-shop.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Getter of email field.
     *
     * @return Customer's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter of email field.
     *
     * @param email Customer's email.
     */
    public void setEmail(final String email) {
        this.email = email;
    }
}
