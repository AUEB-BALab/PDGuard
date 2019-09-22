package models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

public class TestProduct {
    private static final double DELTA = 1e-15;

    private Product product;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        product = new Product(1, "test name", "ABC", 100.0, 10);
        product.save();
    }

    @Test
    public void testGetProducts() {
        List<Product> products = Product.getProducts();
        Assert.assertEquals(products.size(), 1);
        Product product = products.get(0);
        Assert.assertEquals(product.getProductKey(), (Integer) 1);
        Assert.assertEquals(product.getName(), "test name");
        Assert.assertEquals(product.getManufacturer(), "ABC");
        Assert.assertEquals(product.getPrice(), 100.0, DELTA);
        Assert.assertEquals(product.getStock(), 10);
    }

    @Test
    public void testAuthenticate() {
        int wrongProductKey = 1000;
        Assert.assertNull(Product.authenticate(wrongProductKey));

        Assert.assertEquals(Product.authenticate(product.getProductKey()),
                product);
    }
}
