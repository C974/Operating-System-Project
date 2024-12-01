import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class Server {

    // List to track client requests
    private static final List<String> clientInfoList = Collections.synchronizedList(new ArrayList<>());

    // Lock to synchronize access to shared resources
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        int port = 1300;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                // Accept a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Run Network.sh to test connections
                runShellScript("Network.sh");

                // Handle the client in a new thread
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // Method to run a shell script
    private static void runShellScript(String scriptName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", scriptName);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error running script " + scriptName + ": " + e.getMessage());
        }
    }

    // ClientHandler class to manage individual client connections
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String clientRequest;

                while ((clientRequest = in.readLine()) != null) {
                    System.out.println("Received request: " + clientRequest);

                    if (clientRequest.equalsIgnoreCase("REQUEST_SYSTEM_INFO")) {
                        // Send system info file to the client
                        handleSystemInfoRequest(out);
                    } else if (clientRequest.equalsIgnoreCase("GET_CLIENT_INFO")) {
                        // Send client info list to the client
                        sendClientInfo(out);
                    } else {
                        out.println("Unknown request: " + clientRequest);
                    }
                }
            } catch (IOException e) {
                System.err.println("Client connection error: " + e.getMessage());
            }
        }

        private void handleSystemInfoRequest(PrintWriter out) {
            lock.lock();
            try {
                // Run the system.sh script to generate system info
                runShellScript("system.sh");

                // Send the system info file to the client
                File systemInfoFile = new File("system_info.txt");
                if (systemInfoFile.exists()) {
                    out.println("START_FILE_TRANSFER");
                    try (BufferedReader fileReader = new BufferedReader(new FileReader(systemInfoFile))) {
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            out.println(line);
                        }
                    }
                    out.println("END_FILE_TRANSFER");
                } else {
                    out.println("System info file not found.");
                }

                // Add client info to the list
                String clientDetails = "Client: " + clientSocket.getInetAddress() + " at " + new Date();
                clientInfoList.add(clientDetails);
                System.out.println("Client info added: " + clientDetails);
            } catch (IOException e) {
                System.err.println("Error handling system info request: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        }

        private void sendClientInfo(PrintWriter out) {
            lock.lock();
            try {
                out.println("CLIENT_INFO_LIST_START");
                for (String info : clientInfoList) {
                    out.println(info);
                }
                out.println("CLIENT_INFO_LIST_END");
            } finally {
                lock.unlock();
            }
        }
    }
}
