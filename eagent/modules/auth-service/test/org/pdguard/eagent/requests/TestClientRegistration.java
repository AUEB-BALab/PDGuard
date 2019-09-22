package org.pdguard.eagent.requests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.RegistrationFailedException;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.eagent.forms.RegistrationBundle;
import org.pdguard.eagent.models.AuthorizedApplication;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.pdguard.eagent.utils.KeyManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.i18n.Messages;

@RunWith(PowerMockRunner.class)
public class TestClientRegistration {
    private ClientRegistration registration;
    @Before
    public void setUp() {
        registration = new ClientRegistration(new RegistrationBundle(
                "data subject", "data controller", "app"));
    }

    @PrepareForTest({
            DataSubject.class,
            DataController.class,
            AuthorizedApplication.class,
            Messages.class
    })
    @Test(expected = RegistrationFailedException.class)
    public void testValidateRequest1() throws RegistrationFailedException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("client.data_subject")).thenReturn("");
        PowerMockito.mockStatic(DataSubject.class);
        PowerMockito.when(DataSubject.authenticateById("data subject"))
                .thenReturn(null);
        PowerMockito.mockStatic(DataController.class);
        PowerMockito.when(DataController.authenticate("data controller"))
                .thenReturn(null);
        PowerMockito.mockStatic(AuthorizedApplication.class);
        PowerMockito.when(AuthorizedApplication.authenticate("app",
                "data subject")).thenReturn(null);
        registration.validateRequest();
        PowerMockito.verifyStatic();
        DataSubject.authenticateById("data subject");
        DataController.authenticate("data controller");
        AuthorizedApplication.authenticate("app", "data controller");

    }

    @PrepareForTest({
            DataSubject.class,
            DataController.class,
            AuthorizedApplication.class,
            Messages.class
    })
    @Test(expected = RegistrationFailedException.class)
    public void testValidateRequest2() throws RegistrationFailedException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("client.data_controller")).thenReturn("");
        DataSubject mockDataSubject = PowerMockito.mock(DataSubject.class);
        PowerMockito.mockStatic(DataSubject.class);
        PowerMockito.when(DataSubject.authenticateById("data subject"))
                .thenReturn(mockDataSubject);
        PowerMockito.mockStatic(DataController.class);
        PowerMockito.when(DataController.authenticate("data controller"))
                .thenReturn(null);
        PowerMockito.mockStatic(AuthorizedApplication.class);
        PowerMockito.when(AuthorizedApplication.authenticate("app",
                "data subject")).thenReturn(null);
        registration.validateRequest();
        PowerMockito.verifyStatic();
        DataSubject.authenticateById("data subject");
        DataController.authenticate("data controller");
        AuthorizedApplication.authenticate("app", "data controller");
    }

    @PrepareForTest({
            DataSubject.class,
            DataController.class,
            AuthorizedApplication.class,
            Messages.class
    })
    @Test(expected = RegistrationFailedException.class)
    public void testValidateRequest3() throws RegistrationFailedException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("client.auth_app")).thenReturn("");
        DataSubject mockDataSubject = PowerMockito.mock(DataSubject.class);
        DataController mockDataController = PowerMockito.mock(DataController.class);
        PowerMockito.mockStatic(DataSubject.class);
        PowerMockito.when(DataSubject.authenticateById("data subject"))
                .thenReturn(mockDataSubject);
        PowerMockito.mockStatic(DataController.class);
        PowerMockito.when(DataController.authenticate("data controller"))
                .thenReturn(mockDataController);
        PowerMockito.mockStatic(AuthorizedApplication.class);
        PowerMockito.when(AuthorizedApplication.authenticate("app",
                "data subject")).thenReturn(null);
        registration.validateRequest();
        PowerMockito.verifyStatic();
        DataSubject.authenticateById("data subject");
        DataController.authenticate("data controller");
        AuthorizedApplication.authenticate("app", "data controller");
    }

    @PrepareForTest({
            DataSubject.class,
            DataController.class,
            AuthorizedApplication.class,
            Client.class,
            Messages.class
    })
    @Test(expected = RegistrationFailedException.class)
    public void testValidateRequest4() throws RegistrationFailedException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("client.reg_failed")).thenReturn("");
        DataSubject mockDataSubject = PowerMockito.mock(DataSubject.class);
        DataController mockDataController = PowerMockito.mock(DataController.class);
        AuthorizedApplication mockApp = PowerMockito.mock(AuthorizedApplication.class);
        PowerMockito.mockStatic(DataSubject.class);
        PowerMockito.when(DataSubject.authenticateById("data subject"))
                .thenReturn(mockDataSubject);
        PowerMockito.mockStatic(DataController.class);
        PowerMockito.when(DataController.authenticate("data controller"))
                .thenReturn(mockDataController);
        PowerMockito.mockStatic(AuthorizedApplication.class);
        PowerMockito.when(AuthorizedApplication.authenticate("app",
                "data subject")).thenReturn(mockApp);
        PowerMockito.mockStatic(Client.class);
        PowerMockito.when(Client.isUnique("data subject", "data controller",
                "app")).thenReturn(false);
        registration.validateRequest();
        PowerMockito.verifyStatic();
        DataSubject.authenticateById("data subject");
        DataController.authenticate("data controller");
        AuthorizedApplication.authenticate("app", "data controller");
        Client.isUnique("data subject", "data controller",
                "app");
    }

    @PrepareForTest({KeyManager.class})
    @Test
    public void testRegisterClient() throws Exception {
        ClientRegistration mockRegistration = PowerMockito.spy(registration);
        Mockito.doNothing().when(mockRegistration).addClient(Mockito.anyString(),
                Mockito.any(byte[].class));
        PowerMockito.mockStatic(KeyManager.class);
        PowerMockito.doNothing().when(KeyManager.class, "addEncryptionKey",
                null, null);
        ClientCredentials credentials = mockRegistration.registerClient();
        Assert.assertNotNull(credentials.getClientId());
        Assert.assertNotNull(credentials.getClientSecret());
        Mockito.verify(mockRegistration, Mockito.times(1)).addClient(
                Mockito.anyString(), Mockito.any(byte[].class));
        PowerMockito.verifyStatic();
        KeyManager.addEncryptionKey(null, null);
    }
}
