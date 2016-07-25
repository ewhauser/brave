package com.github.kristofa.brave.servlet;

import com.github.kristofa.brave.http.HttpHeaderUtils;
import com.github.kristofa.brave.http.HttpServerRequest;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

public class ServletHttpServerRequest implements HttpServerRequest {

    private final HttpServletRequest request;

    public ServletHttpServerRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getHttpHeaderValue(String headerName) {
        return request.getHeader(headerName);
    }

    @Override
    public InetSocketAddress getClientAddress() {
        //Use data from proxy headers before using data returned by servlet container
        InetSocketAddress remoteAddressFromHeaders = HttpHeaderUtils.getRemoteAddressFromHeaders(this);
        return remoteAddressFromHeaders != null ? remoteAddressFromHeaders :
            InetSocketAddress.createUnresolved(request.getRemoteHost(), request.getRemotePort());
    }

    @Override
    public URI getUri() {
        try {
            return new URI(request.getRequestURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getHttpMethod() {
        return request.getMethod();
    }
}
