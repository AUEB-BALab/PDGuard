package org.pdguard.api.services;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.RequestToken;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.SecureConnection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

public class TestRequestTokenService {
    /** a successful connection to Escrow Agent. */
    private SecureConnection secure;

    private RequestTokenService service;

    public void initializeSecureConnection() {
        secure = new SecureConnection();
    }

    @Before
    public void setUp() throws IOException {
        initializeSecureConnection();
        ClientCredentials client = new ClientCredentials("client id", "client secret");
        EscrowAgent escrowAgent = new EscrowAgent("https://localhost:9443");
        service = new RequestTokenService(client, escrowAgent, secure);
    }

    @Test
    public void testGetParameters() throws IOException {
        ClientCredentials client = new ClientCredentials("client id", "client secret");
        EscrowAgent escrowAgent = new EscrowAgent("https://localhost:9443");
        RequestTokenService service = new RequestTokenService(client,
                escrowAgent, secure);
        TreeMap<String, String> parameters = service.getParameters();
        Assert.assertEquals(parameters.get("client_id"), client.getClientId());
        Assert.assertEquals(parameters.size(), 3);
        Assert.assertNotNull(parameters.get("timestamp"));
        Assert.assertNotNull(parameters.get("nonce"));
    }

    @Test(expected = UnverifiedRequestException.class)
    public void testCheckErrorResponse() throws IOException,
            EscrowAgentErrorResponseException {
        service.checkErrorResponse(401, "authentication_error=error");
    }

    @Test(expected = EscrowAgentErrorResponseException.class)
    public void testCheckErrorResponse2()
            throws IOException, EscrowAgentErrorResponseException {
        service.checkErrorResponse(400, "authentication_error=error");
    }

    @Test(expected = EscrowAgentErrorResponseException.class)
    public void testCheckErrorResponse3()
            throws IOException, EscrowAgentErrorResponseException {
        service.checkErrorResponse(500, "authentication_error=error");
    }

    @Test
    public void testGetNonce() {
        Assert.assertNotNull(service.getNonce());
    }

    @Test
    public void testSignRequest() throws UnsupportedEncodingException {
        SignatureBase signatureBase = new SignatureBase(service.getParameters(),
                "url", "POST");
        Assert.assertNotNull(service.signRequest(signatureBase, "key"));
    }

    @Test
    public void testGetRequestToken() throws IOException,
            EscrowAgentErrorResponseException {
        RequestTokenService mockService = Mockito.spy(service);
        Mockito.doReturn("request_token=token&token_secret=secret&authorized=false")
                .when((EscrowAgentService) mockService)
                .callEscrowAgentService(Mockito.anyString(), Mockito.anyString());
        RequestToken reqToken = mockService.getRequestToken();
        Assert.assertEquals(reqToken.getRequestToken(), "token");
        Assert.assertEquals(reqToken.getTokenSecret(), "secret");
        Assert.assertFalse(reqToken.isAuthorized());
        Mockito.verify((EscrowAgentService) mockService, Mockito.times(1))
                .callEscrowAgentService(Mockito.anyString(), Mockito.anyString());
    }
}
