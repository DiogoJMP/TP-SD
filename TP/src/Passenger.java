import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class Passenger {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 2048)) {

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            welcomeScreen(out, in);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /**if (args.length != 2) {
         System.out.println("Two arguments required: <multicast-host> <port-number>");
         System.exit(-1);
         }
         InetAddress group = InetAddress.getByName(args[0]);
         int port = Integer.parseInt(args[1]);
         MulticastSocket socket = new MulticastSocket(port);
         socket.setTimeToLive(0);
         socket.joinGroup(group);*/
    }

    private static void welcomeScreen(PrintWriter out, BufferedReader in) throws IOException {
        int option;
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMenu("WELCOME", "");
            option = sc.nextInt();
            while (option < 0 || option > 2) {
                System.out.println("Invalid option");
                option = sc.nextInt();
            }
            if (option == 1) {
                signIn(out, in);
            } else if (option == 2) {
                signUp(out, in);
            } else {
                out.println("Bye");
                break;
            }
        }
    }

    private static void signedInScreen(String userName) {
        int option;
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMenu("SIGNEDIN", userName);
            option = sc.nextInt();
            while (option < 0 || option > 2) {
                System.out.println("Invalid option");
                option = sc.nextInt();
            }
            if (option == 1) {
                System.out.println("shit done");
            } else if (option == 2) {
                System.out.println("some other shit done");
            } else {
                break;
            }
        }
    }

    private static void signUp(PrintWriter out, BufferedReader in) throws IOException {
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
    }

    private static void signIn(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Signin");
        JSONObject user = new JSONObject();
        Scanner scanner = new Scanner(System.in);
        String userName, password;
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
        signedInScreen(userName);
    }

    private static void clr() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void printMenu(String option, String userName) {
        clr();
        switch (option) {
            case "WELCOME":
                System.out.print("""
                        ----------------<WELCOME>----------------
                        1- Sign In
                        2- Sign Up
                        0- Exit
                        --------------------------------------->""");
                break;
            case "SIGNEDIN":
                System.out.print("""
                        ----------------<WELCOME,\040""" + userName + """
                        >----------------
                        1- Do shit
                        2- Do some other shit
                        0- Sign out
                        --------------------------------------->""");
        }
    }
}