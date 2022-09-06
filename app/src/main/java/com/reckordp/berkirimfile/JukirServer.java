package com.reckordp.berkirimfile;

import java.net.InetAddress;

public class JukirServer {
    public InetAddress ipAddr;
    public String host;

    public JukirServer(InetAddress ip) {
        ipAddr = ip;
        host = ip.getHostName();
    }
}
