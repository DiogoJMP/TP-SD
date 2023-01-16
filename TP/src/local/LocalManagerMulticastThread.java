package local;

import central.CentralManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class LocalManagerMulticastThread extends Thread {
    private MulticastSocket multicastSocket;
    private InetAddress group;
    private int port;

    public LocalManagerMulticastThread(MulticastSocket socket, InetAddress group, int port) {
        this.multicastSocket = socket;
        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {
        while (!multicastSocket.isClosed()) {
            byte[] buffer = new byte[CentralManager.getMaxBufferLen()];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
            String message;
            try {
                multicastSocket.receive(datagram);
                message = new String(buffer, 0, datagram.getLength(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println("Socket closed!");
            }
        }
    }
}
