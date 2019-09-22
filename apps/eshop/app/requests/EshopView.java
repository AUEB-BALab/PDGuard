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

package requests;

import forms.Transaction;

import java.util.List;

/**
 * This interface defines requests that a user issues to e-shop application in
 * order to retrieve information about his transactions such as orders.
 *
 * @author Thodoris Sotiropoulos
 */
public interface EshopView extends EshopRequest {
    /**
     * This method returns information about customer's transactions as
     * specified in his request.
     *
     * @return List of customer's transactions.
     */
    List<Transaction> view();
}
