package models;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestProductCart {
    private Product product;

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
        product = new Product(1, "name", "manufacturer", 12.0, 5);
        product.save();
    }

    @Before
    public void setUp() throws UnsupportedEncodingException {
        start(fakeApplication(inMemoryDatabase()));
        addData();
    }

    @Test
    public void testProductEntry() {
        Assert.assertEquals(ProductCart.getFinder().all().size(), 0);
        ProductCart productCart = new ProductCart(product, cart, 1);
        productCart.addProductEntry();
        List<ProductCart> productCartList = ProductCart.getFinder().all();
        Assert.assertEquals(productCartList.size(), 1);
        Assert.assertEquals(productCartList.get(0), productCart);

        productCart = new ProductCart(product, cart, 5);
        productCart.addProductEntry();
        productCartList = ProductCart.getFinder().all();
        Assert.assertEquals(productCartList.size(), 1);
        Assert.assertEquals(productCartList.get(0).getQuantity(), 6);
    }
}
