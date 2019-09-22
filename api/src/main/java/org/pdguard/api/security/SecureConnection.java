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

package org.pdguard.api.security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

/**
 * This class is responsible for the establishment of an encrypted connection
 * with the escrow agent's services using the TLS 1.2 protocol.
 *
 * @author Thodoris Sotiropoulos
 */
public final class SecureConnection {
    /**
     * Keystore with the list of all client certificates. It is used to
     * establish an encrypted SSL connection.
     */
    private KeyStore clientKeyStore;

    /** Keystore to verify if Escrow Agent is trusted. */
    private KeyStore clientTrustStore;

    /** Name of properties file. */
    private static final String PROPERTIES_FILE = "config.properties";

    /** Properties object. */
    private static final Properties PROPERTIES = loadProperties();

    /** Get path of key store. */
    private static final String KEYSTORE = PROPERTIES.getProperty("keystore");

    /** Get path of trust store. */
    private static final String TRUSTSTORE = PROPERTIES.getProperty(
            "truststore");

    /** Get password of key store. */
    private static final String KEYSTORE_PASSWORD = PROPERTIES.getProperty(
            "keystore_pswrd");

    /** Get password of trust store. */
    private static final String TRUSTSTORE_PASSWORD = PROPERTIES.getProperty(
            "truststore_pswrd");

    /** Type of key store. */
    private static final String KEYSTORE_TYPE = "JKS";

    /**
     * Configure a secure connection via TLS protocol initializing all required
     * parts.
     */
    public SecureConnection() {
        loadKeyStore();
        loadTrustStore();
    }

    /** Loads key store according to the configuration file of application. */
    public void loadKeyStore() {
        clientKeyStore = loadKeyStore(KEYSTORE, KEYSTORE_PASSWORD);
    }

    /** Loads trust store according to the configuration file of application. */
    public void loadTrustStore() {
        clientTrustStore = loadKeyStore(TRUSTSTORE, TRUSTSTORE_PASSWORD);
    }

    /**
     * Reads and initializes key store which corresponds to the selected file
     * and password.
     *
     * @param path Path of key store file.
     * @param password Password of key store file.
     * @return KeyStore object in order an encrypted connection with escrow
     * agent can be established using TLS protocol.
     */
    KeyStore loadKeyStore(final String path, final String password) {
        KeyStore keyStore;
        FileInputStream inputStream;
        try {
            keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            inputStream = new FileInputStream(path);
            try {
                keyStore.load(inputStream, password.toCharArray());
            } finally {
                inputStream.close();
            }
        } catch (IOException | NoSuchAlgorithmException
                | CertificateException | KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
        return keyStore;
    }

    /**
     * Establish an encrypted SSL connection. Loads client trust store which
     * contains list of trusted certificates such as escrow agent certificate,
     * and client key store which contains all required client certificate in
     * order a mutual authentication can be completed.
     *
     * @throws java.io.IOException IOException is thrown when a socket
     * specified by Transport Layer Security (TLS) protocol cannot be created.
     *
     * @return Socket specified by Transport Layer Security (TLS) protocol.
     */
    public SSLSocketFactory createSSLSocketConnection() throws IOException {
        TrustManagerFactory trustStore;
        KeyManagerFactory keyStore;
        SSLSocketFactory sslSocket;
        try {
            keyStore = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyStore.init(clientKeyStore, KEYSTORE_PASSWORD
                    .toCharArray());
            trustStore = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustStore.init(clientTrustStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(keyStore.getKeyManagers(), trustStore.getTrustManagers(),
                    null);
            sslSocket = ctx.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException
                | KeyStoreException | UnrecoverableKeyException e) {
            e.printStackTrace();
            throw new IOException();
        }
        return sslSocket;
    }

    /**
     * This method loads the security properties of PDGuard client as defined
     * in the corresponding file.
     *
     * @return Security properties.
     */
    public static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(PROPERTIES_FILE);
            try {
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
