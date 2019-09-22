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

package org.pdguard.api.services;

import org.pdguard.api.exceptions.EscrowAgentErrorResponseException;
import org.pdguard.api.httperror.BadRequestErrorResponse;
import org.pdguard.api.httperror.DefaultErrorResponse;
import org.pdguard.api.httperror.EscrowAgentErrorResponse;
import org.pdguard.api.httperror.UnauthorizedErrorResponse;
import org.pdguard.api.model.Client;
import org.pdguard.api.model.EscrowAgent;
import org.pdguard.api.model.SignatureBase;
import org.pdguard.api.security.SecureConnection;
import org.pdguard.api.security.Signature;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class defines a connection to a data subject's escrow agent.
 *
 * It is used to call different escrow agent's web services according to the
 * PDGuard flow. A data controller who wants to have access to specific data
 * of a data subject via an application, they have to register escrow agent as
 * a client. Escrow agent after this sends back client credentials (client id
 * and client secret) which are required for the following PDGuard requests.
 *
 * They use these client credentials firstly to obtain an unauthorized request
 * token and secondly to request authorization from escrow agent based on the
 * way they intend to use data of data subject.
 *
 * If they get authorization (authorized request token), they can retrieve the
 * encryption key for the encryption/decryption of data.
 *
 * This class defines actions common for all Escrow Agent's services such as
 * generation of a nonce, signing of a request with the appropriate signature
 * key using HMAC-SHA1 algorithm, retrieval of escrow agent (error) response.
 *
 * @author Thodoris Sotiropoulos
 */
public abstract class EscrowAgentService {
    /** Client who is requesting escrow agent. */
    private Client client;

    /** Trusted escrow agent by data subject. */
    private EscrowAgent escrowAgent;

    /**
     * Connection configuration to establish an encrypted connection with
     * escrow agent.
     */
    private SecureConnection secureConnection;

    /**
     * Map of a status code with the corresponding escrow agent error response.
     */
    private static final Map<Integer, EscrowAgentErrorResponse> ERRORS = new
            HashMap<>();

    static {
        ERRORS.put(HttpsURLConnection.HTTP_BAD_REQUEST,
                new BadRequestErrorResponse());
        ERRORS.put(HttpsURLConnection.HTTP_UNAUTHORIZED,
                new UnauthorizedErrorResponse());
    }

    /**
     * Create a new connection to a data subject's specified escrow agent.
     *
     * @param client Client who is requesting escrow agent.
     * @param escrowAgent Trusted escrow agent by data subject.
     * @param con Connection configuration to establish an encrypted connection
     * with escrow agent.
     */
    public EscrowAgentService(final Client client,
            final EscrowAgent escrowAgent, final SecureConnection con) {
        this.client = client;
        this.escrowAgent = escrowAgent;
        secureConnection = con;
    }

    /**
     * Create a new connection to a data subject's specified escrow agent.
     *
     * @param escrowAgent Trusted escrow agent by data subject.
     * @param connection Connection configuration to establish an encrypted
     * connection with escrow agent.
     */
    public EscrowAgentService(final EscrowAgent escrowAgent,
            final SecureConnection connection) {
        this.escrowAgent = escrowAgent;
        secureConnection = connection;
    }

    /**
     * This method calls a web service of escrow agent and then returns back
     * its response in a JSON format.
     *
     * @param parameters Parameters of request in an URL encoded format.
     * @param uri URI for the specified escrow agent service.
     *
     * @return Escrow agent's response to the call of its service in a JSON
     * format.
     *
     * @throws IOException is thrown when the communication with the
     * escrow agent fails or it was an error response by escrow agent.
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent
     * sends back an error response.
     */
    public String callEscrowAgentService(final String parameters,
            final String uri) throws IOException,
            EscrowAgentErrorResponseException {
        URL url = new URL(uri);
        HttpsURLConnection connection = (HttpsURLConnection)
                url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        setSecureConnection(connection);
        addParameters(connection, parameters);
        int responseCode = connection.getResponseCode();
        String response = getEscrowAgentResponse(responseCode, connection);
        if (responseCode != HttpURLConnection.HTTP_OK)
            checkErrorResponse(responseCode, response);
        closeConnection(connection);
        return response;
    }

    /**
     * This method stores the base parameters of a request to a map with keys
     * the name of parameters.
     *
     * @return A map of request parameters with keys the name of parameters.
     */
    public abstract TreeMap<String, String> getParameters();

    /**
     * This method add request parameters according to the requirements of the
     * requested escrow agent's web service.
     *
     * @param connection A connection with escrow agent's web service.
     * @param parameters Encoded parameters query.
     *
     * @throws IOException is thrown when the addition of the parameters cannot
     * be completed.
     */
    public final void addParameters(final HttpsURLConnection connection,
            final String parameters) throws IOException {
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os,
                "UTF-8");
        BufferedWriter writer = new BufferedWriter(outputStreamWriter);
        try {
            writer.write(parameters);
        } finally {
            writer.close();
            outputStreamWriter.close();
            os.close();
        }
    }

    /**
     * Method gets an already established connection with the specified escrow
     * agent's web services HttpsURLConnection and it extracts its response.
     *
     * Response is in a URL encoded format.
     *
     * @param responseCode Status code of escrow agent's response.
     * @param connection A connection with escrow agent's web service.
     *
     * @return Escrow agent's response in URL encoded format.
     *
     * @throws IOException is thrown when the extraction of the escrow agent's
     * response cannot be completed.
     */
    public final String getEscrowAgentResponse(final int responseCode,
            final HttpsURLConnection connection) throws IOException {
        InputStream inputStream;
        if (responseCode == HttpURLConnection.HTTP_OK)
            inputStream = connection.getInputStream();
        else
            inputStream = connection.getErrorStream();
        StringBuilder response = new StringBuilder();
        String output;
        BufferedReader br = new BufferedReader(new InputStreamReader(
                inputStream, Charset.defaultCharset()));
        try {
            while ((output = br.readLine()) != null)
                response.append(output);
        } finally {
            br.close();
        }
        return response.toString();
    }

    /**
     * Close connection with a service of escrow agent.
     *
     * @param connection Connection with an escrow agent's service to be closed.
     */
    public final void closeConnection(final HttpsURLConnection connection) {
        connection.disconnect();
    }

    /**
     * This method configures connection with SSL in order an encrypted
     * communication between client and escrow agent to be established.
     *
     * Communication uses TLS protocol v 1.2.
     *
     * @param connection A connection with escrow agent's web service.
     * @throws IOException is thrown when a socket specified by Transport Layer
     * Security (TLS) protocol cannot be created.
     */
    public final void setSecureConnection(final HttpsURLConnection connection)
            throws IOException {
        SSLSocketFactory sslSocket = secureConnection
                .createSSLSocketConnection();
        connection.setSSLSocketFactory(sslSocket);
    }

    /**
     * This method gets current time stamp.
     *
     * @return A positive number representing current timestamp.
     */
    public final long getTimeStamp() {
        return new Date().getTime();
    }

    /**
     * This method generates a unique Nonce value to used by escrow agent to
     * verify that a request has never been made before.
     *
     * @return Random string representing nonce value.
     */
    public final String getNonce() {
        final int length = 16;
        byte[] nonce = new byte[length];
        SecureRandom random = new SecureRandom();
        random.nextBytes(nonce);
        try {
            return new String(nonce, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * This method signs a signature base string which is consisted of HTTP
     * request parts (HTTP request method, encoded base URI, normalized
     * parameters query) with the specified secret.
     *
     * @param signatureBase Signature base string which is consisted of the
     * concatenation of the HTTP request method, encoded URI base string and
     * encoded string of normalized parameters query.
     * @param secret Secret to sign request.
     *
     * @return Parameters query with the addition of signature parameter.
     */
    public final String signRequest(final SignatureBase signatureBase,
            final String secret) {
        SecretKeySpec secretKey = null;
        try {
            secretKey = new SecretKeySpec(secret.getBytes("UTF-8"),
                    "HmacSHA1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Signature signature = new Signature(signatureBase
                .getSignatureBaseString(), secretKey);
        signature.signMessage();
        try {
            return signatureBase.getEncodedParametersString()
                    + "&signature=" + URLEncoder.encode(
                    signature.getSignatureContent(), "UTF-8").replace("+",
                    "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method throws the corresponding exception according to the escrow
     * agent's error response.
     *
     * @param responseCode Status code of escrow agent's response.
     * @param response Escrow agent's error response in URL encoded format.
     *
     * @throws EscrowAgentErrorResponseException is thrown when escrow agent's
     * sends back an error response.
     * @throws IOException is thrown when connection with escrow agent failed.
     */
    public final void checkErrorResponse(final int responseCode,
            final String response) throws IOException,
            EscrowAgentErrorResponseException {
        String decodedResponse = URLDecoder.decode(response, "UTF-8");
        EscrowAgentErrorResponse errorResponse = ERRORS.get(
                responseCode);
        if (errorResponse != null)
            throw errorResponse.getErrorResponseException(
                    decodedResponse);
        else
            throw new DefaultErrorResponse()
                    .getErrorResponseException(
                    "Communication with escrow agent was  not successful."
                    + " Status code:" + responseCode);
    }

    /**m
     * Getter of client field.
     *
     * @return Client who is requesting escrow agent.
     */
    public final Client getClient() {
        return client;
    }

    /**
     * Getter of escrowAgent field.
     *
     * @return Trusted escrow agent by data subject.
     */
    public final EscrowAgent getEscrowAgent() {
        return escrowAgent;
    }
}
