package org.pdguard.eagent.utils;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pdguard.eagent.forms.ParametersBundle;
import org.pdguard.eagent.forms.ParametersBundleReq;

import java.util.TreeMap;

public class TestParametersMapper {
    private ParametersBundle bundle;

    private ParametersBundleReq bundleReq;

    @Before
    public void setUp() {
        bundle = new ParametersBundle("client id", "signature", "nonce",
                1000L);
        bundleReq = new ParametersBundleReq("client id", "signature", "nonce",
                1000L, "token");
    }
    @Test(expected = RuntimeException.class)
    public void testGetFieldValue() {
        Assert.assertEquals(ParametersMapper.getFieldValue(ParametersBundle.class,
                bundle, "clientId"), "client id");
        Assert.assertEquals(ParametersMapper.getFieldValue(ParametersBundleReq.class,
                bundleReq, "clientId"), "client id");
        ParametersMapper.getFieldValue(ParametersBundle.class,
                bundle, "requestToken");
    }

    @Test
    public void testMapObject() {
        TreeMap<String, String> parameters = ParametersMapper.mapObject(
                ParametersBundle.class, bundle);
        Assert.assertEquals(3, parameters.size());
        Assert.assertEquals("client id", parameters.get("client_id"));
        Assert.assertEquals("nonce", parameters.get("nonce"));
        Assert.assertEquals("1000", parameters.get("timestamp"));
        parameters = ParametersMapper.mapObject(
                ParametersBundleReq.class, bundleReq);
        Assert.assertEquals(4, parameters.size());
        Assert.assertEquals("client id", parameters.get("client_id"));
        Assert.assertEquals("nonce", parameters.get("nonce"));
        Assert.assertEquals("1000", parameters.get("timestamp"));
        Assert.assertEquals("token", parameters.get("request_token"));
    }
}
