package org.pdguard.api.services;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.RegistrationFailedException;
import org.pdguard.api.model.Client;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.SecureConnection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;


public class TestRegistrationService {
    /** a successful connection to Escrow Agent. */
    private SecureConnection secure;

    private Client client;

    private RegistrationService service;

    public void initializeSecureConnection() {
        secure = new SecureConnection();
    }

    @Before
    public void setUp() throws IOException {
        initializeSecureConnection();
        client = new Client("data subject", "data controller",
                "app");
        EscrowAgent escrowAgent = new EscrowAgent("https://localhost:9443");
        service = new RegistrationService(client, escrowAgent, secure);
    }

    @Test
    public void testGetParameters() throws IOException {
        TreeMap<String, String> parameters = service.getParameters();
        Assert.assertEquals(parameters.get("data_subject_id"),
                client.getDataSubjectId());
        Assert.assertEquals(parameters.get("data_controller_id"),
                client.getDataControllerId());
        Assert.assertEquals(parameters.get("auth_app_id"), client.getAppId());
    }

    @Test(expected = RegistrationFailedException.class)
    public void testCheckErrorResponse() throws IOException,
            EscrowAgentErrorResponseException {
        service.checkErrorResponse(400, "registration_error=error");
    }

    @Test(expected = EscrowAgentErrorResponseException.class)
    public void testCheckErrorResponse2()
            throws IOException, EscrowAgentErrorResponseException {
        service.checkErrorResponse(400, "error=error");
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
    public void testRegister() throws IOException, EscrowAgentErrorResponseException {
        RegistrationService mockService = Mockito.spy(service);
        Mockito.doReturn("client_id=id&client_secret=secret")
                .when((EscrowAgentService) mockService)
                .callEscrowAgentService(Mockito.anyString(), Mockito.anyString());
        ClientCredentials clientCredentials = mockService.register();
        Assert.assertEquals(clientCredentials.getClientId(), "id");
        Assert.assertEquals(clientCredentials.getClientSecret(), "secret");
        Mockito.verify((EscrowAgentService) mockService, Mockito.times(1))
                .callEscrowAgentService(Mockito.anyString(), Mockito.anyString());
    }
}
