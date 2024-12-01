import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class Server {
    private static final int PORT = 1300;
    private static final String SYSTEM_INFO_FILE = "system_info.txt";
    private static final String SYSTEM_SCRIPT = "./system.sh";
    private static final String NETWORK_SCRIPT = "./network.sh";
    private static final Lock lock = new ReentrantLock();

    private static List<String> connectedClients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                System.out.println("Client connected: " + clientAddress);
                connectedClients.add(clientAddress);

                // Start a new thread for the client
                new Thread(new ClientHandler(clientSocket)).start();

                // Run the Network.sh script
                runNetworkScript();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runNetworkScript() {
        try {
            Process process = Runtime.getRuntime().exec(NETWORK_SCRIPT);
            process.waitFor();
            System.out.println("Network.sh executed successfully.");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing Network.sh: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = in.readLine();
                System.out.println("Request from client: " + request);

                if ("GET_SYSTEM_INFO".equals(request)) {
                    lock.lock(); // Synchronize access to system info generation
                    try {
                        generateSystemInfo();
                        sendSystemInfo(out);
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void generateSystemInfo() {
            try {
                Process process = Runtime.getRuntime().exec(SYSTEM_SCRIPT);
                process.waitFor();
                System.out.println("System information generated using system.sh.");
            } catch (IOException | InterruptedException e) {
                System.err.println("Error generating system info: " + e.getMessage());
            }
        }

        private void sendSystemInfo(PrintWriter out) {
            File file = new File(SYSTEM_INFO_FILE);
            if (!file.exists()) {
                System.err.println("System info file not found: " + SYSTEM_INFO_FILE);
                out.println("Error: System info file not found.");
                return;
            }

            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    out.println(line);
                }
                System.out.println("System info file sent to client.");
            } catch (IOException e) {
                System.err.println("Error reading system info file: " + e.getMessage());
            }
        }
    }
}
