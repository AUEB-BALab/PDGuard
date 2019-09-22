package org.pdguard.eagent.models;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestRequestToken {
    private ReqToken reqToken;

    private Date expirationDate = new Date();

    private ReqToken addRequestToken() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        Client client = new Client("id", new byte[6], dataSubject, dataController,
                authApp);
        client.save();
        ReqToken reqToken = new ReqToken("token", new byte[6],
                client);
        reqToken.save();
        return reqToken;
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        reqToken = addRequestToken();
    }

    @Test
    public void testRequestTokenAddition() {
        ReqToken retrievedToken = ReqToken.getFinder().where()
                .eq("id", reqToken.getId()).findUnique();
        assertNotNull(retrievedToken);
        assertEquals(retrievedToken.getSecret().length, 6);
        assertEquals(retrievedToken.getClient(), reqToken.getClient());
        assertFalse(retrievedToken.isAuthorized());
        assertNotNull(retrievedToken.getValidFrom());
        assertNotNull(retrievedToken.getValidTo());
    }

    @Test
    public void testRequestTokenNotExists() {
        String wrongId = "wrong id";
        ReqToken retrievedToken = ReqToken.getFinder().where()
                .eq("id", wrongId).findUnique();
        assertNull(retrievedToken);
    }

    @Test
    public void testRelationshipWithClient() {
        ReqToken retrievedToken = ReqToken.getFinder()
                .where().eq("client.id", reqToken.getClient().getId())
                .findUnique();
        assertEquals(retrievedToken, reqToken);
        ReqToken newReqToken = new ReqToken("ids", new byte[6],
                retrievedToken.getClient());
        newReqToken.save();
        List<ReqToken> tokenList = ReqToken.getFinder()
                .where().eq("client.id", reqToken.getClient().getId())
                .findList();
        assertEquals(tokenList.size(), 2);
        assertEquals(tokenList.get(0), reqToken);
        assertEquals(tokenList.get(1), newReqToken);
    }

    @Test
    public void testAddRequestToken() {
        ReqToken newToken = new ReqToken("ids", new byte[6],
                reqToken.getClient());
        newToken.addRequestToken();
        List<ReqToken> reqTokens = ReqToken.getFinder().where()
                .findList();
        assertEquals(2, reqTokens.size());
    }

    @Test
    public void testGetExpirationDate() {
        Date currentDate = new Date();
        Date expirationDate = reqToken.getExpirationDate(currentDate);
        long difference = expirationDate.getTime() - currentDate.getTime();
        long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        assertEquals(days, 1);
    }

    @Test
    public void testAuthenticate() {
        assertNotNull(ReqToken.authenticate(reqToken.getId(),
                reqToken.getClient().getId()));
        String wrongId = "wrong id";
        assert !wrongId.equals(reqToken.getId());
        assertNull(ReqToken.authenticate(wrongId, reqToken.getClient()
                .getId()));
    }

    @Test
    public void testRevokeRequestToken() {
        reqToken.revokeRequestToken(false);
        assertTrue(reqToken.isUsed());
        assertEquals(ReqToken.getFinder().all().size(), 1);
        reqToken.revokeRequestToken(true);
        assertEquals(ReqToken.getFinder().all().size(), 0);
    }
}
