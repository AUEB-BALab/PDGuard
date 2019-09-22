package org.pdguard.eagent.requests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.eagent.forms.ParametersBundle;
import org.pdguard.eagent.models.AuthorizedApplication;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.DataController;
import org.pdguard.eagent.models.DataSubject;
import org.pdguard.eagent.utils.CredentialsGenerator;
import org.pdguard.eagent.utils.EscrowAgentInfo;
import org.pdguard.eagent.validation.RequestValidator;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.apache.commons.codec.binary.Hex.encodeHex;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class TestRequestTokenGeneration {
    private RequestTokenGeneration rtGeneration;

    private Client client;

    @Before
    public void setUp() {
        RequestTokenGeneration rt = new RequestTokenGeneration(
                new ParametersBundle("client id", "signature", "nonce", 1000L));
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        client = new Client("id", CredentialsGenerator.generateHmacSHA1Secret(),
                dataSubject, dataController, authApp);
        rtGeneration = PowerMockito.spy(rt);
    }

    @Test
    public void testGenerateRequestToken() throws Exception {
        doNothing().when(rtGeneration).addRequestToken(anyString(), any(byte[].class));
        org.pdguard.api.model.RequestToken rt = rtGeneration.generateRequestToken();
        Mockito.verify(rtGeneration, Mockito.times(1))
                .addRequestToken(anyString(), any(byte[].class));
        Assert.assertNotNull(rt.getRequestToken());
        Assert.assertNotNull(rt.getTokenSecret());
    }

    @PrepareForTest({ RequestValidator.class })
    @Test
    public void testValidateRequest() throws Exception {
        Client mockClient = PowerMockito.spy(client);
        RequestValidator fakeValidator = PowerMockito.mock(RequestValidator.class);
        String signatureKey = new String(encodeHex(client.getSecret())) + "&";
        doNothing().when(fakeValidator).validateRequest(EscrowAgentInfo.getUrl()
                + EscrowAgent.getRequestTokenService(), signatureKey);
        when(rtGeneration.initValidator()).thenReturn(fakeValidator);
        when(fakeValidator.validateClient()).thenReturn(mockClient);
        rtGeneration.validateRequest();
        Mockito.verify(fakeValidator, Mockito.times(1)).validateRequest(
                EscrowAgentInfo.getUrl() + EscrowAgent.getRequestTokenService(),
                signatureKey);
        Mockito.verify(fakeValidator, Mockito.times(1)).validateClient();
    }
}
