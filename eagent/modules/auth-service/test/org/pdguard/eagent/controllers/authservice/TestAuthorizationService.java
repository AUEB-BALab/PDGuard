package org.pdguard.eagent.controllers.authservice;

import com.google.common.collect.ImmutableMap;

import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.ResponseParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;

import org.pdguard.eagent.models.*;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.ReqToken;
import org.pdguard.eagent.utils.CredentialsGenerator;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Class that tests AuthorizationService class with JUnit.
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestAuthorizationService extends WithApplication {
    private String clientId = UUID.randomUUID().toString();

    private String tokeId = UUID.randomUUID().toString();

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
        byte[] tokenKey = CredentialsGenerator.generateHmacSHA1Secret();
        Client client = new Client(clientId, clientKey, dataSubject, dataController,
                authApp);
        client.save();
        ReqToken reqToken = new ReqToken(tokeId, tokenKey, client);
        reqToken.save();
        route(routes.AuthorizationService.encryptService
                ("GIVEN_NAME", "PUBLIC_DATA", false, clientId, tokeId));
        AuthorizationRule authRule = AuthorizationRule.getFinder().where()
                .eq("dataSubject.id", dataSubject.getId())
                .eq("dataType", DataType.GIVEN_NAME).findUnique();
        authRule.getAllowableActions().iterator().next().setDataUse(DataUse.QUERY_THIRD_PARTY);
        authRule.getAllowableProvenances().iterator().next().setDataProvenance(
                DataProvenance.PUBLIC_DATA);
        authRule.update();
    }

    private void validateResponse(Result result, String contentType,
        String charset, int status) {
        assertEquals(status, result.status());
        assertEquals(contentType, result.contentType());
        assertEquals(charset, result.charset());
    }

    @Override
    protected FakeApplication provideFakeApplication() {
        return new FakeApplication(new java.io.File("."), Helpers.class.getClassLoader(),
                ImmutableMap.of("play.http.router", "auth.Routes"), new ArrayList<>(), null);
    }

    @Before
    public void setUp() {
        fakeApplication(inMemoryDatabase());
        addData();
    }

    @Test
    public void testDecryptionServiceSucceeded() {
        Result result = route(routes.AuthorizationService
                .decryptService("GIVEN_NAME", "QUERY_THIRD_PARTY", "ADVERT",
                        clientId, tokeId));
        validateResponse(result, contentType, "utf-8", OK);
        org.pdguard.api.model.RequestToken token =
                (org.pdguard.api.model.RequestToken) ResponseParser.toObject(
                contentAsString(result), org.pdguard.api.model.RequestToken.class);
        assertEquals(token.getRequestToken(), tokeId);
        assertTrue(token.isAuthorized());
        result = route(routes.AuthorizationService
                .decryptServiceDefaultAuth("IDENTITY", "COMPOSE_EMAIL_TO_SUBJECT", "ADVERT",
                        clientId, tokeId));
        validateResponse(result, contentType, "utf-8", OK);
        token = (org.pdguard.api.model.RequestToken) ResponseParser.toObject(
                        contentAsString(result), org.pdguard.api.model.RequestToken.class);
        assertEquals(token.getRequestToken(), tokeId);
        assertTrue(token.isAuthorized());
    }

    @Test
    public void testDecryptionServiceNotAuthenticatedClient() {
        String wrongClient = "Wrong client";
        Result result = route(routes.AuthorizationService
                .decryptService("GIVEN_NAME", "QUERY_THIRD_PARTY", "ADVERT",
                        wrongClient, tokeId));
        validateResponse(result, contentType, "utf-8", UNAUTHORIZED);
        assertEquals(contentAsString(result), "authentication_error=Unauthenticated%20client");
    }

    @Test
    public void testDecryptionServiceInvalidRequestToken() {
        String invalidToken = "Wrong token";
        Result result = route(routes.AuthorizationService
                .decryptService("GIVEN_NAME", "QUERY_THIRD_PARTY", "ADVERT",
                        clientId, invalidToken));
        validateResponse(result, contentType, "utf-8", UNAUTHORIZED);
        assertEquals(contentAsString(result), "authentication_error=Invalid%20request%20token");
    }

    @Test
    public void testDecryptionServiceRuleNotAuthorized() {
        Result result = route(routes.AuthorizationService
                .decryptService("INITIALS", "QUERY_THIRD_PARTY", "ADVERT",
                        clientId, tokeId));
        validateResponse(result, contentType, "utf-8", UNAUTHORIZED);
        assertEquals(contentAsString(result),
                "authorization_error=Access%20denied.%20Cause%3A%20Denied%20by%20data%20subject");
    }

    @Test
    public void testEncryptionServiceSucceeded() {
        Result result = route(routes.AuthorizationService
                .encryptService("GIVEN_NAME", "PUBLIC_DATA", true,
                        clientId, tokeId));
        validateResponse(result, contentType, "utf-8", OK);
        org.pdguard.api.model.RequestToken token = (org.pdguard.api.model.RequestToken)
                ResponseParser.toObject(contentAsString(result),
                        org.pdguard.api.model.RequestToken.class);
        assertEquals(token.getRequestToken(), tokeId);
        assertTrue(token.isAuthorized());
        result = route(routes.AuthorizationService
                .encryptServiceDefaultAuth("IDENTITY", "PUBLIC_DATA", true,
                        clientId, tokeId));
        validateResponse(result, contentType, "utf-8", OK);
        token = (org.pdguard.api.model.RequestToken) ResponseParser.toObject(
                contentAsString(result), org.pdguard.api.model.RequestToken.class);
        assertEquals(token.getRequestToken(), tokeId);
        assertTrue(token.isAuthorized());
    }

    @Test
    public void testEncryptionServiceUnauthenticatedClient() {
        String wrongClient = "wrong client";
        Result result = route(routes.AuthorizationService
                .encryptService("GIVEN_NAME", "PUBLIC_DATA", true,
                        wrongClient, tokeId));
        validateResponse(result, contentType, "utf-8", UNAUTHORIZED);
        assertEquals(contentAsString(result),
                "authentication_error=Unauthenticated%20client");
    }

    @Test
    public void testEncryptionServiceInvalidRequestToken() {
        String invalidToken = "wrong token";
        Result result = route(routes.AuthorizationService
                .encryptService("GIVEN_NAME", "PUBLIC_DATA", true,
                        clientId, invalidToken));
        validateResponse(result, contentType, "utf-8", UNAUTHORIZED);
        assertEquals(contentAsString(result),
                "authentication_error=Invalid%20request%20token");
    }

    @Test
    public void testEncryptionServiceEncryptionDenied() {
        AuthorizationRule authRule = AuthorizationRule.getFinder().all().get(0);
        authRule.update();
        Result result = route(routes.AuthorizationService
                .encryptService("GIVEN_NAME", "OTHER", true,
                        clientId, tokeId));
        validateResponse(result, contentType, "utf-8", UNAUTHORIZED);
        assertEquals(contentAsString(result),
                "authorization_error=Access%20denied.%20Cause%3A%20Denied%20by%20data%20subject");
    }
}
