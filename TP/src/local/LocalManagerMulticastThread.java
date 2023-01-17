package local;

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
        JSONArray notifications = new JSONArray();
        while (!multicastSocket.isClosed()) {
            byte[] buffer = new byte[CentralManager.getMaxBufferLen()];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
            String message;
            JSONObject notification;
            try {
                multicastSocket.receive(datagram);
                message = new String(buffer, 0, datagram.getLength(), StandardCharsets.UTF_8);
                if (message.equals("GetNotifications")) {
                    System.out.println(notifications.toJSONString());
                } else {
                    notification = (JSONObject) new JSONParser().parse(message);
                    notifications.add(notification);
                }
            } catch (IOException | ParseException e) {
                System.out.println("Socket closed!");
            }
        }
    }
}
