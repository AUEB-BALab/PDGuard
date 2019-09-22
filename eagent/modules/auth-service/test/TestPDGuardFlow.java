import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.pdguard.api.model.*;
import org.pdguard.api.security.Signature;
import org.pdguard.api.utils.*;
import org.pdguard.eagent.controllers.authservice.routes;
import org.pdguard.eagent.models.*;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.Key;
import org.pdguard.eagent.models.ReqToken;
import org.pdguard.eagent.utils.CredentialsGenerator;
import org.pdguard.eagent.utils.EscrowAgentInfo;
import org.pdguard.eagent.utils.KeyManager;
import play.mvc.Http;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

import static org.apache.commons.codec.binary.Hex.encodeHex;
import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;


public class TestPDGuardFlow extends WithApplication {
    private DataType dataType = DataType.AIM_ID;

    private DataUse dataUse = DataUse.APPLICATION_DESKTOP;

    private String clientId = UUID.randomUUID().toString();

    private String clientSecret;

    private static final String contentType = "application/x-www-form-urlencoded";

    private void addData() {
        DataController dataController = new DataController("id", "ABC");
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.save();
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "ABC");
        authApp.save();
        byte[] clientKey = CredentialsGenerator.generateHmacSHA1Secret();
        clientSecret = String.valueOf(encodeHex(clientKey));
        Client client = new Client(clientId, clientKey, dataSubject, dataController,
                authApp);
        client.save();
        KeyManager.addEncryptionKey(dataSubject, dataController);
        AuthorizationRule authorizationRule = new AuthorizationRule(dataSubject, dataController,
                dataType);
        authorizationRule.getAllowableActions().add(new AllowableAction(authorizationRule, dataUse,
                new Date(), null));
        authorizationRule.save();
    }

    private TreeMap<String, String> prepareParametersRequestTokenGeneration()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put("client_id", clientId);
        parameters.put("nonce", UUID.randomUUID().toString());
        parameters.put("timestamp", String.valueOf(new Date().getTime()));
        String uri = EscrowAgentInfo.getUrl() + EscrowAgent.getRequestTokenService();
        SignatureBase signatureBase = new SignatureBase(parameters,
                uri, "POST");
        SecretKeySpec secretKey = new SecretKeySpec((clientSecret + "&")
                .getBytes(), "HmacSHA1");
        Signature signature = new Signature(signatureBase
                .getSignatureBaseString(), secretKey);
        signature.signMessage();
        String signatureContent = signature.getSignatureContent();
        parameters.put("signature", signatureContent);
        return parameters;
    }

    private TreeMap<String, String> prepareAuthorizationParameters(
            final String token, final String tokenSecret)
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = new TreeMap<>();
        DecryptionBundle authBundle = new DecryptionBundle(dataType, dataUse,
                InteractionPurpose.ADVERT);
        parameters.put("client_id", clientId);
        parameters.put("nonce", UUID.randomUUID().toString());
        parameters.put("timestamp", String.valueOf(new Date().getTime()));
        parameters.put("request_token", token);
        parameters.put("data_type", authBundle.getDataType().toString());
        parameters.put("data_use", authBundle.getDataUse().toString());
        parameters.put("interaction_purpose", authBundle.getInteractionPurpose().toString());
        parameters.put("request_type", PDGuardRequestType.DECRYPTION.toString());
        String uri = EscrowAgentInfo.getUrl() + EscrowAgent.getAuthorizationService();
        SignatureBase signatureBase = new SignatureBase(parameters,
                uri, "POST");
        SecretKeySpec secretKey = new SecretKeySpec((clientSecret + "&" + tokenSecret)
                .getBytes(), "HmacSHA1");
        Signature signature = new Signature(signatureBase
                .getSignatureBaseString(), secretKey);
        signature.signMessage();
        String signatureContent = signature.getSignatureContent();
        parameters.put("signature", signatureContent);
        return parameters;
    }

    private TreeMap<String, String> prepareParametersRequestTokenExchange(
            final String token, final String tokenSecret)
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put("client_id", clientId);
        parameters.put("nonce", UUID.randomUUID().toString());
        parameters.put("timestamp", String.valueOf(new Date().getTime()));
        parameters.put("request_token", token);
        String uri = EscrowAgentInfo.getUrl() + EscrowAgent.getEncryptionKeyService();
        SignatureBase signatureBase = new SignatureBase(parameters,
                uri, "POST");
        SecretKeySpec secretKey = new SecretKeySpec((clientSecret + "&"
                + tokenSecret).getBytes(), "HmacSHA1");
        Signature signature = new Signature(signatureBase
                .getSignatureBaseString(), secretKey);
        signature.signMessage();
        String signatureContent = signature.getSignatureContent();
        parameters.put("signature", signatureContent);
        return parameters;
    }

    private void validateResponse(final Result result, final int status,
            final String contentType, final String charSet) {
        assertEquals(status, result.status());
        assertEquals(contentType, result.contentType());
        assertEquals(charSet, result.charset());
    }

    @Override
    protected FakeApplication provideFakeApplication() {
        return new FakeApplication(new java.io.File("."), Helpers.class.getClassLoader(),
                ImmutableMap.of("play.http.router", "auth.Routes"), new ArrayList<>(), null);
    }

    private org.pdguard.api.model.RequestToken generateRequestToken()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareParametersRequestTokenGeneration();
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        Result result = route(request);
        validateResponse(result, OK, contentType, "utf-8");
        org.pdguard.api.model.RequestToken token = (org.pdguard.api.model.RequestToken)
                ResponseParser.toObject(contentAsString(result),
                        org.pdguard.api.model.RequestToken.class);
        assertNotNull(token.getRequestToken());
        assertNotNull(token.getTokenSecret());
        assertFalse(token.isAuthorized());
        assertEquals(ReqToken.getFinder().all().size(), 1);
        return token;
    }

    private void authorizeRequestToken(
            final org.pdguard.api.model.RequestToken token)
            throws UnsupportedEncodingException {
        TreeMap<String, String>parameters = prepareAuthorizationParameters(
                token.getRequestToken(), token.getTokenSecret());
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.authorizeRequestToken()))
                .method(POST);
        Result result = route(request);
        validateResponse(result, SEE_OTHER, null, null);
        String nextURL = result.redirectLocation();
        assertTrue(nextURL.contains("/decrypt?"));
        result = route(new Http.RequestBuilder().uri(nextURL).method(GET));
        validateResponse(result, OK, contentType, "utf-8");
        org.pdguard.api.model.RequestToken requestToken =
                (org.pdguard.api.model.RequestToken) ResponseParser.toObject(
                        contentAsString(result), org.pdguard.api.model.RequestToken.class);
        assertEquals(requestToken.getRequestToken(), token.getRequestToken());
        assertEquals(requestToken.getTokenSecret(), token.getTokenSecret());
        assertTrue(requestToken.isAuthorized());
        assertEquals(ReqToken.getFinder().all().size(), 1);
    }

    private org.pdguard.api.model.EncryptionKey exchangeRequestToken(
            final org.pdguard.api.model.RequestToken requestToken)
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters =
                prepareParametersRequestTokenExchange(requestToken.getRequestToken(),
                        requestToken.getTokenSecret());
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestTokenExchange()))
                .method(POST);
        Result result = route(request);
        validateResponse(result, OK, contentType, "utf-8");
        org.pdguard.api.model.EncryptionKey key = (org.pdguard.api.model.EncryptionKey)
                ResponseParser.toObject(contentAsString(result),
                        org.pdguard.api.model.EncryptionKey.class);
        assertNotNull(key.getEncryptionKey());
        assertEquals(Key.getFinder().all().size(), 1);
        assertEquals(ReqToken.getFinder().all().size(), 0);
        return key;
    }

    @Before
    public void setUp() {
        fakeApplication(inMemoryDatabase());
        addData();
    }

    @Test
    public void testPDGuardFlow() throws UnsupportedEncodingException {
        org.pdguard.api.model.RequestToken token = generateRequestToken();
        authorizeRequestToken(token);
        org.pdguard.api.model.EncryptionKey key = exchangeRequestToken(token);
        token = generateRequestToken();
        authorizeRequestToken(token);
        org.pdguard.api.model.EncryptionKey key2 = exchangeRequestToken(token);
        assertEquals(key.getEncryptionKey(), key2.getEncryptionKey());
        assertEquals(Nonce.getFinder().all().size(), 6);
    }

    @Test(expected = AssertionError.class)
    public void testWrongPDGuardFlow() throws UnsupportedEncodingException {
        org.pdguard.api.model.RequestToken token = new
                org.pdguard.api.model.RequestToken("fsdfds", "Gfdgfdgf");
        exchangeRequestToken(token);
    }

    @Test(expected = AssertionError.class)
    public void testWrongPDGuardFlow2() throws UnsupportedEncodingException {
        org.pdguard.api.model.RequestToken token = new
                org.pdguard.api.model.RequestToken("fsdfds", "Gfdgfdgf");
        authorizeRequestToken(token);
    }

    @Test(expected = AssertionError.class)
    public void testWrongPDGuardFlow3() throws UnsupportedEncodingException {
        org.pdguard.api.model.RequestToken token = generateRequestToken();
        exchangeRequestToken(token);
    }
}
