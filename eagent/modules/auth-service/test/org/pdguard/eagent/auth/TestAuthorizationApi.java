package org.pdguard.eagent.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.models.AllowableAction;
import org.pdguard.eagent.models.AllowableProvenance;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
public class TestAuthorizationApi {
    private DataType dataType = DataType.IDENTITY;

    private DataUse dataUse = DataUse.INTRANET;

    private InteractionPurpose interPurpose = InteractionPurpose.ADVERT;

    private DataProvenance dataProvenance = DataProvenance.DATA_CONTROLLER;

    @PrepareForTest({ AllowableAction.class })
    @Test
    public void testProcessAuthorizationDecryption() {
        DecryptionBundle decryptionBundle = new DecryptionBundle(dataType,
                dataUse, interPurpose);
        String dataSubjectId = "data subject id";
        String dataControllerId = "data controller id";
        DecryptionAuthorization auth = new DecryptionAuthorization(
                decryptionBundle, dataSubjectId, dataControllerId);
        PowerMockito.mockStatic(AllowableAction.class);
        PowerMockito.when(AllowableAction.isAuthorized(dataSubjectId,
                dataControllerId, dataType, dataUse))
                .thenReturn(true);
        auth.processAuthorization();
        assertEquals(AuthorizationResult.ALLOWED, auth.getAuthResult());
        PowerMockito.verifyStatic();
        AllowableAction.isAuthorized(dataSubjectId,
                dataControllerId, dataType, dataUse);
        PowerMockito.when(AllowableAction.isAuthorized(dataSubjectId,
                dataControllerId, dataType, dataUse))
                .thenReturn(false);
        auth.processAuthorization();
        assertEquals(AuthorizationResult.DENIED_BY_DATA_SUBJECT,
                auth.getAuthResult());
        AllowableAction.isAuthorized(dataSubjectId,
                dataControllerId, dataType, dataUse);
    }

    @PrepareForTest({ AllowableAction.class })
    @Test
    public void testProcessAuthorizationDefaultDecryption() {
        DecryptionBundle decryptionBundle = new DecryptionBundle(dataType,
                dataUse, interPurpose);
        DefaultDecryptionAuthorization auth = new DefaultDecryptionAuthorization(
                decryptionBundle);
        PowerMockito.mockStatic(AllowableAction.class);
        PowerMockito.when(AllowableAction.isAuthorized(dataType, dataUse))
                .thenReturn(true);
        auth.processAuthorization();
        assertEquals(AuthorizationResult.ALLOWED, auth.getAuthResult());
        PowerMockito.verifyStatic();
        AllowableAction.isAuthorized(dataType, dataUse);
        PowerMockito.when(AllowableAction.isAuthorized(dataType, dataUse))
                .thenReturn(false);
        auth.processAuthorization();
        assertEquals(AuthorizationResult.DENIED_BY_DATA_SUBJECT,
                auth.getAuthResult());
        AllowableAction.isAuthorized(dataType, dataUse);
    }

    @PrepareForTest({ AllowableProvenance.class })
    @Test
    public void testProcessAuthorizationEncryption() {
        DataSubject dataSubject = new DataSubject("ID", "james", "james@example.com",
                "password");
        DataController dataController = new DataController("ID", "example");
        EncryptionBundle encryptionBundle = new EncryptionBundle(dataType,
                dataProvenance, true);
        EncryptionAuthorization auth = new EncryptionAuthorization(
                encryptionBundle, dataSubject, dataController);
        PowerMockito.mockStatic(AllowableProvenance.class);
        PowerMockito.when(AllowableProvenance.isAuthorized(dataSubject.getId(),
                dataController.getId(), dataType, dataProvenance)).thenReturn(true);
        auth.processAuthorization();
        assertEquals(AuthorizationResult.ALLOWED, auth.getAuthResult());
        PowerMockito.verifyStatic();
        AllowableProvenance.isAuthorized(dataSubject.getId(), dataController.getId(),
                dataType, dataProvenance);
        PowerMockito.when(AllowableProvenance.isAuthorized(dataSubject.getId(),
                dataController.getId(), dataType, dataProvenance)).thenReturn(false);
        auth.processAuthorization();
        assertEquals(AuthorizationResult.DENIED_BY_DATA_SUBJECT,
                auth.getAuthResult());
        AllowableProvenance.isAuthorized(dataSubject.getId(), dataController.getId(),
                dataType, dataProvenance);
        encryptionBundle = new EncryptionBundle(dataType, dataProvenance, false);
        auth = new EncryptionAuthorization(encryptionBundle, dataSubject, dataController);
        EncryptionAuthorization mockAuth = PowerMockito.spy(auth);
        doNothing().when(mockAuth).addDefaultAuthorizationRule();
        mockAuth.processAuthorization();
        Mockito.verify(mockAuth, times(1)).addDefaultAuthorizationRule();
        assertEquals(AuthorizationResult.ALLOWED, mockAuth.getAuthResult());
    }

    @PrepareForTest({ AllowableProvenance.class })
    @Test
    public void testProcessAuthorizationDefaultEncryption() {
        DataSubject dataSubject = new DataSubject("ID", "james", "james@example.com",
                "password");
        DataController dataController = new DataController("ID", "example");
        EncryptionBundle encryptionBundle = new EncryptionBundle(dataType,
                dataProvenance, true);
        DefaultEncryptionAuthorization auth = new DefaultEncryptionAuthorization(
                encryptionBundle, dataSubject, dataController);
        PowerMockito.mockStatic(AllowableProvenance.class);
        PowerMockito.when(AllowableProvenance.isAuthorized(dataType, dataProvenance))
                .thenReturn(true);
        auth.processAuthorization();
        assertEquals(AuthorizationResult.ALLOWED, auth.getAuthResult());
        PowerMockito.verifyStatic();
        AllowableProvenance.isAuthorized(dataType, dataProvenance);
        PowerMockito.when(AllowableProvenance.isAuthorized(dataType, dataProvenance))
                .thenReturn(false);
        auth.processAuthorization();
        assertEquals(AuthorizationResult.DENIED_BY_DATA_SUBJECT, auth.getAuthResult());
        AllowableProvenance.isAuthorized(dataType, dataProvenance);
        encryptionBundle = new EncryptionBundle(dataType, dataProvenance, false);
        auth = new DefaultEncryptionAuthorization(encryptionBundle, dataSubject, dataController);
        DefaultEncryptionAuthorization mockAuth = PowerMockito.spy(auth);
        doNothing().when(mockAuth).addDefaultAuthorizationRule();
        mockAuth.processAuthorization();
        Mockito.verify(mockAuth, times(1)).addDefaultAuthorizationRule();
        assertEquals(AuthorizationResult.ALLOWED, mockAuth.getAuthResult());
    }
}
