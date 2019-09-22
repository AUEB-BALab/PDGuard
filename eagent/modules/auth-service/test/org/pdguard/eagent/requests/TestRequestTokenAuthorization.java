package org.pdguard.eagent.requests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.AccessDeniedException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.RequestToken;
import org.pdguard.eagent.auth.AuthorizationApi;
import org.pdguard.eagent.auth.DecryptionAuthorization;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.pdguard.eagent.models.AuthorizedApplication;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.ReqToken;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.i18n.Messages;

import static org.apache.commons.codec.binary.Hex.encodeHex;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
public class TestRequestTokenAuthorization {
    private Client client;

    @Before
    public void setUp() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        client = new Client("id", new byte[0], dataSubject, dataController,
                authApp);
    }

    @Test
    public void testProcessAuthorization()
            throws AccessDeniedException, UnverifiedRequestException {
        AuthorizationApi mockApi = mock(DecryptionAuthorization.class);
        ReqToken reqToken = new ReqToken("token", new byte[0], client);
        ReqToken mockToken = spy(reqToken);
        doNothing().when(mockToken).authorizeRequestToken();
        Mockito.when(mockApi.processAuthorization()).thenReturn(
                AuthorizationResult.ALLOWED);
        RequestTokenAuthorization auth = new RequestTokenAuthorization(client,
                mockApi, "token");
        RequestTokenAuthorization mockAuth = PowerMockito.spy(auth);
        PowerMockito.doReturn(new org.pdguard.api.model.RequestToken(
                reqToken.getId(), new String(encodeHex(reqToken.getSecret()))
        )).when(mockAuth).authorizeRequestToken();
        mockAuth.processAuthorization();
    }

    @PrepareForTest({Messages.class})
    @Test(expected = AccessDeniedException.class)
    public void testProcessAuthorizationFailure()
            throws AccessDeniedException, UnverifiedRequestException {
        AuthorizationApi mockApi = mock(DecryptionAuthorization.class);
        Mockito.when(mockApi.processAuthorization()).thenReturn(
                AuthorizationResult.DENIED_BY_DATA_SUBJECT);
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.access_denied")).thenReturn("");
        RequestTokenAuthorization auth = new RequestTokenAuthorization(client,
                mockApi, "token");
        auth.processAuthorization();
    }

    @PrepareForTest({ ReqToken.class, Messages.class })
    @Test
    public void testAuthorizeRequestToken() throws UnverifiedRequestException {
        AuthorizationApi mockApi = mock(DecryptionAuthorization.class);
        ReqToken reqToken = new ReqToken("token", new byte[0], client);
        ReqToken mockToken = spy(reqToken);
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_req_token")).thenReturn("");
        doNothing().when(mockToken).authorizeRequestToken();
        RequestTokenAuthorization auth = new RequestTokenAuthorization(client,
                mockApi, "token");
        PowerMockito.mockStatic(ReqToken.class);
        PowerMockito.when(ReqToken.authenticate("token", client.getId()))
                .thenReturn(mockToken);
        RequestToken token = auth.authorizeRequestToken();
        assertEquals(token.getRequestToken(), mockToken.getId());
        assertArrayEquals(token.getTokenSecret().getBytes(), mockToken.getSecret());
        assertTrue(token.isAuthorized());
        PowerMockito.verifyStatic();
        ReqToken.authenticate("token", client.getId());
        Mockito.verify(mockToken, Mockito.times(1)).authorizeRequestToken();
    }

    @PrepareForTest({ Messages.class })
    @Test(expected = UnverifiedRequestException.class)
    public void testValidateRequestToken() throws UnverifiedRequestException {
        ReqToken mockToken = mock(ReqToken.class);
        when(mockToken.isValid()).thenReturn(true);
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_req_token")).thenReturn("");
        RequestTokenAuthorization auth = new RequestTokenAuthorization(null,
                null, null);
        try {
            auth.validateRequestToken(mockToken);
        } catch (UnverifiedRequestException e) {
            throw new AssertionError();
        }
        when(mockToken.isValid()).thenReturn(false);
        auth.validateRequestToken(mockToken);
    }
}
