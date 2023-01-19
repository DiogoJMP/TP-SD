package utils;

public class ConsoleHandler {
    public static void clr() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printMenu(String option, String userName) {
        clr();
        switch (option) {
            case "WELCOME" -> System.out.print("""
                    ----------------<WELCOME>----------------
                    1- Sign in
                    2- Sign up
                    0- Exit
                    --------------------------------------->""");
            case "SIGNEDIN" -> System.out.print("""
                    ----------------<WELCOME,\040""" + userName + """
                    >------------
                    1- Check notifications
                    2- Send notification
                    0- Sign out
                    --------------------------------------->""");
            case "CENTRAL" -> System.out.print("""
                    -----------------<WELCOME>-----------------
                    1- Check today's reports
                    2- Send network traffic suspension notice
                    0- Exit
                    ----------------------------------------->""");
        }
    }

}
