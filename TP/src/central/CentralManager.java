package central;

import java.io.IOException;
import java.net.InetAddress;

import local.LocalManager;

public class CentralManager {

    protected static int multicastPort = 4446;
    protected static int tcpPort = 3000;
    protected static String chosenIp = "239.0.0.";
    protected static int maxBufferLen = 2000;

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 24; i++) {
                new LocalManager(i, InetAddress.getByName("239.0.0." + i)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getMulticastPort() {
        return multicastPort;
    }

    public static int getTcpPort() {
        return tcpPort;
    }

    public static String getChosenIp() {
        return chosenIp;
    }

    public static int getMaxBufferLen() {
        return maxBufferLen;
    }
}
