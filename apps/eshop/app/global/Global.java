package global;

import play.Application;
import models.Product;
import play.GlobalSettings;
import play.Logger;
import play.Play;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        if (Play.application().configuration().getBoolean("startAkkaActor")) {
            if (Product.getFinder().all().size() == 0) {
                Logger.info("Application has started");
                addProduct(1, "G580", "Lenovo", 300.0, 2);
                addProduct(2, "Air-Pro", "Apple", 1299.9, 4);
                addProduct(3, "Yoga-300", "Lenovo", 569.99, 5);
                addProduct(4, "G50-80", "Lenovo", 570.0, 2);
            }
		}
    }

    private void addProduct(int productKey, String name, String manufacturer,
             double price, int stock) {
        Product prd = new Product(productKey, name, manufacturer,
            price, stock);
        prd.save();
    }
}
