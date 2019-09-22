package org.pdguard.eagent.models;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestClient {
    private Client client;

    private Client addClient() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        Client client = new Client("id", new byte[6], dataSubject, dataController,
                authApp);
        client.save();
        return client;
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        client = addClient();
    }

    @Test
    public void testClientAddition() {
        Client retrievedClient = Client.getFinder().where()
                .eq("id", client.getId()).findUnique();
        assertNotNull(retrievedClient);
        assertEquals(retrievedClient.getSecret().length, 6);
        assertEquals(retrievedClient.getDataSubject(), client.getDataSubject());
        assertEquals(retrievedClient.getDataController(), client.getDataController());
        assertEquals(retrievedClient.getAuthApp(), client.getAuthApp());
    }

    @Test
    public void testAuthorizationRuleNotExists() {
        String wrongId = "wrong id";
        Client retrievedClient = Client.getFinder().where()
                .eq("id", wrongId).findUnique();
        assertNull(retrievedClient);
    }

    @Test
    public void testRelationshipWithDataController() {
        DataSubject dataSubject = new DataSubject("id2", "Nick Apostolopoulos",
                "nap2@hotmail.com", "password");
        AuthorizedApplication authApp = client.getAuthApp();
        DataController dataController = client.getDataController();
        Client retrievedClient = Client.getFinder()
                .where().eq("dataController.id", dataController.getId())
                .findUnique();
        assertEquals(retrievedClient, client);
        Client newClient = new Client("ids", new byte[6], dataSubject,
                dataController, authApp);
        newClient.save();
        List<Client> clientList = Client.getFinder()
                .where().eq("dataController.id", dataController.getId())
                .findList();
        assertEquals(clientList.size(), 2);
        assertEquals(clientList.get(0), client);
        assertEquals(clientList.get(1), newClient);
    }

    @Test
    public void testRelationshipWithDataSubject() {
        DataController dataController = new DataController("id2", "ABC");
        AuthorizedApplication authApp = client.getAuthApp();
        DataSubject dataSubject = client.getDataSubject();
        Client retrievedClient = Client.getFinder()
                .where().eq("dataSubject.id", dataSubject.getId())
                .findUnique();
        assertEquals(retrievedClient, client);
        Client newClient = new Client("ids", new byte[6], dataSubject,
                dataController, authApp);
        newClient.save();
        List<Client> clientList = Client.getFinder()
                .where().eq("dataSubject.id", dataSubject.getId())
                .findList();
        assertEquals(clientList.size(), 2);
        assertEquals(clientList.get(0), client);
        assertEquals(clientList.get(1), newClient);
    }

    @Test
    public void testRelationshipWithAuthorizedApplication() {
        DataController dataController = new DataController("id2", "ABC");
        AuthorizedApplication authApp = client.getAuthApp();
        DataSubject dataSubject = client.getDataSubject();
        Client retrievedClient = Client.getFinder()
                .where().eq("authApp.id", authApp.getId())
                .findUnique();
        assertEquals(retrievedClient, client);
        Client newClient = new Client("ids", new byte[6], dataSubject,
                dataController, authApp);
        newClient.save();
        List<Client> clientList = Client.getFinder()
                .where().eq("authApp.id", authApp.getId())
                .findList();
        assertEquals(clientList.size(), 2);
        assertEquals(clientList.get(0), client);
        assertEquals(clientList.get(1), newClient);
    }

    @Test
    public void testAddClient() {
        DataSubject dataSubject = new DataSubject("id2", "Nick Apostolopoulos",
                "nap2@hotmail.com", "password");
        DataController dataController = new DataController("id2", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id2",
                dataController, "App");
        Client newClient = new Client("client id", new byte[6], dataSubject,
                dataController, authApp);
        newClient.addClient();
        List<Client> clients = Client.getFinder().where()
                .findList();
        assertEquals(2, clients.size());
    }

    @Test
    public void testGetClientSecret() {
        Client retrievedClient = Client.authenticate(client.getId());
        assertEquals(retrievedClient.getId(), client.getId());
        assertEquals(retrievedClient.getDataSubject(), client.getDataSubject());
        assertEquals(retrievedClient.getDataController(), client.getDataController());
        assertEquals(retrievedClient.getAuthApp(), client.getAuthApp());
        String wrongClientId = "Wrong id";
        assert !wrongClientId.equals(client.getId());
        assertNull(Client.authenticate(wrongClientId));
    }

    @Test(expected = PersistenceException.class)
    public void testIsUniqueAssociation() {
        String clientId = UUID.randomUUID().toString();
        Client newClient = new Client(clientId, new byte[0], client.getDataSubject(),
                client.getDataController(), client.getAuthApp());
        newClient.save();
    }

    @Test
    public void testIsUnique() {
        assertFalse(Client.isUnique(client.getDataSubject().getId(),
                client.getDataController().getId(), client.getAuthApp().getId()));
        assertTrue(Client.isUnique(client.getDataSubject().getId(),
                client.getDataController().getId(), "wrong id"));
        assertTrue(Client.isUnique(client.getDataSubject().getId(),
                "wrong id", client.getAuthApp().getId()));
        assertTrue(Client.isUnique("wrong id", client.getDataController().getId(),
                client.getAuthApp().getId()));
    }
}
