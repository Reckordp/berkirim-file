package com.reckordp.berkirimfile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class JukirServer {
    public InetAddress ipAddr;
    public String host;

    public JukirServer(InetAddress ip) {
        ipAddr = ip;
        host = ip.getHostName();
    }

    public boolean isServer() throws IOException {
        boolean portNormal = false;
        Socket koneksi;
        RuangTunggu.PenghantarServer server;
        if (ipAddr.isReachable(8)) {
            koneksi = new Socket(ipAddr, RuangTunggu.PenghantarServer.SOCKET_PORT_SERVER);
            server = new RuangTunggu.PenghantarServer(koneksi);
            portNormal = server.ujiPort();
        }
        return portNormal;
    }
}
