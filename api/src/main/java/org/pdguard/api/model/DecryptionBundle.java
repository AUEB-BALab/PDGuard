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
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;

/**
 * This class represents the bundle of information which are required for a
 * PDGuard decryption request.
 *
 * It is associated with a request for an encryption key to decrypt a specific
 * block of a subject's data.
 *
 * @author Thodoris Sotiropoulos
 */
public final class DecryptionBundle extends AuthorizationBundle {
    /** The intended use of data. */
    private DataUse dataUse;

    /** The purpose of the requested interaction. */
    private InteractionPurpose interactionPurpose;

    /**
     * Initializes a decryption bundle with all required information.
     *
     * @param dataType The type of the data that data controller requesting to
     * either encrypt or decrypt.
     * @param dataUse The intended use of data.
     * @param interactionPurpose The purpose of the requested interaction.
     */
    public DecryptionBundle(final DataType dataType, final DataUse dataUse,
            final InteractionPurpose interactionPurpose) {
        super(dataType);
        this.dataUse = dataUse;
        this.interactionPurpose = interactionPurpose;
    }

    /**
     * Getter of dataUse field.
     *
     * @return The intended use of data.
     */
    public DataUse getDataUse() {
        return dataUse;
    }

    /**
     * Getter of interactionPurpose field.
     *
     * @return The purpose of the requested interaction.
     */
    public InteractionPurpose getInteractionPurpose() {
        return interactionPurpose;
    }
}
