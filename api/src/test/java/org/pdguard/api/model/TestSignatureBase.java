package org.pdguard.api.model;


import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class TestSignatureBase {
    private String url = "https://localhost:9443/register";

    private TreeMap<String, String> parameters = new TreeMap<>();

    private String requestMethod = "POST";

    @Before
    public void setUp() {
        parameters.put("paramB", "value B");
        parameters.put("paramA", "value A");
    }

    @Test
    public void testGetBaseStringURI() throws UnsupportedEncodingException {
        SignatureBase request = new SignatureBase(parameters,
                url, requestMethod);
        assertEquals(request.getEncodedUrl(), "https%3A%2F%2Flocalhost%3A9443%2Fregister");
        request = new SignatureBase(parameters,
                "https://localhost:443/register", requestMethod);
        assertEquals(request.getEncodedUrl(), "https%3A%2F%2Flocalhost%2Fregister");
        request = new SignatureBase(parameters,
                "http://localhost:80/register", requestMethod);
        assertEquals(request.getEncodedUrl(), "http%3A%2F%2Flocalhost%2Fregister");
    }

    @Test
    public void testGetParametersQuery() throws UnsupportedEncodingException {
        SignatureBase request = new SignatureBase(parameters, url,
                requestMethod);
        assertEquals(request.getEncodedParametersString(), "paramA=value%20A&paramB=value%20B");
    }

    @Test
    public void testSignatureBaseString() throws UnsupportedEncodingException {
        SignatureBase request = new SignatureBase(parameters, url, requestMethod);
        assertEquals(request.getSignatureBaseString(),
                "POST&https%3A%2F%2Flocalhost%3A9443%2Fregister&paramA=value%20A&paramB=value%20B");
    }
}
