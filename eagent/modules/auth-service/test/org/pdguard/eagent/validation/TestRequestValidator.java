package org.pdguard.eagent.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.Signature;
import org.pdguard.eagent.forms.ParametersBundle;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.Nonce;
import org.pdguard.eagent.utils.CredentialsGenerator;
import org.pdguard.eagent.utils.EscrowAgentInfo;
import org.pdguard.eagent.utils.ParametersMapper;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.i18n.Messages;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

import static org.apache.commons.codec.binary.Hex.encodeHex;

@PowerMockIgnore("javax.crypto.*")
@RunWith(PowerMockRunner.class)
public class TestRequestValidator {
    private RequestValidator validator;

    private byte[] secret = CredentialsGenerator.generateHmacSHA1Secret();

    @Before
    public void setUp() throws UnsupportedEncodingException {
        ParametersBundle bundle = new ParametersBundle("client id", "signature",
                "nonce", 1000L);
        TreeMap<String, String> parameters = ParametersMapper.mapObject(
                ParametersBundle.class, bundle);
        String uri = EscrowAgentInfo.getUrl() + EscrowAgent.getRequestTokenService();
        SignatureBase signatureBase = new SignatureBase(parameters,
                uri, "POST");
        String key = new String((encodeHex(secret)));
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Signature signature = new Signature(signatureBase
                .getSignatureBaseString(), secretKey);
        signature.signMessage();
        validator = new RequestValidator(new ParametersBundle("client id",
                signature.getSignatureContent(), "nonce", 1000L));
    }

    @PrepareForTest({Client.class, Messages.class})
    @Test(expected = UnverifiedRequestException.class)
    public void testValidateClient() throws UnverifiedRequestException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.unknown_client")).thenReturn("");
        Client mockClient = PowerMockito.mock(Client.class);
        PowerMockito.mockStatic(Client.class);
        PowerMockito.when(Client.authenticate(validator.getParams()
                .getClientId())).thenReturn(mockClient);
        validator.validateClient();
        PowerMockito.verifyStatic();
        Client.authenticate(validator.getParams().getClientId());
        PowerMockito.when(Client.authenticate(validator.getParams()
                .getClientId())).thenReturn(null);
        validator.validateClient();
    }

    @PrepareForTest({ Nonce.class, Client.class, Messages.class })
    @Test(expected = UnverifiedRequestException.class)
    public void testValidateNonce() throws Exception {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_nonce")).thenReturn("");
        PowerMockito.mockStatic(Nonce.class);
        PowerMockito.when(Nonce.exists(validator.getParams().getNonce()))
                .thenReturn(true);
        validator.validateNonce();
        PowerMockito.verifyStatic();
        Nonce.exists(validator.getParams().getNonce());
    }

    @PrepareForTest({ Messages.class })
    @Test(expected = UnverifiedRequestException.class)
    public void testValidateTimestamp() throws UnverifiedRequestException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_timestamp")).thenReturn("");
        validator.validateTimestamp();
    }

    @Test
    public void testValidateSignature()
            throws UnsupportedEncodingException, UnverifiedRequestException {
        validator.validateSignature(EscrowAgentInfo.getUrl()
                + EscrowAgent.getRequestTokenService(), new String(encodeHex(secret)));
    }

    @PrepareForTest({ Messages.class })
    @Test(expected = UnverifiedRequestException.class)
    public void testValidateSignatureFailed()
            throws UnsupportedEncodingException, UnverifiedRequestException {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.get("auth.invalid_signature")).thenReturn("");
        validator.validateSignature(EscrowAgentInfo.getUrl()
                + EscrowAgent.getAuthorizationService(), new String(encodeHex(secret)));
    }

    @Test
    public void testValidateRequest() throws UnverifiedRequestException,
            UnsupportedEncodingException {
        RequestValidator mockValidator = PowerMockito.spy(validator);
        Mockito.doNothing().when(mockValidator).validateNonce();
        Mockito.doNothing().when(mockValidator).validateTimestamp();
        Mockito.doNothing().when(mockValidator).validateSignature(
                Mockito.anyString(), Mockito.anyString());
        mockValidator.validateRequest("uri", "singature");
        Mockito.verify(mockValidator, Mockito.times(1)).validateNonce();
        Mockito.verify(mockValidator, Mockito.times(1)).validateTimestamp();
        Mockito.verify(mockValidator, Mockito.times(1)).validateSignature(
                Mockito.anyString(), Mockito.anyString());
    }
}
