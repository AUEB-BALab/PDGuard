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

import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;

/**
 * This class represents the bundle of information which are required for a
 * PDGuard encryption request.
 *
 * It is associated with a request for an encryption key to encrypt a specific
 * block of a subject's data.
 *
 * @author Thodoris Sotiropoulos
 */
public final class EncryptionBundle extends AuthorizationBundle {
    /** The data's provenance; where the data was obtained from. */
    private DataProvenance dataProvenance;

    /** True if an existing field is updated; false otherwise. */
    private boolean update;

    /**
     * Initializes an encryption bundle with all required information.
     *
     * @param dataType The type of the data that data controller requesting to
     * either encrypt or decrypt.
     * @param dataProvenance The data's provenance; where the data was obtained
     * from.
     * @param update True if an existing field is updated; false otherwise.
     */
    public EncryptionBundle(final DataType dataType,
            final DataProvenance dataProvenance, final boolean update) {
        super(dataType);
        this.dataProvenance = dataProvenance;
        this.update = update;
    }

    /**
     * Getter of dataProvenance field.
     *
     * @return The data's provenance; where the data was obtained from.
     */
    public DataProvenance getDataProvenance() {
        return dataProvenance;
    }

    /**
     * Getter of update field.
     *
     * @return True if an existing field is updated; false otherwise.
     */
    public boolean isUpdate() {
        return update;
    }
}
