package org.pdguard.eagent.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import javax.persistence.PersistenceException;

import java.util.Date;
import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

/**
 * Class that tests EncryptionKey class with JUnit.
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestEncryptionKey {

    private Key addEncryptionKey() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.save();
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        Key key = new Key(dataSubject, dataController,
                new Date());
        key.save();
        return key;
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void testEncryptionKeyAddition() {
        Key key = this.addEncryptionKey();
        Key retrievedKey = Key.getFinder().where().eq("id", "1")
                .findUnique();
        assertNotNull(retrievedKey);
        assertEquals(retrievedKey, key);
    }

    @Test
    public void testEncryptionKeyNotExists() {
        Key key = Key.getFinder().where()
                .eq("id", "2").findUnique();
        assertNull(key);
    }

    @Test
    public void testEncryptionKeyUpdate() {
        Key key = this.addEncryptionKey();
        String initialContent = key.getKeyContent();
        byte[] content = "content".getBytes();
        key.setKeyContent(content);
        key.update();
        Key retrievedKey = Key.getFinder().where().eq("id", "1")
                .findUnique();
        assertNotNull(retrievedKey);
        assertFalse(retrievedKey.getKeyContent().equals(initialContent));
    }

    @Test
    public void testEncryptionKeyDeletion() {
        Key key = this.addEncryptionKey();
        key = Key.getFinder().where().eq("id", "1")
                .findUnique();
        assertNotNull(key);
        key.delete();
        key = Key.getFinder().where().eq("id", "1")
                .findUnique();
        assertNull(key);
    }

    @Test
    public void testRelationshipWithDataController() {
        DataSubject dataSubject = new DataSubject("id2", "Nick Apostolopoulos",
                "nap2@hotmail.com", "password");
        dataSubject.save();
        Key key1 = this.addEncryptionKey();
        DataController dataController = key1.getDataController();
        Key retrievedKey = Key.getFinder()
                .where().eq("dataController.id", dataController.getId())
                .findUnique();
        assertEquals(retrievedKey, key1);
        Key key2 = new Key(dataSubject, dataController,
                new Date());
        key2.save();
        List<Key> enKeys = Key.getFinder()
                .where().eq("dataController.id", dataController.getId())
                .findList();
        assertEquals(enKeys.size(), 2);
        assertEquals(enKeys.get(0), key1);
        assertEquals(enKeys.get(1), key2);
    }

    @Test(expected = PersistenceException.class)
    public void testRelationshipWithDataSubject() {
        DataController dataController = new DataController("id", "ABC");
        Key key1 = this.addEncryptionKey();
        DataSubject dataSubject = key1.getDataSubject();
        Key retrievedKey = Key.getFinder()
                .where().eq("dataSubject.id", dataSubject.getId())
                .findUnique();
        assertEquals(retrievedKey, key1);
        Key key2 = new Key(dataSubject, dataController,
                new Date());
        key2.save();
    }

    @Test(expected = PersistenceException.class)
    public void testFieldsAreRequired() {
        Key key = new Key();
        key.save();
    }

    @Test
    public void testAddKey() {
        Key key = this.addEncryptionKey();
        Key retrievedKey = Key.getFinder().where().eq("id", "1")
                .findUnique();
        assertNotNull(retrievedKey);
        assertEquals(retrievedKey, key);
    }

    @Test
    public void testCreateKey() {
        Key key = this.addEncryptionKey();
        assertNotNull(key.createKey());
    }

    @Test
    public void testLoadKey() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap2@hotmail.com", "password");
        dataSubject.save();
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        assertNull(Key.loadKey(dataSubject.getId(),
                dataController.getId()));
        Key key = new Key(dataSubject, dataController,
                new Date());
        key.save();
        assertNotNull(Key.loadKey(dataSubject.getId(),
                dataController.getId()));
        assertNull(Key.loadKey(null, dataController.getId()));
        String wrongId = "wrong id";
        assertNull(Key.loadKey(wrongId, dataController.getId()));
    }
}
