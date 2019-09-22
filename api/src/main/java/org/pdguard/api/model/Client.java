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

package org.pdguard.api.model;

/**
 * This class represents a client requesting escrow agent for its services.
 * It is associated with one data subject, with one application which handles
 * data of the specified data subject, one data controller who serves
 * application and address where application is running.
 *
 * @author Thodoris Sotiropoulos
 */
public final class Client {
    /**
     * A String uniquely identifying the data subject
     * with the specified escrow agent.
     */
    private String dataSubjectId;

    /**
     * A String uniquely identifying the data controller
     * with the specified escrow agent.
     */
    private String dataControllerId;

    /**
     * A String uniquely identifying the authorized application requesting
     * escrow agent.
     */
    private String appId;

    /**
     * Initialize a client with all required information.
     *
     * @param dataSubjectId A String uniquely identifying the data subject
     * with the specified escrow agent.
     * @param dataControllerId A String uniquely identifying the data controller
     * with the specified escrow agent.
     * @param appId A String uniquely identifying the authorized application
     * requesting escrow agent.
     */
    public Client(final String dataSubjectId, final String dataControllerId,
            final String appId) {
        this.dataSubjectId = dataSubjectId;
        this.dataControllerId = dataControllerId;
        this.appId = appId;
    }

    /**
     * Check that class fields are not null. If null, then this methods throws
     * a NullPointerException.
     *
     * @throws NullPointerException is thrown when class fields are null.
     */
    public void checkNotNull() {
        if (dataSubjectId == null || dataControllerId == null
                || appId == null)
            throw new NullPointerException("Parameters which describe"
                    + " a client cannot be null");
    }

    /**
     * Getter of dataSubjectId field.
     *
     * @return A String uniquely identifying the data subject with the specified
     * escrow agent.
     */
    public String getDataSubjectId() {
        return dataSubjectId;
    }

    /**
     * Getter of dataControllerId field.
     *
     * @return A String uniquely identifying the data controller with the
     * specified escrow agent.
     */
    public String getDataControllerId() {
        return dataControllerId;
    }

    /**
     * Getter of appId field.
     *
     * @return A String uniquely identifying the authorized application
     * requesting escrow agent.
     */
    public String getAppId() {
        return appId;
    }
}
