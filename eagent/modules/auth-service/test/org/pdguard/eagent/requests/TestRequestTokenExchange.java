package org.pdguard.eagent.requests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.UnretrievableKeyException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.EncryptionKey;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.utils.DataType;
import org.pdguard.eagent.forms.ParametersBundleReq;
import org.pdguard.eagent.models.*;
import org.pdguard.eagent.utils.CredentialsGenerator;
import org.pdguard.eagent.utils.EscrowAgentInfo;
import org.pdguard.eagent.utils.KeyManager;
import org.pdguard.eagent.validation.RequestValidator;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.i18n.Messages;

import static org.apache.commons.codec.binary.Hex.encodeHex;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class TestRequestTokenExchange {
    private RequestTokenExchange rtExchange;

    private Client client;

    private ReqToken reqToken;

    private void mockMessages() {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_req_token")).thenReturn("");
        PowerMockito.when(Messages.get("auth.invalid_nonce")).thenReturn("");
        PowerMockito.when(Messages.get("auth.invalid_timestamp")).thenReturn("");
    }

    @Before
    public void setUp() {
        String clientId = "client id";
        Long timestamp = 1000L;
        String nonce = "nonce";
        RequestTokenExchange rt = new RequestTokenExchange(
                new ParametersBundleReq(clientId, "signature", nonce, timestamp,
                        "req"), DataType.AIM_ID);
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        client = new Client("id", CredentialsGenerator.generateHmacSHA1Secret(),
                dataSubject, dataController, authApp);
        reqToken = new ReqToken("token", new byte[0], client);
        rtExchange = PowerMockito.spy(rt);
    }

    @PrepareForTest({ ReqToken.class, RequestValidator.class })
    @Test
    public void testValidateRequest() throws Exception {
        Client mockClient = PowerMockito.spy(client);
        ReqToken mockToken = PowerMockito.spy(reqToken);
        RequestValidator fakeValidator = PowerMockito.mock(RequestValidator.class);
        String signatureKey = new String(encodeHex(client.getSecret())) + "&"
                + new String(encodeHex(reqToken.getSecret()));
        doNothing().when(fakeValidator).validateRequest(EscrowAgentInfo.getUrl()
                + EscrowAgent.getEncryptionKeyService(), signatureKey);
        when(fakeValidator.validateClient()).thenReturn(mockClient);
        when(rtExchange.initValidator()).thenReturn(fakeValidator);
        PowerMockito.mockStatic(ReqToken.class);
        PowerMockito.when(ReqToken.authenticate(rtExchange.getParams()
                .getRequestToken(), mockClient.getId()))
                .thenReturn(mockToken);
        doNothing().when(rtExchange).validateRequestToken(mockToken);
        rtExchange.validateRequest();
        Mockito.verify(fakeValidator, Mockito.times(1)).validateRequest(
                EscrowAgentInfo.getUrl() + EscrowAgent.getEncryptionKeyService(),
                signatureKey);
        Mockito.verify(rtExchange, Mockito.times(1)).validateRequestToken(mockToken);
        Mockito.verify(fakeValidator, Mockito.times(1)).validateClient();
        PowerMockito.verifyStatic();
        ReqToken.authenticate(rtExchange.getParams()
                .getRequestToken(), mockClient.getId());
    }

    @PrepareForTest({KeyManager.class})
    @Test
    public void testExchangeEncryptionKey() throws UnretrievableKeyException {
        EncryptionKey mockKey = new EncryptionKey("key");
        PowerMockito.mockStatic(KeyManager.class);
        PowerMockito.when(KeyManager.requestEncryptionKey(client.getDataSubject()
                .getId(), client.getDataController().getId(), DataType.AIM_ID))
                .thenReturn(mockKey);
        ReqToken mockToken = PowerMockito.mock(ReqToken.class);
        Mockito.doNothing().when(mockToken).revokeRequestToken(true);
        rtExchange.setReqToken(mockToken);
        rtExchange.setClient(client);
        org.pdguard.api.model.EncryptionKey key = rtExchange.exchangeEncryptionKey();
        Assert.assertEquals(key.getEncryptionKey(), "key");
        PowerMockito.verifyStatic();
        KeyManager.requestEncryptionKey(client.getDataSubject()
                .getId(), client.getDataController().getId(), DataType.AIM_ID);
    }

    @PrepareForTest({ Messages.class })
    @Test(expected = UnverifiedRequestException.class)
    public void testValidateRequestToken1() throws UnverifiedRequestException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_req_token")).thenReturn("");
        ReqToken mockToken = PowerMockito.mock(ReqToken.class);
        Mockito.when(mockToken.isValid()).thenReturn(false);
        Mockito.when(mockToken.isAuthorized()).thenReturn(true);
        Mockito.doNothing().when(mockToken).revokeRequestToken(true);
        rtExchange.validateRequestToken(mockToken);
    }

    @PrepareForTest({ Messages.class })
    @Test(expected = UnverifiedRequestException.class)
    public void testValidateRequestToken2() throws UnverifiedRequestException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_req_token")).thenReturn("");
        ReqToken mockToken = PowerMockito.mock(ReqToken.class);
        Mockito.when(mockToken.isValid()).thenReturn(true);
        Mockito.when(mockToken.isAuthorized()).thenReturn(false);
        Mockito.doNothing().when(mockToken).revokeRequestToken(true);
        rtExchange.validateRequestToken(mockToken);
    }

    @PrepareForTest({ Messages.class })
    @Test(expected = UnverifiedRequestException.class)
    public void testValidateRequestToken3() throws UnverifiedRequestException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_req_token")).thenReturn("");
        rtExchange.validateRequestToken(null);
    }
}
