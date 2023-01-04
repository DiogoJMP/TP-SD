import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class LocalManagerThread implements Runnable {

    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private final int MAX_LEN = 5000;

    LocalManagerThread(MulticastSocket socket, InetAddress group, int port) {
        this.socket = socket;
        this.group = group;
        this.port = port;
    }

    public void userSignup(JSONObject user) {
        user.remove("type");
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray users = (JSONArray) jsonParser.parse(new FileReader("files/users.json"));
           } catch (IOException e) {
            System.out.println("Error opening file");
        } catch (ParseException e) {
            System.out.println("Parsing error");
        }
    }

    public void userSignin(JSONObject user) {

    }

    @Override
    public void run() {
        JSONParser parser = new JSONParser();
        while (true) {
            byte[] buffer = new byte[MAX_LEN];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
            String message;
            try {
                socket.receive(datagram);
                message = new String(buffer, 0, datagram.getLength(), StandardCharsets.UTF_8);
                JSONObject jsonMessage = (JSONObject) parser.parse(message);
                if (jsonMessage.get("type").equals("Signup")) {
                    userSignup(jsonMessage);
                } else if (jsonMessage.get("type").equals("Signin")) {
                    userSignin(jsonMessage);
                }
            } catch (IOException e) {
                System.out.println("Socket closed!");
            } catch (ParseException e) {
                System.out.println("Error while parsing message");
            }
        }
    }
}