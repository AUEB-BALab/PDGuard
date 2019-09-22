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

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * This class is responsible for the generation of credentials (unique id and
 * random HMAC-SHA1 key) required for the verification of a client or a request
 * token.
 *
 * @author Thodoris Sotiropoulos
 */
public final class CredentialsGenerator {
    /** Hide util class constructor. */
    private CredentialsGenerator() { }
    /**
     * Generates a random unique ID.
     *
     * @return Random unique ID.
     */
    public static String generateUniqueID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a shared random HMAC-SHA1 key used for the verification and
     * signing of PDGuard requests.
     *
     * @return A shared random HMAC-SHA1 key.
     */
    public static byte[] generateHmacSHA1Secret() {
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
        return keyGen.generateKey().getEncoded();
    }
}
