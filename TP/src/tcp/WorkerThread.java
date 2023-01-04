package tcp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;

public class WorkerThread extends Thread {
    private Socket socket = null;
    private PrintWriter out;
    private BufferedReader in;

    private InetAddress ip;

    public WorkerThread(Socket socket) {
        super("WorkerThread");
        this.socket = socket;
        this.ip = socket.getInetAddress();
    }

    private boolean userNameAlreadyExists(JSONArray users, String userName) throws IOException, ParseException {
        JSONObject userJson;
        for (Object o : users) {
            userJson = (JSONObject) o;
            if (userJson.get("userName").equals(userName)) {
                out.println("A user with that name already exists.");
                return true;
            }
        }
        out.println("Success");
        return false;
    }

    private void signUp() throws IOException, ParseException {
        JSONObject user;
        JSONParser jsonParser = new JSONParser();
        JSONArray users = (JSONArray) jsonParser.parse(new FileReader("../files/users.json"));
        do {
            user = (JSONObject) jsonParser.parse(in.readLine());
        } while (userNameAlreadyExists(users, (String) user.get("userName")));

        users.add(user);
        FileWriter file = new FileWriter("../files/users.json");
        file.write(users.toJSONString());
        file.close();
    }

    private void signIn() {

    }

    public void run() {
        try {

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("Signup")) {
                    signUp();
                }
                if (inputLine.equals("Signin")) {
                    signIn();
                }
                if (inputLine.equals("Bye")) {
                    break;
                }
            }
            out.close();
            in.close();
            socket.close();

        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
    }
}