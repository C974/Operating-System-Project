import java.io.*;
import java.net.*;

public class Client2 {

    public static void main(String[] args) {
        String serverAddress = "192.168.15.129"; // Server IP Address
        int serverPort = 1300; // Server Port

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("Connected to the server.");

            // Run search.sh and display output
            System.out.println("Executing search.sh...");
            runShellScript("search.sh");

            // Run clientinfo.sh and display output
            System.out.println("Executing clientinfo.sh...");
            runShellScript("clientinfo.sh");

        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }

    // Method to run a shell script and display its output
    private static void runShellScript(String scriptName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptName);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println(scriptName + " exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing script " + scriptName + ": " + e.getMessage());
        }
    }
}
