package local;

import central.CentralManager;

import java.net.InetAddress;

public class LocalManager extends Thread {

    public final int id;
    private final InetAddress group;
    private final int multicastPort;
    private final int tcpPort;

    public LocalManager(int id, InetAddress group) {
        this.id = id;
        this.group = group;
        this.multicastPort = CentralManager.getMulticastPort();
        this.tcpPort = CentralManager.getTcpPort() + id;
    }

    //local manager receives notification via tcp
    //transmits it to everyone connected to its group via multicast

    @Override
    public void run() {
        new LocalManagerTCPThread(tcpPort).start();
    }
}