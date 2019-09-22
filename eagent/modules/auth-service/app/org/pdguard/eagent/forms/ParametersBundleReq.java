package org.pdguard.eagent.forms;

import play.data.validation.Constraints;

/**
 * This class represents a bundle of parameters of a request which is part of
 * authorization protocol of PDGuard.
 *
 * This bundle also includes request token which is required for the retrieval
 * of an encryption key or the authorization grant.
 *
 * @author Thodoris Sotiropoulos
 */
public class ParametersBundleReq extends ParametersBundle {
    /**
     * Request token which is needed to be processed by escrow agent. This
     * field is required.
     */
    @Constraints.Required
    private String requestToken;

    /** Default constructor. */
    public ParametersBundleReq() { }

    /**
     * Creates a new bundle of request parameters.
     *
     * @param clientId ID of client who's requesting escrow agent.
     * @param signature Request signature.
     * @param nonce Nonce value; a unique randomly generated string.
     * @param timestamp Timestamp of request.
     * @param reqToken Request token which is needed to be processed by
     * escrow agent.
     */
    public ParametersBundleReq(final String clientId, final String signature,
            final String nonce, final Long timestamp, final String reqToken) {
        super(clientId, signature, nonce, timestamp);
        requestToken = reqToken;
    }

    /**
     * Getter of requestToken field.
     *
     * @return Request token which is needed to be processed by escrow agent.
     */
    public String getRequestToken() {
        return requestToken;
    }

    /**
     * Setter of requestToken field.
     *
     * @param requestToken Request token which is needed to be processed by
     * escrow agent.
     */
    public void setRequestToken(final String requestToken) {
        this.requestToken = requestToken;
    }
}
