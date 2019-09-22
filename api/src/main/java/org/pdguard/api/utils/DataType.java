/*-
 * Copyright 2009-2014 Diomidis Spinellis
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

package org.pdguard.api.utils;

/**
 * This enumeration defines the types of data that can be specified in
 * PDGuard API calls.
 *
 * For the purposes of this class personal and sensitive data include any
 * information relating to an identified or identifiable natural person
 * ("data subject"); an identifiable person is one who can be identified,
 * directly or indirectly, in particular by reference to an identification
 * number or to one or more factors specific to his physical, physiological,
 * mental, economic, cultural or social identity.
 *
 * See the EU Data Protection Directive (Directive 95/46/EC on the protection
 * of individuals with regard to the processing of personal data and on the
 * free movement of such data).
 *
 * Data are organized in a hierarchy, which allows the simplified selection
 * via grouping (e.g. restrict the handling of all medical data), and also
 * offers the possibility of backward compatible additions (the escrow agent
 * or the application can handle the more general type).
 *
 * @author Diomidis Spinellis
 */
public enum DataType implements Subsumption {
    /**
     * Personal but not sensitive data.
     *
     * Example: a data subject's name and address.
     */
    PERSONAL,

    /** Data that is typically used to identify a data subject. */
    IDENTITY(PERSONAL),

    /** The data subject's given name. */
    GIVEN_NAME(IDENTITY),

    /** The data subject's surname. */
    SURNAME(IDENTITY),

    /** The data subject's initials. */
    INITIALS(IDENTITY),

    /** The data subject's pseudonym. */
    PSEUDONYM(IDENTITY),

    /** The data subject's generation qualifier (e.g. Jr, 3rd, IV). */
    GENERATION_QUALIFIER(IDENTITY),

    /** The data subject's title (e.g. Mr Ms Dr) . */
    TITLE(IDENTITY),

    /** The data subject's common name. */
    COMMON_NAME(IDENTITY),

    /** The data subject's country. */
    COUNTRY(IDENTITY),

    /** The data subject's organization. */
    ORGANIZATION(IDENTITY),

    /** The data subject's organizational unit. */
    ORGANIZATIONAL_UNIT(IDENTITY),

    /** The data subject's complete birth date . */
    BIRTH_DATE(IDENTITY),

    /** The data subject's birth year. */
    BIRTH_YEAR(IDENTITY),

    /** The data subject's father given name. */
    FATHER_GIVEN_NAME(IDENTITY),

    /** The data subject's mother given name. */
    MOTHER_GIVEN_NAME(IDENTITY),

    /** The data subject's father maiden name. */
    FATHER_MAIDEN_NAME(IDENTITY),

    /** The data subject's nationality. */
    NATIONALITY(IDENTITY),

    /** Data used to contact the data subject at work. */
    WORK_CONTACT(PERSONAL),

    /** The data subject's work landline phone. */
    WORK_LANDLINE_PHONE(WORK_CONTACT),

    /** The data subject's work mobile phone. */
    WORK_MOBILE_PHONE(WORK_CONTACT),

    /** The data subject's work email. */
    WORK_EMAIL(WORK_CONTACT),

    /** The data subject's work department. */
    WORK_DEPARTMENT(WORK_CONTACT),

    /**
     * The data subject's work street address.
     *
     * This includes appartment name, number, c/o, etc.
     */
    WORK_STREET_ADDRESS(WORK_CONTACT),

    /** The data subject's work city. */
    WORK_CITY(WORK_CONTACT),

    /** The data subject's work state or province. */
    WORK_STATE_OR_PROVINCE(WORK_CONTACT),

    /** The data subject's work country. */
    WORK_COUNTRY(WORK_CONTACT),

    /** The data subject's work postal code. */
    WORK_POSTAL_CODE(WORK_CONTACT),

    /** Data used to contact the data subject at home. */
    HOME_CONTACT(PERSONAL),

    /** The data subject's home phone. */
    HOME_PHONE(HOME_CONTACT),

    /** The data subject's personal mobile phone. */
    PERSONAL_MOBILE_PHONE(HOME_CONTACT),

    /** The data subject's personal email. */
    PERSONAL_EMAIL(HOME_CONTACT),

    /**
     * The data subject's home street-level address.
     *
     * This includes appartment name, number, c/o, etc.
     */
    HOME_STREET_ADDRESS(HOME_CONTACT),

    /** The data subject's home city. */
    HOME_CITY(HOME_CONTACT),

    /** The data subject's home state or province. */
    HOME_STATE_OR_PROVINCE(HOME_CONTACT),

    /** The data subject's home country. */
    HOME_COUNTRY(HOME_CONTACT),

    /** The data subject's home postal code. */
    HOME_POSTAL_CODE(HOME_CONTACT),

    /** Biometrical data. */
    BIOMETRICAL(PERSONAL),

    /** The data subject's photograph. */
    PHOTOGRAPH(BIOMETRICAL),

    /** The data subject's signature. */
    SIGNATURE(BIOMETRICAL),

    /** The data subject's fingerprint(s). */
    FINGERPRINT(BIOMETRICAL),

    /** The data subject's iris scan. */
    IRIS_SCAN(BIOMETRICAL),

    /** Official identification. */
    OFFICIAL_IDENTIFICATION(PERSONAL),

    /** The data subject's tax identification number. */
    TAX_IDENTIFICATION_NUMBER(OFFICIAL_IDENTIFICATION),

    /** The data subject's social security identification number. */
    SOCIAL_SECURITY_IDENTIFICATION_NUMBER(OFFICIAL_IDENTIFICATION),

    /** The data subject's personal identification number. */
    PERSONAL_IDENTIFICATION_NUMBER(OFFICIAL_IDENTIFICATION),

    /** The data subject's driver's license number. */
    DRIVER_LICENSE_NUMBER(OFFICIAL_IDENTIFICATION),

    /** The data subject's passport number. */
    PASSPORT_NUMBER(OFFICIAL_IDENTIFICATION),

    /** The data subject's passport expiry date. */
    PASSPORT_EXPIRY_DATE(OFFICIAL_IDENTIFICATION),

    /** The data subject's passport issue country. */
    PASSPORT_ISSUE_COUNTRY(OFFICIAL_IDENTIFICATION),

    /** A handle to an online service. */
    ONLINE_HANDLE(PERSONAL),

    /** The data subject's work web page. */
    WORK_WEB_PAGE(ONLINE_HANDLE),

    /** The data subject's personal web page. */
    PERSONAL_WEB_PAGE(ONLINE_HANDLE),

    /** The data subject's Twitter identifier. */
    TWITTER_ID(ONLINE_HANDLE),

    /** The data subject's Facebook identifier. */
    FACEBOOK_ID(ONLINE_HANDLE),

    /** The data subject's LinkedIn identifier. */
    LINKEDIN_ID(ONLINE_HANDLE),

    /** The data subject's Google Plus identifier. */
    GOOGLE_PLUS_ID(ONLINE_HANDLE),

    /** The data subject's Yahoo identifier. */
    YAHOO_ID(ONLINE_HANDLE),

    /** The data subject's AIM identifier. */
    AIM_ID(ONLINE_HANDLE),

    /** The data subject's Skype identifier. */
    SKYPE_ID(ONLINE_HANDLE),

    /** The data subject's Jabber identifier. */
    JABBER_ID(ONLINE_HANDLE),

    /** The data subject's IRC identifier. */
    IRC_ID(ONLINE_HANDLE),

    /**
     * Sensitive personal data.
     *
     * Sensitive personal data may reveal racial or ethnic origin, political
     * opinions, religious or philosophical beliefs, trade-union membership,
     * or concern health or sex life.
     *
     * Example: results of a medical diagnostic test.
     *
     * The EU Data Protection Directive (Article 8, from which the above
     * definition is derived) stipulates additional restrictions
     * on the processing of such data.
     */
    SENSITIVE,

    /** Data that may reveal the data subject's racial origin. */
    RACIAL_ORIGIN(SENSITIVE),

    /** Data that may reveal the data subject's racial origin. */
    ETHNIC_ORIGIN(SENSITIVE),

    /** Data that may reveal the data subject's political opinions. */
    POLITICAL_OPINIONS(SENSITIVE),

    /** Data that may reveal the data subject's religious beliefs. */
    RELIGIOUS_BELIEFS(SENSITIVE),

    /** Data that may reveal the data subject's trade union membership. */
    TRADE_UNION_MEMBERSHIP(SENSITIVE),

    /** Data that may reveal the data subject's philosophical beliefs. */
    PHILOSOPHICAL_BELIEFS(SENSITIVE),

    /** Data concerning the data subject's health. */
    HEALTH(SENSITIVE),

    /** Data concerning the data subject's sex life. */
    SEX_LIFE(SENSITIVE),

    /**
     * Nonpersonal data, which are not covered by privacy laws.
     *
     * Example: the weather in New York on December 31st 2000.
     * Nonpersonal data are not further expanded.
     */
    NONPERSONAL;
    /*
     * Leave the above definition at the end of the list to avoid changing
     * the trailing semicolon.
     */


    /** The data type's parent type. */
    private Subsumption parent;

    /**
     * Obtain a data type's parent type.
     *
     * @return the object's parent type.
     */
    public Subsumption getParent() {
        return parent;
    }

    /**
     * Construct a new data type with the specified parent type.
     * @param pParent The parent of the specified type.
     */
    DataType(final Subsumption pParent) {
        this.parent = pParent;
    }

    /**
     * Construct a new data type without a parent.
     */
    DataType() {
        this.parent = null;
    }
}
