/*-
 * Copyright 2014-2015 Thodoris Mavrikis, Thodoris Sotiropoulos
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.LinkedList;
import java.util.List;

import com.avaje.ebean.Model;

/**
 * This class defines the entity of organization products which are available
 * for sale at the e-shop of organization.
 *
 * @author Thodoris Mavrikis
 */
@Entity
public class Product extends Model {
    /** Key of the product. This field is required. */
    @Id
    private Integer productKey;

    /** The name of the product. This field is required. */
    @Column(nullable = false)
    private String name;

    /** The manufacturer of the product. This field is required. */
    @Column(nullable = false)
    private String manufacturer;

    /** The product's price. This field is required. */
    @Column(nullable = false)
    private double price;

    /** The product's description. This field is required. */
    @Column(nullable = true)
    private String description;

    /**
     * The product's stock on the organization's storage.
     * This field is required.
     */
    @Column(nullable = false)
    private int stock;

    /**
     * This field is used to create a many to many relation
     * between the cart and the product.
     */
    @OneToMany(mappedBy = "product")
    private List<ProductCart> carts = new LinkedList<>();

    /**
     * This field will be used to pragmatically make queries which return
     * object of this class.
     */
    private static Finder<Integer, Product> finder = new Finder<>(
            Product.class);

    /**
     * Initialize Product with all information associated with their.
     *
     * @param productKey The key that identifies the product.
     * @param name The name of the product.
     * @param manufacturer The manufacturer that has produced the product.
     * @param price The price of the product in the e-shop of the organization.
     * @param stock The available stock of products in the storage of the
     * the organization.
     */
    public Product(final Integer productKey, final String name,
            final String manufacturer, final double price, final int stock) {
        this.productKey = productKey;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.stock = stock;
    }

    /**
     * Checks if there any product stored in database of escrow agent with
     * ID as the one given as parameter.
     *
     * @param id Id to search product.
     *
     * @return Product.
     */
    public static Product authenticate(final int id) {
        return Product.finder.where().eq("productKey", id).findUnique();
    }

    /**
     * Gets all products which are available for sale.
     *
     * @return List of product which are available for sale.
     */
    public static List<Product> getProducts() {
        return Product.getFinder().all();
    }

    /**
     * Getter method of the product's description.
     *
     * @return the description of the product.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter of the product key.
     *
     * @return the product's key.
     */
    public Integer getProductKey() {
        return productKey;
    }

    /**
     * Getter of the product's name.
     *
     * @return The name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter of the product's Manufacturer.
     *
     *@return the product's manufacturer.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Getter of the product's price.
     *
     * @return the product's price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Getter of the product's stock.
     *
     * @return the product's available stock.
     */
    public int getStock() {
        return stock;
    }

    /**
     * Getter of finder field.
     *
     * @return This field will be used to pragmatically make queries which
     * returns object of this class.
     */
    public static Finder<Integer, Product> getFinder() {
        return finder;
    }
}
