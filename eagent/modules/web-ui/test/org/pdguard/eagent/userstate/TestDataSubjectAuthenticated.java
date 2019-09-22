package org.pdguard.eagent.userstate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mockito.Mockito;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.eagent.exceptions.DataNotFoundException;
import org.pdguard.eagent.logs.AuthorizationProcess;
import org.pdguard.eagent.models.*;
import org.pdguard.eagent.models.DataSubject;
import org.pdguard.eagent.rule.AllowableUse;
import org.pdguard.eagent.rule.DataSubjectAuthRule;
import junit.framework.TestCase;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.logs.AuthorizationRequestType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.libs.Json;

import java.util.*;

import static play.test.Helpers.*;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
public class TestDataSubjectAuthenticated extends TestCase {
    final private String userEmail = "nap@hotmail.com";

    final private String dataControllerName = "ABC";

    final private DataType dataType = DataType.GIVEN_NAME;

    final private DataUse dataUse = DataUse.UPDATE_THIRD_PARTY_DATA;

    private AuthorizationRule authorizationRule;

    private AuthorizationRule addData() {
        org.pdguard.eagent.models.DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                userEmail, "password");
        dataSubject.save();
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        AuthorizationRule authorizationRule = new AuthorizationRule(dataSubject,
                dataController, dataType);
        authorizationRule.getAllowableActions().add(new AllowableAction(authorizationRule, dataUse,
                new Date(), null));
        authorizationRule.save();
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        Client client = new Client("id", new byte[0], dataSubject, dataController,
                authApp);
        AuthorizationLog authorizationLog = new AuthorizationLog(
                AuthorizationResult.ALLOWED,
                AuthorizationRequestType.READ, AuthorizationProcess.DATA_SUBJECT_RULES, new Date(),
                dataType, client);
        authorizationLog.setDataUse(DataUse.UPDATE_THIRD_PARTY_DATA);
        authorizationLog.setInteractionPurpose(InteractionPurpose.ADVERT);
        authorizationLog.save();
        authorizationLog = new AuthorizationLog(AuthorizationResult.DENIED_BY_DATA_SUBJECT,
                AuthorizationRequestType.UPDATE,
                AuthorizationProcess.DATA_SUBJECT_RULES, new Date(), dataType, client);
        authorizationLog.save();
        return authorizationRule;
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        authorizationRule = addData();
    }

    @PrepareForTest({ AuthorizationRule.class })
    @Test
    public void testRetrieveAuthRules() {
        String userEmail = "test@example.com";
        List<ObjectNode> json = new ArrayList<>();
        json.add((ObjectNode) Json.parse("{\"dataType\":\"GIVEN_NAME\"," +
                "\"dataController\":\"ABC\",\"actions\":[{\"id\":1,\"dataUse\":" +
                "\"UPDATE_THIRD_PARTY_DATA\",\"validFrom\":\"2015-12-20\"," +
                "\"validTo\":\"2017-12-20\"}],\"provenances\":[]}\n"));
        PowerMockito.mockStatic(AuthorizationRule.class);
        PowerMockito.when(AuthorizationRule.getDataSubjectAuthRules(userEmail))
                .thenReturn(json);
        DataSubjectAuthenticated user = new DataSubjectAuthenticated(userEmail);
        user.retrieveAuthRules();
        assertNotNull(user.getAuthRules());
        assertEquals(user.getAuthRules().size(), 1);
        DataSubjectAuthRule authRule = user.getAuthRules().get(0);
        assertEquals(authRule.getDataController(), "ABC");
        assertEquals(authRule.getDataType(), DataType.GIVEN_NAME);
        Set<AllowableUse> allowableUses = authRule.getActions();
        assertEquals(allowableUses.size(), 1);
        AllowableUse allowableUse = allowableUses.iterator().next();
        assertEquals(allowableUse.getDataUse(), DataUse.UPDATE_THIRD_PARTY_DATA);
        assertNotNull(allowableUse.getValidFrom());
        assertNotNull(allowableUse.getValidTo());
        PowerMockito.verifyStatic(Mockito.times(1));
        AuthorizationRule.getDataSubjectAuthRules(userEmail);
    }

    @Test
    public void testCreateAuthRule() throws DataNotFoundException {
        DataSubjectAuthenticated dataSubject = PowerMockito.spy(
                new DataSubjectAuthenticated("test@example.com"));
        PowerMockito.doReturn(null).when(dataSubject).getDataSubjectAuthRule(
                "data controller", DataType.COMMON_NAME);
        DataSubjectAuthRule authRule = new DataSubjectAuthRule(DataType.COMMON_NAME,
                DataUse.ANALYTICS, null, "data controller", new Date(), new Date());
        assertEquals(dataSubject.createAuthRule(authRule), 0);
        Mockito.verify(dataSubject, Mockito.times(1)).getDataSubjectAuthRule(
                "data controller", DataType.COMMON_NAME);
        AuthorizationRule mockRule = PowerMockito.mock(AuthorizationRule.class);
        PowerMockito.doReturn(mockRule).when(dataSubject).getDataSubjectAuthRule(
                "data controller", DataType.COMMON_NAME);
        PowerMockito.doReturn(1).when(dataSubject).addAllowableAction(mockRule, authRule);
        assertEquals(dataSubject.createAuthRule(authRule), 1);
        Mockito.verify(dataSubject, Mockito.times(2)).getDataSubjectAuthRule(
                "data controller", DataType.COMMON_NAME);
        Mockito.verify(dataSubject, Mockito.times(1)).addAllowableAction(mockRule,
                authRule);
        authRule = new DataSubjectAuthRule(DataType.COMMON_NAME,
                null, DataProvenance.PUBLIC_REGISTRY, "data controller",
                new Date(), new Date());
        PowerMockito.doReturn(1).when(dataSubject).addAllowableProvenance(
                mockRule, authRule);
        assertEquals(dataSubject.createAuthRule(authRule), 1);
        Mockito.verify(dataSubject, Mockito.times(3)).getDataSubjectAuthRule(
                "data controller", DataType.COMMON_NAME);
        Mockito.verify(dataSubject, Mockito.times(1)).addAllowableProvenance(
                mockRule, authRule);
    }

    @PrepareForTest({
            AllowableAction.class,
            AllowableProvenance.class
    })
    @Test
    public void testAuthorizationRuleDeletion() {
        PowerMockito.mockStatic(AllowableAction.class);
        PowerMockito.when(AllowableAction.deleteAllowableAction(1))
                .thenReturn(0);
        DataSubjectAuthenticated dataSubject = PowerMockito.spy(
                new DataSubjectAuthenticated("test@example.com"));
        assertEquals(dataSubject.deleteAuthRule(1, true), 0);
        PowerMockito.verifyStatic(Mockito.times(1));
        AllowableAction.deleteAllowableAction(1);
        PowerMockito.mockStatic(AllowableProvenance.class);
        PowerMockito.when(AllowableProvenance.deleteAllowableProvenance(1))
                .thenReturn(0);
        assertEquals(dataSubject.deleteAuthRule(1, true), 0);
        AllowableProvenance.deleteAllowableProvenance(1);
    }

    @PrepareForTest({ AuthorizationLog.class })
    @Test
    public void testRetrieveAuthLogs() {
        List<ObjectNode> response = new LinkedList<>();
        Date validFrom = new Date();
        Date validTo = new Date();
        response.add(Json.newObject());
        DataSubjectAuthenticated dataSubject = PowerMockito.spy(
                new DataSubjectAuthenticated("test@example.com"));
        PowerMockito.mockStatic(AuthorizationLog.class);
        PowerMockito.when(AuthorizationLog.getDataSubjectAuthLogs("test@example.com",
            validFrom, validTo)).thenReturn(response);
        assertEquals(dataSubject.retrieveAuthLogs(validFrom, validTo), response);
        PowerMockito.verifyStatic(Mockito.times(1));
        AuthorizationLog.getDataSubjectAuthLogs("test@example.com",
                validFrom, validTo);
    }

    @Test(expected = DataNotFoundException.class)
    public void testLockData() throws DataNotFoundException {
        DataSubjectAuthenticated dataSubject = PowerMockito.spy(
                new DataSubjectAuthenticated("test@example.com"));
        AuthorizationRule mockRule = PowerMockito.mock(AuthorizationRule.class);
        PowerMockito.doNothing().when(mockRule).lockData();
        PowerMockito.when(dataSubject.getDataSubjectAuthRule("data controller",
                DataType.AIM_ID)).thenReturn(mockRule);
        dataSubject.lockData(DataType.AIM_ID, "data controller");
        Mockito.verify(mockRule, Mockito.times(1)).lockData();
        Mockito.verify(dataSubject, Mockito.times(1)).getDataSubjectAuthRule(
                "data controller", DataType.AIM_ID);
    }

    @PrepareForTest({
            DataSubject.class,
            DataController.class,
            AuthorizationRule.class
    })
    @Test
    public void testGetDataSubjectAuthRule() throws DataNotFoundException {
        DataSubject dataSubject = PowerMockito.mock(DataSubject.class);
        DataController dataController = PowerMockito.mock(DataController.class);
        AuthorizationRule authRule = PowerMockito.mock(AuthorizationRule.class);
        PowerMockito.mockStatic(DataSubject.class);
        PowerMockito.mockStatic(DataController.class);
        PowerMockito.mockStatic(AuthorizationRule.class);
        PowerMockito.when(DataSubject.authenticateByEmail("test@example.com"))
                .thenReturn(dataSubject);
        PowerMockito.when(DataController.authenticate("test", "test@example.com",
                DataType.AIM_ID)).thenReturn(dataController);
        PowerMockito.when(AuthorizationRule.authenticate(null, null, DataType.AIM_ID))
                .thenReturn(authRule);
        DataSubjectAuthenticated authenticated = new DataSubjectAuthenticated(
                "test@example.com");
        AuthorizationRule rule = authenticated.getDataSubjectAuthRule("test", DataType.AIM_ID);
        assertEquals(rule, authRule);
        PowerMockito.verifyStatic(Mockito.times(1));
        DataSubject.authenticateByEmail("test@example.com");
        DataController.authenticate("test", "test@example.com", DataType.AIM_ID);
        AuthorizationRule.authenticate(null, null, DataType.AIM_ID);
    }

    @PrepareForTest({
            DataSubject.class,
            DataController.class
    })
    @Test(expected = DataNotFoundException.class)
    public void testGetDataSubjectAuthRuleFailed() throws DataNotFoundException {
        DataSubject dataSubject = PowerMockito.mock(DataSubject.class);
        PowerMockito.mockStatic(DataSubject.class);
        PowerMockito.mockStatic(DataController.class);
        PowerMockito.when(DataSubject.authenticateByEmail("test@example.com"))
                .thenReturn(dataSubject);
        PowerMockito.when(DataController.authenticate("test", "test@example.com",
                DataType.AIM_ID)).thenReturn(null);
        DataSubjectAuthenticated authenticated = new DataSubjectAuthenticated(
                "test@example.com");
        authenticated.getDataSubjectAuthRule("test", DataType.AIM_ID);
    }
}
