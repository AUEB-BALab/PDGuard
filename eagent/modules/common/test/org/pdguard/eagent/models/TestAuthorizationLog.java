package org.pdguard.eagent.models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.pdguard.eagent.logs.AuthorizationProcess;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.logs.AuthorizationRequestType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;

import java.util.Date;
import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

/**
 * Class that tests AuthorizationRule class with JUnit.
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestAuthorizationLog {
    private AuthorizationLog authLog;

    private AuthorizationLog addAuthorizationLog() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        Client client = new Client("id", new byte[0], dataSubject, dataController,
                authApp);
        AuthorizationLog authorizationLog = new AuthorizationLog(
                AuthorizationResult.ALLOWED,
                AuthorizationRequestType.READ, AuthorizationProcess.DEFAULT, new Date(),
                DataType.AIM_ID, client);
        authorizationLog.save();
        return authorizationLog;
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        authLog = addAuthorizationLog();
    }

    @Test
    public void testAuthorizationLogAddition() {
        AuthorizationLog retrievedLog = AuthorizationLog.getFinder().where()
                .eq("id", authLog.getId()).findUnique();
        assertNotNull(retrievedLog);
        assertEquals(retrievedLog.getDataType(), authLog.getDataType());
        assertEquals(retrievedLog.getDataUse(), authLog.getDataUse());
        assertEquals(retrievedLog.getInteractionPurpose(), authLog
                .getInteractionPurpose());
        assertEquals(retrievedLog.getRequestType(), authLog.getRequestType());
        assertEquals(retrievedLog.getResult(), authLog.getResult());
    }

    @Test
    public void testAuthorizationRuleNotExists() {
        final int wrongID = 300;
        AuthorizationLog retrievedLog = AuthorizationLog.getFinder().where()
                .eq("id", wrongID).findUnique();
        assertNull(retrievedLog);
    }

    @Test
    public void testAddAuthorizationLog() {
        DataSubject dataSubject = new DataSubject("id2", "Nick Apostolopoulos",
                "nap2@hotmail.com", "password");
        DataController dataController = new DataController("id2", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id2",
                dataController, "App");
        Client client = new Client("id2", new byte[0], dataSubject, dataController,
                authApp);
        AuthorizationLog authorizationLog = new AuthorizationLog(
                AuthorizationResult.ALLOWED,
                AuthorizationRequestType.READ, AuthorizationProcess.DEFAULT, new Date(),
                DataType.AIM_ID, client);
        authorizationLog.addAuthorizationLog();
        List<AuthorizationLog> authLogs = AuthorizationLog.getFinder().where()
                .findList();
        assertEquals(2, authLogs.size());
    }

    @Test
    public void testGetDataSubjectAuthLogs() {
        List<ObjectNode> authLogs = AuthorizationLog
                .getDataSubjectAuthLogs("nap@hotmail.com", null, new Date());
        assertEquals(authLogs.size(), 1);
        ObjectNode json = authLogs.get(0);
        assertEquals(json.get("dataController").asText(), authLog
                .getClient().getDataController().getDataControllerName());
        assertEquals(json.get("app").asText(), authLog.getClient()
                .getAuthApp().getAuthorizedApplicationName());
        assertEquals(AuthorizationRequestType.valueOf(
                json.get("requestType").asText()), authLog.getRequestType());
        assertEquals(json.get("interactionPurpose").isNull()
                        ? null : InteractionPurpose.valueOf(json.get
                        ("interactionPurpose")
                        .asText()),
                authLog.getInteractionPurpose() == null
                        ? null : authLog.getInteractionPurpose());
        assertEquals(AuthorizationResult.valueOf(json.get("requestResult")
                .asText()), authLog.getResult());
        assertEquals(DataType.valueOf(json.get("dataType").textValue()),
                authLog.getDataType());
        assertEquals(json.get("dataUse").isNull()
                        ? null : DataUse.valueOf(json.get("dataUse").asText()),
                authLog.getDataUse() == null
                        ? null : authLog.getDataUse());
        authLogs = AuthorizationLog.getDataSubjectAuthLogs("nap@hotmail.com",
                new Date(), new Date());
        assertEquals(authLogs.size(), 0);
    }
}
