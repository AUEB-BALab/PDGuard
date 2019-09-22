package org.pdguard.api.security;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;


public class TestSecureConnection {
    @Test
    public void testCreateSSLConnection() throws IOException {
        try {
            SecureConnection secureConnection = new SecureConnection();
            secureConnection.createSSLSocketConnection();
        } catch (IOException e) {
            throw new AssertionError("SSL connection could not be established.");
        }
    }

    @Test
    public void testLoadKeyStore() {
        String path = "../certs/app.keystore";
        String password = "clientpassword";
        SecureConnection secureConnection = new SecureConnection();
        Assert.assertNotNull(secureConnection.loadKeyStore(path, password));
    }

    @Test
    public void testLoadProperties() {
        Properties properties = SecureConnection.loadProperties();
        Assert.assertEquals(properties.getProperty("keystore"),
                "../certs/app.keystore");
        Assert.assertEquals(properties.getProperty("truststore"),
                "../certs/app.trustore");
        Assert.assertEquals(properties.getProperty("keystore_pswrd"),
                "clientpassword");
        Assert.assertEquals(properties.getProperty("truststore_pswrd"),
                "clientpassword");
    }
}
