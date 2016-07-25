package com.github.kristofa.brave.http;

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HttpHeaderUtils {

    private static final Pattern DELIMITER_SUBTRACT_WHITESPACE = Pattern.compile("(,|;)[\\w]*");
    private static final Pattern EQUALS = Pattern.compile("=");

    /**
     * Attempts to match any valid host and port combinations for RFC-7239.  See Section #6 on node identifiers for the
     * matching rules.  A couple of notes:
     *
     * <ul>
     *     <li>We don't care if the address is actually valid, so match the loosest definition which is the host
     *     <li>Quotes are optional at the beginning or end, but required if a port is supplied
     *     <li>Underscores and hyphens are valid in the host name
     * </ul>
     */
    private static final Pattern HOST_AND_PORT = Pattern.compile(
            "(?:\")" //optional quotes
            + "?(?<host>[a-zA-Z0-9:_\\-\\.]+)" // ipv4, ipv6, or host
            + "(?::)"
            + "(?:(?<port>\\d{1,5}))" //match the port
            + "(?:\")?" //optional surrounding quotes
    );

    private HttpHeaderUtils() {
    }

    /**
     * Parses known proxy headers according to https://tools.ietf.org/html/rfc7239.
     *
     * @param request The request
     * @return a socket address representing the remote host or null if not found
     */
    public static InetSocketAddress getRemoteAddressFromHeaders(HttpServerRequest request) {
        String host = null;
        int port = 0;

        String forwardedForValue = request.getHttpHeaderValue("X-Forwarded-For");
        if (forwardedForValue != null) {
            final String[] hosts = DELIMITER_SUBTRACT_WHITESPACE.split(forwardedForValue);
            host = hosts[0];
        }

        String portHeaderValue = request.getHttpHeaderValue("X-Forwarded-Port");
        if (portHeaderValue != null) {
            port = silentlyParseInt(portHeaderValue);
        }

        String forwardedValue = request.getHttpHeaderValue("Forwarded");
        if (forwardedValue != null) {
            final String[] values = DELIMITER_SUBTRACT_WHITESPACE.split(forwardedValue);
            for (String value : values) {
                String[] kv = EQUALS.split(value);
                if (kv.length == 2 && "for".equalsIgnoreCase(kv[0])) {
                    Matcher matcher = HOST_AND_PORT.matcher(kv[1]);
                    if (matcher.matches()) {
                        host = matcher.group("host");
                        port = silentlyParseInt(matcher.group("port"));
                        break;
                    }
                }
            }
        }

        return host != null ? InetSocketAddress.createUnresolved(host, port) : null;
    }

    private static int silentlyParseInt(String maybeInt) {
        int value = 0;
        try {
            value = Integer.parseInt(maybeInt);
        } catch (NumberFormatException ignored) {
        }
        return value;
    }

}
