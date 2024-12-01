import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class Client2 {
    private static final String SERVER_IP = "192.168.15.132"; // Server IP
    private static final int SERVER_PORT = 1300; // Server port
    private static final String SYSTEM_INFO_FILE = "system_info_client2.txt";

    public static void main(String[] args) {
        while (true) {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);

                // Request system information
                out.println("GET_SYSTEM_INFO");
                System.out.println("Request sent: GET_SYSTEM_INFO");

                // Read and save the system information file
                try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(SYSTEM_INFO_FILE))) {
                    String line;
                    System.out.println("Receiving system information from the server...");
                    while ((line = in.readLine()) != null) {
                        fileWriter.write(line);
                        fileWriter.newLine();
                        System.out.println(line); // Display content on terminal
                    }
                }

                System.out.println("System information saved to: " + SYSTEM_INFO_FILE);

            } catch (IOException e) {
                System.err.println("Error connecting to the server: " + e.getMessage());
                e.printStackTrace();
            }

            // Wait for 5 minutes before the next request
            try {
                System.out.println("Waiting for 5 minutes before the next request...");
                TimeUnit.MINUTES.sleep(5);
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted: " + e.getMessage());
            }
        }
    }
}
