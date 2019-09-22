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

import javax.persistence.PersistenceException;

import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

/**
 * Class that tests AuthorizedApplication class with JUnit.
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestAuthorizedApplication {

    private AuthorizedApplication addAuthorizedApplication(
            final String authName) {
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, authName);
        authApp.save();
        return authApp;
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void testAuthorizedApplicationAddition() {
        AuthorizedApplication authApp = this.addAuthorizedApplication("ABC");
        authApp = AuthorizedApplication.getFinder().where().eq("id", "id").findUnique();
        assertNotNull(authApp);
        assertEquals("ABC", authApp.getAuthorizedApplicationName());
    }

    @Test
    public void testAuthorizedApplicationNotExists() {
        AuthorizedApplication authApp = AuthorizedApplication.getFinder().where()
                .eq("id", "2").findUnique();
        assertNull(authApp);
    }

    @Test
    public void testAuthorizedApplicationUpdate() {
        AuthorizedApplication authApp = this.addAuthorizedApplication("ABC");
        authApp.setAuthorizedApplicationName("DFG");
        authApp.update();
        authApp = AuthorizedApplication.getFinder().where().eq("id", "id").findUnique();
        assertNotNull(authApp);
        assertEquals("DFG", authApp.getAuthorizedApplicationName());
        assertFalse("ABC".equals(authApp.getAuthorizedApplicationName()));
    }

    @Test
    public void testAuthorizedApplicationDeletion() {
        AuthorizedApplication authApp = this.addAuthorizedApplication("ABC");
        authApp = AuthorizedApplication.getFinder().where().eq("id", "id")
                .findUnique();
        assertNotNull(authApp);
        authApp.delete();
        authApp = AuthorizedApplication.getFinder().where().eq("id", "id")
                .findUnique();
        assertNull(authApp);
    }

    @Test
    public void testRelationshipWithDataController() {
        AuthorizedApplication authApp1 = this.addAuthorizedApplication("ABC");
        DataController dataController = authApp1.getDataControllers().iterator()
                .next();
        AuthorizedApplication retrievedApplication = AuthorizedApplication
                .getFinder().where().eq("dataControllers.id", dataController.getId())
                .findUnique();
        assertEquals(retrievedApplication, authApp1);
        AuthorizedApplication authApp2 = new AuthorizedApplication("id2",
                dataController, "DFC");
        authApp2.save();
        List<AuthorizedApplication> appList = AuthorizedApplication.getFinder()
                .where().eq("dataControllers.id", dataController.getId())
                .findList();
        assertEquals(appList.size(), 2);
        assertEquals(appList.get(0), authApp1);
        assertEquals(appList.get(1), authApp2);
    }

    @Test(expected = PersistenceException.class)
    public void testDataSubjectNameIsRequired() {
        AuthorizedApplication authApp = new AuthorizedApplication();
        authApp.save();
    }

    @Test
    public void getIsAuthorized() {
        AuthorizedApplication authApp = this.addAuthorizedApplication("ABC");
        DataController dataController = authApp.getDataControllers().get(0);
        assertTrue(authApp.isAuthorized(dataController.getId()));
        assertFalse(authApp.isAuthorized("WRONG DATA CONTROLLER"));
    }

    @Test
    public void testAuthentication() {
        AuthorizedApplication authApp = addAuthorizedApplication("ABC");
        AuthorizedApplication retrievedAuthApp  = AuthorizedApplication
                .authenticate(authApp.getId(), authApp.getDataControllers()
                        .get(0).getId());
        assertNotNull(retrievedAuthApp);
        assertEquals(retrievedAuthApp.getAuthorizedApplicationName(),
                authApp.getAuthorizedApplicationName());
        final String wrongAuthAppId = "wrong id";
        final String wrongDataControllerId = "wrong id";
        assertNull(AuthorizedApplication.authenticate(wrongAuthAppId,
                authApp.getDataControllers().get(0).getId()));
        assertNull(AuthorizedApplication.authenticate(authApp.getId(),
                wrongDataControllerId));
    }
}
