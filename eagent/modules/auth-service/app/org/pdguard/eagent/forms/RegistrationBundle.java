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

package org.pdguard.eagent.forms;

import play.data.validation.Constraints;

/**
 * This class represents a bundle of parameters of a client registration
 * request.
 *
 * Client registration requires the data controller id, data subject id and
 * application id in order escrow agent can make the association between them.
 *
 * @author Thodoris Sotiropoulos
 */
public class RegistrationBundle {
    /** Id of data subject. This field is required.*/
    @Constraints.Required
    private String dataSubjectId;

    /** Id of data controller. This field is required. */
    @Constraints.Required
    private String dataControllerId;

    /** Id of authorized application. This field is required. */
    @Constraints.Required
    private String authAppId;

    /** Default constructor. */
    public RegistrationBundle() { }

    /**
     * Creates a new registration bundle.
     *
     * @param dataSubjectId ID of data subject.
     * @param dataControllerId ID of data controller.
     * @param authAppId ID of authorized application.
     */
    public RegistrationBundle(final String dataSubjectId,
            final String dataControllerId, final String authAppId) {
        this.dataSubjectId = dataSubjectId;
        this.dataControllerId = dataControllerId;
        this.authAppId = authAppId;
    }

    /**
     * Getter of authAppId field.
     *
     * @return ID of application.
     */
    public String getAuthAppId() {
        return authAppId;
    }

    /**
     * Getter of dataSubject field.
     *
     * @return ID of data subject.
     */
    public String getDataSubjectId() {
        return dataSubjectId;
    }

    /**
     * Getter of dataControllerId field.
     *
     * @return ID of data controller.
     */
    public String getDataControllerId() {
        return dataControllerId;
    }

    /**
     * Setter of dataSubjectId field.
     *
     * @param dataSubjectId ID of dataSubject.
     */
    public void setDataSubjectId(final String dataSubjectId) {
        this.dataSubjectId = dataSubjectId;
    }

    /**
     * Setter of authAppId field.
     *
     * @param authAppId ID of application.
     */
    public void setAuthAppId(final String authAppId) {
        this.authAppId = authAppId;
    }

    /**
     * Setter of dataControllerId field.
     *
     * @param dataControllerId ID of data controller.
     */
    public void setDataControllerId(final String dataControllerId) {
        this.dataControllerId = dataControllerId;
    }
}
