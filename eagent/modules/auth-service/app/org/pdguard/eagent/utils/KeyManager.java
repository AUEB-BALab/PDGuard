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

package org.pdguard.eagent.utils;

import org.pdguard.api.exceptions.CryptoException;
import org.pdguard.api.exceptions.UnretrievableKeyException;
import org.pdguard.api.model.EncryptionKey;
import org.pdguard.api.utils.DataType;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.pdguard.eagent.models.Key;
import play.Play;
import play.i18n.Messages;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.apache.commons.codec.binary.Hex.encodeHex;

/**
 * This class defines a Key Manager.
 *
 * Key Manager is responsible for the retrieval, management and construction of
 * the right keys in order to be sent to the upstream party.
 *
 * A master key in key hierarchy is used to decrypt and encrypt the encryption
 * keys which are used for the construction of the keys which are responsible
 * for the encryption and decryption of piece of data associated with data
 * subjects.
 *
 * When an entity wants to have access to encrypted bulk of data, it requests
 * escrow agent for the encryption key in order to decrypt them. Escrow agent
 * retrieves secret keys which are assigned to a data controller and a data
 * subject and decrypts them with a master key. Then, it gives them as an input
 * in a SHA-256 hash function with data type and constructs the final key which
 * is going to be used for the encryption or decryption of data.
 *
 * @author Thodoris Sotiropoulos
 */
public final class KeyManager {
    /** Path to master key file. */
    private static final String MASTER_KEY_PATH = "/keys/master_key";

    /** Algorithm used for the encryption/decryption. */
    private static final String CRYPTOGRAPHIC_ALGORITHM = "AES";

    /** Cryptographic hash algorithm. */
    private static final String HASH_ALGORITHM = "SHA-256";

    /** Hide constructor. */
    private KeyManager() { }

    /**
     * Restores master key by reading its content in sequence of bytes from the
     * file which stores it. Then, symmetric key object based on AES
     * cryptography system is returned.
     *
     * If master key cannot be retrieved, null value is returned.
     *
     * @return Symmetric key object based on AES cryptography system.
     */
    static SecretKey loadMasterKey() {
        String path = Play.application().path() + MASTER_KEY_PATH;
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(path));
            return new SecretKeySpec(keyBytes, CRYPTOGRAPHIC_ALGORITHM);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * This method encrypts an Encryption key.
     *
     * An encryption key is encrypted by master key. This operation is done when
     * encryption key is needed to be stored in escrow agent's database
     * encrypted.
     *
     * So, master key is used to store encryption key safely.
     *
     * @throws CryptoException when an unexpected error in the encryption
     * occurs.
     *
     * @param key Encryption key which is used by data controllers to
     * encrypt and decrypt data of a subject.
     *
     * @return Encrypted encryption key.
     */
    static byte[] encryptKey(final byte[] key) {
        SecretKey masterKey = loadMasterKey();
        try {
            Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, masterKey);
            return cipher.doFinal(key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new CryptoException(Messages.get("crypto.error"),
                    e.getCause());
        }
    }

    /**
     * This method decrypts an Encryption key.
     *
     * An encryption key is decrypted by master key. This operation is done when
     * encryption key is needed to be decrypted in order to be sent it back to
     * the data controller who requests it for decryption or encryption of
     * personal data of a subject.
     *
     * So, master key is used to decrypt it, because encryption keys are stored
     * encrypted in escrow agent's database.
     *
     * @throws CryptoException when an unexpected error in the encryption
     * occurs.
     *
     * @param encryptedKey Encryption key which is used by data controllers to
     * encrypt and decrypt data of a subject.
     *
     * @return Decrypted encryption key.
     */
    static byte[] decryptKey(final byte[] encryptedKey) {
        SecretKey masterKey = loadMasterKey();
        try {
            Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, masterKey);
            return cipher.doFinal(encryptedKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new CryptoException(Messages.get("crypto.error"),
                    e.getCause());
        }
    }

    /**
     * This method generates and then adds a new encryption key for the given
     * data subject and data controller.
     *
     * Generated key has to be first encrypted by Master key before its storage
     * to the escrow agent's database.
     *
     * @param dataSubject Data subject to whom data belongs.
     * @param dataController Data controller who is requesting access to a
     * block of data.
     */
    public static void addEncryptionKey(final DataSubject dataSubject,
            final DataController dataController) {
        Key key = new Key(dataSubject,
                dataController, new Date());
        key.setKeyContent(encryptKey(key.getEncodedKeyContent()));
        key.addKey();
    }

    /**
     * This method requests for an encryption key in order to be sent back to
     * the data controller who requested escrow agent for it.
     *
     * This method implements the following scenario.
     *
     * A data controller may request for an encryption key in order to
     * encrypt data of a subject. In this case encryption key must be decrypted
     * using the Master Key.
     *
     * @param dataSubjectId Data subject to whom data belongs.
     * @param dataControllerId Data controller who is requesting access to a
     * block of data.
     *
     * @throws UnretrievableKeyException when encryption key cannot be used or
     * it cannot be retrieved.
     *
     * @return Requested encryption key in decrypted format.
     */
    public static EncryptionKey requestEncryptionKey(final String dataSubjectId,
            final String dataControllerId, final DataType dataType)
            throws UnretrievableKeyException {
        Key key = Key.loadKey(
                dataSubjectId, dataControllerId);
        if (key == null)
            throw new UnretrievableKeyException(Messages.get("key.unretrievable"));
        if (key.isExpired(key.getValidFrom(),
                key.getValidTo()))
            throw new UnretrievableKeyException(Messages.get("key.expired"));
        String decryptedKey = String.valueOf(encodeHex(decryptKey(
                key.getEncodedKeyContent())));
        byte[] hash = calculateHash(decryptedKey, dataType);
        assert hash != null;
        return buildEncryptionKey(hash);
    }

    /**
     * This method builds a new symmetric key from the given hash value.
     *
     * @param hash Hash value which is used to generate a new symmetric key.
     *
     * @return Encryption key.
     */
    public static EncryptionKey buildEncryptionKey(final byte[] hash) {
        SecretKey secKey = new SecretKeySpec(hash, CRYPTOGRAPHIC_ALGORITHM);
        return new EncryptionKey(String.valueOf(encodeHex(secKey.getEncoded())));
    }

    /**
     * This method calculates a hash value using SHA-256 algorithm from the
     * concatenation of cryptographic key stored in escrow agent and type of
     * data which is going to be used.
     *
     * @param key Decrypted key which corresponds to a data subject and a data
     * controller.
     * @param dataType Type of data which key is going to be used.
     *
     * @return Hash value of key and data type using SHA-256 hash function.
     */
    public static byte[] calculateHash(final String key,
            final DataType dataType) {
        String valueToHash = key + "&" + dataType.toString();
        MessageDigest hash;
        try {
            hash = MessageDigest.getInstance(HASH_ALGORITHM);
            hash.update(valueToHash.getBytes("UTF-8"));
            return hash.digest();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
