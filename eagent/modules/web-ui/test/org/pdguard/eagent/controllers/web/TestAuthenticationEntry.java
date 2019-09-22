package org.pdguard.eagent.controllers.web;

import com.google.common.collect.ImmutableMap;
import org.pdguard.eagent.models.DataSubject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.pdguard.api.utils.DataType;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.*;

import static play.mvc.Http.Status.OK;

import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.*;

/**
 * Class that tests Application class with JUnit.
 */
@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestAuthenticationEntry extends WithApplication {
    final String fieldsRequiredMessage = "This field is required";

    final String validEmailRequiredMessage = "Valid email required";

    final String minLengthMessage = "Minimum length is 6";

    final String passwordsNotMatchMessage = "Passwords do not match";

    final String welcomeMessage = "Welcome to Escrow Agent Service";

    final String wrongCredentialsMessage = "Your email or password is incorrect";

    final String userEmail = "nap@hotmail.com";

    final String userPassword = "password";

    private void addDataSubject() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                userEmail, userPassword);
        dataSubject.save();
    }

    private void validateResponse(final Result result, final int status,
            final String message1, final String contentType, final String charSet) {
        assertEquals(status, result.status());
        assertEquals(contentType, result.contentType());
        assertEquals(charSet, result.charset());
        assertTrue(contentAsString(result).contains(message1));
    }

    @Override
    protected FakeApplication provideFakeApplication() {
        return new FakeApplication(new java.io.File("."), Helpers.class.getClassLoader(),
                ImmutableMap.of("play.http.router", "web.Routes"), new ArrayList<>(), null);
    }

    @Before
    public void setUp() {
        fakeApplication(inMemoryDatabase());
        this.addDataSubject();
    }

    @Test
    public void testIndex() {
        Result result = route(routes.AuthenticationEntry.index());
        validateResponse(result, OK, welcomeMessage, "text/html",
                "utf-8");
        assertFalse(contentAsString(result).contains(wrongCredentialsMessage));
    }

    @Test
    public void testAuthenticationGiven() {
        Map<String, String> data = new HashMap<>();
        data.put("email", userEmail);
        data.put("password", userPassword);
        RequestBuilder request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry
                        .authenticateDataSubject())).method(POST);
        Result result = route(request);
        assertEquals(SEE_OTHER, result.status());
        String nextURL = result.redirectLocation();
        assertTrue(nextURL.equals("/mainpage"));
        Result newResult = route(new RequestBuilder().uri(nextURL).method(GET)
                .session("user", userEmail));
        validateResponse(newResult, OK, userEmail, "text/html", "utf-8");
    }

    @Test
    public void testWrongEmailGiven() {
        Map<String, String> data = new HashMap<>();
        final String wrongEmail = "wrongemail@hotmail.com";
        data.put("email", wrongEmail);
        data.put("password", userPassword);
        RequestBuilder request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry
                        .authenticateDataSubject())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, wrongCredentialsMessage, "text/html", "utf-8");
    }

    @Test
    public void testWrongPasswordGiven() {
        Map<String, String> data = new HashMap<>();
        final String wrongPassword = "wrongPassword";
        data.put("email", userEmail);
        data.put("password", wrongPassword);
        RequestBuilder request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry
                        .authenticateDataSubject())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, wrongCredentialsMessage, "text/html", "utf-8");
    }

    @Test
    public void testMainpage() {
        Result result = route(routes.AuthenticationEntry.mainpage());
        assertEquals(SEE_OTHER, result.status());
        String nextURL = result.redirectLocation();
        assertTrue(nextURL.equals("/"));
        Result newResult = route(new RequestBuilder().uri(nextURL).method(GET));
        validateResponse(newResult, OK, welcomeMessage, "text/html", "utf-8");
        assertFalse(contentAsString(newResult).contains(userEmail));
    }

    @Test
    public void testRegistration() {
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "George");
        data.put("surname", "Antoniou");
        data.put("email", "abc@gmail.com");
        data.put("username", "username2");
        data.put("password", "123456");
        data.put("repeatedPassword", "123456");
        RequestBuilder request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry
                        .registration())).method(POST);
        Result result = route(request);
        assertEquals(SEE_OTHER, result.status());
        String nextURL = result.redirectLocation();
        assertTrue(nextURL.equals("/"));
        Result newResult = route(new RequestBuilder().uri(nextURL).method(GET));
        validateResponse(newResult, OK, welcomeMessage, "text/html", "utf-8");
        assertFalse(contentAsString(newResult).contains
                (wrongCredentialsMessage));
        assertFalse(contentAsString(newResult).contains
                (validEmailRequiredMessage));
        assertFalse(contentAsString(newResult).contains(fieldsRequiredMessage));
        assertFalse(contentAsString(newResult).contains(minLengthMessage));
        assertFalse(contentAsString(newResult).contains
                (passwordsNotMatchMessage));
        DataSubject dataSubject = DataSubject.getFinder().where()
                .eq("dataSubjectEmail", "abc@gmail.com")
                .eq("dataSubjectPassword", "123456").findUnique();
        assertNotNull(dataSubject);
        assertEquals(dataSubject.getDataSubjectEmail(), "abc@gmail.com");
        assertEquals(dataSubject.getDataSubjectPassword(), "123456");
        assertEquals(dataSubject.getId(), "username2");
    }

    @Test
    public void testFieldsAreRequired() {
        Map<String, String> data = new HashMap<>();
        data.put("surname", "Antoniou");
        data.put("email", "abc@gmail.com");
        data.put("username", "username");
        data.put("password", "123456");
        data.put("repeatedPassword", "123456");
        RequestBuilder request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry.registration())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, welcomeMessage, "text/html", "utf-8");
        assertTrue(contentAsString(result).contains(fieldsRequiredMessage));
        data.put("firstName", "George");
        data.remove("email");
        request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry.registration())).method(POST);
        result = route(request);
        validateResponse(result, BAD_REQUEST, welcomeMessage, "text/html", "utf-8");
        assertTrue(contentAsString(result).contains(fieldsRequiredMessage));
        data.put("email", "abc@gmail.com");
        data.remove("surname");
        request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry.registration())).method(POST);
        result = route(request);
        validateResponse(result, BAD_REQUEST, welcomeMessage, "text/html", "utf-8");
        assertTrue(contentAsString(result).contains(fieldsRequiredMessage));
        data.put("surname", "Antoniou");
        data.remove("password");
        request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry.registration())).method(POST);
        result = route(request);
        validateResponse(result, BAD_REQUEST, welcomeMessage, "text/html", "utf-8");
        assertTrue(contentAsString(result).contains(fieldsRequiredMessage));
    }

    @Test
    public void testEmailPattern() {
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "George");
        data.put("surname", "Antoniou");
        data.put("username", "username");
        data.put("email", "email in wrong format");
        data.put("password", "123456");
        data.put("repeatedPassword", "123456");
        RequestBuilder request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry.registration())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, welcomeMessage, "text/html", "utf-8");
        assertTrue(contentAsString(result).contains(validEmailRequiredMessage));
    }

    @Test
    public void testPasswordLength() {
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "George");
        data.put("surname", "Antoniou");
        data.put("username", "username");
        data.put("email", "gap@hotmail.com");
        data.put("password", "12");
        data.put("repeatedPassword", "12");
        RequestBuilder request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry.registration())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, "Welcome to Escrow Agent Service",
                "text/html", "utf-8");
        assertTrue(contentAsString(result).contains("Minimum length is 6"));
    }

    @Test
    public void testPasswordsNotMatch() {
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "George");
        data.put("surname", "Antoniou");
        data.put("username", "username");
        data.put("email", "gap@hotmail.com");
        data.put("password", "123456");
        data.put("repeatedPassword", "different password");
        RequestBuilder request = new RequestBuilder().bodyForm(data).uri(
                String.valueOf(routes.AuthenticationEntry.registration())).method(POST);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, "Welcome to Escrow Agent Service",
                "text/html", "utf-8");
        assertTrue(contentAsString(result).contains("Passwords do not match"));
    }

    @Test
    public void testCheckEmailAvailability() {
        Result result = route(routes.AuthenticationEntry
                .checkEmailAvailability("nap@hotmail.com"));
        assertNotNull(result);
        validateResponse(result, OK, "Your email already exists",
                "text/plain", "utf-8");
        Result newResult = route(routes.AuthenticationEntry
                .checkEmailAvailability("wrong@hotmail.com"));
        assertNotNull(newResult);
        validateResponse(newResult, OK, "Your email is available",
                "text/plain", "utf-8");
    }

    @Test
    public void testLogout() {
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthenticationEntry.logout())).method(GET);
        Result result = route(request);
        assertEquals(SEE_OTHER, result.status());
        String nextURL = result.redirectLocation();
        assertTrue(nextURL.equals("/"));
        Result newResult = route(new RequestBuilder().uri(nextURL).method(GET));
        validateResponse(newResult, OK, welcomeMessage, "text/html", "utf-8");
    }

    @Test
    public void testPutDataHierarchically() {
        EnumSet<DataType> dataTypes = EnumSet.allOf(DataType.class);
        Iterator<DataType> iter = dataTypes.iterator();
        Stack<DataType> categories = new Stack<>();
        String content = AuthenticationEntry.putDataTypeHierarchically(
                DataType.PERSONAL, iter.next(), new StringBuilder("<ul>"), iter,
                categories);
        assertTrue(content.contains("<li id=\"" + DataType.PERSONAL + "\">"
                + DataType.PERSONAL + "<ul>"));
        assertTrue(content.contains("<ul><li id=\"" + DataType.IDENTITY + "\">"
                + DataType.IDENTITY + "<ul>"));
        assertTrue(content.contains("<ul><li id=\"" + DataType.GIVEN_NAME + "\">"
                + DataType.GIVEN_NAME.toString().replace("_", " ")));
        assertFalse(content.contains("<ul><li id=\"" + DataType.SURNAME + "\">"
                + DataType.SURNAME));
        assertTrue(content.contains("<li id=\"" + DataType.NATIONALITY + "\">"
                + DataType.NATIONALITY + "</ul>"));
        assertTrue(content.contains("</ul><li id=\"" + DataType.WORK_CONTACT + "\">"
                + DataType.WORK_CONTACT.toString().replace("_", " ") + "<ul>"));
        assertTrue(content.contains("</ul><li id=\"" + DataType.HOME_CONTACT + "\">"
                + DataType.HOME_CONTACT.toString().replace("_", " ") + "<ul>"));
        assertTrue(content.contains("</ul><li id=\"" + DataType.BIOMETRICAL + "\">"
                + DataType.BIOMETRICAL + "<ul>"));
        assertTrue(content.contains("</ul><li id=\"" + DataType.OFFICIAL_IDENTIFICATION + "\">"
                + DataType.OFFICIAL_IDENTIFICATION.toString().replace("_", " ") + "<ul>"));
        assertTrue(content.contains("</ul><li id=\"" + DataType.ONLINE_HANDLE + "\">"
                + DataType.ONLINE_HANDLE.toString().replace("_", " ") + "<ul>"));
        assertTrue(content.contains("</ul></ul><li id=\"" + DataType.SENSITIVE + "\">"
                + DataType.SENSITIVE + "<ul>"));
        assertTrue(content.contains("</ul><li id=\"" + DataType.NONPERSONAL + "\">"
                + DataType.NONPERSONAL));
    }
}
