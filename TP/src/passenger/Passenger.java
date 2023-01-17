package passenger;

import central.CentralManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Passenger {

    private static PrintWriter out;
    private static BufferedReader in;
    private static String userName;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", CentralManager.getTcpPort())) {

            out = new PrintWriter(socket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            welcomeScreen();

        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void welcomeScreen() throws IOException, ParseException {
        int option;
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMenu("WELCOME");
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

    private static void signedInScreen() throws IOException, ParseException {
        int option;
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMenu("SIGNEDIN");

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
            clr();
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
            clr();
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

    private static void signIn() throws IOException {
        out.println("Signin");
        JSONObject user = new JSONObject();
        Scanner scanner = new Scanner(System.in);
        String password;
        String output = "";
        do {
            clr();
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
    }

    private static void checkNotifications() throws IOException, ParseException {
        out.println("GetNotifications");
        Scanner scanner = new Scanner(System.in);
        String output = in.readLine();
        while (!output.equals("") && !output.equals("End")) {
            JSONArray notifications = (JSONArray) new JSONParser().parse(output);
            if (notifications.size() != 0) {
                System.out.println(notifications.toJSONString());
            }
            output = in.readLine();
        }
        int option;
        do {
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
            clr();
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
            clr();
            System.out.println(output);
            System.out.print("Write a comment: ");
            comment = scanner.nextLine();
            out.println(comment);
            output = in.readLine();
        } while (!output.equals("Success"));

    }

    private static void clr() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    private static void printMenu(String option) {
        clr();
        switch (option) {
            case "WELCOME":
                System.out.print("""
                        ----------------<WELCOME>----------------
                        1- Sign in
                        2- Sign up
                        0- Exit
                        --------------------------------------->""");
                break;
            case "SIGNEDIN":
                System.out.print("""
                        ----------------<WELCOME,\040""" + userName + """
                        >------------
                        1- Check notifications
                        2- Send notification
                        0- Sign out
                        --------------------------------------->""");
        }
    }

}