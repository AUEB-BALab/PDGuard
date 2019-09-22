package org.pdguard.eagent.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;

import javax.persistence.PersistenceException;

import java.util.Date;
import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

/**
 * Class that tests DataSubject class with JUnit.
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestDataSubject {

    private DataSubject addDataSubject() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.save();
        return dataSubject;
    }

    private DataController addDataController() {
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        return dataController;
    }

    private Key addEncryptionKey(final DataSubject dataSubject,
            final DataController dataController) {
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
    public void testDataSubjectAddition() {
        DataSubject dataSubject = this.addDataSubject();
        dataSubject = DataSubject.getFinder().where().eq("id", "id").findUnique();
        assertNotNull(dataSubject);
        assertEquals("Nick Apostolopoulos", dataSubject.getDataSubjectName());
        assertEquals("password", dataSubject.getDataSubjectPassword());
        assertEquals("nap@hotmail.com", dataSubject.getDataSubjectEmail());
    }

    @Test
    public void testDataSubjectNotExists() {
        DataSubject dataSubject = DataSubject.getFinder().where().eq("id", "2")
                .findUnique();
        assertNull(dataSubject);
    }

    @Test
    public void testDataSubjectUpdate() {
        DataSubject dataSubject = this.addDataSubject();
        dataSubject.setDataSubjectName("Thodoris Sotiropoulos");
        dataSubject.setDataSubjectEmail("theosotr@windowslive.com");
        dataSubject.setDataSubjectPassword("password2");
        dataSubject.update();
        dataSubject = DataSubject.getFinder().where().eq("id", "id")
                .findUnique();
        assertNotNull(dataSubject);
        assertEquals("Thodoris Sotiropoulos", dataSubject.getDataSubjectName());
        assertEquals("theosotr@windowslive.com", dataSubject.getDataSubjectEmail());
        assertEquals("password2", dataSubject.getDataSubjectPassword());
        assertFalse("Nick Apostolopoulos".equals(dataSubject.getDataSubjectName()));
        assertFalse("nap@hotmail.com".equals(dataSubject.getDataSubjectEmail()));
        assertFalse("password".equals(dataSubject.getDataSubjectPassword()));
    }

    @Test
    public void testDataSubjectDeletion() {
        DataSubject dataSubject = this.addDataSubject();
        dataSubject = DataSubject.getFinder().where().eq("id", "id").findUnique();
        assertNotNull(dataSubject);
        dataSubject.delete();
        dataSubject = DataSubject.getFinder().where().eq("id", "id").findUnique();
        assertNull(dataSubject);
    }

    @Test
    public void testRelationshipWithEncryptionKey() {
        DataSubject dataSubject = this.addDataSubject();
        DataController dataController = this.addDataController();
        Key key = this.addEncryptionKey(dataSubject, dataController);
        DataSubject retrievedSubject = DataSubject.getFinder().where()
                .eq("keys.id", key.getId()).findUnique();
        assertEquals(retrievedSubject, dataSubject);
        assertEquals(retrievedSubject.getKeys().iterator().next(),
                key);
    }

    @Test
    public void testRelationshipWithAuthorizationRule() {
        DataSubject dataSubject = this.addDataSubject();
        DataController dataController = this.addDataController();
        AuthorizationRule authRule1 = new AuthorizationRule(dataSubject, dataController,
                DataType.GIVEN_NAME);
        authRule1.getAllowableActions().add(new AllowableAction(authRule1,
                DataUse.UPDATE_THIRD_PARTY_DATA, new Date(), null));
        authRule1.save();
        AuthorizationRule authRule2 = new AuthorizationRule(dataSubject, dataController,
                DataType.BIRTH_DATE);
        authRule2.save();
        authRule2.getAllowableActions().add(new AllowableAction(authRule2, DataUse.UPDATE_THIRD_PARTY_DATA,
                new Date(), null));
        authRule2.update();
        DataSubject retrievedSubject = DataSubject.getFinder().where()
                .eq("authorizationRules.id", authRule1.getId()).findUnique();
        assertEquals(retrievedSubject, dataSubject);
        List<AuthorizationRule> authorizationRules = retrievedSubject
                .getAuthorizationRules();
        assertEquals(authorizationRules.size(), 2);
        assertEquals(authorizationRules.get(0), authRule1);
        assertEquals(authorizationRules.get(1), authRule2);
    }

    @Test(expected = PersistenceException.class)
    public void testDataSubjectNameIsRequired() {
        DataSubject dataSubject = new DataSubject();
        dataSubject.save();
    }

    @Test(expected = PersistenceException.class)
    public void testUniquenessOfEncryptionKey() {
        DataSubject dataSubject = this.addDataSubject();
        DataController dataController = this.addDataController();
        this.addEncryptionKey(dataSubject, dataController);
        this.addEncryptionKey(dataSubject, dataController);
    }

    @Test
    public void testAuthentication() {
        DataSubject dataSubject = this.addDataSubject();
        DataSubject retrievedSubject = DataSubject.authenticateById(
                dataSubject.getId());
        assertNotNull(retrievedSubject);
        assertEquals(retrievedSubject, dataSubject);
        String wrongId = "wrong id";
        assertNull(DataSubject.authenticateById(wrongId));
        retrievedSubject = DataSubject.authenticateByEmail(dataSubject
                .getDataSubjectEmail());
        assertNotNull(retrievedSubject);
        assertEquals(retrievedSubject, dataSubject);
        String wrongEmail = "wrong@email.gr";
        assertNull(DataSubject.authenticateByEmail(wrongEmail));
    }

    @Test
    public void testCheckCredentials() {
        DataSubject dataSubject = this.addDataSubject();
        assertTrue(DataSubject.isAuthenticated(dataSubject.getDataSubjectEmail(),
                dataSubject.getDataSubjectPassword()));
        DataSubject dataSubjectWithWrongPassword = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "wrongPassword");
        assertFalse(DataSubject.isAuthenticated(dataSubjectWithWrongPassword.getDataSubjectEmail(),
                dataSubjectWithWrongPassword.getDataSubjectPassword()));
        DataSubject dataSubjectWithWrongEmail = new DataSubject("id2", "Nick Apostolopoulos",
                "wrongemail@hotmail.com", "password");
        assertFalse(DataSubject.isAuthenticated(dataSubjectWithWrongEmail.getDataSubjectEmail(),
                dataSubjectWithWrongEmail.getDataSubjectPassword()));
        DataSubject dataSubjectWithWrongCredentials = new DataSubject("id3", "Nick Apostolopoulos",
                "wrongemail@hotmail.com", "wrongPassword");
        assertFalse(DataSubject.isAuthenticated(dataSubjectWithWrongCredentials.getDataSubjectEmail(),
                dataSubjectWithWrongCredentials.getDataSubjectPassword()));
    }

    @Test
    public void testAddDataSubject() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.addDataSubject();
        dataSubject = DataSubject.getFinder().where().eq("id", "id").findUnique();
        assertNotNull(dataSubject);
        assertEquals("Nick Apostolopoulos", dataSubject.getDataSubjectName());
        assertEquals("password", dataSubject.getDataSubjectPassword());
        assertEquals("nap@hotmail.com", dataSubject.getDataSubjectEmail());
    }

    @Test
    public void testIsEmailAvailable() {
        final String userEmail = "nap@hotmail.com";
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                userEmail, "password");
        dataSubject.addDataSubject();
        assertTrue(DataSubject.isEmailAvailable(userEmail));
        assertFalse(DataSubject.isEmailAvailable("wrong@email.com"));
    }

    @Test
    public void testIsIdAvailable() {
        final String identifier = "id";
        DataSubject dataSubject = new DataSubject(identifier, "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.addDataSubject();
        assertTrue(DataSubject.isIdAvailable(identifier));
        assertFalse(DataSubject.isIdAvailable("wrong id"));
    }
}
