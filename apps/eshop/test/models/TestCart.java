package models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

@RunWith(PowerMockRunner.class)
public class TestCart {
    private Cart cart;

    private void addData() throws UnsupportedEncodingException {
        DataSubject dataSubject = new DataSubject("id", "eagent", "client id",
                "secret");
        dataSubject.save();
        Customer customer = new Customer("email".getBytes("UTF-8"),
                "name".getBytes("UTF-8"), "password",
                "surname".getBytes("UTF-8"), dataSubject);
        customer.save();
        cart = new Cart(customer);
        cart.save();
    }

    @Before
    public void setUp() throws UnsupportedEncodingException {
        start(fakeApplication(inMemoryDatabase()));
        addData();
    }

    @Test
    public void testAuthenticate() {
        Cart cart = Cart.authenticate("id");
        Assert.assertEquals(cart, this.cart);

        this.cart.setOrdered(true);
        this.cart.update();
        Assert.assertNull(Cart.authenticate("id"));
    }
}
