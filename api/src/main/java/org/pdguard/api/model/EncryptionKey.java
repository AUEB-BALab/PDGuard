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

import org.apache.commons.codec.DecoderException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.apache.commons.codec.binary.Hex.decodeHex;

/**
 * This class represents an encryption key used to either encrypt or decrypt
 * a block of data.
 *
 * @author Thodoris Sotiropoulos
 */
public final class EncryptionKey {
    /** Encryption key in an encoded string format. */
    private String encryptionKey;

    /** Default constructor. */
    public EncryptionKey() { }

    /**
     * Creates a new Encryption key.
     *
     * @param encryptionKey Encryption key in an encoded string format.
     */
    public EncryptionKey(final String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * Restores encryption key by decrypting String content to a sequence of
     * bytes encoded to hexadecimal system.
     *
     * If string content cannot be decoded, null value is returned.
     *
     * @return Encryption key.
     */
    public SecretKey loadKey() {
        byte[] encoded;
        try {
            encoded = decodeHex(encryptionKey.toCharArray());
        } catch (DecoderException e) {
            return null;
        }
        return new SecretKeySpec(encoded, "AES");
    }

    /**
     * Getter of encryptionKey field.
     *
     * @return Encryption key in an encoded string format.
     */
    public String getEncryptionKey() {
        return encryptionKey;
    }

    /**
     * Setter of encryptionKey field.
     *
     * @param encryptionKey Encryption key in an encoded string format.
     */
    public void setEncryptionKey(final String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}
