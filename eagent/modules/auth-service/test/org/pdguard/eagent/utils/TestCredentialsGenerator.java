package org.pdguard.eagent.utils;

import org.junit.Test;
import org.pdguard.eagent.utils.CredentialsGenerator;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class TestCredentialsGenerator {
    @Test
    public void testGenerateUniqueId() {
        assertNotNull(CredentialsGenerator.generateUniqueID());
    }

    @Test
    public void testGenerateHmacSHA1Secret() throws NoSuchAlgorithmException {
        byte[] secret = CredentialsGenerator.generateHmacSHA1Secret();
        assertNotNull(secret);
        assertFalse(secret.length == 0);
    }
}
