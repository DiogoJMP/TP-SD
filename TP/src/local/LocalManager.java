package local;

import threads.LocalManagerMulticastThread;
import threads.LocalManagerUnicastThread;
import threads.ReportSenderThread;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalManager extends Thread {

    public final int id;
    private final InetAddress group;
    private final int port;
    private MulticastSocket multicastSocket;

    public LocalManager(int id, InetAddress group, int port, MulticastSocket multicastSocket) {
        this.id = id;
        this.group = group;
        this.port = port;
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        new LocalManagerUnicastThread(port).start();
        new LocalManagerMulticastThread(id, multicastSocket, group).start();

        boolean sent = false;
        while (!multicastSocket.isClosed()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime now = LocalDateTime.now(ZoneId.of("GMT+0"));
            if (dtf.format(now).split(":")[1].equals("00") && Server.getNotifications().get(id).size() != 0 && !sent) {
                new ReportSenderThread(id, multicastSocket).start();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sent = true;
            }
        }
    }
}