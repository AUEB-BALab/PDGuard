package org.pdguard.eagent.userstate;

import org.mockito.Mockito;
import org.pdguard.eagent.exceptions.EmailNotAvailableException;
import org.pdguard.eagent.exceptions.PasswordsNotMatchException;
import org.pdguard.eagent.exceptions.UsernameNotAvailableException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class TestDataSubjectNew {
    final String firstName = "Nick";

    final String surname = "Leventis";

    final String email = "nap@hotmail.com";

    final String username = "username2";

    final String password = "password";

    final String repeatedPassword = "password";

    DataSubjectNew newUser;

    @Before
    public void setUp() {
        newUser = PowerMockito.spy(new DataSubjectNew(firstName, surname,
                email, username, password, repeatedPassword));
    }

    @Test
    public void testVerifyPasswords() {
        assertTrue(new DataSubjectNew(firstName, surname, email, username, password,
                repeatedPassword).verifyPasswords());
        assertFalse(new DataSubjectNew(firstName, surname, email, username, password,
                "different password").verifyPasswords());
    }

    @Test(expected = PasswordsNotMatchException.class)
    public void testPasswordsNotMach() throws PasswordsNotMatchException,
            EmailNotAvailableException, UsernameNotAvailableException {
        PowerMockito.when(newUser.verifyPasswords()).thenReturn(false);
        newUser.addUser();
    }

    @Test(expected = EmailNotAvailableException.class)
    public void testEmailNotAvailable() throws PasswordsNotMatchException,
            EmailNotAvailableException, UsernameNotAvailableException {
        PowerMockito.doReturn(true).when(newUser).verifyPasswords();
        PowerMockito.doReturn(false).when((DataSubject) newUser).isEmailAvailable();
        newUser.addUser();
    }

    @Test(expected = UsernameNotAvailableException.class)
    public void testUsernameNotAvailable() throws PasswordsNotMatchException,
            UsernameNotAvailableException, EmailNotAvailableException {
        PowerMockito.doReturn(true).when(newUser).verifyPasswords();
        PowerMockito.doReturn(true).when((DataSubject) newUser).isEmailAvailable();
        PowerMockito.doReturn(false).when(newUser).isUsernameAvailable();
        newUser.addUser();
    }

    @PrepareForTest({ org.pdguard.eagent.models.DataSubject.class })
    @Test
    public void testIsUsernameAvailable() {
        PowerMockito.mockStatic(org.pdguard.eagent.models.DataSubject.class);
        PowerMockito.when(org.pdguard.eagent.models.DataSubject.isIdAvailable("username2"))
                .thenReturn(true);
        assertFalse(newUser.isUsernameAvailable());
        PowerMockito.verifyStatic(Mockito.times(1));
        org.pdguard.eagent.models.DataSubject.isIdAvailable("username2");
    }
}
