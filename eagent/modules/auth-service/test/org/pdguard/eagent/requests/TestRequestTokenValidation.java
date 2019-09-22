package org.pdguard.eagent.requests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.AuthorizationBundle;
import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.PDGuardRequestType;
import org.pdguard.api.utils.InteractionPurpose;
import org.pdguard.eagent.forms.ParametersBundleReq;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.pdguard.eagent.models.AuthorizedApplication;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.ReqToken;
import org.pdguard.eagent.utils.CredentialsGenerator;
import org.pdguard.eagent.utils.EscrowAgentInfo;
import org.pdguard.eagent.validation.RequestValidator;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

import static org.apache.commons.codec.binary.Hex.encodeHex;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@RunWith(PowerMockRunner.class)
public class TestRequestTokenValidation {
    private RequestTokenValidation rtValidation;

    private DataType dataType = DataType.AIM_ID;

    private DataUse dataUse = DataUse.UPDATE_THIRD_PARTY_DATA;

    private InteractionPurpose interPurpose = InteractionPurpose.ADVERT;

    private Client client;

    @Before
    public void setUp() {
        AuthorizationBundle authBundle = new DecryptionBundle(dataType,
                dataUse, interPurpose);
        String clientId = "client id";
        String nonce = "nonce";
        Long timestamp = 1000L;
        RequestTokenValidation rt = new RequestTokenValidation(
                new ParametersBundleReq(clientId, "signature", nonce, timestamp,
                        "req"), authBundle, PDGuardRequestType.DECRYPTION);
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        client = new Client("id", CredentialsGenerator.generateHmacSHA1Secret(),
                dataSubject, dataController, authApp);
        rtValidation = PowerMockito.spy(rt);
    }

    @Test
    public void testAddAuthorizationParameters() {
        TreeMap<String, String> parameters = new TreeMap<>();
        rtValidation.addAuthorizationParameters(parameters);
        assertEquals(parameters.get("data_type"), dataType.toString());
        assertEquals(parameters.get("data_use"), dataUse.toString());
        assertEquals(parameters.get("interaction_purpose"),
                interPurpose.toString());
    }

    @PrepareForTest({ ReqToken.class })
    @Test
    public void testValidateAuthorizationRequest()
            throws UnverifiedRequestException, UnsupportedEncodingException {
        Client mockClient = PowerMockito.spy(client);
        ReqToken token = new ReqToken("token", new byte[0], mockClient);
        ReqToken mockToken = PowerMockito.spy(token);
        RequestValidator fakeValidator = PowerMockito.mock(RequestValidator.class);
        String signatureKey = new String(encodeHex(client.getSecret())) + "&"
                + new String(encodeHex(mockToken.getSecret()));
        doNothing().when(fakeValidator).validateRequest(EscrowAgentInfo.getUrl()
                + EscrowAgent.getAuthorizationService(), signatureKey);
        doNothing().when(rtValidation).validateRequestToken(mockToken);
        when(fakeValidator.validateClient()).thenReturn(mockClient);
        when(rtValidation.initValidator()).thenReturn(fakeValidator);
        PowerMockito.mockStatic(ReqToken.class);
        PowerMockito.when(ReqToken.authenticate(rtValidation.getParams()
                .getRequestToken(), mockClient.getId()))
                .thenReturn(mockToken);
        rtValidation.validateAuthorizationRequest();
        Mockito.verify(fakeValidator, Mockito.times(1)).validateRequest(
                EscrowAgentInfo.getUrl() + EscrowAgent.getAuthorizationService(),
                signatureKey);
        PowerMockito.verifyStatic();
        ReqToken.authenticate(rtValidation.getParams()
                .getRequestToken(), mockClient.getId());
        verify(rtValidation, times(1)).validateRequestToken(mockToken);
    }
}
