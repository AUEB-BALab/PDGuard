package org.pdguard.api.utils;

import org.junit.Assert;
import org.junit.Test;
import org.pdguard.api.model.ClientCredentials;
import org.pdguard.api.model.EncryptionKey;
import org.pdguard.api.model.RequestToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class TestResponseParser {
    public class TestClass {
        public TestClass() { }
        public String field1;

        public boolean field2;
    }

    @Test
    public void testParseResponse() {
        RequestToken requestToken = new RequestToken("fd  fd", "fh");
        String response = ResponseParser.toURLEncoded(RequestToken.class,
                requestToken);
        assertEquals(response, "request_token=fd%20%20fd&token_secret=fh&authorized=false");
        ClientCredentials clientCredentials = new ClientCredentials("client id",
                "client secret");
        response = ResponseParser.toURLEncoded(ClientCredentials.class,
                clientCredentials);
        assertEquals(response, "client_id=client%20id&client_secret=client%20secret");
        EncryptionKey encryptionKey = new EncryptionKey("key content");
        response = ResponseParser.toURLEncoded(EncryptionKey.class, encryptionKey);
        assertEquals(response, "encryption_key=key%20content");
    }

    @Test(expected = RuntimeException.class)
    public void testParseResponseRuntimeException() {
        ResponseParser.toURLEncoded(EncryptionKey.class, new TestClass());
    }

    @Test
    public void testNonPublicFields() throws NoSuchFieldException,
            NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        RequestToken requestToken = new RequestToken("token", "secret");
        Field field = RequestToken.class.getDeclaredField("authorized");
        String parameterPair = ResponseParser.nonPublicField(RequestToken.class,
                requestToken, field);
        assertEquals(parameterPair, "authorized=false");
        field = RequestToken.class.getDeclaredField("requestToken");
        parameterPair = ResponseParser.nonPublicField(RequestToken.class,
                requestToken, field);
        assertEquals(parameterPair, "request_token=token");
    }

    @Test
    public void testNonPublicFieldsObjectCreation()
            throws IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException,
            UnsupportedEncodingException, NoSuchFieldException {
        Class aclass = RequestToken.class;
        RequestToken obj = (RequestToken) aclass.newInstance();
        String parameterName = "requestToken";
        String parameterValue = "token";
        ResponseParser.nonPublicField(parameterName, parameterValue, aclass, obj);
        assertEquals(obj.getRequestToken(), "token");
        assertNull(obj.getTokenSecret());
        assertFalse(obj.isAuthorized());
        parameterName = "authorized";
        parameterValue = "true";
        ResponseParser.nonPublicField(parameterName, parameterValue, aclass, obj);
        assertEquals(obj.getRequestToken(), "token");
        assertNull(obj.getTokenSecret());
        assertTrue(obj.isAuthorized());
    }

    @Test
    public void testToObject() {
        RequestToken token = (RequestToken) ResponseParser.toObject(
                "request_token=fd%20%20fd&token_secret=fh&authorized=false",
                RequestToken.class);
        assertNotNull(token);
        assertEquals(token.getRequestToken(), "fd  fd");
        assertEquals(token.getTokenSecret(), "fh");
        assertFalse(token.isAuthorized());
        ClientCredentials clientCredentials = (ClientCredentials) ResponseParser
                .toObject("client_id=client%20id&client_secret=client%20secret",
                        ClientCredentials.class);
        assertNotNull(clientCredentials);
        assertEquals(clientCredentials.getClientId(), "client id");
        assertEquals(clientCredentials.getClientSecret(), "client secret");
        EncryptionKey encryptionKey = (EncryptionKey) ResponseParser.toObject(
                "encryption_key=key%20content", EncryptionKey.class);
        assertNotNull(encryptionKey);
        assertEquals(encryptionKey.getEncryptionKey(), "key content");
    }

    @Test(expected = RuntimeException.class)
    public void testToObjectRuntimeException() {
        ResponseParser.toObject("encryption_key=dfds", RequestToken.class);
    }

    @Test
    public void testEncodeParameter() {
        String parameterName = "name";
        String parameterValue = "df aa.d@";
        assertEquals(ResponseParser.encodeParameter(parameterName, parameterValue),
                "name=df%20aa.d%40");
    }

    @Test
    public void testPublicField()
            throws IllegalAccessException, UnsupportedEncodingException,
            NoSuchFieldException {
        TestClass test = new TestClass();
        Assert.assertNull(test.field1);
        ResponseParser.publicField("field1", "value", TestClass.class,
                test);
        Assert.assertEquals(test.field1, "value");
        Field field = TestClass.class.getDeclaredField("field1");
        String url = ResponseParser.publicField(test, field);
        Assert.assertEquals(url, "field1=value");
        ResponseParser.publicField("field2", "true", TestClass.class, test);
        Assert.assertTrue(test.field2);
    }
}
