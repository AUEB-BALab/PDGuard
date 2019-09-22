package models;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestCustomer {
    private String email = "nap@hotmail.com";

    private String firstName = "Nick";

    private String surname = "James";

    private String password = "123456";

    private String dataSubjectId = "test-id";


    private void addCustomer() {
        DataSubject dataSubject = new DataSubject(dataSubjectId, "eagent",
                "client id", "secret");
        dataSubject.save();
        Customer customer = new Customer(email.getBytes(Charset.forName("UTF-8")),
                firstName.getBytes(Charset.forName("UTF-8")), password,
                surname.getBytes(Charset.forName("UTF-8")),
                dataSubject);
        customer.save();
    }

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        addCustomer();
    }

    @Test
    public void testAddCustomer() throws UnsupportedEncodingException {
        DataSubject dataSubject = new DataSubject("id2", "eagent", "client id2",
                "secret");
        dataSubject.save();
        Customer customer = new Customer("abc@hotmail.com"
                .getBytes(Charset.forName("UTF-8")),
                firstName.getBytes(Charset.forName("UTF-8")), password,
                surname.getBytes(Charset.forName("UTF-8")),
                dataSubject);
        customer.addCustomer();
        List<Customer> customers = Customer.getFinder().all();
        assertEquals(2, customers.size());
        Customer retrievedCustomer = customers.get(1);
        assertEquals(retrievedCustomer.getId(), (Integer) 2);
        assertEquals(retrievedCustomer.getPassword(), password);
        assertEquals(new String(retrievedCustomer.getFirstName(), "UTF-8"),
                firstName);
        assertEquals(new String(retrievedCustomer.getSurname(), "UTF-8"), surname);
        assertEquals(new String(retrievedCustomer.getEmail(), "UTF-8"),
                "abc@hotmail.com");
        assertEquals(retrievedCustomer.getDataSubject(), dataSubject);
    }

    @Test
    public void testAuthenticate() {
        Customer customer = Customer.getFinder().all().get(0);
        Customer retrievedCustomer = Customer.authenticate(
                dataSubjectId, password);
        assertNotNull(retrievedCustomer);
        assertEquals(retrievedCustomer, customer);
        String wrongId = "wrong id";
        assertNull(Customer.authenticate(wrongId, password));
        String wrongPassword = "wrong";
        assertNull(Customer.authenticate(dataSubjectId, wrongPassword));

        retrievedCustomer = Customer.authenticate(dataSubjectId);
        assertNotNull(retrievedCustomer);
        assertEquals(retrievedCustomer, customer);
        assertNull(Customer.authenticate("wrong id"));
    }

    @Test
    public void testIsDataSubjectIdAvailable() {
        assertFalse(Customer.isDataSubjectIdAvailable(dataSubjectId));
        assertTrue(Customer.isDataSubjectIdAvailable("wrong id"));
    }
}
