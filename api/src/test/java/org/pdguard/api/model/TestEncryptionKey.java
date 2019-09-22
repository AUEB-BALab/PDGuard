package org.pdguard.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestEncryptionKey {
    private EncryptionKey stringToEncryptionKey(final String keyContent)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(keyContent);
        return mapper.treeToValue(json, EncryptionKey.class);
    }

    @Test
    public void testLoadKey() throws IOException {
        String jsonString = "{\"encryptionKey\": \"e5419ab619863c4cb44425ce892fea8e\"}";
        EncryptionKey key = stringToEncryptionKey(jsonString);
        assertNotNull(key.loadKey());
        jsonString = "{\"encryptionKey\": \"wrong key\"}";
        key = stringToEncryptionKey(jsonString);
        assertNull(key.loadKey());
        assertEquals(key.getEncryptionKey(), "wrong key");
    }
}
