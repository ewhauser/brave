package com.github.kristofa.brave.resteasy;

import com.github.kristofa.brave.http.HttpHeaderUtils;
import com.github.kristofa.brave.http.HttpServerRequest;

import org.jboss.resteasy.spi.HttpRequest;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;


class RestEasyHttpServerRequest implements HttpServerRequest {

    private final HttpRequest req;

    public RestEasyHttpServerRequest(HttpRequest req) {
        this.req = req;
    }
    
    @Override
    public String getHttpHeaderValue(String headerName) {
        HttpHeaders allHeaders = req.getHttpHeaders();
        List<String> headers = allHeaders.getRequestHeader(headerName);
        return headers == null || headers.isEmpty() ? null : headers.iterator().next();
    }

    @Override
    public InetSocketAddress getClientAddress() {
        return HttpHeaderUtils.getRemoteAddressFromHeaders(this);
    }

    @Override
    public URI getUri() {
        return req.getUri().getAbsolutePath();
    }

    @Override
    public String getHttpMethod() {
        return req.getHttpMethod();
    }
}
