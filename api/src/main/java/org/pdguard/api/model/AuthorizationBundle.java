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

import org.pdguard.api.utils.DataType;

/**
 * This class represents the bundle of information which are required for all
 * types of PDGuard requests. Either a decryption request or an encryption
 * request.
 *
 * @author Thodoris Sotiropoulos
 */
public class AuthorizationBundle {
    /**
     * The type of the data that data controller requesting to either encrypt
     * or decrypt.
     */
    private DataType dataType;

    /**
     * Initializes an authorization bundle with all required information.
     *
     * @param dataType The type of the data that data controller requesting to
     * either encrypt or decrypt.
     */
    public AuthorizationBundle(final DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Getter of dataType field.
     *
     * @return The type of the data that data controller requesting to
     * either encrypt or decrypt.
     */
    public final DataType getDataType() {
        return dataType;
    }
}
