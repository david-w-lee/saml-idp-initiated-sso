package com.davidwlee.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SamlConfig {

    // -------------------Identity Provider Values-------------------
    // SAML Version
    // There are two versions(i.e. 1.1, 2.0) but mostly 2.0 is used.
    private String samlVersion;

    // Issuer is Entity Id of the identity provider. It is normally in URL form and its purpose is to uniquely identify the SAML identity provider.
    private String issuer;

    // -------------------Service Provider Values-------------------
    // Subject. username or federated id in service provider side
    private String subject;

    // Assertion Consumer Service (ACS) URL or destination url
    private String acsUrl;

    // Recipient Url. The values for recipient url and destination url are the same.
    // Only difference is that they are populated in different parts of SAML.
    // destination is an attribute of response tag whereas recipient is an attribute of assertion tag.
    private String recipient;

    // Audience is EntityID of service provider. It indicates whom this SAML assertion is intended for.
    private String audience;

    // In SAML 1.1, TARGET name value pair is added in POST request body(https://en.wikipedia.org/wiki/SAML_1.1)
    // In SAML 2.0, RelayState name value pair is added in POST request body.
    private String relayState;

    // ssoStartPage is the page to which the user should be redirected when trying to log in with SAML.
    private String ssoStartPage;

    public String getSamlVersion() {
        return samlVersion;
    }

    public void setSamlVersion(String samlVersion) {
        this.samlVersion = samlVersion;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    public String getSsoStartPage() {
        return ssoStartPage;
    }

    public void setSsoStartPage(String ssoStartPage) {
        this.ssoStartPage = ssoStartPage;
    }
}
