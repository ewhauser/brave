package com.github.kristofa.brave.http;


import java.net.InetSocketAddress;

public interface HttpServerRequest extends HttpRequest {

    /**
     * Get http header value.
     *
     * @param headerName
     * @return
     */
    String getHttpHeaderValue(String headerName);

    /**
     * Get the endpoint of the client that generated the request.
     *
     * @return the endpoint
     */
    InetSocketAddress getClientAddress();

}
