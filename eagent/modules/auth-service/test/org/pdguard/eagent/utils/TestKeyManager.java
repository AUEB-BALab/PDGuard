package org.pdguard.eagent.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.UnretrievableKeyException;
import org.pdguard.api.model.EncryptionKey;
import org.pdguard.api.utils.DataType;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.pdguard.eagent.models.Key;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.Application;
import play.Play;
import play.i18n.Messages;

import java.io.File;
import java.util.Date;

import static org.apache.commons.codec.binary.Hex.encodeHex;

@PowerMockIgnore("javax.crypto.*")
@RunWith(PowerMockRunner.class)
public class TestKeyManager {
    private Key key;

    private void mockApplication() {
        Application application = PowerMockito.mock(Application.class);
        PowerMockito.when(application.path()).thenReturn(new File("."));
        PowerMockito.mockStatic(Play.class);
        PowerMockito.when(Play.application()).thenReturn(application);
    }

    @Before
    public void setUp() {
        DataSubject dataSubject = new DataSubject("id", "james",
                "test@example.com", "password");
        DataController dataController = new DataController("id", "example");
        key = new Key(dataSubject, dataController, new Date());
    }

    @Test
    @PrepareForTest({Play.class})
    public void testLoadMasterKey() {
        mockApplication();
        Assert.assertNotNull(KeyManager.loadMasterKey());
    }

    @Test
    @PrepareForTest({Play.class})
    public void testEncryptKey() {
        mockApplication();
        byte[] encryptedKey = KeyManager.encryptKey(key.getEncodedKeyContent());
        Assert.assertNotEquals(String.valueOf(encodeHex(encryptedKey)),
                key.getKeyContent());
    }

    @Test
    @PrepareForTest({Play.class})
    public void testDecryptKey() {
        mockApplication();
        byte[] encryptedKey = KeyManager.encryptKey(key.getEncodedKeyContent());
        byte[] decryptedKey = KeyManager.decryptKey(encryptedKey);
        Assert.assertTrue(String.valueOf(encodeHex(decryptedKey)).equals(
                key.getKeyContent()));
        Assert.assertFalse(String.valueOf(encodeHex(decryptedKey)).equals(
                String.valueOf(encodeHex(encryptedKey))));
    }

    @PrepareForTest({Key.class, Play.class})
    @Test
    public void testRequestEncryptionKey() throws UnretrievableKeyException,
            CloneNotSupportedException {
        mockApplication();
        byte[] encryptKey = KeyManager.encryptKey(key.getEncodedKeyContent());
        Key encryptedKey = (Key) key.clone();
        encryptedKey.setKeyContent(encryptKey);
        Key mockKey = PowerMockito.spy(encryptedKey);
        PowerMockito.mockStatic(Key.class);
        PowerMockito.when(Key.loadKey("data subject", "data controller"))
                .thenReturn(mockKey);
        EncryptionKey decryptedKey = KeyManager.requestEncryptionKey("data subject",
                "data controller", DataType.AIM_ID);
        PowerMockito.verifyStatic();
        Key.loadKey("data subject", "data controller");
        Assert.assertFalse(decryptedKey.getEncryptionKey().equals(
                key.getKeyContent()));
    }

    @PrepareForTest({ Key.class, Messages.class, Play.class })
    @Test(expected = UnretrievableKeyException.class)
    public void testRequestEncryptionKeyFailure() throws UnretrievableKeyException,
            CloneNotSupportedException {
        mockApplication();
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("key.expired")).thenReturn("");
        byte[] encryptKey = KeyManager.encryptKey(key.getEncodedKeyContent());
        Key encryptedKey = (Key) key.clone();
        encryptedKey.setKeyContent(encryptKey);
        Key mockKey = PowerMockito.spy(encryptedKey);
        Mockito.when(mockKey.isExpired(mockKey.getValidFrom(), mockKey.getValidTo()))
                .thenReturn(true);
        PowerMockito.mockStatic(Key.class);
        PowerMockito.when(Key.loadKey("data subject", "data controller"))
                .thenReturn(mockKey);
        KeyManager.requestEncryptionKey("data subject", "data controller",
                DataType.AIM_ID);
    }

    @PrepareForTest({ Key.class, Messages.class, Play.class })
    @Test(expected = UnretrievableKeyException.class)
    public void testRequestEncryptionKeyNull() throws UnretrievableKeyException {
        mockApplication();
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("key.expired")).thenReturn("");
        PowerMockito.mockStatic(Key.class);
        PowerMockito.when(Key.loadKey("data subject", "data controller"))
                .thenReturn(null);
        KeyManager.requestEncryptionKey("data subject", "data controller",
                DataType.AIM_ID);
    }

    @Test
    public void testCalculateHash() {
        Assert.assertNotNull(KeyManager.calculateHash("key", DataType.AIM_ID));
    }

    @Test
    public void testBuildEncryptionKey() {
        Assert.assertNotNull(KeyManager.buildEncryptionKey(KeyManager
                .calculateHash("key", DataType.AIM_ID)).getEncryptionKey());
    }
}
