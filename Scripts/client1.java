import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Client1 {
    private static final String SERVER_IP = "192.168.15.132"; // Replace with your server's IP
    private static final int SERVER_PORT = 1300;

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Send system info request
                out.println("GET_SYSTEM_INFO");
                System.out.println("Requested system info from the server...");

                // Receive and display system info
                System.out.println("System Information Received:");
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }

            } catch (IOException e) {
                System.err.println("Error connecting to server: " + e.getMessage());
            }
        };

        // Schedule the task to run every 5 minutes
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);
    }
}
