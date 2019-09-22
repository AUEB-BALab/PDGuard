package org.pdguard.eagent.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;

import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

/**
 * Class that tests DataController class with JUnit.
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestDataController {

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

    private DataController addAuthorizationRule() {
        DataSubject dataSubject = addDataSubject();
        DataController dataController = addDataController();
        AuthorizationRule authorizationRule = new AuthorizationRule(dataSubject, dataController,
                DataType.GIVEN_NAME);
        authorizationRule.getAllowableActions().add(new AllowableAction(
                authorizationRule, DataUse.UPDATE_THIRD_PARTY_DATA,
                new Date(), null));
        authorizationRule.save();
        return dataController;
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void testDataControllerAddition() {
        DataController dataController = this.addDataController();
        dataController = DataController.getFinder().where().eq("id", "id").findUnique();
        assertNotNull(dataController);
        assertEquals("ABC", dataController.getDataControllerName());
    }

    @Test
    public void testDataControllerNotExists() {
        DataController dataController = DataController.getFinder().where().eq("id", "2")
                .findUnique();
        assertNull(dataController);
    }

    @Test
    public void testDataControllerUpdate() {
        DataController dataController = this.addDataController();
        dataController.setDataControllerName("DFG");
        dataController.update();
        dataController = DataController.getFinder().where().eq("id", "id")
                .findUnique();
        assertNotNull(dataController);
        assertEquals("DFG", dataController.getDataControllerName());
        assertFalse("ABC".equals(dataController.getDataControllerName()));
    }

    @Test
    public void testDataSubjectDeletion() {
        DataController dataController = this.addDataController();
        dataController = DataController.getFinder().where().eq("id", "id")
                .findUnique();
        assertNotNull(dataController);
        dataController.delete();
        dataController = DataController.getFinder().where().eq("id", "id")
                .findUnique();
        assertNull(dataController);
    }

    @Test
    public void testRelationshipWithEncryptionKey() {
        DataSubject dataSubject = this.addDataSubject();
        DataController dataController = this.addDataController();
        Key key1 = new Key(dataSubject, dataController,
                new Date());
        key1.save();
        DataSubject dataSubject2 = new DataSubject("id2", "Nick Apostolopoulos",
                "nap2@hotmail.com", "password");
        dataSubject2.save();
        Key key2 = new Key(dataSubject2, dataController,
                new Date());
        key2.save();
        DataController retrievedController = DataController.getFinder().where()
                .eq("keys.id", key1.getId()).findUnique();
        assertEquals(retrievedController, dataController);
        List<Key> keys = retrievedController
                .getKeys();
        assertEquals(keys.size(), 2);
        assertEquals(keys.get(0), key1);
        assertEquals(keys.get(1), key2);
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
        authRule2.getAllowableActions().add(new AllowableAction(authRule2,
                DataUse.UPDATE_THIRD_PARTY_DATA, new Date(), null));
        authRule2.update();
        DataController retrievedController = DataController.getFinder().where()
                .eq("authorizationRules.id", authRule1.getId()).findUnique();
        assertEquals(retrievedController, dataController);
        List<AuthorizationRule> authorizationRules = retrievedController
                .getAuthorizationRules();
        assertEquals(authorizationRules.size(), 2);
        assertEquals(authorizationRules.get(0), authRule1);
        assertEquals(authorizationRules.get(1), authRule2);
    }

    @Test
    public void testRelationshipWithAuthorizedApplication() {
        DataController dataController = this.addDataController();
        AuthorizedApplication authApp1 = new AuthorizedApplication("id",
                dataController, "DFG");
        authApp1.save();
        AuthorizedApplication authApp2 = new AuthorizedApplication("id2",
                dataController, "DFG");
        authApp2.save();
        DataController retrievedController = DataController.getFinder().where()
                .eq("authorizedApplications.id", authApp1.getId()).findUnique();
        assertEquals(retrievedController, dataController);
        List<AuthorizedApplication> authorizedApps = retrievedController
                .getAuthorizedApplications();
        assertEquals(authorizedApps.size(), 2);
        assertEquals(authorizedApps.get(0), authApp1);
        assertEquals(authorizedApps.get(1), authApp2);
    }

    @Test(expected = PersistenceException.class)
    public void testDataSubjectNameIsRequired() {
        DataController dataController = new DataController();
        dataController.save();
    }

    @Test
    public void testAuthentication() {
        DataController dataController = this.addAuthorizationRule();
        DataController retrievedController = DataController.authenticate(
                dataController.getId());
        assertNotNull(retrievedController);
        assertEquals(retrievedController, dataController);
        String wrongId = "wrong id";
        assertNull(DataController.authenticate(wrongId));
        retrievedController = DataController.authenticate(dataController
                .getDataControllerName(), "nap@hotmail.com", DataType.GIVEN_NAME);
        assertNotNull(retrievedController);
        assertEquals(retrievedController, dataController);
    }

    @Test
    public void testStoresData() {
        DataController dataController = this.addAuthorizationRule();
        assertTrue(dataController.storesData("nap@hotmail.com", DataType
                .GIVEN_NAME));
        assertFalse(dataController.storesData("nap@hotmail.com", DataType.IRIS_SCAN));
        assertFalse(dataController.storesData("wrong@email.com", DataType
                .GIVEN_NAME));
        assertFalse(dataController.storesData(null, null));
    }
}
