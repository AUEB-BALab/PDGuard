package org.pdguard.api.utils;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class TestRequestEncoder {
    @Test
    public void testEncodeBaseURI() throws UnsupportedEncodingException {
        String url = "https://localhost:9443/register";
        assertEquals(RequestEncoder.encodeBaseURI(url), "https%3A%2F%2Flocalhost%3A9443%2Fregister");
        url = "https://localhost:443/register";
        assertEquals(RequestEncoder.encodeBaseURI(url), "https%3A%2F%2Flocalhost%2Fregister");
        url = "http://localhost:80/register";
        assertEquals(RequestEncoder.encodeBaseURI(url), "http%3A%2F%2Flocalhost%2Fregister");
    }

    @Test
    public void testNormalizeParametersQuery() throws UnsupportedEncodingException {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put("paramB", "value B");
        parameters.put("paramA", "value A");
        assertEquals(RequestEncoder.normalizeParametersQuery(parameters),
                "paramA=value%20A&paramB=value%20B");
    }
}
