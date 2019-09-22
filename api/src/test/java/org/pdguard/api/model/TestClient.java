package org.pdguard.api.model;

import org.junit.Test;

public class TestClient {
    @Test(expected = NullPointerException.class)
    public void testCheckNotNull() {
        Client client = new Client("1", "1", "1");
        client.checkNotNull();
        client = new Client("1", "1", null);
        client.checkNotNull();
    }
}
