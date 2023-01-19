package local;

import central.CentralManager;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class LocalManager extends Thread {

    public final int id;
    private final InetAddress group;
    private DatagramSocket socket;
    private int port;

    public LocalManager(int id, InetAddress group, int port) {
        this.id = id;
        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            new LocalManagerTCPThread(port).start();
            new LocalManagerMulticastThread(id, new MulticastSocket(CentralManager.getMulticastPort()), group).start();
            /**
             socket = new DatagramSocket(CentralManager.getBroadcastPort());
             *while (!socket.isClosed()) {
             *                 byte[] buffer = new byte[CentralManager.getMaxBufferLen()];
             *                 DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("0.0.0.0"),
             *                         CentralManager.getMulticastPort());
             *                 String message;
             *                 socket.receive(datagram);
             *             }
             */
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}