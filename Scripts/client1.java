import java.io.*;
import java.net.*;

public class Client1 {
    private static final String SERVER_IP = "192.168.15.132"; // Server IP
    private static final int SERVER_PORT = 1300; // Server port
    private static final String LOGIN_SCRIPT = "./login.sh";
    private static final String CHECK_SCRIPT = "./check.sh";

    public static void main(String[] args) {
        // Run login.sh script
        System.out.println("Running login script...");
        runShellScript(LOGIN_SCRIPT);

        // Run check.sh script
        System.out.println("Running check script...");
        runShellScript(CHECK_SCRIPT);

        // Connect to the server
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);

            // Request system information
            out.println("GET_SYSTEM_INFO");
            System.out.println("Request sent: GET_SYSTEM_INFO");

            // Read and display the response from the server
            System.out.println("Receiving system information from the server...");
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runShellScript(String scriptPath) {
        try {
            Process process = Runtime.getRuntime().exec(scriptPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Output of " + scriptPath + ":");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error running shell script: " + scriptPath);
            e.printStackTrace();
        }
    }
}
