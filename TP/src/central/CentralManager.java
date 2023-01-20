package central;

import org.json.simple.JSONObject;
import threads.MulticastThread;
import org.json.simple.JSONArray;
import utils.ConsoleHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class CentralManager {

    protected static int multicastPort = 4446;
    protected static int tcpPort = 3000;
    protected static String multicastIp = "239.0.0.";
    protected static int maxBufferLen = 2000;
    private static JSONArray notifications = new JSONArray();
    private static MulticastSocket multicastSocket;

    public static void main(String[] args) {
        try {
            multicastSocket = new MulticastSocket(multicastPort);
            for (int i = 0; i < 24; i++) {
                new MulticastThread(multicastSocket,
                        InetAddress.getByName(multicastIp + i), notifications).start();
            }
            welcomeScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void welcomeScreen() throws IOException {
        int option;
        Scanner sc = new Scanner(System.in);
        while (true) {
            ConsoleHandler.printMenu("CENTRAL", "");
            option = sc.nextInt();
            while (option < 0 || option > 2) {
                System.out.println("Invalid option");
                option = sc.nextInt();
            }
            if (option == 1) {
                checkReports();
            } else if (option == 2) {
                sendSuspensionNotice();
            } else {
                multicastSocket.close();
                break;
            }
        }
    }

    private static void checkReports() {

    }

    private static void sendSuspensionNotice() throws IOException {
        JSONObject notification = new JSONObject();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("GMT+0"));

        notification.put("date", dtf.format(now));
        notification.put("linesAffected", "Network-wide");
        notification.put("comment", "Traffic suspended");
        notification.put("usersNotified", new JSONArray());

        byte[] notificationBytes = notification.toJSONString().getBytes();
        for (int i = 0; i < 24; i++) {
            InetAddress address = InetAddress.getByName(multicastIp + i);
            DatagramPacket datagram = new DatagramPacket(notificationBytes, notificationBytes.length, address, multicastPort);
            multicastSocket.send(datagram);
        }

    }

    public static int getMulticastPort() {
        return multicastPort;
    }

    public static int getTCPPort() {
        return tcpPort;
    }

    public static String getMulticastIp() {
        return multicastIp;
    }

    public static int getMaxBufferLen() {
        return maxBufferLen;
    }
}
