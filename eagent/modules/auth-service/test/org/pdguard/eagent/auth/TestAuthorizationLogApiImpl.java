package org.pdguard.eagent.auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;
import org.pdguard.eagent.logs.AuthorizationProcess;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.models.AuthorizedApplication;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestAuthorizationLogApiImpl {
    private AuthorizationLogApiImpl mockApi;

    private DecryptionAuthorization mockDecryption;

    private EncryptionAuthorization mockEncryption;

    private DefaultDecryptionAuthorization mockDefaultDecryption;

    private DefaultEncryptionAuthorization mockDefaultEncryption;

    @Before
    public void setUp() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        Client client = new Client("id", new byte[0], dataSubject, dataController,
                authApp);
        AuthorizationLogApiImpl logApi = new AuthorizationLogApiImpl(client);
        mockApi = PowerMockito.spy(logApi);
        mockDecryption = PowerMockito.spy(new DecryptionAuthorization(new DecryptionBundle(
                DataType.AIM_ID, DataUse.ANALYTICS, InteractionPurpose.ADVERT),
                dataSubject.getId(), dataController.getId()));
        mockDefaultDecryption = PowerMockito.spy(new DefaultDecryptionAuthorization(
                new DecryptionBundle(DataType.AIM_ID, DataUse.ANALYTICS,
                        InteractionPurpose.ADVERT)));
        mockEncryption = PowerMockito.spy(new EncryptionAuthorization(
                new EncryptionBundle(DataType.AIM_ID,
                        DataProvenance.DATA_CONTROLLER, true), dataSubject, dataController));
        mockDefaultEncryption = PowerMockito.spy(new DefaultEncryptionAuthorization(
                new EncryptionBundle(DataType.AIM_ID, DataProvenance.DATA_CONTROLLER, true),
                dataSubject, dataController));
    }

    @Test
    public void testAddAuthorizationLogDecryption() {
        DecryptionBundle authBundle = mockDecryption.getAuthBundle();
        PowerMockito.doNothing().when(mockApi).addDecryptionAuthorizationLog(
                authBundle, AuthorizationResult.ALLOWED,
                AuthorizationProcess.DATA_SUBJECT_RULES);
        PowerMockito.doReturn(AuthorizationResult.ALLOWED).when(mockDecryption).getAuthResult();
        mockApi.addAuthorizationLog(mockDecryption);
        Mockito.verify(mockApi, Mockito.times(1)).addDecryptionAuthorizationLog(
                mockDecryption.getAuthBundle(), AuthorizationResult.ALLOWED,
                AuthorizationProcess.DATA_SUBJECT_RULES);
    }

    @Test
    public void testAddAuthorizationLogDefaultDecryption() {
        DecryptionBundle authBundle = mockDefaultDecryption.getAuthBundle();
        PowerMockito.doNothing().when(mockApi).addDecryptionAuthorizationLog(
                authBundle, AuthorizationResult.ALLOWED,
                AuthorizationProcess.DEFAULT);
        PowerMockito.doReturn(AuthorizationResult.ALLOWED).when(mockDefaultDecryption)
                .getAuthResult();
        mockApi.addAuthorizationLog(mockDefaultDecryption);
        Mockito.verify(mockApi, Mockito.times(1)).addDecryptionAuthorizationLog(
                mockDefaultDecryption.getAuthBundle(), AuthorizationResult.ALLOWED,
                AuthorizationProcess.DEFAULT);
    }

    @Test
    public void testAddAuthorizationLogEncryption() {
        EncryptionBundle authBundle = mockEncryption.getAuthBundle();
        PowerMockito.doNothing().when(mockApi).addEncryptionAuthorizationLog(
                authBundle, AuthorizationResult.ALLOWED,
                AuthorizationProcess.DATA_SUBJECT_RULES);
        PowerMockito.doReturn(AuthorizationResult.ALLOWED).when(mockEncryption)
                .getAuthResult();
        mockApi.addAuthorizationLog(mockEncryption);
        Mockito.verify(mockApi, Mockito.times(1)).addEncryptionAuthorizationLog(
                mockEncryption.getAuthBundle(), AuthorizationResult.ALLOWED,
                AuthorizationProcess.DATA_SUBJECT_RULES);
    }

    @Test
    public void testAddAuthorizationLogDefaultEncryption() {
        EncryptionBundle authBundle = mockDefaultEncryption.getAuthBundle();
        PowerMockito.doNothing().when(mockApi).addEncryptionAuthorizationLog(
                authBundle, AuthorizationResult.ALLOWED,
                AuthorizationProcess.DEFAULT);
        PowerMockito.doReturn(AuthorizationResult.ALLOWED).when(mockDefaultEncryption)
                .getAuthResult();
        mockApi.addAuthorizationLog(mockDefaultEncryption);
        Mockito.verify(mockApi, Mockito.times(1)).addEncryptionAuthorizationLog(
                mockDefaultEncryption.getAuthBundle(), AuthorizationResult.ALLOWED,
                AuthorizationProcess.DEFAULT);
    }
}
