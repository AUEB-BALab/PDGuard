package org.pdguard.eagent.controllers.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import org.pdguard.eagent.logs.AuthorizationProcess;
import org.pdguard.eagent.logs.AuthorizationResult;
import org.pdguard.eagent.logs.AuthorizationRequestType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.pdguard.api.utils.DataType;
import org.pdguard.eagent.models.*;
import org.pdguard.eagent.models.AuthorizationLog;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestAuthorizationLog extends WithApplication {
    final String userEmail = "nap@hotmail.com";

    final private DateFormat dateForm = new SimpleDateFormat("HH:mm yyyy-MM-dd");

    private org.pdguard.eagent.models.AuthorizationLog authorizationLog;

    public org.pdguard.eagent.models.AuthorizationLog addAuthorizationLog() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                userEmail, "password");
        DataController dataController = new DataController("id", "ABC");
        AuthorizedApplication authApp = new AuthorizedApplication("id",
                dataController, "App");
        Client client = new Client("id", new byte[0], dataSubject, dataController,
                authApp);
        org.pdguard.eagent.models.AuthorizationLog authorizationLog = new AuthorizationLog(
                AuthorizationResult.ALLOWED,
                AuthorizationRequestType.READ, AuthorizationProcess.DATA_SUBJECT_RULES,
                new Date(), DataType.AIM_ID, client);
        authorizationLog.save();
        return authorizationLog;
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
                ImmutableMap.of("play.http.router", "web.Routes"), new ArrayList<>(), null);
    }

    @Before
    public void setUp() {
        fakeApplication(inMemoryDatabase());
        authorizationLog = addAuthorizationLog();
    }

    @Test
    public void testGetAuthorizationLogs() {
        String from = "2012-15-05";
        String to = "2999-15-05";
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationLog.getAuthorizationLogs(from, to)))
                .method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertTrue(json.isArray());
        assertEquals(json.size(), 1);
        JsonNode authLog = json.get(0);
        assertTrue(authLog.get("dataUse").isNull());
        assertTrue(authLog.get("interactionPurpose").isNull());
        assertEquals(authLog.get("dataType").asText(), authorizationLog
                .getDataType().toString());
        assertEquals(authLog.get("dataController").asText(), authorizationLog
                .getClient().getDataController().getDataControllerName());
        assertEquals(authLog.get("app").asText(), authorizationLog
                .getClient().getAuthApp().getAuthorizedApplicationName());
        assertEquals(authLog.get("requestType").asText(), authorizationLog
                .getRequestType().toString());
        assertEquals(authLog.get("requestResult").asText(), authorizationLog
                .getResult().toString());
        assertEquals(authLog.get("requestDate").asText(), dateForm.format(
                authorizationLog.getRequestDate()));
    }

    @Test
    public void testNoAuthorizationLogsRetrieved() {
        String from = "2999-15-05";
        String to = "2999-15-05";
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationLog.getAuthorizationLogs(from, to)))
                .method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertTrue(json.isArray());
        assertEquals(json.size(), 0);
    }

    @Test
    public void testNotWellFormattedDates() {
        String from = "bad format";
        String to = "bad format";
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationLog.getAuthorizationLogs(from, to)))
                .method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals("Date parameter cannot be understood"));
    }
}
