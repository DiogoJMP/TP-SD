package passenger;

import central.CentralManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class PassengerMulticastThread extends Thread {
    private InetAddress group;
    private MulticastSocket multicastSocket;
    private JSONArray notifications;

    public PassengerMulticastThread(MulticastSocket multicastSocket, InetAddress group, JSONArray notifications) {
        this.multicastSocket = multicastSocket;
        this.group = group;
        this.notifications = notifications;
    }

    @Override
    public synchronized void run() {
        try {
            multicastSocket.joinGroup(group);
            while (!multicastSocket.isClosed()) {
                byte[] buffer = new byte[CentralManager.getMaxBufferLen()];
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, CentralManager.getMulticastPort());
                JSONObject notification;
                multicastSocket.receive(datagram);
                String message = new String(buffer, 0, datagram.getLength(), StandardCharsets.UTF_8);
                notification = (JSONObject) new JSONParser().parse(message);
                notifications.add(notification);
            }
        } catch (IOException | ParseException ignored) {
        }
    }
}
