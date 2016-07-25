package com.github.kristofa.brave.http;

import static com.github.kristofa.brave.http.HttpHeaderUtils.getRemoteAddressFromHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.InetSocketAddress;

@RunWith(MockitoJUnitRunner.class)
public class HttpHeaderUtilsTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Mock
    HttpServerRequest req;

    @Test
    public void returnsNullIfNoHeadersFound() {
        assertThat(getRemoteAddressFromHeaders(req)).isNull();
    }

    @Test
    public void parsesSimpleForwardedForHeader() {
        when(req.getHttpHeaderValue("X-Forwarded-For")).thenReturn("192.168.0.1");
    }

    @Test
    public void parsesMulitvaluedForwardedForHeader() {
        when(req.getHttpHeaderValue("X-Forwarded-For")).thenReturn("192.168.0.1, 192.168.0.2");
        assertHeaders("192.168.0.1", 0);
    }

    @Test
    public void parsesCompoundForwardedPortHeader() {
        when(req.getHttpHeaderValue("X-Forwarded-For")).thenReturn("192.168.0.1, 192.168.0.2");
        when(req.getHttpHeaderValue("X-Forwarded-Port")).thenReturn("20021");
        assertHeaders("192.168.0.1", 20021);
    }

    @Test
    public void parseForwardedHeader() {
        when(req.getHttpHeaderValue("Forwarded")).thenReturn("for=192.168.0.1:20021");
        assertHeaders("192.168.0.1", 20021);
    }

    @Test
    public void parseForwardedHeaderAndPort() {
        when(req.getHttpHeaderValue("Forwarded")).thenReturn("for=\"192.168.0.1:20021\"");
        assertHeaders("192.168.0.1", 20021);
    }

    @Test
    public void parseCompoundForwardedHeader() {
        when(req.getHttpHeaderValue("Forwarded")).thenReturn("for=\"192.168.0.1:20021\"; " +
            "for=\"10.0.0.1:20022\"");
        assertHeaders("192.168.0.1", 20021);
    }

    @Test
    public void parseIpv6ForwardedHeader() {
        when(req.getHttpHeaderValue("Forwarded")).thenReturn("for=[2001:db8:cafe::17];proto=http;by=203.0.113.43");
        assertHeaders("[2001:db8:cafe::17]", 0);
    }

    @Test
    public void parseIpv6HeaderWithPort() {
        when(req.getHttpHeaderValue("Forwarded")).thenReturn("for=[2001:db8:cafe::17]:1234;proto=http;by=203.0.113.43");
        assertHeaders("[2001:db8:cafe::17]", 1234);
    }

    @Test
    public void parseMultivaluedForwardedHeader() {
        when(req.getHttpHeaderValue("Forwarded")).thenReturn("for=192.0.2.60;proto=http;by=203.0.113.43");
        assertHeaders("192.0.2.60", 0);
    }

    @Test
    public void parseMultivaluedForwardedHeaderWithPort() {
        when(req.getHttpHeaderValue("Forwarded")).thenReturn("for=\"192.0.2.60:1234\";proto=http;by=203.0.113.43\"");
        assertHeaders("192.0.2.60", 1234);
    }

    @Test
    public void handlesMalformedForwardedHeader() {
        when(req.getHttpHeaderValue("Forwarded")).thenReturn("192.0.2.60;proto=http;by=203.0.113.43");
        InetSocketAddress address = req.getClientAddress();
        assertThat(address).isNull();
    }

    private void assertHeaders(String host, int port) {
        InetSocketAddress address = getRemoteAddressFromHeaders(req);
        assertThat(address).isNotNull();
        softly.assertThat(address.getHostName()).isEqualTo(host);
        softly.assertThat(address.getPort()).isEqualTo(port);
    }

}
