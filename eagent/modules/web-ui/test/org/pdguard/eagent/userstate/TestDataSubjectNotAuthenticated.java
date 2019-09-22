package org.pdguard.eagent.userstate;

import org.mockito.Mockito;
import org.pdguard.eagent.models.DataSubject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;

@RunWith(value = PowerMockRunner.class)
public class TestDataSubjectNotAuthenticated {
    @PrepareForTest({ DataSubject.class })
    @Test
    public void testValidateUser() {
        String userEmail = "test@example.com";
        String userPassword = "password";
        PowerMockito.mockStatic(DataSubject.class);
        PowerMockito.when(DataSubject.isAuthenticated(userEmail,
                userPassword)).thenReturn(true);
        assertTrue(new DataSubjectNotAuthenticated(userEmail,
                userPassword).isAuthenticated());
        PowerMockito.verifyStatic(Mockito.times(1));
        DataSubject.isAuthenticated(userEmail, userPassword);
    }
}
