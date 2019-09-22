package org.pdguard.eagent.requests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.AccessDeniedException;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.exceptions.RegistrationFailedException;
import org.pdguard.api.exceptions.UnretrievableKeyException;
import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import play.mvc.Http;

@RunWith(PowerMockRunner.class)
public class TestPDGuardProcessor {
    private PDGuardRequestProcessor processor;

    private PDGuardRequest pdGuardRequest;

    @Before
    public void setUp() {
        pdGuardRequest = PowerMockito.mock(PDGuardRequest.class);
        processor = new PDGuardRequestProcessor(pdGuardRequest);
    }

    @Test
    public void testProcessRequest() throws EscrowAgentErrorResponseException {
        Mockito.doNothing().when(pdGuardRequest).validate();
        Mockito.when(pdGuardRequest.process()).thenReturn("response");
        processor.processRequest();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).validate();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).process();
        Assert.assertEquals("response", processor.getResponse());
        Assert.assertEquals(Http.Status.OK, processor.getStatusCode());
    }

    @Test
    public void testProcessRequestAccessDeniedException()
        throws EscrowAgentErrorResponseException {
        Mockito.when(pdGuardRequest.process()).thenThrow(
                new AccessDeniedException("exception"));
        processor.processRequest();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).validate();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).process();
        Assert.assertEquals("authorization_error=exception",
                processor.getResponse());
        Assert.assertEquals(Http.Status.UNAUTHORIZED, processor.getStatusCode());
    }

    @Test
    public void testProcessRequestUnverifiedRequestException()
            throws EscrowAgentErrorResponseException {
        Mockito.when(pdGuardRequest.process()).thenThrow(
                new UnverifiedRequestException("exception"));
        processor.processRequest();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).validate();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).process();
        Assert.assertEquals("authentication_error=exception",
                processor.getResponse());
        Assert.assertEquals(Http.Status.UNAUTHORIZED, processor.getStatusCode());
    }

    @Test
    public void testProcessRequestUnretrievableKeyException()
            throws EscrowAgentErrorResponseException {
        Mockito.when(pdGuardRequest.process()).thenThrow(
                new UnretrievableKeyException("exception"));
        processor.processRequest();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).validate();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).process();
        Assert.assertEquals("key_error=exception",
                processor.getResponse());
        Assert.assertEquals(Http.Status.UNAUTHORIZED, processor.getStatusCode());
    }

    @Test
    public void testProcessRequestRegistrationFailedException()
            throws EscrowAgentErrorResponseException {
        Mockito.when(pdGuardRequest.process()).thenThrow(
                new RegistrationFailedException("exception"));
        processor.processRequest();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).validate();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).process();
        Assert.assertEquals("registration_error=exception",
                processor.getResponse());
        Assert.assertEquals(Http.Status.BAD_REQUEST, processor.getStatusCode());
    }

    @Test
    public void testProcessRequestEscrowAgentErrorResponseException()
            throws EscrowAgentErrorResponseException {
        Mockito.when(pdGuardRequest.process()).thenThrow(
                new EscrowAgentErrorResponseException("exception"));
        processor.processRequest();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).validate();
        Mockito.verify(pdGuardRequest, Mockito.times(1)).process();
        Assert.assertEquals("error=exception",
                processor.getResponse());
        Assert.assertEquals(Http.Status.UNAUTHORIZED, processor.getStatusCode());
    }
}
