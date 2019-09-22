package org.pdguard.api.model;

import org.junit.Test;

public class TestClientCredentials {
    @Test(expected = NullPointerException.class)
    public void testCheckNotNull() {
        ClientCredentials clientCredentials = new ClientCredentials("id", "secret");
        clientCredentials.checkNotNull();
        clientCredentials = new ClientCredentials(null, null);
        clientCredentials.checkNotNull();
    }
}
