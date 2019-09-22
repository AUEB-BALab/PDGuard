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
 * This package implements the validation mechanism of
 * PDGuard requests.
 *
 * Each PDGuard request has to be validated for its nonce, timestamp and
 * signature or validation of request token is required.
 * These classes included in this package are responsible for the validation
 * of requests so that the can serve their responses e.g generation
 * of a request token, exchange with an encryption key.
 *
 * @author Thodoris Sotiropoulos
 */
package org.pdguard.eagent.validation;
