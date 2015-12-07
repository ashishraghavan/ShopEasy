package com.shopping.shopeasy.network;

import org.apache.http.HttpResponse;

import java.io.InputStream;

public class Response {

    public enum ResponseType {
        STRING("String"),
        INPUTSTREAM("InputStream");

        private String responseType;
        ResponseType(final String responseType) {
            this.responseType = responseType;
        }

        public String getResponseType() {
            return responseType;
        }
    }

    private HttpResponse httpResponse;
    private InputStream inputStream;
    private String responseEntity;
    private Object convertedEntity;

    public Response(final HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
        initialize(ResponseType.STRING);
    }

    public Response(final String responseString) {
        this.responseEntity = responseString;
        initialize(ResponseType.STRING);
    }

    public Response(final InputStream inputStream) {
        this.inputStream = inputStream;
        initialize(ResponseType.INPUTSTREAM);
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    void initialize(final ResponseType responseType) {
        if ( responseType == ResponseType.STRING ) {

        }
    }
}
