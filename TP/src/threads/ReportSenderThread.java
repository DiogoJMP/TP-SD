package threads;

import central.CentralManager;
import local.Server;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.JSONHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ReportSenderThread extends Thread {
    private MulticastSocket multicastSocket;
    private int id;

    public ReportSenderThread(int id, MulticastSocket socket) {
        this.id = id;
        this.multicastSocket = socket;
    }

    @Override
    public synchronized void run() {
        JSONArray lines = null;
        try {
            lines = JSONHandler.readJSONArrayFromFile("lines");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        JSONArray notifications = Server.getNotifications().get(id);
        JSONObject report = new JSONObject();
        int usersNotified = 0;
        for (int i = 0; i < notifications.size(); i++) {
            JSONObject tempJson = (JSONObject) notifications.get(i);
            JSONArray usersNotifiedJ = (JSONArray) tempJson.get("usersNotified");
            usersNotified += usersNotifiedJ.size();
        }
        report.put("totalNotifications", notifications.size());
        report.put("totalUsersNotified", usersNotified);

        try {
            JSONObject line = (JSONObject) lines.get(id);
            report.put("line", line.get("name"));
            byte[] buffer = report.toJSONString().getBytes();
            multicastSocket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(CentralManager.getMulticastIp() + 24),
                    CentralManager.getMulticastPort()));
        } catch (IOException ignored) {
        }
    }
}
