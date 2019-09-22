package org.pdguard.eagent.controllers.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.eagent.models.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;
import static play.test.Helpers.*;


@RunWith(value = BlockJUnit4ClassRunner.class)
public class TestAuthorizationRuleCRUD extends WithApplication {
    final String userEmail = "nap@hotmail.com";

    private AuthorizationRule authorizationRule;

    private AuthorizationRule addAuthorizationRule() {
        DataSubject dataSubject = new DataSubject("id", "Nick Apostolopoulos",
                userEmail, "password");
        dataSubject.save();
        DataController dataController = new DataController("id", "ABC");
        dataController.save();
        AuthorizationRule authorizationRule = new AuthorizationRule(dataSubject,
                dataController, DataType.GIVEN_NAME);
        authorizationRule.getAllowableActions().add(new AllowableAction(
                authorizationRule, DataUse.UPDATE_THIRD_PARTY_DATA,
                new Date(), null));
        authorizationRule.getAllowableProvenances().add(new AllowableProvenance(
                authorizationRule, DataProvenance.DATA_CONTROLLER,
                new Date(), null));
        authorizationRule.save();
        return authorizationRule;
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
        authorizationRule = addAuthorizationRule();
    }

    @Test
    public void testGetAuthorizationRules() {
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.getAuthorizationRules()))
                .method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertTrue(json.isArray());
        JsonNode authRule = json.get(0);
        assertEquals(authRule.size(), 4);
        assertEquals(authRule.get("dataController").asText(), authorizationRule
                .getDataController().getDataControllerName());
        assertEquals(authRule.get("dataType").asText(),
                authorizationRule.getDataType().toString());
        JsonNode allowableActions = authRule.get("actions");
        assertTrue(allowableActions.isArray());
        assertEquals(allowableActions.size(), 1);
        assertEquals(allowableActions.get(0).get("dataUse").asText(),
                DataUse.UPDATE_THIRD_PARTY_DATA.toString());
        assertEquals((Integer) allowableActions.get(0).get("id").asInt(),
                authorizationRule.getAllowableActions().iterator().next().getId());
        assertNotNull(allowableActions.get(0).get("validFrom"));
        assertNotNull(allowableActions.get(0).get("validTo"));
    }

    @Test
    public void testNoAuthorizationRulesRetrieved() {
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.getAuthorizationRules()))
                .method(GET).session("user", "wrong@email.com");
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertTrue(json.isArray());
        assertNull(json.get(0));
    }

    @Test
    public void testAuthorizationRuleCreationSucceeded() {
        ArrayNode dataTypes = Json.newArray();
        dataTypes.add(DataType.GIVEN_NAME.toString());
        ObjectNode jsonParam = Json.newObject();
        jsonParam.set("dataType", dataTypes);
        jsonParam.put("dataUse", DataUse.ANALYTICS.toString());
        jsonParam.put("dataController", "ABC");
        jsonParam.put("validTo", "2016-07-06");
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .createAuthorizationRule()))
                .method(POST).session("user", userEmail)
                .bodyJson(jsonParam);
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertEquals(json.size(), 1);
        JsonNode childNode = json.get(DataType.GIVEN_NAME.toString());
        assertEquals(childNode.get("message").asText(), Messages.get("web.addition"));
        assertFalse(childNode.get("id").asInt() == 0);

    }

    @Test
    public void testAuthorizationRuleCreationFailedDataNotFound() {
        String dataControllerName = "ABC";
        String dataType = DataType.AIM_ID.toString();
        ArrayNode dataTypes = Json.newArray();
        dataTypes.add(dataType);
        ObjectNode jsonParam = Json.newObject();
        jsonParam.set("dataType", dataTypes);
        jsonParam.put("dataUse", DataUse.ANALYTICS.toString());
        jsonParam.put("dataController", dataControllerName);
        jsonParam.put("validTo", "2016-07-06");
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.createAuthorizationRule()))
                .method(POST).session("user", userEmail).bodyJson(jsonParam);
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertEquals(json.size(), 1);
        JsonNode childNode = json.get(dataType);
        assertEquals(childNode.get("message").asText(), "Data controller "
                + dataControllerName + " does not hold data"
                + " of type " + dataType.replace("_", " ").toLowerCase()
                + " which belongs to you");
        assertNull(childNode.get("id"));
    }

    @Test
    public void testAuthorizationRuleCreationFailedAuthRuleDefined() {
        String dataControllerName = "ABC";
        String dataType = DataType.GIVEN_NAME.toString();
        ArrayNode dataTypes = Json.newArray();
        dataTypes.add(dataType);
        ObjectNode jsonParam = Json.newObject();
        jsonParam.set("dataType", dataTypes);
        jsonParam.put("dataUse", DataUse.UPDATE_THIRD_PARTY_DATA.toString());
        jsonParam.put("dataController", dataControllerName);
        jsonParam.put("validTo", "2016-07-06");
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .createAuthorizationRule()))
                .method(POST).session("user", userEmail).bodyJson(jsonParam);
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertEquals(json.size(), 1);
        JsonNode childNode = json.get(dataType);
        assertEquals(childNode.get("message").asText(), Messages.get("web.defined_auth_rule"));
        assertNull(childNode.get("id"));
    }

    @Test
    public void testAuthorizationRuleCreationFailedBadDate() {
        ArrayNode dataTypes = Json.newArray();
        dataTypes.add(DataType.GIVEN_NAME.toString());
        ObjectNode jsonParam = Json.newObject();
        jsonParam.set("dataType", dataTypes);
        jsonParam.put("dataUse", DataUse.ANALYTICS.toString());
        jsonParam.put("dataController", "ABC");
        jsonParam.put("validTo", "BAD FORMAT");
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .createAuthorizationRule()))
                .method(POST).session("user", userEmail)
                .bodyJson(jsonParam);
        Result result = route(request);
        validateResponse(result, BAD_REQUEST, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertEquals(json.size(), 1);
        assertEquals(json.get("message").asText(), Messages.get("web.misformatted_date"));
    }

    @Test
    public void testCreateAuthRule() {
        String dataType1 = DataType.GIVEN_NAME.toString();
        String dataType2 = DataType.AIM_ID.toString();
        String dataControllerName = "ABC";
        ArrayNode dataTypes = Json.newArray();
        dataTypes.add(dataType1);
        dataTypes.add(dataType2);
        ObjectNode jsonParam = Json.newObject();
        jsonParam.set("dataType", dataTypes);
        jsonParam.put("dataUse", DataUse.ANALYTICS.toString());
        jsonParam.put("dataController", "ABC");
        jsonParam.put("validTo", "2016-07-06");
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .createAuthorizationRule()))
                .method(POST).session("user", userEmail)
                .bodyJson(jsonParam);
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode json = Json.parse(contentAsString(result));
        assertEquals(json.size(), 2);
        JsonNode childNode = json.get(dataType1);
        assertEquals(childNode.get("message").asText(), Messages.get("web.addition"));
        assertFalse(childNode.get("id").asInt() == 0);
        JsonNode childNode2 = json.get(dataType2);
        assertEquals(childNode2.get("message").asText(), "Data controller "
                + dataControllerName + " does not hold data"
                + " of type " + dataType2.replace("_", " ").toLowerCase()
                + " which belongs to you");
        assertNull(childNode2.get("id"));
    }

    @Test
    public void testAllowableActionUpdateSucceeded() {
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.updateAllowableAction(
                        DataType.GIVEN_NAME.toString(), DataUse.ANALYTICS.toString(),
                        "ABC", "2016-07-06", authorizationRule.getId())))
                .method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, OK, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals(Messages.get("web.update")));
    }

    @Test
    public void testAllowableActionUpdateFailed() {
        final String dataControllerName = "ABC";
        final String dataType = DataType.AIM_ID.toString();
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.updateAllowableAction(dataType,
                        DataUse.ANALYTICS.toString(), dataControllerName, "2016-07-06",
                        authorizationRule.getId()))).method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, OK, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals("Data controller "
                + dataControllerName + " does not hold data"
                + " of type " + dataType.replace("_", " ").toLowerCase()
                + " which belongs to you"));
        AllowableAction authAllowableAction = new AllowableAction(authorizationRule, DataUse.INTRANET,
                new Date(), null);
        authAllowableAction.save();
        request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .updateAllowableAction(authorizationRule.getDataType().toString(),
                                authAllowableAction.getDataUse().toString(),
                                authorizationRule.getDataController()
                                        .getDataControllerName(), "2016-07-06",
                                authorizationRule.getId()))).method(GET).session("user", userEmail);
        result = route(request);
        validateResponse(result, OK, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals(Messages.get("web.defined_auth_rule")));
        final String badFormattedDate = "badFormat";
        request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.updateAllowableAction(
                        DataType.GIVEN_NAME.toString(), DataUse.ANALYTICS.toString(),
                        "ABC", badFormattedDate, authorizationRule.getId())))
                .method(GET).session("user", userEmail);
        result = route(request);
        validateResponse(result, BAD_REQUEST, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals(Messages.get("web.misformatted_date")));
    }

    @Test
    public void testAllowableProvenanceUpdateSucceeded() {
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.updateAllowableProvenance(
                        DataType.GIVEN_NAME.toString(), DataProvenance.DATA_CONTROLLER.toString(),
                        "ABC", "2016-07-06", authorizationRule.getId())))
                .method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, OK, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals(Messages.get("web.update")));
    }

    @Test
    public void testAllowableProvenanceUpdateFailed() {
        final String dataControllerName = "ABC";
        final String dataType = DataType.AIM_ID.toString();
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.updateAllowableProvenance(dataType,
                        DataProvenance.DATA_CONTROLLER.toString(),
                        dataControllerName, "2016-07-06",
                        authorizationRule.getId()))).method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, OK, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals("Data controller "
                + dataControllerName + " does not hold data"
                + " of type " + dataType.replace("_", " ").toLowerCase()
                + " which belongs to you"));
        AllowableProvenance authAllowableProvenance = new AllowableProvenance(
                authorizationRule, DataProvenance.PUBLIC_REGISTRY, new Date(), null);
        authAllowableProvenance.save();
        request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .updateAllowableProvenance(authorizationRule.getDataType().toString(),
                                authAllowableProvenance.getDataProvenance().toString(),
                                authorizationRule.getDataController()
                                        .getDataControllerName(), "2016-07-06",
                                authorizationRule.getId()))).method(GET).session("user", userEmail);
        result = route(request);
        validateResponse(result, OK, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals(Messages.get("web.defined_auth_rule")));
        final String badFormattedDate = "badFormat";
        request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD.updateAllowableProvenance(
                        DataType.GIVEN_NAME.toString(), DataUse.PUBLISH_INTERNAL.toString(),
                        "ABC", badFormattedDate, authorizationRule.getId())))
                .method(GET).session("user", userEmail);
        result = route(request);
        validateResponse(result, BAD_REQUEST, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals(Messages.get("web.misformatted_date")));
    }

    @Test
    public void testAuthorizationRuleDeletion() {
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .deleteAuthorizationRule(authorizationRule.getId(), true)))
                .method(GET).session("user", userEmail);
        Result result = route(request);
        validateResponse(result, OK, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals(Messages.get("web.deletion")));
        final int wrongID = 300;
        request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .deleteAuthorizationRule(wrongID, true)))
                .method(GET).session("user", userEmail);
        result = route(request);
        validateResponse(result, OK, "text/plain", "utf-8");
        assertTrue(contentAsString(result).equals(Messages.get("web.auth_rule_not_found")));
    }

    @Test
    public void testLockData() {
        JsonNode json = Json.parse("{\"dataTypes\": [\"" + authorizationRule
                .getDataType()
                .toString() + "\"], \"dataController\": \"" + authorizationRule
                .getDataController().getDataControllerName() + "\"}");
        RequestBuilder request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .lockData())).method(POST).session("user", userEmail).bodyJson(json);
        Result result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        JsonNode response = Json.parse(contentAsString(result));
        assertEquals(response.size(), 1);
        assertEquals(response.get(authorizationRule.getDataType().toString())
                        .asText(), Messages.get("web.lock"));
        DataType dataType = DataType.BIOMETRICAL;
        json = Json.parse("{\"dataTypes\": [\"" + dataType.toString()
                + "\"], \"dataController\": \"" + authorizationRule
                .getDataController().getDataControllerName() + "\"}");
        request = new RequestBuilder().uri(
                String.valueOf(routes.AuthorizationRuleCRUD
                        .lockData())).method(POST).session("user", userEmail).bodyJson(json);
        result = route(request);
        validateResponse(result, OK, "application/json", "utf-8");
        response = Json.parse(contentAsString(result));
        assertEquals(response.size(), 1);
        assertEquals(response.get(dataType.toString()).asText(), "Data controller "
                + authorizationRule.getDataController().getDataControllerName()
                + " does not hold data of type " + dataType.toString().replace("_", " ")
                .toLowerCase() + " which belongs to you");
    }

    @Test(expected = ParseException.class)
    public void testValidateDate() throws ParseException {
        assertNull(AuthorizationRuleCRUD.validateExpirationDate(""));
        assertNotNull(AuthorizationRuleCRUD.validateExpirationDate("2015-06-06"));
        AuthorizationRuleCRUD.validateExpirationDate("wrong format");
    }
}
