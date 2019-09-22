package org.pdguard.eagent.ssl;

import play.Play;
import play.server.ApplicationProvider;
import play.server.SSLEngineProvider;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * This class configures SSL certificates on this current application.
 *
 * By default, Play will generate itself a self-signed certificate, however
 * typically this will not be suitable for serving a website.
 *
 * For this current site a customized SSLEngine is required, because a mutual
 * authentication is required between client and server according to the TLS
 * (Transport Layer Security) protocol.
 *
 * @author Thodoris Sotiropoulos
 */
public final class CustomSSLEngineProvider implements SSLEngineProvider {
    /**
     * Provides information about a Play Application running inside a Play
     * server.
     */
    private ApplicationProvider appProvider;

    /** Path to escrow agent's keystore file. */
    private static final String KEYSTORE = "/../../certs/agent.keystore";

    /** Path to escrow agent's trust store file. */
    private static final String TRUST_STORE = "/../../certs/agent.keystore";

    /** Path to password for escrow agent's keystore file. */
    private static final String PSWRD = "/../../certs/pswrd";

    /**
     * Initializes context which play application is running.
     *
     * @param appProvider Provides information about a Play Application running
     * inside a Play server.
     */
    public CustomSSLEngineProvider(final ApplicationProvider appProvider) {
        this.appProvider = appProvider;
    }

    /**
     * Connects to escrow agent's key store file with the list of private keys
     * in order an encrypted communication can be established with a client.
     *
     * @return InputStream object for reading data of key store file.
     * @throws java.io.FileNotFoundException If file cannot be found at the
     * given path.
     */
    public InputStream readKeyInputStream() throws FileNotFoundException {
        return new FileInputStream(Play.application().path() + KEYSTORE);
    }

    /**
     * Connects to escrow agent's trust store file with the list of public keys
     * of the entities that it trusts them in order an encrypted communication
     * can be established with a client.
     *
     * @return InputStream object for reading data of trust store file.
     * @throws java.io.FileNotFoundException If file cannot be found at the
     * given path.
     */
    public InputStream readTrustInputStream() throws FileNotFoundException {
        return new FileInputStream(Play.application().path() + TRUST_STORE);
    }

    /**
     * This method reads and returns the password for the escrow agent's
     * keystore file.
     *
     * @return Password for escrow agent's keystore file.
     *
     * @throws IOException If file cannot be found at the
     * given path.
     */
    public String readPassword() throws IOException {
        Path path = Paths.get(Play.application().path() + PSWRD);
        return new String(Files.readAllBytes(path), "UTF-8");
    }

    /**
     * Initializes KeyManagers which are responsible for managing the private
     * keys included on key store file of escrow agent in order a client can
     * authenticate server.
     *
     * @return An Array of private keys including on key store file of escrow
     * agent.
     * @throws java.io.IOException IOException may be thrown when Input Stream
     * is going to be closed.
     */
    public KeyManager[] readKeyManagers() throws IOException {
        KeyStore keyStore;
        KeyManagerFactory kmf = null;
        char[] keyPassword = readPassword().toCharArray();
        InputStream keyInputStream = readKeyInputStream();
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keyInputStream, keyPassword);
            kmf = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keyPassword);
        } catch (KeyStoreException | CertificateException | IOException
                | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        } finally {
            keyInputStream.close();
        }
        assert kmf != null;
        return kmf.getKeyManagers();
    }

    /**
     * Initializes TrustManagers which are responsible for managing the list of
     * trusted entities included on trust store file of escrow agent in order a
     * client can be authenticated server.
     *
     * @return An Array of trusted entities including on trust store file of
     * escrow agent.
     * @throws java.io.IOException IOException may be thrown when Input Stream
     * is going to be closed.
     */
    public TrustManager[] readTrustManagers() throws IOException {
        KeyStore keyStore;
        TrustManagerFactory tmf = null;
        char[] keyPassword = readPassword().toCharArray();
        InputStream trustInputStream = readTrustInputStream();
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(trustInputStream, keyPassword);
            tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
        } catch (KeyStoreException | CertificateException | IOException
                | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            trustInputStream.close();
        }
        assert tmf != null;
        return tmf.getTrustManagers();
    }

    /**
     * Initializes SSL Context in which an encrypted communication is
     * established between client and server with a mutual authentication
     * (both server and client authentication) based on TLS protocol.
     *
     * @return Socket specified by Transport Layer Security (TLS) protocol.
     */
    public SSLContext createSSLContext() {
        SSLContext sslContext = null;
        try {
            KeyManager[] keyManagers = readKeyManagers();
            TrustManager[] trustManagers = readTrustManagers();
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
        } catch (NoSuchAlgorithmException | IOException
                | KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    /**
     * Changes SSL Engine to the custom implementation.
     *
     * Reads the key stores and trust stores of the escrow agent in order to
     * be used during the mutual authentication between escrow agent and the
     * entity requesting it using TLS cryptographic protocol.
     *
     * @return Custom SSL Engine to be used on this current Play Application.
     */
    @Override
    public SSLEngine createSSLEngine() {
        SSLEngine sslEngine;
        SSLContext sslContext = createSSLContext();
        SSLParameters sslParameters = sslContext.getDefaultSSLParameters();
        sslParameters.setUseCipherSuitesOrder(true);
        String needClientAuth = System.getProperty("play.ssl.needClientAuth");
        sslParameters.setNeedClientAuth(Boolean.parseBoolean(needClientAuth));
        sslEngine = sslContext.createSSLEngine();
        sslEngine.setSSLParameters(sslParameters);
        return sslEngine;
    }
}
