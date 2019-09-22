/*-
 * Copyright 2014-2015 Thodoris Sotiropoulos
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

/**
 * This package is responsible for the call of escrow agent's services according
 * to the PDGuard flow.
 *
 * There's client registration service for the obtainment of client credentials
 * (client id and client secret) required for the issuing of PDGuard requests.
 *
 * There's request token service for the obtainment of an unauthorized request
 * token given as input on authorization service where escrow agent grants
 * authorization to data controller to use personal data of data subject and
 * finally to get encryption key.
 *
 * There's a class responsible for the preparation of a request and extraction
 * of response of each escrow gent's service described above.
 *
 * @author Thodoris Sotiropoulos
 */
package org.pdguard.api.services;
