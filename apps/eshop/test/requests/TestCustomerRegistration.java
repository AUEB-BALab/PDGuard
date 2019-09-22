package requests;

import exceptions.EshopException;
import forms.Registration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import security.CryptographyController;

import java.io.IOException;
import java.nio.charset.Charset;

@RunWith(PowerMockRunner.class)
public class TestCustomerRegistration {
    private CustomerRegistration custReg;

    @Before
    public void setUp() {
        custReg = new CustomerRegistration(new Registration(
                "name", "surname", "email", "password", "password", "eagent",
                "id"));
    }

    @Test
    public void testEncryptData() throws IOException,
            EscrowAgentErrorResponseException {
        CustomerRegistration mockCustReg = PowerMockito.spy(custReg);
        byte[] returnedBytes = new byte[0];
        CryptographyController encryptor = PowerMockito.mock(
                CryptographyController.class);
        PowerMockito.doReturn(returnedBytes).when(encryptor).encrypt(
                "surname".getBytes(Charset.forName("UTF-8")),
                DataType.SURNAME, DataProvenance.DATA_SUBJECT_EXPLICIT,
                false);
        PowerMockito.doReturn(returnedBytes).when(encryptor).encrypt(
                "surname".getBytes(Charset.forName("UTF-8")),
                DataType.SURNAME, DataProvenance.DATA_SUBJECT_EXPLICIT,
                false);
        PowerMockito.doReturn(returnedBytes).when(encryptor).encrypt(
                "name".getBytes(Charset.forName("UTF-8")),
                DataType.GIVEN_NAME, DataProvenance.DATA_SUBJECT_EXPLICIT,
                false);
        PowerMockito.doReturn(returnedBytes).when(encryptor).encrypt(
                "email".getBytes(Charset.forName("UTF-8")),
                DataType.PERSONAL_EMAIL, DataProvenance.DATA_SUBJECT_EXPLICIT,
                false);
        mockCustReg.encyptData(encryptor);
        Assert.assertEquals(mockCustReg.getRegistration().getEmail(),
                new String(returnedBytes));
        Assert.assertEquals(mockCustReg.getRegistration().getFirstName(),
                new String(returnedBytes));
        Assert.assertEquals(mockCustReg.getRegistration().getSurname(),
                new String(returnedBytes));

        Mockito.verify(encryptor, Mockito.times(1)).encrypt(
                "surname".getBytes(Charset.forName("UTF-8")),
                DataType.SURNAME, DataProvenance.DATA_SUBJECT_EXPLICIT,
                false);
        Mockito.verify(encryptor, Mockito.times(1)).encrypt(
                "name".getBytes(Charset.forName("UTF-8")),
                DataType.GIVEN_NAME, DataProvenance.DATA_SUBJECT_EXPLICIT,
                false);
        Mockito.verify(encryptor, Mockito.times(1)).encrypt(
                "email".getBytes(Charset.forName("UTF-8")),
                DataType.PERSONAL_EMAIL, DataProvenance.DATA_SUBJECT_EXPLICIT,
                false);
    }

    @Test
    public void testProcess() throws IOException,
            EscrowAgentErrorResponseException, EshopException {
        CustomerRegistration mockCustReg = PowerMockito.spy(custReg);
        PowerMockito.doNothing().when(mockCustReg).addCustomer();
        mockCustReg.process();
        Mockito.verify(mockCustReg, Mockito.times(1)).addCustomer();
    }

    @Test(expected = EshopException.class)
    public void testProcessFailed1() throws IOException,
            EscrowAgentErrorResponseException, EshopException {
        CustomerRegistration mockCustReg = PowerMockito.spy(custReg);
        PowerMockito.doThrow(new IOException()).when(mockCustReg).addCustomer();
        mockCustReg.process();
    }

    @Test(expected = EshopException.class)
    public void testProcessFailed2() throws IOException,
            EscrowAgentErrorResponseException, EshopException {
        CustomerRegistration mockCustReg = PowerMockito.spy(custReg);
        PowerMockito.doThrow(new EscrowAgentErrorResponseException(""))
                .when(mockCustReg).addCustomer();
        mockCustReg.process();
    }
}
