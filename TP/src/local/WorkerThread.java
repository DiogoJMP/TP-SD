package local;

import central.CentralManager;
import utils.JSONHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WorkerThread extends Thread {
    private Socket socket;
    private MulticastSocket multicastSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int[] userGroups;
    private String userName;

    public WorkerThread(Socket socket) {
        this.socket = socket;
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

    private void chooseLines(JSONArray lines, JSONObject input) {
        if (lines.contains(input)) {
            lines.remove(input);
        } else {
            lines.add(input);
        }
    }

    private void printLinesSignUp(JSONArray linesJSON, JSONArray lines) {
        JSONObject tempJSON;
        long tempId;
        String[] linesArray;

        linesArray = new String[linesJSON.size()];
        for (int i = 0; i < linesArray.length; i++) {
            tempJSON = (JSONObject) linesJSON.get(i);
            tempId = (long) tempJSON.get("id") + 1;
            if (lines.contains(tempJSON)) {
                linesArray[i] = (tempId + "- " + tempJSON.get("name") + " (Selected) ");
            } else {
                linesArray[i] = (tempId + "- " + tempJSON.get("name") + " ");
            }
        }

        String finalOutput = "";
        for (int i = 0; i < linesArray.length / 3; i++) {
            finalOutput += "|" + linesArray[i] + "| " + linesArray[i + 8] + "| " + linesArray[i + 16];
            finalOutput += "\n";
        }
        out.println(finalOutput);
        out.println("End");
    }

    private void printLinesNotification(JSONArray linesJSON, JSONArray lines) {
        JSONObject tempJSON;
        long tempId;
        String[] linesArray;
        linesArray = new String[userGroups.length];

        for (int i = 0; i < linesArray.length; i++) {
            tempJSON = (JSONObject) linesJSON.get(userGroups[i]);
            tempId = (long) tempJSON.get("id") + 1;
            if (lines.contains(tempJSON)) {
                linesArray[i] = (tempId + "- " + tempJSON.get("name") + " (Selected) ");
            } else {
                linesArray[i] = (tempId + "- " + tempJSON.get("name") + " ");
            }
        }

        String finalOutput = "";
        for (int i = 0; i < linesArray.length; i++) {
            finalOutput += linesArray[i] + "\n";
        }
        out.println(finalOutput);
        out.println("End");
    }

    private void signUp() throws IOException, ParseException {
        JSONObject user;
        JSONParser jsonParser = new JSONParser();
        JSONArray users = JSONHandler.readJSONArrayFromFile("users");
        JSONArray lines = new JSONArray();
        while (true) {
            user = (JSONObject) jsonParser.parse(in.readLine());
            if (userNameExists(users, (String) user.get("userName"))) {
                out.println("A user with that name already exists.");
            } else {
                out.println("Success");
                break;
            }
        }
        JSONArray linesJSON = JSONHandler.readJSONArrayFromFile("lines");
        int input;
        while (true) {
            printLinesSignUp(linesJSON, lines);
            input = Integer.parseInt(in.readLine());
            if (input > 0 && input <= 24) {
                chooseLines(lines, (JSONObject) linesJSON.get(input - 1));
            }
            if (input == 0 && lines.size() != 0) {
                out.println("Success");
                break;
            } else {
                out.println("Must select at least one line");
            }
        }

        user.put("lines", lines);
        users.add(user);
        JSONHandler.writeToJSONFile(users.toJSONString(), "users");
    }

    private void setUserGroups(JSONArray users, String userName) {
        JSONObject userJson;
        JSONArray linesJ = new JSONArray();
        for (Object o : users) {
            userJson = (JSONObject) o;
            if (userName.equals(userJson.get("userName"))) {
                linesJ = (JSONArray) userJson.get("lines");
                out.println(linesJ);
                break;
            }
        }
        long lline;
        userGroups = new int[linesJ.size()];

        for (int i = 0; i < userGroups.length; i++) {
            JSONObject tempJson = (JSONObject) linesJ.get(i);
            lline = (long) tempJson.get("id");
            userGroups[i] = (int) lline;
        }
    }

    private void signIn() throws IOException, ParseException {
        JSONObject user;
        JSONParser jsonParser = new JSONParser();
        JSONArray users = JSONHandler.readJSONArrayFromFile("users");
        while (true) {
            user = (JSONObject) jsonParser.parse(in.readLine());
            if (!userNameExists(users, (String) user.get("userName"))) {
                out.println("No such user exists.");
            } else if (userNameExists(users, (String) user.get("userName")) &&
                    !passwordIsCorrect(users, (String) user.get("userName"), (String) user.get("password"))) {
                out.println("Wrong password.");
            } else {
                out.println("Success");
                setUserGroups(users, (String) user.get("userName"));
                userName = (String) user.get("userName");
                multicastSocket = new MulticastSocket(CentralManager.getMulticastPort());
                break;
            }
        }
    }

    private void sendNotification() throws IOException, ParseException {
        JSONObject notification = new JSONObject();
        JSONArray lines = new JSONArray();
        JSONArray notificationsJSON = JSONHandler.readJSONArrayFromFile("notifications");
        JSONArray linesJSON = JSONHandler.readJSONArrayFromFile("lines");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("GMT+0"));

        notification.put("date", dtf.format(now));
        int input;
        while (true) {
            printLinesNotification(linesJSON, lines);
            input = Integer.parseInt(in.readLine());
            if (input > 0 && input <= userGroups.length) {
                chooseLines(lines, (JSONObject) linesJSON.get(input - 1));
            }
            if (input == 0 && lines.size() != 0) {
                out.println("Success");
                break;
            } else {
                out.println("Must select at least one line");
            }
        }
        notification.put("linesAffected", lines);
        String comment;
        while (true) {
            comment = in.readLine();
            if (comment.equals("") || comment.equals("\n") || comment.equals("\r")) {
                out.println("Comment cannot be empty");
            } else {
                out.println("Success");
                break;
            }
        }
        notification.put("comment", comment);
        notification.put("usersNotified", new JSONArray());

        byte[] notificationBytes = notification.toJSONString().getBytes();
        ArrayList<Integer> tempList = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            JSONObject tempJSON = (JSONObject) lines.get(i);
            long tempId = (long) tempJSON.get("id");
            tempList.add((int) tempId);
        }
        for (int userGroup : userGroups) {
            if (tempList.contains(userGroup)) {
                InetAddress address = InetAddress.getByName(CentralManager.getMulticastIp() + userGroup);
                DatagramPacket datagram = new DatagramPacket(notificationBytes, notificationBytes.length, address, CentralManager.getMulticastPort());
                multicastSocket.send(datagram);
            }
        }
        notificationsJSON.add(notification);
        JSONHandler.writeToJSONFile(notificationsJSON.toJSONString(), "notifications");
    }

    private void getNotifications() {
        for (int i = 0; i < userGroups.length; i++) {
            for (int j = 0; j < Server.notifications.get(userGroups[i]).size(); j++) {
                JSONObject tempJSON = (JSONObject) Server.notifications.get(userGroups[i]).get(j);
                JSONArray usersNotified = (JSONArray) tempJSON.get("usersNotified");
                if (!usersNotified.contains(userName)) {
                    usersNotified.add(userName);
                }
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
                if (inputLine.equals("SendNotification")) {
                    sendNotification();
                }
                if (inputLine.equals("GetNotifications")) {
                    getNotifications();
                }
                if (inputLine.equals("Signout")) {
                    multicastSocket.close();
                    userName = "";
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