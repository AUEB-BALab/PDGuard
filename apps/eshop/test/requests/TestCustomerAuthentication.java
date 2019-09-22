package requests;

import exceptions.EshopException;
import forms.Login;
import models.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class TestCustomerAuthentication {
    private CustomerAuthentication custAuth;

    @Before
    public void setUp() {
        custAuth = new CustomerAuthentication(new Login("id", "password"));
    }

    @PrepareForTest({ Customer.class })
    @Test
    public void testIsAuthenticated() throws IOException,
            EscrowAgentErrorResponseException {
        PowerMockito.mockStatic(Customer.class);
        PowerMockito.when(Customer.authenticate("id", "password")).thenReturn(null);
        Assert.assertFalse(custAuth.isAuthenticated());
        PowerMockito.verifyStatic();
        Customer.authenticate("id", "password");

        Customer mockCustomer = PowerMockito.mock(Customer.class);
        CustomerAuthentication mockCustAuth = PowerMockito.spy(custAuth);
        PowerMockito.when(Customer.authenticate("id", "password")).thenReturn(
                mockCustomer);
        PowerMockito.doReturn("email").when(mockCustAuth).getCustomerEmail(
                mockCustomer);
        Assert.assertTrue(mockCustAuth.isAuthenticated());
        PowerMockito.verifyStatic(Mockito.times(2));
        Customer.authenticate("id", "password");
    }

    @Test
    public void testProcess() throws IOException,
            EscrowAgentErrorResponseException, EshopException {
        CustomerAuthentication mockCustAuth = PowerMockito.spy(custAuth);
        PowerMockito.doReturn(true).when(mockCustAuth).isAuthenticated();
        mockCustAuth.process();
        Mockito.verify(mockCustAuth, Mockito.times(1)).isAuthenticated();
    }

    @Test(expected = EshopException.class)
    public void testProcessFailed() throws IOException,
            EscrowAgentErrorResponseException, EshopException {
        CustomerAuthentication mockCustAuth = PowerMockito.spy(custAuth);
        PowerMockito.doReturn(false).when(mockCustAuth).isAuthenticated();
        mockCustAuth.process();
    }
}
