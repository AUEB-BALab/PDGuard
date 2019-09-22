package org.pdguard.eagent.controllers.authservice;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.utils.ResponseParser;
import org.pdguard.eagent.models.*;
import play.mvc.Http;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.*;

public class TestClientRegistrationController extends WithApplication {
    private DataSubject dataSubject;

    private DataController dataController;

    private AuthorizedApplication authApp;

    private final String contentType = "application/x-www-form-urlencoded";

    private void addData() {
        dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                "nap@hotmail.com", "password");
        dataSubject.save();
        dataController = new DataController("id", "ABC");
        dataController.save();
        authApp = new AuthorizedApplication("id",
                dataController, "App");
        authApp.save();
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
    public void setUp() {
        fakeApplication(inMemoryDatabase());
        addData();
    }

    @Test
    public void testRegistrationSuccessful() {
        Map<String, String> data = new TreeMap<>();
        data.put("data_subject_id", String.valueOf(dataSubject.getId()));
        data.put("data_controller_id", String.valueOf(dataController.getId()));
        data.put("auth_app_id", String.valueOf(authApp.getId()));
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.ClientRegistrationController
                        .registerClient())).method(POST);
        Result result = route(request);
        validateResponse(result, OK, contentType, "utf-8");
        ClientCredentials clientCredentials = (ClientCredentials) ResponseParser.toObject(
                contentAsString(result), ClientCredentials.class);
        assertNotNull(clientCredentials.getClientId());
        assertNotNull(clientCredentials.getClientSecret());
    }


    @Test
    public void testDataSubjectNotFound() {
        Map<String, String> data = new TreeMap<>();
        String wrongId = "1000";
        assert !wrongId.equals(String.valueOf(dataSubject.getId()));
        data.put("data_subject_id", wrongId);
        data.put("data_controller_id", String.valueOf(dataController.getId()));
        data.put("auth_app_id", String.valueOf(authApp.getId()));
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.ClientRegistrationController
                        .registerClient())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        assertEquals(contentAsString(result), "registration_error=Data%20subject%20not%20found");
    }

    @Test
    public void testDataControllerNotFound() {
        Map<String, String> data = new TreeMap<>();
        String wrongId = "1000";
        assert !wrongId.equals(String.valueOf(dataController.getId()));
        data.put("data_subject_id", String.valueOf(dataSubject.getId()));
        data.put("data_controller_id", wrongId);
        data.put("auth_app_id", String.valueOf(authApp.getId()));
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.ClientRegistrationController
                        .registerClient())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        assertEquals(contentAsString(result), "registration_error=Data%20controller%20not%20found");
    }

    @Test
    public void testApplicationNotFound() {
        Map<String, String> data = new TreeMap<>();
        String wrongId = "1000";
        assert !wrongId.equals(String.valueOf(authApp.getId()));
        data.put("data_subject_id", String.valueOf(dataSubject.getId()));
        data.put("data_controller_id", String.valueOf(dataController.getId()));
        data.put("auth_app_id", wrongId);
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.ClientRegistrationController
                        .registerClient())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        assertEquals(contentAsString(result), "registration_error=Application%20not%20found");
    }

    @Test
    public void testClientAlreadyExists() {
        new Client("id", new byte[0], dataSubject, dataController, authApp).save();
        Map<String, String> data = new TreeMap<>();
        data.put("data_subject_id", String.valueOf(dataSubject.getId()));
        data.put("data_controller_id", String.valueOf(dataController.getId()));
        data.put("auth_app_id", String.valueOf(authApp.getId()));
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.ClientRegistrationController
                        .registerClient())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        assertEquals(contentAsString(result), "registration_error=Client%20already%20exists");
    }

    @Test
    public void testFieldsAreRequired() {
        Map<String, String> data = new TreeMap<>();
        data.put("data_subject_id", String.valueOf(dataSubject.getId()));
        data.put("data_controller_id", String.valueOf(dataController.getId()));
        Http.RequestBuilder request = new Http.RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.ClientRegistrationController
                        .registerClient())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        data.remove("data_subject_id");
        data.put("auth_app_id", String.valueOf(authApp.getId()));
        request = new Http.RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.ClientRegistrationController
                        .registerClient())).method(POST);
        result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
        data.remove("data_controller_id");
        data.put("data_subject_id", String.valueOf(dataSubject.getId()));
        request = new Http.RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.ClientRegistrationController
                        .registerClient())).method(POST);
        result = route(request);
        validateResponse(result, BAD_REQUEST, contentType, "utf-8");
    }
}
