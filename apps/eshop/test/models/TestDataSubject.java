package models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestDataSubject {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        new DataSubject("id", "eagent", "clientId", "secret").save();
    }

    @Test
    public void testGetDataSubject() {
        Assert.assertNull(DataSubject.getDataSubject("wrong id"));
        DataSubject dataSubject = DataSubject.getDataSubject("id");
        Assert.assertNotNull(dataSubject);
        Assert.assertEquals(dataSubject.getId(), "id");
        Assert.assertEquals(dataSubject.getEagent(), "eagent");
        Assert.assertEquals(dataSubject.getClientId(), "clientId");
        Assert.assertEquals(dataSubject.getSecret(), "secret");
    }

    @Test
    public void testDataSubjectExists() {
        Assert.assertTrue(DataSubject.subjectExists("id"));
        Assert.assertFalse(DataSubject.subjectExists("wrong id"));
    }
}
