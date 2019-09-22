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

package org.pdguard.eagent.common;

import org.pdguard.api.utils.DataProvenance;
import org.pdguard.api.utils.DataType;
import org.pdguard.api.utils.DataUse;
import org.pdguard.eagent.models.*;
import play.Application;
import play.GlobalSettings;
import play.Play;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static org.apache.commons.codec.binary.Hex.encodeHex;

/**
 * This class extends the global settings of current project and it configures
 * it when application is running for the first time without initial data.
 *
 * It is used to add initial data to escrow agent's database for demonstration.
 *
 * @author Thodoris Sotiropoulos
 */
public class Global extends GlobalSettings {
    /** Adds initial data when application is running for fist time. */
    @Override
    public void onStart(Application app) {
        if (Play.application().configuration().getBoolean("startAkkaActor")) {
            if (DataSubject.getFinder().all().size() == 0)
                try {
                    addInitialData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }
    /**
     * This method adds initial data when escrow agent runs for demonstration.
     *
     * @throws IOException
     */
    public void addInitialData() throws IOException {
        DataController guardian = new DataController("test-id2", "Guardian");
        guardian.save();
        DataController istLab = new DataController("test-id", "ISTLab");
        istLab.save();
        DataSubject dataSubject = new DataSubject("test-id",
                "Nick Apostolopoulos", "nap@hotmail.com", "password");
        dataSubject.save();
        DataSubject dataSubject2 = new DataSubject("test-id2", "James Parker",
                "test@example.com", "password");
        dataSubject2.save();
        AuthorizedApplication frontend = new AuthorizedApplication("test-id2",
                guardian, "frontend") ;
        frontend.save();
        AuthorizedApplication eshop = new AuthorizedApplication("test-id",
                istLab, "e-shop");
        eshop.save();
        AuthorizationRule authorizationRule = new AuthorizationRule(dataSubject,
                istLab, DataType.PERSONAL);
        authorizationRule.getAllowableActions().add(new AllowableAction(
                authorizationRule, DataUse.REPORT, new Date(), null));
        authorizationRule.getAllowableProvenances().add(new AllowableProvenance(
                authorizationRule, null, new Date(), null));
        authorizationRule.save();
        AuthorizationRule authorizationRule2 = new AuthorizationRule(
                dataSubject2, guardian, DataType.GIVEN_NAME);
        authorizationRule2.getAllowableActions().add(new AllowableAction(
                authorizationRule2, DataUse.REPORT, new Date(), null));
        authorizationRule2.getAllowableProvenances().add(
                new AllowableProvenance(authorizationRule2, null, new Date(),
                        null));
        authorizationRule2.save();
        AuthorizationRule authorizationRule3 = new AuthorizationRule(
                dataSubject2, guardian, DataType.SURNAME);
        authorizationRule3.getAllowableActions().add(new AllowableAction(
                authorizationRule3, DataUse.REPORT, new Date(), null));
        authorizationRule3.getAllowableActions().add(new AllowableAction(
                authorizationRule3, DataUse.OTHER, new Date(), null));
        authorizationRule3.getAllowableProvenances().add(
                new AllowableProvenance(authorizationRule3,
                        DataProvenance.DATA_CONTROLLER, new Date(), null));
        authorizationRule3.save();
        AuthorizationRule authorizationRule4 = new AuthorizationRule(
                dataSubject2, guardian, DataType.HOME_STREET_ADDRESS);
        authorizationRule4.getAllowableActions().add(new AllowableAction(
                authorizationRule4, null, new Date(), null));
        authorizationRule4.getAllowableProvenances().add(
                new AllowableProvenance(authorizationRule4, null, new Date(),
                        null));
        authorizationRule4.save();
        AuthorizationRule authorizationRule5 = new AuthorizationRule(
                dataSubject2, istLab, DataType.DRIVER_LICENSE_NUMBER);
        authorizationRule5.getAllowableActions().add(new AllowableAction(
                authorizationRule5, null, new Date(), null));
        authorizationRule5.getAllowableProvenances().add(
                new AllowableProvenance(authorizationRule5, null, new Date(),
                        null));
        authorizationRule5.save();
        Key key = new Key(dataSubject2, guardian,
                new Date());
        key.setKeyContent(loadKey("keys/test_key"));
        key.save();
        byte[] signatureKey = loadKey("keys/signature_key");
        play.Logger.warn(new String(encodeHex(signatureKey)));
        Client client = new Client("dbf21b3a-92e0-44cc-92fc-ba27ca07119e",
                signatureKey, dataSubject2, guardian, frontend);
        client.save();
    }

    public byte[] loadKey(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.readAllBytes(path);
    }
}
