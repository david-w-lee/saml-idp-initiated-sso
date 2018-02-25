package com.davidwlee.model;

public class SamlOutputs {
    private String samlResponse;
    private String samlResponseSigned;
    private String samlResponseSingedAndBase64Encoded;

    public String getSamlResponse() {
        return samlResponse;
    }

    public void setSamlResponse(String samlResponse) {
        this.samlResponse = samlResponse;
    }

    public String getSamlResponseSigned() {
        return samlResponseSigned;
    }

    public void setSamlResponseSigned(String samlResponseSigned) {
        this.samlResponseSigned = samlResponseSigned;
    }

    public String getSamlResponseSingedAndBase64Encoded() {
        return samlResponseSingedAndBase64Encoded;
    }

    public void setSamlResponseSingedAndBase64Encoded(String samlResponseSingedAndBase64Encoded) {
        this.samlResponseSingedAndBase64Encoded = samlResponseSingedAndBase64Encoded;
    }
}
