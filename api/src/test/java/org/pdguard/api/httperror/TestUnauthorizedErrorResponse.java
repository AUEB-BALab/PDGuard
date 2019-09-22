package org.pdguard.api.httperror;

import org.junit.Assert;
import org.junit.Test;
import org.pdguard.api.exceptions.AccessDeniedException;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.UnverifiedRequestException;

public class TestUnauthorizedErrorResponse {
    @Test(expected = AccessDeniedException.class)
    public void testAccessDeniedException()
            throws EscrowAgentErrorResponseException {
        EscrowAgentErrorResponseException ex = new UnauthorizedErrorResponse()
                .getErrorResponseException("authorization_error=failed");
        Assert.assertEquals(ex.getMessage(), "failed");
        throw ex;
    }

    @Test(expected = UnverifiedRequestException.class)
    public void testUnverifiedRequestException()
            throws EscrowAgentErrorResponseException {
        EscrowAgentErrorResponseException ex = new UnauthorizedErrorResponse()
                .getErrorResponseException("authentication_error=failed");
        Assert.assertEquals(ex.getMessage(), "failed");
        throw ex;
    }

    @Test(expected = EscrowAgentErrorResponseException.class)
    public void testEscowAgentErrorResponse()
            throws EscrowAgentErrorResponseException {
        EscrowAgentErrorResponseException ex = new UnauthorizedErrorResponse()
                .getErrorResponseException("error=error");
        throw ex;
    }
}
