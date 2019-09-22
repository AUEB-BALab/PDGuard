package utils;

import org.junit.Before;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import utils.OrganizationInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestOrganizationInfo {

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void testRecoverOrganisationInfo() {
        JsonNode json = OrganizationInfo.loadOrganizationInfo();
        assertNotNull(json);
        assertNotNull(json.get("dataControllerId"));
        assertNotNull(json.get("apps"));
        assertEquals(json.size(), 2);
    }
}
