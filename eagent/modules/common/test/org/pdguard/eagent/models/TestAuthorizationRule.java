package org.pdguard.eagent.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestAuthorizationRule {
    private AuthorizationRule authorizationRule;

    private void addData() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.save();
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        authorizationRule = new AuthorizationRule(dataSubject, dataController,
                DataType.AIM_ID);
        authorizationRule.getAllowableActions().add(new AllowableAction(
                authorizationRule, DataUse.UPDATE_THIRD_PARTY_DATA,
                new Date(), null));
        authorizationRule.save();
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        addData();
    }

    @Test
    public void testAuthenticate() {
        assertNull(AuthorizationRule.authenticate("wrong id", authorizationRule
                .getDataController().getId(), authorizationRule.getDataType()));
        assertNotNull(AuthorizationRule.authenticate(authorizationRule.getDataSubject().getId(),
                authorizationRule.getDataController().getId(), authorizationRule.getDataType()));
    }

    @Test
    public void testAddDefaultAuthRule() {
        AuthorizationRule perData = new AuthorizationRule(authorizationRule.getDataSubject(),
                authorizationRule.getDataController(), DataType.GIVEN_NAME);
        perData.addDefaultAuthRule();
        List<AuthorizationRule> ps = AuthorizationRule.getFinder().all();
        assertEquals(ps.size(), 4);
        AuthorizationRule data1 = ps.get(1);
        assertEquals(DataType.GIVEN_NAME, data1.getDataType());
        assertEquals(data1.getAllowableActions().size(), 1);
        assertNull(data1.getAllowableActions().iterator().next().getDataUse());
        assertEquals(data1.getAllowableProvenances().size(), 1);
        assertNull(data1.getAllowableProvenances().iterator().next().getDataProvenance());
        AuthorizationRule data2 = ps.get(2);
        assertEquals(data2.getAllowableActions().size(), 1);
        assertEquals(data2.getAllowableProvenances().size(), 1);
        assertNull(data2.getAllowableProvenances().iterator().next().getDataProvenance());
        assertEquals(DataType.GIVEN_NAME.getParent(), data2.getDataType());
        assertNull(data2.getAllowableActions().iterator().next().getDataUse());
        AuthorizationRule data3 = ps.get(3);
        assertEquals(DataType.GIVEN_NAME.getParent().getParent(), data3.getDataType());
        assertEquals(data3.getAllowableActions().size(), 1);
        assertNull(data3.getAllowableActions().iterator().next().getDataUse());
        assertEquals(data3.getAllowableProvenances().size(), 1);
        assertNull(data3.getAllowableProvenances().iterator().next().getDataProvenance());
    }

    @Test
    public void testLockData() {
        authorizationRule.getAllowableActions().add(new AllowableAction(authorizationRule,
                DataUse.APPLICATION_REMOTE, new Date(), null));
        authorizationRule.update();
        assertEquals(AllowableAction.getFinder().all().size(), 2);
        AuthorizationRule.getFinder().all().get(0).lockData();
        List<AllowableAction> authAllowableActions = AllowableAction.getFinder().all();
        assertEquals(authAllowableActions.size(), 1);
        assertNull(authAllowableActions.get(0).getDataUse());
        assertTrue(authAllowableActions.get(0).getId() == 1
                || authAllowableActions.get(0).getId() == 2);
    }

    @Test
    public void testDataSubjectAuthRules() {
        List<ObjectNode> authRules = AuthorizationRule.getDataSubjectAuthRules(
                authorizationRule.getDataSubject().getDataSubjectEmail());
        AllowableAction allowableAction = authorizationRule.getAllowableActions().iterator().next();
        assertEquals(authRules.size(), 1);
        ObjectNode json = authRules.get(0);
        assertEquals(json.size(), 4);
        assertEquals(DataType.valueOf(json.get("dataType").textValue()),
                authorizationRule.getDataType());
        assertEquals(json.get("dataController").textValue(),
                authorizationRule.getDataController()
                        .getDataControllerName());
        JsonNode actions = json.get("actions");
        assertTrue(actions.isArray());
        assertEquals(1, actions.size());
        JsonNode authRule = actions.get(0);
        assertEquals(4, authRule.size());
        assertEquals(allowableAction.getDataUse().toString(), authRule.get("dataUse").textValue());
        assertEquals(allowableAction.getId(), (Integer) authRule.get("id").asInt());
        assertNotNull(authRule.get("validTo"));
        assertNotNull(authRule.get("validFrom"));
        JsonNode provenances = json.get("provenances");
        assertTrue(provenances.isArray());
        assertEquals(0, provenances.size());
    }
}
