package org.pdguard.eagent.controllers.authservice;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.pdguard.api.model.*;
import org.pdguard.api.model.EncryptionKey;
import org.pdguard.api.security.Signature;
import org.pdguard.api.utils.*;
import org.pdguard.eagent.models.*;
import org.pdguard.eagent.models.Client;
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
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

public class TestPDGuardService extends WithApplication {
    private String clientId = "client id";

    private String tokeId = UUID.randomUUID().toString();

    private String nonce = UUID.randomUUID().toString();

    private long timestamp = new Date().getTime();

    private String clientSecret;

    private String tokenSecret;

    private String signatureContent;

    private static final String contentType = "application/x-www-form-urlencoded";

    private void addData() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        byte[] clientKey = CredentialsGenerator.generateHmacSHA1Secret();
        byte[] tokenKey = CredentialsGenerator.generateHmacSHA1Secret();
        clientSecret = String.valueOf(encodeHex(clientKey));
        tokenSecret = String.valueOf(encodeHex(tokenKey));
        Client client = new Client(clientId, clientKey, dataSubject, dataController,
                authApp);
        client.save();
        KeyManager.addEncryptionKey(dataSubject, dataController);
        ReqToken reqToken = new ReqToken(tokeId, tokenKey, client);
        PDGuardService.getDataTypes().put(tokeId, DataType.AIM_ID);
        reqToken.save();
    }

    private TreeMap<String, String> prepareParameters()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put("client_id", clientId);
        parameters.put("nonce", nonce);
        parameters.put("timestamp", String.valueOf(timestamp));
        String uri = EscrowAgentInfo.getUrl() + EscrowAgent.getRequestTokenService();
        SignatureBase signatureBase = new SignatureBase(parameters,
                uri, "POST");
        SecretKeySpec secretKey = new SecretKeySpec((clientSecret + "&")
                .getBytes(), "HmacSHA1");
        Signature signature = new Signature(signatureBase
                .getSignatureBaseString(), secretKey);
        signature.signMessage();
        signatureContent = signature.getSignatureContent();
        parameters.put("signature", signatureContent);
        return parameters;
    }

    private TreeMap<String, String> prepareRequestTokenParameters()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put("client_id", clientId);
        parameters.put("nonce", nonce);
        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("request_token", tokeId);
        String uri = EscrowAgentInfo.getUrl() + EscrowAgent.getEncryptionKeyService();
        SignatureBase signatureBase = new SignatureBase(parameters,
                uri, "POST");
        SecretKeySpec secretKey = new SecretKeySpec((clientSecret + "&"
                + tokenSecret).getBytes(), "HmacSHA1");
        Signature signature = new Signature(signatureBase
                .getSignatureBaseString(), secretKey);
        signature.signMessage();
        signatureContent = signature.getSignatureContent();
        parameters.put("signature", signatureContent);
        return parameters;
    }

    private TreeMap<String, String> prepareAuthorizationParameters()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = new TreeMap<>();
        DecryptionBundle authBundle = new DecryptionBundle(DataType.AIM_ID,
                DataUse.ANALYTICS, InteractionPurpose.ADVERT);
        parameters.put("client_id", clientId);
        parameters.put("nonce", nonce);
        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("request_token", tokeId);
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
        signatureContent = signature.getSignatureContent();
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

    @Before
    public void setUp() throws UnsupportedEncodingException {
        start(fakeApplication(inMemoryDatabase()));
        addData();
    }

    @Test
    public void testRequestTokenGenerationSucceeded()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareParameters();
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
    }

    @Test
    public void testInvalidTimestamp() throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareParameters();
        parameters.put("timestamp", String.valueOf(timestamp + 50 * 60 * 1000L));
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20timestamp");
    }

    @Test
    public void testInvalidNonce() throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareParameters();
        new Nonce(nonce, timestamp, Client.authenticate(clientId)).addNonce();
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20nonce%20value");
    }

    @Test
    public void testInvalidSignature() throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareParameters();
        parameters.put("signature", "invalid signature");
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20signature");
    }

    @Test
    public void testParametersMissing() throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareParameters();
        parameters.remove("client_id");
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        parameters.remove("signature");
        parameters.put("client_id", clientId);
        request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        parameters.remove("timestamp");
        parameters.put("signature", signatureContent);
        request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        parameters.remove("nonce");
        parameters.put("timestamp", String.valueOf(timestamp));
        request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
    }

    @Test
    public void testRequestTokenAuthorization()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareAuthorizationParameters();
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.authorizeRequestToken()))
                .method(POST);
        Result result = route(request);
        validateResponse(result, SEE_OTHER, null, null);
        assertTrue(result.redirectLocation().contains("/decrypt?"));
    }

    @Test
    public void testAuthorizeRequestTokenInvalidSignature()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareAuthorizationParameters();
        parameters.put("signature", "invalid signature");
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.authorizeRequestToken()))
                .method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20signature");
    }

    @Test
    public void testAuthorizedRequestTokenInvalidNonce()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareAuthorizationParameters();
        new Nonce(nonce, timestamp, Client.authenticate(clientId)).addNonce();
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.authorizeRequestToken()))
                .method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20nonce%20value");
    }

    @Test
    public void testAuthorizeRequestTokenInvalidTimestamp()
            throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = prepareAuthorizationParameters();
        parameters.put("timestamp", String.valueOf(timestamp + 50 * 60 * 1000L));
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestToken())).method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20timestamp");
    }

    @Test
    public void testRequestTokenExchange() throws UnsupportedEncodingException {
        ReqToken token = ReqToken.authenticate(tokeId, clientId);
        token.authorizeRequestToken();
        TreeMap<String, String> parameters = prepareRequestTokenParameters();
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestTokenExchange()))
                .method(POST);
        Result result = route(request);
        validateResponse(result, OK, contentType, "utf-8");
        org.pdguard.api.model.EncryptionKey key = (EncryptionKey) ResponseParser.toObject(
                contentAsString(result), EncryptionKey.class);
        assertNotNull(key.getEncryptionKey());
    }

    @Test
    public void testRequestTokenExchangeInvalidSignature()
            throws UnsupportedEncodingException {
        ReqToken token = ReqToken.authenticate(tokeId, clientId);
        token.authorizeRequestToken();
        TreeMap<String, String> parameters = prepareRequestTokenParameters();
        parameters.put("signature", "invalid signature");
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestTokenExchange()))
                .method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20signature");
    }

    @Test
    public void testRequestTokenExchangeInvalidNonce()
            throws UnsupportedEncodingException {
        ReqToken token = ReqToken.authenticate(tokeId, clientId);
        token.authorizeRequestToken();
        TreeMap<String, String> parameters = prepareRequestTokenParameters();
        new Nonce(nonce, timestamp, Client.authenticate(clientId)).addNonce();
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestTokenExchange()))
                .method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20nonce%20value");
    }

    @Test
    public void testRequestTokenExchangeInvalidTimestamp()
            throws UnsupportedEncodingException {
        ReqToken token = ReqToken.authenticate(tokeId, clientId);
        token.authorizeRequestToken();
        TreeMap<String, String> parameters = prepareRequestTokenParameters();
        parameters.put("timestamp", String.valueOf(timestamp + 50 * 60 * 1000L));
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(parameters).uri(
                String.valueOf(routes.PDGuardService.requestTokenExchange())).method(POST);
        Result result = route(request);
        validateResponse(result, UNAUTHORIZED, contentType, "utf-8");
        assertEquals(contentAsString(result), "authentication_error=Invalid%20timestamp");
    }
}
