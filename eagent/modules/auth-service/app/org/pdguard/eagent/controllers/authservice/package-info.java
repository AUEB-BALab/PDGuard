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
 * This package implements the request handler classes for the services of
 * escrow agent according to the PDGuard flow.
 *
 * There's implementation of client registration service where escrow agent
 * sends back client credentials required for the issuing of PDGuard requests,
 * request token service for the generation of an unauthorized request token
 * which is going to be exchanged later for an encryption key.
 *
 * There's authorization services where escrow agent grants authorization to
 * data controllers to use or not specific data of a data subject and then
 * sends back an authorized request token which is ready for exchange for an
 * encryption key to either encrypt or decrypt data.
 *
 * @author Thodoris Sotiropoulos
 */
package org.pdguard.eagent.controllers.authservice;
