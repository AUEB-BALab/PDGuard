package forms;

import models.Customer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestRegistration {
    @Test
    public void testCheckPassword() {
        Assert.assertTrue(new Registration("name", "surname", "email@test.com",
                "password", "password", "eagent", "id").checkPassword());
        Assert.assertFalse(new Registration("name", "surname", "email@test.com",
                "password", "wrong password", "eagent", "id").checkPassword());
    }

    @PrepareForTest({Customer.class})
    @Test
    public void testIsDataSubjectIdAvailable() {
        String dataSubjectId = "id";
        PowerMockito.mockStatic(Customer.class);
        PowerMockito.when(Customer.isDataSubjectIdAvailable(dataSubjectId))
                .thenReturn(false);
        Registration reg = new Registration("name", "surname", "email@test.com",
                "password", "password", "eagent", dataSubjectId);
        Assert.assertFalse(reg.isDataSubjectIdAvailable());
        PowerMockito.verifyStatic(Mockito.times(1));
        Customer.isDataSubjectIdAvailable(dataSubjectId);

        PowerMockito.when(Customer.isDataSubjectIdAvailable(dataSubjectId))
                .thenReturn(true);
        Assert.assertTrue(reg.isDataSubjectIdAvailable());
        PowerMockito.verifyStatic(Mockito.times(2));
        Customer.isDataSubjectIdAvailable(dataSubjectId);
    }
}
