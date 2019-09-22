package org.pdguard.api.httperror;

import org.junit.Assert;
import org.junit.Test;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;

public class TestDefaultErrorResponse {
    @Test(expected = EscrowAgentErrorResponseException.class)
    public void testGetErrorResponseException()
            throws EscrowAgentErrorResponseException {
        EscrowAgentErrorResponseException ex = new DefaultErrorResponse()
                .getErrorResponseException("error");
        Assert.assertEquals(ex.getMessage(), "error");
        throw ex;
    }
}
