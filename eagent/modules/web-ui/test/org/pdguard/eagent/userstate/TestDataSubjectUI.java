package org.pdguard.eagent.userstate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(value = PowerMockRunner.class)
public class TestDataSubjectUI {
    @PrepareForTest({org.pdguard.eagent.models.DataSubject.class })
    @Test
    public void testIsEmailAvailable() {
        String userEmail = "test@example.com";
        PowerMockito.mockStatic(org.pdguard.eagent.models.DataSubject.class);
        PowerMockito.when(org.pdguard.eagent.models.DataSubject
                .isEmailAvailable(userEmail)).thenReturn(true);
        Assert.assertFalse(new DataSubject(userEmail).isEmailAvailable());
        PowerMockito.verifyStatic(Mockito.times(1));
        org.pdguard.eagent.models.DataSubject.isEmailAvailable(userEmail);
    }
}
