package org.pdguard.api.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.AccessDeniedException;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.AuthorizationBundle;
import org.pdguard.api.model.DecryptionBundle;
import org.pdguard.api.model.EncryptionBundle;
import org.pdguard.api.model.RequestToken;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.SecureConnection;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.api.utils.InteractionPurpose;
import org.pdguard.api.utils.PDGuardRequestType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;


public class TestAuthorizationService {
    /** a successful connection to Escrow Agent. */
    private SecureConnection secure;

    private AuthorizationBundle encryptBundle;

    private AuthorizationBundle decryptBundle;

    private RequestToken requestToken;

    private ClientCredentials client;

    private EscrowAgent escrowAgent;

    private RequestToken stringToRequestToken(final String requestToken)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(requestToken);
        return mapper.treeToValue(json, RequestToken.class);
    }

    public void initializeSecureConnection() {
        secure = new SecureConnection();
    }

    @Before
    public void setUp() throws IOException {
        initializeSecureConnection();
        client = new ClientCredentials("client id", "client secret");
        escrowAgent = new EscrowAgent("https://localhost:9443");
        encryptBundle = new EncryptionBundle(DataType.AIM_ID,
                DataProvenance.DATA_CONTROLLER, true);
        decryptBundle = new DecryptionBundle(DataType.AIM_ID,
                DataUse.UPDATE_THIRD_PARTY_DATA, InteractionPurpose.ADVERT);
        String jsonString = "{\"requestToken\": \"token\", \"tokenSecret\":"
                + "\"secret\"}";
        requestToken = stringToRequestToken(jsonString);
    }

    @Test
    public void testGetParametersEncryption() throws IOException {
        AuthorizationService service = new AuthorizationService(client, escrowAgent,
                secure, encryptBundle, requestToken, PDGuardRequestType.ENCRYPTION);
        TreeMap<String, String> parameters = service.getParameters();
        Assert.assertEquals(parameters.get("client_id"), client.getClientId());
        Assert.assertEquals(parameters.get("request_token"),
                requestToken.getRequestToken());
        Assert.assertEquals(parameters.get("data_type"),
                encryptBundle.getDataType().toString());
        Assert.assertEquals(parameters.get("data_provenance"),
                ((EncryptionBundle) encryptBundle).getDataProvenance().toString());
        Assert.assertEquals(parameters.get("update"),
                String.valueOf(((EncryptionBundle) encryptBundle).isUpdate()));
        Assert.assertEquals(parameters.size(), 8);
        Assert.assertNotNull(parameters.get("timestamp"));
        Assert.assertNotNull(parameters.get("nonce"));
        Assert.assertNull(parameters.get("data_use"));
        Assert.assertNull(parameters.get("interaction_purpose"));
        Assert.assertEquals(parameters.get("request_type"),
                PDGuardRequestType.ENCRYPTION.toString());
    }

    @Test
    public void testGetParametersDecryption() throws IOException {
        AuthorizationService service = new AuthorizationService(client, escrowAgent,
                secure, decryptBundle, requestToken, PDGuardRequestType.DECRYPTION);
        TreeMap<String, String> parameters = service.getParameters();
        Assert.assertEquals(parameters.get("client_id"), client.getClientId());
        Assert.assertEquals(parameters.get("request_token"),
                requestToken.getRequestToken());
        Assert.assertEquals(parameters.get("data_type"),
                decryptBundle.getDataType().toString());
        Assert.assertEquals(parameters.get("data_use"),
                ((DecryptionBundle) decryptBundle).getDataUse().toString());
        Assert.assertEquals(parameters.get("interaction_purpose"),
                ((DecryptionBundle) decryptBundle)
                        .getInteractionPurpose().toString());
        Assert.assertEquals(parameters.size(), 8);
        Assert.assertNotNull(parameters.get("timestamp"));
        Assert.assertNotNull(parameters.get("nonce"));
        Assert.assertNull(parameters.get("data_provenance"));
        Assert.assertNull(parameters.get("update"));
        Assert.assertEquals(parameters.get("request_type"),
                PDGuardRequestType.DECRYPTION.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testGetParametersWrongBundle1() {
        AuthorizationService service = new AuthorizationService(client,
                escrowAgent, secure, decryptBundle, requestToken,
                PDGuardRequestType.ENCRYPTION);
        service.getParameters();
    }

    @Test(expected = RuntimeException.class)
    public void testGetParametersWrongBundle2() {
        AuthorizationService service = new AuthorizationService(client,
                escrowAgent, secure, encryptBundle, requestToken,
                PDGuardRequestType.DECRYPTION);
        service.getParameters();
    }

    @Test(expected = UnverifiedRequestException.class)
    public void testCheckErrorResponse() throws IOException,
            EscrowAgentErrorResponseException {
        AuthorizationService service = new AuthorizationService(client, escrowAgent,
                secure, encryptBundle, requestToken, PDGuardRequestType.ENCRYPTION);
        service.checkErrorResponse(401, "authentication_error=error");
    }

    @Test(expected = AccessDeniedException.class)
    public void testCheckErrorResponse2()
            throws IOException, EscrowAgentErrorResponseException {
        AuthorizationService service = new AuthorizationService(client, escrowAgent,
                secure, encryptBundle, requestToken, PDGuardRequestType.ENCRYPTION);
        service.checkErrorResponse(401, "authorization_error=error");
    }

    @Test(expected = EscrowAgentErrorResponseException.class)
    public void testCheckErrorResponse3()
            throws IOException, EscrowAgentErrorResponseException {
        AuthorizationService service = new AuthorizationService(client, escrowAgent,
                secure, encryptBundle, requestToken, PDGuardRequestType.ENCRYPTION);
        service.checkErrorResponse(400, "error=error");
    }

    @Test
    public void testGetNonce() {
        AuthorizationService service = new AuthorizationService(client, escrowAgent,
                secure, encryptBundle, requestToken, PDGuardRequestType.ENCRYPTION);
        Assert.assertNotNull(service.getNonce());
    }

    @Test
    public void testSignRequest() throws UnsupportedEncodingException {
        AuthorizationService service = new AuthorizationService(client, escrowAgent,
                secure, encryptBundle, requestToken, PDGuardRequestType.ENCRYPTION);
        SignatureBase signatureBase = new SignatureBase(service.getParameters(),
                "url", "POST");
        Assert.assertNotNull(service.signRequest(signatureBase, "key"));
    }

    @Test
    public void testAuthorizeRequestToken()
            throws IOException, EscrowAgentErrorResponseException {
        AuthorizationService mockService = Mockito.spy(new AuthorizationService(
                client, escrowAgent, secure, encryptBundle, requestToken,
                PDGuardRequestType.ENCRYPTION));
        Mockito.doReturn("request_token=token&token_secret=secret&authorized=true")
                .when((EscrowAgentService) mockService)
                .callEscrowAgentService(Mockito.anyString(), Mockito.anyString());
        RequestToken reqToken = mockService.authorizeRequestToken();
        Assert.assertEquals(reqToken.getRequestToken(), "token");
        Assert.assertEquals(reqToken.getTokenSecret(), "secret");
        Assert.assertTrue(reqToken.isAuthorized());
        Mockito.verify((EscrowAgentService) mockService, Mockito.times(1))
                .callEscrowAgentService(Mockito.anyString(), Mockito.anyString());
    }
}
