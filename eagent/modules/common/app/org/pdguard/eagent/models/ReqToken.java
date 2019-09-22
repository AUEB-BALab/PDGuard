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

package org.pdguard.eagent.models;

import com.avaje.ebean.Model;
import org.pdguard.eagent.common.PeriodValidity;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Calendar;
import java.util.Date;

/**
 * This class defines the entity of RequestToken for database schema of
 * this application using Ebean ORM.
 *
 * A request token is needed from client in order to obtain an encryption key
 * to either encrypt or decrypt data which belongs to a data subject. For this
 * reason, escrow agent generates unique request tokens for authenticated
 * clients with a limited validation period.
 *
 * In order these generated request tokens can be exchanged for encryption key,
 * a PDGuard request has to be authorized by escrow agent according to the
 * defined authorization rules by data subject associated with the client
 * related to request token.
 *
 * After the exchange of request token with encryption key, request token should
 * be deleted.
 *
 * For that purpose, there is an relation with the {@link Client}
 * class declaring as @ManyToOne. This means that each request token is
 * associated with one client and one client is associated with many request
 * token.
 *
 * @author Thodoris Sotiropoulos
 */
@Entity
public class ReqToken extends Model implements PeriodValidity {
    /** A String uniquely describing request token. */
    @Id
    private String id;

    /** Request token's secret. This field is required. */
    @Constraints.Required
    @Column(nullable = false)
    private byte[] secret;

    /**
     * True if request token is authorized; false otherwise. This field is
     * required.
     */
    @Constraints.Required
    @Column(nullable = false)
    private boolean authorized = false;

    /**
     * True if this request token has been exchanged for an encryption key;
     * false otherwise. This field is required.
     */
    @Constraints.Required
    @Column(nullable = false)
    private boolean used = false;

    /** Date when request token starts to be valid. This field is required. */
    @Constraints.Required
    @Formats.DateTime(pattern = "yyyy-mm-dd hh:mm:ss")
    @Column(nullable = false)
    private Date validFrom;

    /** Date when request token stops to be valid. This field is required. */
    @Constraints.Required
    @Formats.DateTime(pattern = "yyyy-mm-dd hh:mm:ss")
    @Column(nullable = false)
    private Date validTo;

    /**
     * Mapping with the {@link Client} entity with as ManyToOne
     * relation. Each request token is associated with one client.
     */
    @Constraints.Required
    @ManyToOne(optional = false)
    private Client client;

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<String, ReqToken> finder = new Finder<>(
            ReqToken.class);

    /**
     * Creates a new request token associated with one client.
     *
     * @param id A String uniquely describing request token.
     * @param secret Request token's secret.
     * @param client Client related to this request token.
     */
    public ReqToken(final String id, final byte[] secret, final Client
            client) {
        this.id = id;
        this.secret = secret;
        this.client = client;
        this.validFrom = new Date();
        validTo = getExpirationDate(validFrom);
    }

    /** Adds request token to escrow agent's database. */
    public void addRequestToken() {
        this.save();
    }

    /**
     * This method sets the validation period of request token.
     *
     * According to current escrow agent's policy, a request token is valid for
     * one day after its creation.
     */
    @Override
    public Date getExpirationDate(final Date validFrom) {
        final int oneDay = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(validFrom);
        calendar.add(Calendar.DATE, oneDay);
        return calendar.getTime();
    }

    /**
     * This method returns an existing request token with the specified ids of
     * both request token and client given as parameters.
     *
     * @param requestToken ID of request token.
     * @param clientId ID of client.
     *
     * @return Request token.
     */
    public static ReqToken authenticate(final String requestToken,
            final String clientId) {
        return ReqToken.finder.where()
                .eq("id", requestToken).eq("client.id", clientId).findUnique();
    }

    /**
     * This method checks if request token is valid.
     *
     * A request token to be valit, it must not have expired and it mustn't
     * have been used.
     *
     * @return True if request token is valid; false otherwise.
     */
    public boolean isValid() {
        return !this.isExpired(this.validFrom, this.validTo) && !this.isUsed();
    }

    /**
     * This method revokes a request token which is going to be exchanged for
     * an encryption so that, each request token is used only once.
     *
     * This can be done with two ways:
     * Either by deleting the corresponding request token or by marking it as
     * used.
     *
     * @param delete True if request token should be deleted; false otherwise.
     */
    public void revokeRequestToken(final boolean delete) {
        if (delete)
            this.delete();
        else {
            this.used = true;
            this.update();
        }
    }

    /** This method updates request token by setting it authorized. */
    public void authorizeRequestToken() {
        this.authorized = true;
        this.update();
    }

    /**
     * Getter of authorized field.
     *
     * @return True if request token is authorized; false otherwise.
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * Getter of id field.
     *
     * @return A String uniquely describing request token.
     */
    public String getId() {
        return id;
    }

    /**
     * Getter of secret field.
     *
     * @return Request token's secret.
     */
    public byte[] getSecret() {
        return secret;
    }

    /**
     * Getter of validFrom field.
     *
     * @return Date when request token starts to be valid.
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * Getter of client field.
     *
     * @return Client associated with request token.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Getter validTo field.
     *
     * @return Date when request token stops to be valid.
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * Getter of used field.
     *
     * @return True if this request token has been exchanged for an encryption
     * key; false otherwise.
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Setter of validTo field.
     *
     * @param validTo Date when request token stops to be valid.
     */
    public void setValidTo(final Date validTo) {
        this.validTo = validTo;
    }

    /**
     * Getter of finder field.
     *
     * @return This field will be used to programatically make queries which
     * return object of this class.
     */
    public static Finder<String, ReqToken> getFinder() {
        return finder;
    }
}
