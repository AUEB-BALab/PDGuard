package org.pdguard.eagent.models;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestNonce {
    private Nonce nonce;

    private Nonce addNonce() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        Client client = new Client("id", new byte[6], dataSubject, dataController,
                authApp);
        client.save();
        Nonce nonce = new Nonce("id", new Date().getTime(), client);
        nonce.save();
        return nonce;
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        nonce = addNonce();
    }

    @Test
    public void testNonceAddition() {
        Nonce retrievedNonce = Nonce.getFinder().where()
                .eq("id", nonce.getId()).findUnique();
        assertNotNull(retrievedNonce);
        assertEquals(retrievedNonce.getClient(), nonce.getClient());
    }

    @Test
    public void testRequestTokenNotExists() {
        String wrongId = "wrong id";
        Nonce retrievedNonce = Nonce.getFinder().where()
                .eq("id", wrongId).findUnique();
        assertNull(retrievedNonce);
    }

    @Test
    public void testRelationshipWithClient() {
        Nonce retrievedNonce = Nonce.getFinder()
                .where().eq("client.id", nonce.getClient().getId())
                .findUnique();
        assertEquals(retrievedNonce, nonce);
        Nonce newNonce = new Nonce("ids", new Date().getTime(), nonce.getClient());
        newNonce.save();
        List<Nonce> nonces = Nonce.getFinder()
                .where().eq("client.id", nonce.getClient().getId())
                .findList();
        assertEquals(nonces.size(), 2);
        assertEquals(nonces.get(0), nonce);
        assertEquals(nonces.get(1), newNonce);
    }

    @Test
    public void testAddRequestToken() {
        Nonce newNonce = new Nonce("ids", new Date().getTime(), nonce.getClient());
        newNonce.addNonce();
        List<Nonce> nonces = Nonce.getFinder().where()
                .findList();
        assertEquals(2, nonces.size());
    }

    @Test
    public void testExists() {
        assertTrue(Nonce.exists(nonce.getId()));
        assertFalse(Nonce.exists("wrong nonce value"));
    }
}
