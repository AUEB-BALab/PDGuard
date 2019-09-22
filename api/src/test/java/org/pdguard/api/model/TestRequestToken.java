package org.pdguard.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

public class TestRequestToken {
    private RequestToken stringToRequestToken(final String requestToken)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(requestToken);
        return mapper.treeToValue(json, RequestToken.class);
    }

    @Test(expected = NullPointerException.class)
    public void testCheckNotNull() throws IOException {
        String jsonString = "{\"requestToken\": \"token\", \"tokenSecret\":"
                + "\"secret\"}";
        RequestToken token = stringToRequestToken(jsonString);
        token.checkNotNull();
        jsonString = "{\"requestToken\": \"token\"}";
        token = stringToRequestToken(jsonString);
        token.checkNotNull();
    }
}
