/*-
 * Copyright 2014-2015 Thodoris Mavrikis, Thodoris Sotiropoulos
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

package security;

import org.pdguard.api.exceptions.AccessDeniedException;
import org.pdguard.api.DataProtection;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.InteractionPurpose;

import org.pdguard.api.model.ClientCredentials;

import java.io.IOException;

/**
 * This class is responsible for the connection of current app with escrow agent
 * for the encryption and decryption of personal data which belongs to a
 * specific data subject.
 *
 * @author Thodoris Sotiropoulos, Thodoris Mavrikis
 */
public class CryptographyController {
    /**
     * The EscrowAgentConnection which is responsible for the encryption and
     * decryption of data.
     */
    protected DataProtection dataProtection;

    /**
     * Initialize object of PDGuard API which is responsible for the encryption
     * and decryption of data with all required information.
     *
     * @param eagent Name of trusted escrow agent of customer.
     * @param clientCredentials Client credentials which are required to issue
     * PDGuard requests.
     */
    public CryptographyController(final String eagent,
            final ClientCredentials clientCredentials) {
        dataProtection = new DataProtection(eagent, clientCredentials);
    }

    /**
     * Encrypt the specified data block.
     *
     * Through this method data subjects can control and audit the types
     * of personal data stored by a data controller.
     * Requests can support two scenarios. First, the data subject can
     * deny the storage of particular data types (e.g. mobile phone). Second,
     * the data subject can lock-down a particular data type (e.g. home
     * address) to prevent fraud.
     *
     * @param cleartextData The data to encrypt.
     * @param dataType The type of the data to encrypt.
     * @param dataProvenance The data's provenance; where the data was obtained
     * from.
     * @param update True if an existing field is updated; false otherwise.
     *
     * @throws java.io.IOException IOException is thrown when the communication
     * with the escrow agent fails.
     * @throws AccessDeniedException when the data subject
     * denies access to the corresponding data.
     *
     * @return The data encrypted.
     */
    public byte[] encrypt(final byte[] cleartextData, final DataType dataType,
            final DataProvenance dataProvenance, final boolean update)
            throws IOException, EscrowAgentErrorResponseException {
        return dataProtection.encryptData(cleartextData, dataType,
                dataProvenance, update);
    }

    /**
     * Decrypt the specified data block.
     *
     * Through this method data subjects can control and audit how the data
     * controller uses their personal data.
     *
     * @param encryptedData The data to decrypt.
     * @param dataType The type of the data to decrypt.
     * @param dataUse The intended use of the decrypted data.
     * @param interactionPurpose The purpose of the requested interaction.
     *
     * @throws java.io.IOException IOException is thrown when the communication
     * with the escrow agent fails.
     * @throws AccessDeniedException when the data subject
     * denies access to the corresponding data.
     *
     * @return The data decrypted.
     */
    public byte[] decrypt(final byte[] encryptedData, final DataType dataType,
            final DataUse dataUse,
            final InteractionPurpose interactionPurpose)
            throws IOException, EscrowAgentErrorResponseException {
        return dataProtection.decryptData(encryptedData, dataType, dataUse,
                interactionPurpose);
    }
}
