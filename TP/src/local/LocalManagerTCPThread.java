package local;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import central.CentralManager;

public class LocalManagerTCPThread extends Thread {

    private final int port;
    private InetAddress group;

    public LocalManagerTCPThread(int port) {
        this.port = port;
    }

    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                new WorkerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
