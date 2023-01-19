package local;

import org.json.simple.JSONArray;

import java.io.IOException;
import java.net.ServerSocket;

public class LocalManagerTCPThread extends Thread {

    private final int port;

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
