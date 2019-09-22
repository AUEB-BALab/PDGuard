package org.pdguard.api.security;

import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.assertNotNull;

public class TestSignature {
    @Test
    public void testSignMessage() {
        String secret = "fsdgrgxgfd";
        String message = "message";
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
        Signature signature = new Signature(message, signingKey);
        signature.signMessage();
        assertNotNull(signature.getSignatureContent());
    }
}
