/*-
 * Copyright 2009-2014 Diomidis Spinellis
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

package org.pdguard.api.exceptions;

/**
 * This exception is thrown when there are cryptography problems.
 *
 * Cryptography problems may occur during the initialization of the
 * cryptographic infrastructure, encryption, or decryption.  Normally
 * these should not occur unless there is a programming error or
 * system misconfiguration.  Hence this exception need not be caught.
 *
 * @author Diomidis Spinellis
 */
public class CryptoException extends RuntimeException {
    /**
     * Construct a new CryptException with the specified explanatory
     * message.
     *
     * @param message the detail message.
     */
    public CryptoException(final String message) {
        super(message);
    }

    /**
     * Construct a new CryptException with the specified explanatory
     * message and cause.
     *
     * @param message the detail message.
     * @param cause another exception, which indicates the underlying cause.
     */
    public CryptoException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct a new CryptException with the specified cause.
     *
     * @param cause another exception, which indicates the underlying cause.
     */
    public CryptoException(final Throwable cause) {
        super(cause);
    }
}
