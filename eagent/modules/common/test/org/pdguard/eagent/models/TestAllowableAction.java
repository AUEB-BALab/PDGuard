package org.pdguard.eagent.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
import org.junit.Test;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestAllowableAction {

    private AllowableAction authAllowableAction;

    private void addData() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.save();
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        AuthorizationRule authorizationRule = new AuthorizationRule(dataSubject, dataController,
                DataType.AIM_ID);
        authorizationRule.save();
        authAllowableAction = new AllowableAction(authorizationRule, null, new Date(), null);
        authAllowableAction.save();
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        addData();
    }

    @Test
    public void testIsAuthorized() {
        String dataSubjectId = authAllowableAction.getAuthorizationRule().getDataSubject().getId();
        String dataControllerId = authAllowableAction.getAuthorizationRule().getDataController().getId();
        DataType dataType = authAllowableAction.getAuthorizationRule().getDataType();
        boolean notAuthorized = AllowableAction.isAuthorized(dataSubjectId,
                dataControllerId, dataType, DataUse.UPDATE_THIRD_PARTY_DATA);
        assertFalse(notAuthorized);
        authAllowableAction.setDataUse(DataUse.UPDATE_THIRD_PARTY_DATA);
        authAllowableAction.update();
        boolean isAuthorized = AllowableAction.isAuthorized(dataSubjectId,
                dataControllerId, dataType, DataUse.UPDATE_THIRD_PARTY_DATA);
        assertTrue(isAuthorized);
    }

    @Test
    public void testIsAuthorizedDefault() {
        assertTrue(AllowableAction.isAuthorized(DataType.PERSONAL,
                DataUse.INTRANET));
        assertFalse(AllowableAction.isAuthorized(DataType.PERSONAL,
                DataUse.SEND_PACKAGE_TO_SUBJECT));
        assertTrue(AllowableAction.isAuthorized(DataType.IDENTITY,
                DataUse.COMPOSE_EMAIL_TO_SUBJECT));
        assertFalse(AllowableAction.isAuthorized(DataType.IDENTITY,
                DataUse.SEND_SMS_TO_SUBJECT));
        assertFalse(AllowableAction.isAuthorized(DataType.GIVEN_NAME,
                DataUse.INTRANET));
    }

    @Test
    public void testAddAllowableAction() {
        assertTrue(AllowableAction.getFinder().all().size() == 1);
        AuthorizationRule authorizationRule = AuthorizationRule.getFinder().all().get(0);
        AllowableAction newAllowableAction = new AllowableAction(authorizationRule,
                DataUse.UPDATE_THIRD_PARTY_DATA, new Date(), null);
        assertFalse(newAllowableAction.addAllowableAction() == 0);
        List<AllowableAction> authAllowableActions = AllowableAction.getFinder().all();
        assertEquals(1, authAllowableActions.size());
        AllowableAction retrievedAuthAllowableAction = authAllowableActions.get(0);
        assertNotNull(retrievedAuthAllowableAction);
        assertEquals(DataType.AIM_ID, authorizationRule.getDataType());
        assertEquals(DataUse.UPDATE_THIRD_PARTY_DATA, retrievedAuthAllowableAction
                .getDataUse());
        newAllowableAction.setDataUse(DataUse.ANALYTICS);
        assertTrue(newAllowableAction.addAllowableAction() == 2);
        authAllowableActions = AllowableAction.getFinder().findList();
        assertEquals(2, authAllowableActions.size());
        retrievedAuthAllowableAction = authAllowableActions.get(1);
        assertNotNull(retrievedAuthAllowableAction);
        assertEquals(DataType.AIM_ID, authorizationRule.getDataType());
        assertEquals(DataUse.ANALYTICS, retrievedAuthAllowableAction
                .getDataUse());
        assertTrue(newAllowableAction.addAllowableAction() == 0);
        assertEquals(2, authAllowableActions.size());
        assertEquals(AuthorizationRule.getFinder().all().size(), 1);
    }

    @Test
    public void testUpdateAllowableAction() {
        Date newDate = new Date();
        AuthorizationRule authorizationRule = AuthorizationRule.getFinder().all().get(0);
        AllowableAction updatedAuthAllowableAction = new AllowableAction(authorizationRule,
                DataUse.BROADCAST_VIDEO, newDate, null);
        assertEquals(0, updatedAuthAllowableAction.updateAllowableAction(authAllowableAction.getId()));
        List<AllowableAction> authAllowableActions = AllowableAction.getFinder().findList();
        assertEquals(1, authAllowableActions.size());
        AllowableAction retrievedAuthAllowableAction = authAllowableActions.get(0);
        assertNotNull(retrievedAuthAllowableAction);
        assertEquals(retrievedAuthAllowableAction.getAuthorizationRule().getDataType(),
                authorizationRule.getDataType());
        assertEquals(retrievedAuthAllowableAction.getDataUse(), DataUse.BROADCAST_VIDEO);
        assertEquals(retrievedAuthAllowableAction.getValidFrom(), newDate);
    }

    @Test
    public void testFailedAllowableActionUpdate() {
        AuthorizationRule authorizationRule = AuthorizationRule.getFinder().all().get(0);
        AllowableAction newAuthAllowableAction = new AllowableAction(authorizationRule,
                DataUse.ANALYTICS, new Date(), null);
        assertEquals(newAuthAllowableAction.updateAllowableAction(2), 1);
        List<AllowableAction> authAllowableActions = AllowableAction.getFinder().findList();
        assertEquals(1, authAllowableActions.size());
    }

    @Test
    public void testAllowableActionDeletionSucceeded() {
        authAllowableAction.setDataUse(DataUse.UPDATE_THIRD_PARTY_DATA);
        authAllowableAction.update();
        assertEquals(0, AllowableAction.deleteAllowableAction(authAllowableAction.getId()));
        List<AllowableAction> authAllowableActions = AllowableAction.getFinder().all();
        assertEquals(1, authAllowableActions.size());
        AllowableAction retrievedAuthAllowableAction = authAllowableActions.get(0);
        assertNotNull(retrievedAuthAllowableAction);
        assertNull(retrievedAuthAllowableAction.getDataUse());
        assertEquals(authAllowableAction.getId(), retrievedAuthAllowableAction.getId());
    }

    @Test
    public void testAllowableActionDeletionFailed() {
        final int wrongID = 30;
        assertEquals(1, AllowableAction.deleteAllowableAction(wrongID));
    }

    @Test
    public void testSetDefaultAuthRule() {
        authAllowableAction.setDefaultAuthRule();
        AllowableAction retrievedAuthAllowableAction = AllowableAction.getFinder().where()
                .eq("id", authAllowableAction.getId()).findUnique();
        assertNotNull(retrievedAuthAllowableAction);
        assertNull(retrievedAuthAllowableAction.getDataUse());
        assertEquals(authAllowableAction.getId(), retrievedAuthAllowableAction.getId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(retrievedAuthAllowableAction.getValidTo());
        assertEquals(calendar.get(Calendar.YEAR), 9999);
        assertEquals(calendar.get(Calendar.MONTH), Calendar.DECEMBER);
        assertEquals(calendar.get(Calendar.DATE), 31);
    }

    @Test
    public void testParseAllowableActions() {
        Set<AllowableAction> allowableActions = new HashSet<>();
        allowableActions.add(new AllowableAction(null, DataUse.ANALYTICS,
                new Date(), null));
        ArrayNode actions = AllowableAction.parseAllowableActions(allowableActions);
        assertEquals(actions.size(), 1);
        JsonNode action = actions.get(0);
        assertEquals(action.size(), 4);
        assertEquals(action.get("dataUse").asText(), DataUse.ANALYTICS.toString());
        assertNotNull(action.get("validTo"));
        assertNotNull(action.get("validFrom"));
        assertTrue(action.get("id").isNull());
    }
}
