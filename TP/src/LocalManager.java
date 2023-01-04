import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class LocalManager {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Two arguments required: <multicast-host> <port-number>");
            System.exit(-1);
        }
        try {
            InetAddress group = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            MulticastSocket socket = new MulticastSocket(port);
            socket.setTimeToLive(0);
            socket.joinGroup(group);
            Thread t = new Thread(new LocalManagerThread(socket, group, port));

            t.start();

        } catch (IOException e) {
            System.out.println("Unknown Host");
        }
    }
}