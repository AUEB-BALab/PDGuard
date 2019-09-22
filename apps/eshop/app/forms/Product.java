/*-
 * Copyright 2014-2016 Thodoris Mavrikis, Thodoris Sotiropoulos
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

package forms;

/**
 * This class defines the information of products which are available
 * for sale at the e-shop of organization.
 *
 * @author Thodoris Sotiropoulos
 */
public class Product {
    /** Key of the product. This field is required. */
    private Integer productKey;

    /** The name of the product. This field is required. */
    private String name;

    /** The manufacturer of the product. This field is required. */
    private String manufacturer;

    /** The product's price. This field is required. */
    private double price;

    /** Product's description. */
    private String description;

    /** Product's quantity. */
    private int quantity;

    public Product() { }

    /**
     * Creates new product.
     *
     * @param productKey Key of the product.
     * @param manufacturer The manufacturer of the product.
     * @param name The name of the product.
     * @param price The product's price.
     */
    public Product(final Integer productKey, final String manufacturer,
            final String name, final double price, final String description,
            final int quantity) {
        this.productKey = productKey;
        this.manufacturer = manufacturer;
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }

    /**
     * Getter of manufacturer field.
     *
     * @return The manufacturer of the product.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Setter of manufacturer field.
     *
     * @param manufacturer The manufacturer of the product.
     */
    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Getter of productKey field.
     *
     * @return The key of product.
     */
    public Integer getProductKey() {
        return productKey;
    }

    /**
     * Setter of productKey field.
     *
     * @param productKey The key of product.
     */
    public void setProductKey(final Integer productKey) {
        this.productKey = productKey;
    }

    /**
     * The getter of name field.
     *
     * @return The name of product.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of product field.
     *
     * @param name The name of product.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter of price field.
     *
     * @return Product's price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setter of price field.
     *
     * @param price Product's price.
     */
    public void setPrice(final double price) {
        this.price = price;
    }

    /**
     * Getter of quantity field.
     *
     * @return Product's quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Getter of description field.
     *
     * @return Product's description.
     */
    public String getDescription() {
        return description;
    }
}
