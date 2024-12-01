import java.io.*;
import java.net.*;

public class Client1 {
    private static final String SERVER_IP = "192.168.15.132";
    private static final int SERVER_PORT = 1300;
    private static final String LOGIN_SCRIPT = "./login.sh";
    private static final String CHECK_SCRIPT = "./check.sh";

    public static void main(String[] args) {
        runShellScript(LOGIN_SCRIPT);
        runShellScript(CHECK_SCRIPT);

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send request for system info
            out.println("GET_SYSTEM_INFO");

            // Display the received system info
            System.out.println("System Information Received:");
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runShellScript(String scriptPath) {
        try {
            Process process = Runtime.getRuntime().exec(scriptPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
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
