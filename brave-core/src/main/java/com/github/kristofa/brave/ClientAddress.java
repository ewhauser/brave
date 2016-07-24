package com.github.kristofa.brave;

public final class ClientAddress {

    public static final ClientAddress UNKNOWN = new ClientAddress();

    private int ipv4Address;
    private int port;
    private String serviceName;

    private ClientAddress() {
    }

    public ClientAddress(int ipv4Address, int port) {
        this(ipv4Address, port, null);
    }

    public ClientAddress(int ipv4Address, int port, String serviceName) {
        this.ipv4Address = ipv4Address;
        this.port = port;
        this.serviceName = serviceName;
    }

    public int getIpv4Address() {
        return ipv4Address;
    }

    public int getPort() {
        return port;
    }

    public String getServiceName() {
        return serviceName;
    }
}
