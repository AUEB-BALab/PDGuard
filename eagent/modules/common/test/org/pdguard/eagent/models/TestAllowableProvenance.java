package org.pdguard.eagent.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.libs.Json;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
public class TestAllowableProvenance {
    private AllowableProvenance authAllowableProvenance;

    private void addData() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.save();
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        AuthorizationRule authorizationRule = new AuthorizationRule(dataSubject,
                dataController, DataType.AIM_ID);
        authorizationRule.save();
        authAllowableProvenance = new AllowableProvenance(
                authorizationRule, null,
                new Date(), null);
        authAllowableProvenance.save();
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        addData();
    }

    @Test
    public void testIsAuthorized() {
        String dataSubjectId = authAllowableProvenance
                .getAuthorizationRule().getDataSubject().getId();
        String dataControllerId = authAllowableProvenance
                .getAuthorizationRule().getDataController().getId();
        DataType dataType = authAllowableProvenance
                .getAuthorizationRule().getDataType();
        boolean notAuthorized = AllowableProvenance.isAuthorized(dataSubjectId,
                dataControllerId, dataType, DataProvenance.DATA_SUBJECT_BEHAVIOR);
        assertFalse(notAuthorized);
        authAllowableProvenance.setDataProvenance(DataProvenance.DATA_CONTROLLER);
        authAllowableProvenance.update();
        boolean isAuthorized = AllowableProvenance.isAuthorized(dataSubjectId,
                dataControllerId, dataType, DataProvenance.DATA_CONTROLLER);
        assertTrue(isAuthorized);
        authAllowableProvenance.getAuthorizationRule().update();
        notAuthorized = AllowableProvenance.isAuthorized(dataSubjectId,
                dataControllerId, dataType, DataProvenance.DATA_CONTROLLER);
        assertFalse(notAuthorized);
    }

    @PrepareForTest({ AuthorizationRule.class })
    @Test
    public void testIsAuthorizedDefaultRules() {
        JsonNode defaultRules = Json.parse("{\n" +
                "    \"PERSONAL\": {\n" +
                "        \"provenances\": [\"PUBLIC_DATA\"],\n" +
                "        \"actions\": [\"INTRANET\"]\n" +
                "    }}");
        PowerMockito.mockStatic(AuthorizationRule.class);
        PowerMockito.when(AuthorizationRule.getDefaultAuthRules())
                .thenReturn(defaultRules);
        assertTrue(AllowableProvenance.isAuthorized(DataType.PERSONAL,
                DataProvenance.PUBLIC_DATA));
        assertFalse(AllowableProvenance.isAuthorized(DataType.PERSONAL,
                DataProvenance.DATA_SUBJECT_EXPLICIT));
        PowerMockito.verifyStatic(Mockito.times(2));
        AuthorizationRule.getDefaultAuthRules();
    }

    @Test
    public void testParseAllowableProvenances() {
        Set<AllowableProvenance> allowableProvenances = new HashSet<>();
        allowableProvenances.add(new AllowableProvenance(null,
                DataProvenance.DATA_CONTROLLER, new Date(), null));
        ArrayNode provenances = AllowableProvenance.parseAllowableProvenances(allowableProvenances);
        assertEquals(provenances.size(), 1);
        JsonNode provenance = provenances.get(0);
        assertEquals(provenance.size(), 4);
        assertEquals(provenance.get("dataProvenance").asText(),
                DataProvenance.DATA_CONTROLLER.toString());
        assertNotNull(provenance.get("validTo"));
        assertNotNull(provenance.get("validFrom"));
        assertTrue(provenance.get("id").isNull());
    }

    @Test
    public void testAddAllowableProvenance() {
        assertTrue(AllowableProvenance.getFinder().all().size() == 1);
        AuthorizationRule authorizationRule = AuthorizationRule.getFinder().all().get(0);
        AllowableProvenance newAllowableProvenance = new AllowableProvenance(authorizationRule,
                DataProvenance.THIRD_PARTY, new Date(), null);
        assertFalse(newAllowableProvenance.addAllowableProvenance() == 0);
        List<AllowableProvenance> authAllowableProvenances = AllowableProvenance
                .getFinder().all();
        assertEquals(1, authAllowableProvenances.size());
        AllowableProvenance retrievedAllowableProvenance = authAllowableProvenances.get(0);
        assertNotNull(retrievedAllowableProvenance);
        assertEquals(DataType.AIM_ID, authorizationRule.getDataType());
        assertEquals(DataProvenance.THIRD_PARTY, retrievedAllowableProvenance
                .getDataProvenance());
        newAllowableProvenance.setDataProvenance(DataProvenance.PUBLIC_REGISTRY);
        assertTrue(newAllowableProvenance.addAllowableProvenance() == 2);
        authAllowableProvenances = AllowableProvenance.getFinder().findList();
        assertEquals(2, authAllowableProvenances.size());
        retrievedAllowableProvenance = authAllowableProvenances.get(1);
        assertNotNull(retrievedAllowableProvenance);
        assertEquals(DataType.AIM_ID, authorizationRule.getDataType());
        assertEquals(DataProvenance.PUBLIC_REGISTRY, retrievedAllowableProvenance
                .getDataProvenance());
        assertTrue(newAllowableProvenance.addAllowableProvenance() == 0);
        assertEquals(2, authAllowableProvenances.size());
        assertEquals(AuthorizationRule.getFinder().all().size(), 1);
    }

    @Test
    public void testUpdateAllowableProvenance() {
        Date newDate = new Date();
        AuthorizationRule authorizationRule = AuthorizationRule.getFinder().all().get(0);
        AllowableProvenance updatedProvenance = new AllowableProvenance(authorizationRule,
                DataProvenance.DATA_CONTROLLER, newDate, null);
        assertEquals(0, updatedProvenance.updateAllowableProvenance(
                authAllowableProvenance.getId()));
        List<AllowableProvenance> provenances = AllowableProvenance.getFinder().findList();
        assertEquals(1, provenances.size());
        AllowableProvenance retrievedProvenance = provenances.get(0);
        assertNotNull(retrievedProvenance);
        assertEquals(retrievedProvenance.getAuthorizationRule().getDataType(),
                authorizationRule.getDataType());
        assertEquals(retrievedProvenance.getDataProvenance(),
                DataProvenance.DATA_CONTROLLER);
        assertEquals(retrievedProvenance.getValidFrom(), newDate);
    }

    @Test
    public void testFailedAllowableProvenanceUpdate() {
        AuthorizationRule authorizationRule = AuthorizationRule.getFinder().all().get(0);
        AllowableProvenance newProvenance = new AllowableProvenance(authorizationRule,
                DataProvenance.DATA_CONTROLLER, new Date(), null);
        assertEquals(newProvenance.updateAllowableProvenance(2), 1);
        List<AllowableProvenance> provenances = AllowableProvenance
                .getFinder().findList();
        assertEquals(1, provenances.size());
    }

    @Test
    public void testSetDefaultAuthRule() {
        authAllowableProvenance.setDefaultAuthRule();
        AllowableProvenance retrievedProvenance = AllowableProvenance
                .getFinder().where().eq("id", authAllowableProvenance.getId()).findUnique();
        assertNotNull(retrievedProvenance);
        assertNull(retrievedProvenance.getDataProvenance());
        assertEquals(authAllowableProvenance.getId(), retrievedProvenance.getId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(retrievedProvenance.getValidTo());
        assertEquals(calendar.get(Calendar.YEAR), 9999);
        assertEquals(calendar.get(Calendar.MONTH), Calendar.DECEMBER);
        assertEquals(calendar.get(Calendar.DATE), 31);
    }

    @Test
    public void testAuthorizationRuleDeletionSucceeded() {
        authAllowableProvenance.setDataProvenance(DataProvenance.OTHER);
        authAllowableProvenance.update();
        assertEquals(0, AllowableProvenance.deleteAllowableProvenance(authAllowableProvenance
                .getId()));
        List<AllowableProvenance> provenances = AllowableProvenance
                .getFinder().all();
        assertEquals(1, provenances.size());
        AllowableProvenance retrievedProvenance = provenances.get(0);
        assertNotNull(retrievedProvenance);
        assertNull(retrievedProvenance.getDataProvenance());
        assertEquals(authAllowableProvenance.getId(), retrievedProvenance.getId());
    }

    @Test
    public void testAuthorizationRuleDeletionFailed() {
        final int wrongID = 30;
        assertEquals(1, AllowableProvenance.deleteAllowableProvenance(wrongID));
    }
}
