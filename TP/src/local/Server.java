package local;

import central.CentralManager;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Server {
    protected volatile static AtomicReferenceArray<JSONArray> notifications = new AtomicReferenceArray<>(24);

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 24; i++) {
                notifications.set(i, new JSONArray());
                new LocalManager(i, InetAddress.getByName(CentralManager.getMulticastIp() + i), CentralManager.getTCPPort() + i,
                        new MulticastSocket(CentralManager.getMulticastPort())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AtomicReferenceArray<JSONArray> getNotifications() {
        return notifications;
    }

}
