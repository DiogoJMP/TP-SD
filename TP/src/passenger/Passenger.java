package passenger;

import central.CentralManager;
import com.sun.security.jgss.GSSUtil;
import threads.MulticastThread;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.ConsoleHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Passenger {

    private static PrintWriter out;
    private static BufferedReader in;
    private static String userName;
    private static JSONArray notifications = new JSONArray();
    private static MulticastSocket multicastSocket;
    private static AtomicBoolean newNotifications = new AtomicBoolean(false);

    public static void main(String[] args) {
        if (args.length != 1 || !(Integer.parseInt(args[0]) >= 1 && Integer.parseInt(args[0]) <= 24)) {
            System.out.println("Function: Passenger <lineNumber> (1-24)");
            System.exit(-1);
        }
        try (Socket socket = new Socket("localhost",
                CentralManager.getTCPPort() + Integer.parseInt(args[0]))) {

            out = new PrintWriter(socket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            welcomeScreen();

        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void welcomeScreen() throws IOException, ParseException, InterruptedException {
        int option;
        Scanner sc = new Scanner(System.in);
        while (true) {
            ConsoleHandler.printMenu("WELCOME", "");
            option = sc.nextInt();
            while (option < 0 || option > 2) {
                System.out.println("Invalid option");
                option = sc.nextInt();
            }
            if (option == 1) {
                signIn();
                signedInScreen();
            } else if (option == 2) {
                signUp();
            } else {
                out.println("Bye");
                break;
            }
        }
    }

    private static void signedInScreen() throws IOException, InterruptedException {
        int option;
        Scanner sc = new Scanner(System.in);
        while (true) {
            Thread.sleep(100);
            if (newNotifications.get()) {
                ConsoleHandler.printMenu("SIGNEDIN_NEW", userName);
            } else {
                ConsoleHandler.printMenu("SIGNEDIN", userName);
            }
            option = sc.nextInt();
            while (option < 0 || option > 2) {
                System.out.println("Invalid option");
                option = sc.nextInt();
            }
            if (option == 1) {
                checkNotifications();
            } else if (option == 2) {
                sendNotification();
            } else {
                out.println("Signout");
                multicastSocket.close();
                break;
            }
        }
    }

    private static void signUp() throws IOException {
        out.println("Signup");
        JSONObject user = new JSONObject();
        Scanner scanner = new Scanner(System.in);
        String userName, password;
        String output = "";
        do {
            ConsoleHandler.clr();
            System.out.println(output);
            System.out.print("Enter a username: ");
            userName = scanner.nextLine();
            System.out.print("Enter a password: ");
            password = scanner.nextLine();
            user.put("userName", userName);
            user.put("password", password);
            out.println(user.toJSONString());
            output = in.readLine();
        } while (!output.equals("Success"));
        int option;
        do {
            ConsoleHandler.clr();
            while (!(output = in.readLine()).equals("End")) {
                System.out.println(output);
            }
            System.out.println("|0- Exit");
            System.out.print("Select your lines: ");
            option = scanner.nextInt();
            if (option <= 24) {
                out.println(option);
            }
            output = in.readLine();
        } while (!output.equals("Success"));
    }

    private static void signIn() throws IOException, ParseException {
        out.println("Signin");
        JSONObject user = new JSONObject();
        Scanner scanner = new Scanner(System.in);
        String password;
        String output = "";
        do {
            ConsoleHandler.clr();
            System.out.println(output);

            System.out.print("Enter your username: ");
            userName = scanner.nextLine();
            System.out.print("Enter your password: ");
            password = scanner.nextLine();

            user.put("userName", userName);
            user.put("password", password);
            out.println(user.toJSONString());

            output = in.readLine();
        } while (!output.equals("Success"));

        output = in.readLine();
        JSONArray groupsJ = (JSONArray) new JSONParser().parse(output);
        int[] groups = new int[groupsJ.size()];
        long lgroup;
        multicastSocket = new MulticastSocket(CentralManager.getMulticastPort());

        for (int i = 0; i < groups.length; i++) {
            JSONObject tempJson = (JSONObject) groupsJ.get(i);
            lgroup = (long) tempJson.get("id");
            groups[i] = (int) lgroup;
            new MulticastThread(multicastSocket,
                    InetAddress.getByName(CentralManager.getMulticastIp() + groups[i]), notifications, newNotifications).start();
        }
    }

    private static void checkNotifications() {
        Scanner scanner = new Scanner(System.in);
        int option;
        if (notifications.size() == 0) {
            do {
                System.out.print("""
                        No notifications
                        ----------------------------------------
                        0- Exit
                        --------------------------------------->""");
                option = scanner.nextInt();
            } while (option != 0);
            return;
        }
        newNotifications.set(false);
        out.println("GetNotifications");
        JSONArray formattedNotifications = ConsoleHandler.formatNotifications(notifications);
        ConsoleHandler.clr();

        for (int i = 0; i < formattedNotifications.size(); i++) {
            String linesAffected = "";
            JSONObject notification = (JSONObject) formattedNotifications.get(i);
            String[] dateTime = notification.get("date").toString().split(" ");
            if (notification.get("linesAffected") instanceof JSONArray) {
                JSONArray linesAffectedJ = (JSONArray) notification.get("linesAffected");
                for (int j = 0; j < linesAffectedJ.size(); j++) {
                    JSONObject lineAffected = (JSONObject) linesAffectedJ.get(j);
                    linesAffected += lineAffected.get("name").toString();
                    if (j != linesAffectedJ.size() - 1) {
                        linesAffected += ", ";
                    }
                }

            } else {
                linesAffected = notification.get("linesAffected").toString();
            }
            System.out.println("""
                    -------------<NOTIFICATION>-------------
                    Date:\040""" + dateTime[0] + """
                    \nTime:\040""" + dateTime[1] + """
                    \nComment:\040""" + notification.get("comment").toString() + """
                    \nLines affected:\040""" + linesAffected + """
                    \n----------------------------------------""");
        }
        do {
            System.out.print("""
                    0- Exit
                    --------------------------------------->""");
            option = scanner.nextInt();
        } while (option != 0);
    }

    private static void sendNotification() throws IOException {
        out.println("SendNotification");
        Scanner scanner = new Scanner(System.in);
        String output = "";
        String comment;
        int option;
        do {
            ConsoleHandler.clr();
            while (!(output = in.readLine()).equals("End")) {
                System.out.println(output);
            }
            System.out.println("|0- Confirm");
            System.out.print("Select the lines that have been affected: ");
            option = scanner.nextInt();
            if (option <= 24) {
                out.println(option);
            }
            output = in.readLine();
        } while (!output.equals("Success"));

        do {
            ConsoleHandler.clr();
            System.out.println(output);
            System.out.print("Write a comment: ");
            comment = scanner.nextLine();
            out.println(comment);
            output = in.readLine();
        } while (!output.equals("Success"));

    }

}