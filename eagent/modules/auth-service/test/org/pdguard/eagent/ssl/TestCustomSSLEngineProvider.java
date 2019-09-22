package org.pdguard.eagent.ssl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import org.pdguard.eagent.ssl.CustomSSLEngineProvider;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

import java.io.FileNotFoundException;
import java.io.IOException;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

/**
 * Class that tests CustomSSLEngineProvider class with JUnit.
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestCustomSSLEngineProvider {
    /** Custom SSL Engine Provider for this current application. */
    private CustomSSLEngineProvider sslEngineProvider;

    /**
     * Sets parameters up to run diverse test cases.
     */
    @Before
    public void setUp() {
        this.sslEngineProvider = new CustomSSLEngineProvider(null);
    }

    /**
     * Method passes test when no Exception is thrown and key store file of
     * escrow agent can be found.
     */
    @Test
    public void testReadKeyInputStream() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            try {
                sslEngineProvider.readKeyInputStream();
            } catch (FileNotFoundException e) {
                throw new AssertionError(e.getMessage());
            }
        });
    }

    /**
     * Method passes test when no Exception is thrown and trust store file of
     * escrow agent can be found.
     */
    @Test
    public void testReadTrustInputStream() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            try {
                sslEngineProvider.readTrustInputStream();
            } catch (FileNotFoundException e) {
                throw new AssertionError(e.getMessage());
            }
        });
    }

    /**
     * Method passes test when no Exception is thrown and key managers are
     * initialized properly with the exact number of private keys including on
     * key store file.
     */
    @Test
    public void testKeyManagers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            try {
                KeyManager[] keyManagers = sslEngineProvider
                        .readKeyManagers();
                Assert.assertNotNull(keyManagers);
                int numberPrivateKeys = 1;
                Assert.assertEquals(keyManagers.length,
                        numberPrivateKeys);
            } catch (IOException e) {
                throw new AssertionError(e.getMessage());
            }
        });
    }

    /**
     * Method passes test when no Exception is thrown and trust managers are
     * initialized properly with the exact number of trusted entities including on
     * key store file.
     */
    @Test
    public void testTrustManagers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            try {
                TrustManager[] trustManagers = sslEngineProvider
                        .readTrustManagers();
                Assert.assertNotNull(trustManagers);
                int numberOfTrustedEntities = 1;
                Assert.assertEquals(trustManagers.length,
                        numberOfTrustedEntities);
            } catch (IOException e) {
                throw new AssertionError(e.getMessage());
            }
        });
    }

    /**
     * Method passes test when SSL context has no null value.
     */
    @Test
    public void testCreateSSLContext() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            SSLContext sslContext = sslEngineProvider.createSSLContext();
            Assert.assertNotNull(sslContext);
        });
    }

    /**
     * Method passes test when client authentication is turned on as being
     * default.
     */
    @Test
    public void testCreateSSLEngine() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            SSLEngine sslEngine = sslEngineProvider.createSSLEngine();
            Assert.assertTrue(sslEngine.getNeedClientAuth());
        });
    }
}
