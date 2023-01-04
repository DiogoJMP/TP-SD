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

    private boolean userNameExists(JSONArray users, String userName) {
        JSONObject userJson;
        for (Object o : users) {
            userJson = (JSONObject) o;
            if (userJson.get("userName").equals(userName)) {
                return true;
            }
        }
        return false;
    }

    private boolean passwordIsCorrect(JSONArray users, String userName, String password) {
        JSONObject userJson;
        for (Object o : users) {
            userJson = (JSONObject) o;
            if (userJson.get("userName").equals(userName) && userJson.get("password").equals(password)) {
                return true;
            }
        }
        return false;

    }

    private void signUp() throws IOException, ParseException {
        JSONObject user;
        JSONParser jsonParser = new JSONParser();
        JSONArray users = (JSONArray) jsonParser.parse(new FileReader("../files/users.json"));
        while (true) {
            user = (JSONObject) jsonParser.parse(in.readLine());
            if (userNameExists(users, (String) user.get("userName"))) {
                out.println("A user with that name already exists.");
            } else {
                out.println("Success");
                break;
            }
        }
        users.add(user);
        FileWriter file = new FileWriter("../files/users.json");
        file.write(users.toJSONString());
        file.close();
    }

    private void signIn() throws IOException, ParseException {
        JSONObject user;
        JSONParser jsonParser = new JSONParser();
        JSONArray users = (JSONArray) jsonParser.parse(new FileReader("../files/users.json"));
        while (true) {
            user = (JSONObject) jsonParser.parse(in.readLine());
            if (!userNameExists(users, (String) user.get("userName"))) {
                out.println("No such user exists.");
            } else if (userNameExists(users, (String) user.get("userName")) &&
                    !passwordIsCorrect(users, (String) user.get("userName"), (String) user.get("password"))) {
                out.println("Wrong password.");
            } else {
                out.println("Success");
                break;
            }
        }
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