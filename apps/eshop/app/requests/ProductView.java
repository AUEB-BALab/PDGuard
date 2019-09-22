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

package requests;

import forms.Product;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for the retrieval of all available products.
 *
 * @author Thodoris Sotiropoulos
 */
public class ProductView {
    /**
     * Get all available products for sale.
     *
     * @return List of products.
     */
    public static List<Product> getProducts() {
        List<Product> availableProducts = new LinkedList<>();
        List<models.Product> products = models.Product.getProducts();
        availableProducts.addAll(products.stream().map(product ->
                new Product(product.getProductKey(),
                product.getManufacturer(), product.getName(),
                product.getPrice(), product.getDescription(), 0))
                .collect(Collectors.toList()));
        return availableProducts;
    }
}
