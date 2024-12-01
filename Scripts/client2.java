import java.io.*;
import java.net.*;

public class Client2 {
    public static void main(String[] args) {
        String serverIP = "192.168.15.132"; // Server IP
        int port = 1300;

        // Run search.sh
        runShellScript("./search.sh");

        // Run clientinfo.sh
        runShellScript("./clientinfo.sh");

        // Connect to the server
        try (Socket socket = new Socket(serverIP, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to the server.");

            // Request system info
            out.println("REQUEST_SYSTEM_INFO");

            // Read and display system info
            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END_OF_FILE")) break;
                System.out.println(response);
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
            e.printStackTrace();
        }
    }
}
