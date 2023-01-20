package threads;

import java.io.IOException;
import java.net.ServerSocket;

public class LocalManagerUnicastThread extends Thread {

    private final int port;

    public LocalManagerUnicastThread(int port) {
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
