package threads;

import central.CentralManager;
import local.Server;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class LocalManagerMulticastThread extends Thread {
    private MulticastSocket multicastSocket;
    private InetAddress group;
    private int id;

    public LocalManagerMulticastThread(int id, MulticastSocket socket, InetAddress group) {
        this.id = id;
        this.multicastSocket = socket;
        this.group = group;
    }

    @Override
    public void run() {
        try {
            multicastSocket.joinGroup(group);
            while (!multicastSocket.isClosed()) {
                byte[] buffer = new byte[CentralManager.getMaxBufferLen()];
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, CentralManager.getMulticastPort());
                JSONObject notification;
                multicastSocket.receive(datagram);
                JSONArray notifications = Server.getNotifications().get(id);
                String message = new String(buffer, 0, datagram.getLength(), StandardCharsets.UTF_8);
                notification = (JSONObject) new JSONParser().parse(message);
                notifications.add(notification);
                Server.getNotifications().set(id, notifications);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
