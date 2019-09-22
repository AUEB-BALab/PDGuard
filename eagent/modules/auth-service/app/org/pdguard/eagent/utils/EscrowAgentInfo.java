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

package org.pdguard.eagent.utils;

/**
 * This class contains information about current escrow agent.
 *
 * @author Thodoris Sotiropoulos
 */
public final class EscrowAgentInfo {
    /** The DNS URL of the escrow agent's web service endpoint. */
    private static final String URL = "https://localhost:9443";

    /** Hide util class constructor. */
    private EscrowAgentInfo() { }

    /**
     * Getter of URL field.
     *
     * @return The DNS URL of the escrow agent's web service endpoint.
     */
    public static String getUrl() {
        return URL;
    }
}
