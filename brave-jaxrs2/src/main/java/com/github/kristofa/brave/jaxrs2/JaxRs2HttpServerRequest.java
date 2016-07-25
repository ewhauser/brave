package com.github.kristofa.brave.jaxrs2;

import com.github.kristofa.brave.http.HttpHeaderUtils;
import com.github.kristofa.brave.http.HttpServerRequest;

import java.net.InetSocketAddress;
import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;

public class JaxRs2HttpServerRequest implements HttpServerRequest {

    private final ContainerRequestContext containerRequestContext;

    public JaxRs2HttpServerRequest(ContainerRequestContext containerRequestContext) {
        this.containerRequestContext = containerRequestContext;
    }

    @Override
    public String getHttpHeaderValue(String headerName) {
        return containerRequestContext.getHeaderString(headerName);
    }

    @Override
    public InetSocketAddress getClientAddress() {
        return HttpHeaderUtils.getRemoteAddressFromHeaders(this);
    }

    @Override
    public URI getUri() {
        return containerRequestContext.getUriInfo().getRequestUri();
    }

    @Override
    public String getHttpMethod() {
        return containerRequestContext.getMethod();
    }
}
