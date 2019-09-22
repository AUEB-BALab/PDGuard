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

package org.pdguard.eagent.validation;

import org.pdguard.api.exceptions.UnverifiedRequestException;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.Signature;
import org.pdguard.eagent.forms.ParametersBundle;
import org.pdguard.eagent.forms.ParametersBundleReq;
import org.pdguard.eagent.models.Client;
import org.pdguard.eagent.models.Nonce;
import org.pdguard.eagent.utils.ParametersMapper;
import play.i18n.Messages;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.TreeMap;

/**
 * This class is responsible for the validation of a PDGuard request received
 * by a data controller via an authorized application.
 *
 * @author Thodoris Sotiropoulos
 */
public class RequestValidator {
    /** Bundle of request parameters to validate. */
    private ParametersBundle params;

    /** Map that stores parameters used for the validation of signature. */
    private TreeMap<String, String> parameters;

    /** Allowable difference between current timestamp and request timestamp. */
    private static final long TIMESTAMP_DURATION = 5 * 60 * 1000L;

    /**
     * Creates a new request validator according to bundle of request
     * parameters.
     *
     * @param params Bundle of request parameters to validate.
     */
    public RequestValidator(final ParametersBundle params) {
        this.params = params;
        parameters = params instanceof ParametersBundleReq
                ? ParametersMapper.mapObject(ParametersBundleReq.class, params)
                : ParametersMapper.mapObject(ParametersBundle.class, params);
    }

    /**
     * This method validates a client according to the client id.
     *
     * @return Client with the specified client id.
     *
     * @throws UnverifiedRequestException is thrown when client cannot be
     * vefified.
     */
    public Client validateClient() throws UnverifiedRequestException {
        Client client = Client.authenticate(params.getClientId());
        if (client == null)
            throw new UnverifiedRequestException(
                    Messages.get("auth.unknown_client"));
        return client;
    }

    /**
     * This method validates the signature value sent by data controller.
     *
     * Signature uses HMAC-SHA1 algorithm, so escrow agent validates signature
     * by recalculating signature using the secret key defining by corresponding
     * entity's id and comparing with that signature sent on request.
     *
     * @param uri Escrow agent's web service end point.
     * @param signatureKey Signature key used to calculate signature.
     *
     * @throws UnverifiedRequestException is thrown when given signature is not
     * valid or there is not any client being described by the given client id.
     */
    void validateSignature(final String uri,
            final String signatureKey)
            throws UnsupportedEncodingException, UnverifiedRequestException {
        SignatureBase signatureBase = new SignatureBase(parameters,
                uri, "POST");
        SecretKeySpec secretKey = new SecretKeySpec(signatureKey
                .getBytes("UTF-8"), "HmacSHA1");
        Signature signature = new Signature(signatureBase
                .getSignatureBaseString(), secretKey);
        signature.signMessage();
        if (!signature.getSignatureContent().equals(params.getSignature()))
            throw new UnverifiedRequestException(
                    Messages.get("auth.invalid_signature"));
    }

    /**
     * This method validates nonce value so that this request has never been
     * made before.
     *
     * @throws UnverifiedRequestException is thrown when the defined nonce value
     * has already been used.
     */
    void validateNonce() throws UnverifiedRequestException {
        if (Nonce.exists(params.getNonce()))
            throw new UnverifiedRequestException(
                    Messages.get("auth.invalid_nonce"));
        new Nonce(params.getNonce(), params.getTimestamp(),
                Client.authenticate(params.getClientId())).addNonce();
    }

    /**
     * This method validates timestamp so that it is between current timestamp
     * and minimum allowed timestamp.
     *
     * @throws UnverifiedRequestException is thrown when given timestamp is
     * invalid.
     */
    void validateTimestamp() throws UnverifiedRequestException {
        long currentTimestamp = new Date().getTime();
        long minTimestamp = currentTimestamp - TIMESTAMP_DURATION;
        if (params.getTimestamp() < minTimestamp
                || params.getTimestamp() > currentTimestamp)
            throw new UnverifiedRequestException(
                    Messages.get("auth.invalid_timestamp"));
    }

    /**
     * This method validates a PDGuard request.
     *
     * Request validation is associated with the validation of nonce so that
     * given nonce has never been used before. Moreover, it is related to the
     * validation of timestamp sot that given timestamp is between allowable
     * limits.
     * Finally, this method validates the signature of a request using HMAC-SHA1
     * algorithm by recalculating signature using the secret key defining by
     * corresponding entity's id and comparing with that signature sent on
     * request.
     *
     * @param uri Escrow agent's web service end point.
     * @param signatureKey Signature key used to calculate signature.
     *
     * @throws UnverifiedRequestException is thrown when escrow agent cannot
     * validate request.
     */
    public void validateRequest(final String uri, final String signatureKey)
            throws UnverifiedRequestException {
        validateNonce();
        validateTimestamp();
        try {
            validateSignature(uri, signatureKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter of params field.
     *
     * @return Bundle of request parameters to validate.
     */
    public ParametersBundle getParams() {
        return params;
    }

    /**
     * Getter of parameters field.
     *
     * @return Map that stores parameters used for the validation of signature.
     */
    public TreeMap<String, String> getParameters() {
        return parameters;
    }

    /**
     * Setter of parameters field.
     *
     * @param parameters Map that stores parameters used for the validation of
     * signature.
     */
    public void setParameters(final TreeMap<String, String> parameters) {
        this.parameters = parameters;
    }
}
