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

package controllers;

import exceptions.EshopException;
import forms.OrderInfo;
import forms.ProductEntry;
import forms.Transaction;
import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import java.io.IOException;
import java.util.List;

import requests.OrderEntry;
import requests.OrderHistory;
import requests.CartView;
import requests.CartEntry;
import requests.EshopView;
import requests.EshopRequest;
import requests.ProductView;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author Thodoris Mavrikis , Thodoris Sotiropoulos
 *
 * This class extends {@link play.mvc.Controller} class and contains public
 * static methods which represent an action.
 * Usually, an action method does not include a return statement. The method
 * exit is done by the invocation of a result method.
 *
 * Actions which are included in this class represent the e-shop store as well
 * as the cart and the order functions that the shop provides to the customer.
 *
 */
public class StoreEntry extends Controller {
    /**An empty order form. */
    private static Form<OrderInfo> orderForm = Form
            .form(OrderInfo.class);

    /** An empty product form. */
    private static Form<ProductEntry> productForm = Form
            .form(ProductEntry.class);

    /**
     * The addToCart method finds the product with
     * the id that matches it's param and adds it to
     * the customer's cart.
     *
     * @param id the key of the chosen product.
     */
    public static Result addToCart() {
        Form<ProductEntry> cartEntry = Form.form(
                ProductEntry.class).bindFromRequest();
        if (cartEntry.hasErrors()) {
            return redirect(routes.StoreEntry.store());
        }
        EshopRequest cartManager = new CartEntry(
                session().get("customerId"), cartEntry.get());
        try {
            cartManager.process();
            return ok(views.html.store.render(ProductView.getProducts(),
                    productForm,
                    "Product with id: " + cartEntry.get().getProductId()
                    + " was added to cart", session().get("user")));
           } catch (EshopException e) {
            return badRequest(views.html.store.render(ProductView.getProducts(),
                    productForm, e.getMessage(), session().get("user")));
        }
    }    

    /**
     * This method renders the cart page which contains the 
     * selected products that the customer will order.
     *
     * @return the rendered cart page.
     */
    public static Result cart() {
        EshopView cartView = new CartView(session().get("customerId"));
        List<Transaction> cart = null;
        try {
            cartView.process();
            cart = cartView.view();
            assert cart.size() == 1;
            return ok(views.html.cart.render(cart.get(0), false));
        } catch (EshopException e) {
            return ok(views.html.cart.render(null, true));
        }
    }

    /**
     * This method is used to display the history of orders that
     * the customer has made in the past.
     */
    public static Result history() {
        EshopView orderHistory = new OrderHistory(session().get("customerId"));
        try {
            orderHistory.process();
        } catch (EshopException e) {
            return ok(views.html.history.render(orderHistory.view()));
        }
        return ok(views.html.history.render(orderHistory.view()));
    }

    /**
     * This method is used to display the order for
     * to be filled with the personal address and 
     * billing information of the customer.
     */
    public static Result order() {
        return ok(views.html.order.render(orderForm));
    }

    /**
     * This method is used to issue an order. This method
     * get's the customer's information from the form on 
     * the order page and encrypts it to the database.
     */
    public static Result makeOrder()  throws 
        IOException, EscrowAgentErrorResponseException {
        Form<OrderInfo> orderForm = Form.form(
                OrderInfo.class).bindFromRequest();
        if (orderForm.hasErrors())
            badRequest(views.html.order.render(orderForm));
        EshopRequest orderEntry = new OrderEntry(session().get("customerId"),
                orderForm.get());
        try {
            orderEntry.process();
            return ok(views.html.orderIssued.render(
                    "Your order has been made!"));
        } catch (EshopException e) {
            return badRequest(views.html.orderIssued.render(
                    "Error: " + e.getMessage()));
        }
    }

    /**
     * This method renders the store page which contains the 
     * available products for sale.
     *
     * @return the rendered store page.
     */
    public static Result store() {
        if (session().get("user") != null)
            return ok(views.html.store.render(ProductView.getProducts(),
                    productForm, "", session().get("user")));
        else
            return redirect(routes.AuthenticationEntry.index());
    }
}
