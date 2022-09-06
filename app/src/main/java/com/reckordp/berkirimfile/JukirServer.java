package com.reckordp.berkirimfile;

import java.io.IOException;
import java.net.InetAddress;

public class JukirServer {
    public InetAddress ipAddr;
    public String host;

    public JukirServer(InetAddress ip) {
        ipAddr = ip;
        host = ip.getHostName();
    }

    public boolean isServer() throws IOException {
        return ipAddr.isReachable(1000);
    }
}
