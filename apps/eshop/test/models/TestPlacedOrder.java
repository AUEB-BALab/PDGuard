package models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestPlacedOrder {
    PlacedOrder order;

    private void addData() throws UnsupportedEncodingException {
        DataSubject dataSubject = new DataSubject("id", "eagent", "client id",
                "secret");
        dataSubject.save();
        Customer customer = new Customer("email".getBytes("UTF-8"),
                "name".getBytes("UTF-8"), "password",
                "surname".getBytes("UTF-8"), dataSubject);
        customer.save();
        Cart cart = new Cart(customer);
        cart.save();
        order = new PlacedOrder(new Date(), "address".getBytes("UTF-8"),
                "card".getBytes("UTF-8"), "country".getBytes("UTF-8"),
                "city".getBytes("UTF-8"), cart);
    }

    @Before
    public void setUp() throws UnsupportedEncodingException {
        start(fakeApplication(inMemoryDatabase()));
        addData();
    }

    @Test
    public void testAddOrder() {
        Assert.assertFalse(order.getCart().isOrdered());
        Assert.assertEquals(PlacedOrder.getFinder().all().size(), 0);
        order.addOrder();
        Assert.assertTrue(order.getCart().isOrdered());
        List<PlacedOrder> orders = PlacedOrder.getFinder().all();
        Assert.assertEquals(orders.size(), 1);
        PlacedOrder retrievedOrder = orders.get(0);
        Assert.assertEquals(retrievedOrder, order);
    }

    @Test
    public void testGetOrders() {
        List<PlacedOrder> orders = PlacedOrder.getOrders("id");
        Assert.assertEquals(orders.size(), 0);
        order.save();

        orders = PlacedOrder.getOrders("id");
        Assert.assertEquals(orders.size(), 1);
        Assert.assertEquals(orders.get(0), order);
    }
}
