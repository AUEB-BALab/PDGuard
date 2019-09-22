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

package utils;

import com.fasterxml.jackson.databind.JsonNode;
import play.Play;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class is responsible for the retrieval of data which describe the
 * whole organization where the e-shop application is running.
 *
 * These data are stored in a JSON file and they are required by application
 * which issued PDGuard requests.
 *
 * @author Thodoris Mavrikis, Thodoris Sotiropoulos
 */
public class OrganizationInfo {
    /** Path to JSON file which stores organization data. */
    private static final String ORGANIZATION_DATA = Play.application().path()
            + "/data/org.json";

    /**
     * This method reads a JSON file and it creates a JSON object using Jackson
     * library according to it.
     *
     * JSON file stores data about organization and application of it. Data
     * describe the way escrow agent knows current application and organization
     * required for the obtainment of client credentials to issue PDGuard
     * requests.
     *
     * @return Location where JSON object with information about
     * organization and the applications is located.
     */
    public static JsonNode loadOrganizationInfo() {
        StringBuilder response = new StringBuilder();
        String output;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ORGANIZATION_DATA));
            while ((output = br.readLine()) != null)
                response.append(output);
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Json.parse(response.toString());
    }
}
