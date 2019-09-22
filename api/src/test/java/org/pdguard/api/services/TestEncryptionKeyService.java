package org.pdguard.api.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.InvalidRequestTokenException;
import org.pdguard.api.exceptions.UnretrievableKeyException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.EncryptionKey;
import org.pdguard.api.model.RequestToken;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.SecureConnection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

public class TestEncryptionKeyService {
    /** a successful connection to Escrow Agent. */
    private SecureConnection secure;

    private ClientCredentials client;

    private RequestToken requestToken;

    private RequestToken unauthorizedToken;

    private EscrowAgent escrowAgent;

    public void initializeSecureConnection() {
        secure = new SecureConnection();
    }

    @Before
    public void setUp() throws IOException {
        initializeSecureConnection();
        client = new ClientCredentials("client id", "client secret");
        escrowAgent = new EscrowAgent("https://localhost:9443");
        requestToken = new RequestToken("token", "secret");
        requestToken.setAuthorized(true);
        unauthorizedToken = new RequestToken("token", "secret");
    }

    @Test
    public void testGetParameters() throws IOException {
        EncryptionKeyService service = new EncryptionKeyService(client,
                escrowAgent, secure, requestToken);
        TreeMap<String, String> parameters = service.getParameters();
        Assert.assertEquals(parameters.get("client_id"), client.getClientId());
        Assert.assertEquals(parameters.get("request_token"),
                requestToken.getRequestToken());
        Assert.assertEquals(parameters.size(), 4);
        Assert.assertNotNull(parameters.get("timestamp"));
        Assert.assertNotNull(parameters.get("nonce"));
    }

    @Test(expected = InvalidRequestTokenException.class)
    public void testUnauthorizedRequestToken() throws IOException,
            EscrowAgentErrorResponseException  {
        EncryptionKeyService service = new EncryptionKeyService(client,
                escrowAgent, secure, unauthorizedToken);
        service.requestEncryptionKey();
    }

    @Test(expected = UnverifiedRequestException.class)
    public void testCheckErrorResponse() throws IOException,
            EscrowAgentErrorResponseException {
        EncryptionKeyService service = new EncryptionKeyService(client,
                escrowAgent, secure, requestToken);
        service.checkErrorResponse(401, "authentication_error=error");
    }

    @Test(expected = EscrowAgentErrorResponseException.class)
    public void testCheckErrorResponse2()
            throws IOException, EscrowAgentErrorResponseException {
        EncryptionKeyService service = new EncryptionKeyService(client,
                escrowAgent, secure, requestToken);
        service.checkErrorResponse(400, "authentication_error=error");
    }

    @Test(expected = UnretrievableKeyException.class)
    public void testCheckErrorResponse3()
            throws IOException, EscrowAgentErrorResponseException {
        EncryptionKeyService service = new EncryptionKeyService(client,
                escrowAgent, secure, requestToken);
        service.checkErrorResponse(401, "key_error=error");
    }

    @Test
    public void testGetNonce() {
        EncryptionKeyService service = new EncryptionKeyService(client,
                escrowAgent, secure, requestToken);
        Assert.assertNotNull(service.getNonce());
    }

    @Test
    public void testSignRequest() throws UnsupportedEncodingException {
        EncryptionKeyService service = new EncryptionKeyService(client,
                escrowAgent, secure, requestToken);
        SignatureBase signatureBase = new SignatureBase(service.getParameters(),
                "url", "POST");
        Assert.assertNotNull(service.signRequest(signatureBase, "key"));
    }

    @Test
    public void testRequestEncryptionKey()
            throws IOException, EscrowAgentErrorResponseException {
        EncryptionKeyService mockService = Mockito.spy(new EncryptionKeyService(
                client, escrowAgent, secure, requestToken));
        Mockito.doReturn("encryption_key=key")
                .when((EscrowAgentService) mockService)
                .callEscrowAgentService(Mockito.anyString(), Mockito.anyString());
        EncryptionKey key = mockService.requestEncryptionKey();
        Assert.assertEquals(key.getEncryptionKey(), "key");
        Mockito.verify((EscrowAgentService) mockService, Mockito.times(1))
                .callEscrowAgentService(Mockito.anyString(), Mockito.anyString());
    }
}
