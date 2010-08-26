package com.reckordp.berkirimfile;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class JukirServer {
    public InetAddress ipAddr;
    public String host;

    static class BukanServer extends ConnectException {

        public BukanServer(String msg) {
            super(msg);
        }
    }

    public JukirServer(InetAddress ip) {
        ipAddr = ip;
        host = ip.getHostName();
    }

    public boolean isServer() throws ConnectException, SocketTimeoutException {
        boolean portNormal;
        Socket koneksi;
        RuangTunggu.PenghantarServer server;
        InetSocketAddress sockAddr = new InetSocketAddress(ipAddr,
                RuangTunggu.PenghantarServer.SOCKET_PORT_SERVER);
        try {
            if (ipAddr.isReachable(18)) {
                koneksi = new Socket();
                koneksi.connect(sockAddr, 24);
                server = new RuangTunggu.PenghantarServer(koneksi);
                portNormal = server.ujiPort();
                server.close();
            } else {
                throw new BukanServer(host + " tidak bisa dihubungi");
            }
        } catch (ConnectException | SocketTimeoutException e) {
            throw e;
        } catch (IOException e) {
            portNormal = false;
        }
        return portNormal;
    }
}
