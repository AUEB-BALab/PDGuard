package org.pdguard.api;

import org.junit.Before;
import org.junit.Test;
import org.pdguard.api.exceptions.CryptoException;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;

import javax.crypto.*;
import java.io.IOException;

import java.security.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TestDataProtection {
    private DataProtection mockCon;

    private String decodeBytes(byte[] content) {
        return new String(content);
    }

    /**
     * Sets parameters up to run diverse test cases.
     */
    @Before
    public void setUp() throws NoSuchAlgorithmException, IOException,
            EscrowAgentErrorResponseException {
        final String escrowAgentName = "localhost:9443";
        ClientCredentials clientCredentials = new ClientCredentials("id", "secret");
        DataProtection con = new DataProtection(escrowAgentName, clientCredentials);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom(); // cryptograph. secure random
        keyGen.init(random);
        SecretKey secretKey = keyGen.generateKey();
        mockCon = spy(con);
        doReturn(secretKey).when(mockCon).getEncryptionKey(anyObject(), anyObject());
        when(mockCon.getEncryptionKey(anyObject(), anyObject())).thenReturn(secretKey);
    }

    @Test
    public void testDataDecryptionSucceeded()
            throws IOException, EscrowAgentErrorResponseException {
        byte[] cleartextData = "My personal data".getBytes();
        byte[] encryptedData = mockCon.encryptData(cleartextData,
                DataType.SURNAME, DataProvenance.DATA_CONTROLLER, true);
        assertFalse("My personal data".equals(decodeBytes(encryptedData)));
        byte[] decryptedData = mockCon.decryptData(encryptedData,
                DataType.SURNAME, DataUse.COMPOSE_EMAIL_TO_SUBJECT, InteractionPurpose.ADVERT);
        assertNotNull(encryptedData);
        assertNotNull(decryptedData);
        assertEquals("My personal data", decodeBytes(decryptedData));
    }

    @Test(expected = CryptoException.class)
    public void testDataDecryptionFailed() throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException,
            IOException, EscrowAgentErrorResponseException {
        byte[] cleartextData = "My personal data".getBytes();
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom(); // cryptograph. secure random
        keyGen.init(random);
        SecretKey secretKey = keyGen.generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(cleartextData);
        mockCon.decryptData(encryptedData, DataType.SURNAME,
                DataUse.COMPOSE_EMAIL_TO_SUBJECT, InteractionPurpose.ADVERT);
    }

    @Test(expected = CryptoException.class)
    public void testDataEncryptionFailed() throws IOException,
            EscrowAgentErrorResponseException {
        byte[] cleartextData = "My personal data".getBytes();
        doReturn(null).when(mockCon).getEncryptionKey(anyObject(), anyObject());
        when(mockCon.getEncryptionKey(anyObject(), anyObject())).thenReturn(null);
        mockCon.encryptData(cleartextData, DataType.SURNAME,
                DataProvenance.DATA_CONTROLLER, true);
    }
}
