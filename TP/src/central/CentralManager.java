package central;

import utils.ConsoleHandler;

import java.util.Scanner;

public class CentralManager {

    protected static int multicastPort = 4446;
    protected static int broadcastPort = 3024;
    protected static int tcpPort = 3000;
    protected static String multicastIp = "239.0.0.";
    protected static String broadcastIp = "192.168.1.255";
    protected static int maxBufferLen = 2000;

    public static void main(String[] args) {
        welcomeScreen();
    }

    private static void welcomeScreen() {
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
                break;
            }
        }
    }

    private static void checkReports() {

    }

    private static void sendSuspensionNotice() {

    }

    public static int getMulticastPort() {
        return multicastPort;
    }

    public static int getBroadcastPort() {
        return broadcastPort;
    }

    public static int getTCPPort() {
        return tcpPort;
    }

    public static String getMulticastIp() {
        return multicastIp;
    }

    public static String getBroadcastIp() {
        return broadcastIp;
    }

    public static int getMaxBufferLen() {
        return maxBufferLen;
    }
}
