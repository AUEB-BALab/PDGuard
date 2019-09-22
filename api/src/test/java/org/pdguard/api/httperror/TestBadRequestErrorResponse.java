package org.pdguard.api.httperror;

import org.junit.Assert;
import org.junit.Test;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.RegistrationFailedException;

public class TestBadRequestErrorResponse {
    @Test(expected = RegistrationFailedException.class)
    public void testRegistrationFailedException()
            throws EscrowAgentErrorResponseException {
        EscrowAgentErrorResponseException ex = new BadRequestErrorResponse()
                .getErrorResponseException("registration_error=failed");
        Assert.assertEquals(ex.getMessage(), "failed");
        throw ex;
    }

    @Test(expected = EscrowAgentErrorResponseException.class)
    public void testEscowAgentErrorResponse()
            throws EscrowAgentErrorResponseException {
        EscrowAgentErrorResponseException ex = new BadRequestErrorResponse()
                .getErrorResponseException("error=error");
       throw ex;
    }
}
