/*-
 * Copyright 2009-2014 Diomidis Spinellis, Thodoris Sotiropoulos
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

package org.pdguard.api;

import org.pdguard.api.exceptions.CryptoException;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.model.AuthorizationBundle;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.security.SecureConnection;
import org.pdguard.api.services.AuthorizationService;
import org.pdguard.api.services.EncryptionKeyService;
import org.pdguard.api.services.RequestTokenService;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.InteractionPurpose;
import org.pdguard.api.utils.PDGuardRequestType;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * This class is responsible for the decryption or encryption of data associated
 * with a data subject.
 *
 * @author Diomidis Spinellis, Thodoris Sotiropoulos
 */
public class DataProtection {
    /**
     * A number uniquely identifying the data subject
     * with the specified escrow agent.
     */
    private ClientCredentials clientCredentials;

    /** The DNS name of the escrow agent's web service endpoint. */
    private String escrowAgentName;

    /**
     * Initializes all required parameters to establish a secure connection
     * with the specified escrow agent to obtain encryption key.
     *
     * @param escrowAgentName The DNS name of the escrow agent's web service
     * endpoint.
     * @param clientCredentials Client credentials used by escrow agent to
     * verify request.
     */
    public DataProtection(final String escrowAgentName,
            final ClientCredentials clientCredentials) {
        this.clientCredentials = clientCredentials;
        this.escrowAgentName = escrowAgentName;
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
     * @throws EscrowAgentErrorResponseException when the data subject
     * denies access to the corresponding data or there is an error response
     * from escrow agent.
     * @throws CryptoException (Runtime) when an unexpected
     * error in the decryption occurs.
     *
     * @return The data decrypted.
     */
    public byte[] decryptData(final byte[] encryptedData,
            final DataType dataType, final DataUse dataUse,
            final InteractionPurpose interactionPurpose)
            throws IOException, EscrowAgentErrorResponseException {
        AuthorizationBundle authBundle = new DecryptionBundle(dataType, dataUse,
                interactionPurpose);
        SecretKey key = getEncryptionKey(authBundle, PDGuardRequestType
                .DECRYPTION);
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new CryptoException("Data decryption cannot be completed. An"
                    + " unexpected error occurred.", e.getCause());
        }
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
     * @throws EscrowAgentErrorResponseException when the data subject denies
     * access to the corresponding data or there is an error response from
     * escrow agent.
     * @throws CryptoException (Runtime) when an unexpected
     * error in the encryption occurs.
     *
     * @return The data encrypted.
     */
    public byte[] encryptData(final byte[] cleartextData,
            final DataType dataType, final DataProvenance dataProvenance,
            final boolean update) throws IOException,
            EscrowAgentErrorResponseException {
        AuthorizationBundle authBundle = new EncryptionBundle(dataType,
                dataProvenance, update);
        SecretKey key = getEncryptionKey(authBundle, PDGuardRequestType
                .ENCRYPTION);
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(cleartextData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new CryptoException("Data encryption cannot be completed. An"
                    + " unexpected error occurred.", e.getCause());
        }
    }

    /**
     * This method processes a request to get the encryption key to either
     * encrypt or decrypt a block of data.
     *
     * It requests a request token in order to exchange it with a encryption
     * key. Then, it requests escrow agent to grant authorization for the
     * specified use of data and finally, it gets encryption key.
     *
     * @param authBundle Bundle of parameters required by escrow agent to grant
     * authorization.
     * @param requestType Type of PDGuard request. Either a encryption or
     * decryption request.
     *
     * @return Encryption key to either encrypt or decrypt data.
     *
     * @throws IOException is thrown when the communication
     * with the escrow agent fails.
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * sends back an error response.
     */
    public SecretKey getEncryptionKey(
            final AuthorizationBundle authBundle, final PDGuardRequestType
            requestType) throws IOException, EscrowAgentErrorResponseException {
        EscrowAgent escrowAgent = new EscrowAgent(escrowAgentName);
        SecureConnection secure = new SecureConnection();
        RequestTokenService tokenService = new RequestTokenService(
                clientCredentials, escrowAgent, secure);
        AuthorizationService authService = new AuthorizationService(
                clientCredentials, escrowAgent, secure, authBundle,
                tokenService.getRequestToken(), requestType);
        EncryptionKeyService keyService = new EncryptionKeyService(
                clientCredentials, escrowAgent, secure, authService
                .authorizeRequestToken());
        return keyService.requestEncryptionKey().loadKey();
    }
}
