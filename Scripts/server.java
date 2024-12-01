// import java.io.*;
// import java.net.*;
// import java.util.*;
// import java.util.concurrent.*;

// public class Server {
//     private static final int PORT = 1300;
//     private static final String SYSTEM_INFO_SCRIPT = "./system.sh";
//     private static final String NETWORK_SCRIPT = "./network.sh";
//     private static final List<String> clientRequests = Collections.synchronizedList(new ArrayList<>());

//     public static void main(String[] args) {
//         System.out.println("Server is running on port " + PORT);

//         try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//             while (true) {
//                 Socket clientSocket = serverSocket.accept();
//                 String clientAddress = clientSocket.getInetAddress().getHostAddress();
//                 System.out.println("Client connected: " + clientAddress);

//                 // Run the Network.sh script to test connection
//                 runShellScript(NETWORK_SCRIPT);

//                 // Handle the client request in a separate thread
//                 new Thread(new ClientHandler(clientSocket, clientAddress)).start();
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void runShellScript(String scriptPath) {
//         try {
//             Process process = Runtime.getRuntime().exec(scriptPath);
//             process.waitFor();
//             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//             String line;
//             while ((line = reader.readLine()) != null) {
//                 System.out.println(line);
//             }
//         } catch (Exception e) {
//             System.err.println("Error running shell script: " + scriptPath);
//             e.printStackTrace();
//         }
//     }

//     private static class ClientHandler implements Runnable {
//         private final Socket clientSocket;
//         private final String clientAddress;

//         public ClientHandler(Socket clientSocket, String clientAddress) {
//             this.clientSocket = clientSocket;
//             this.clientAddress = clientAddress;
//         }

//         @Override
//         public void run() {
//             try (
//                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
//             ) {
//                 String request = in.readLine();

//                 if ("GET_SYSTEM_INFO".equals(request)) {
//                     System.out.println("Processing system info request from " + clientAddress);

//                     synchronized (clientRequests) {
//                         clientRequests.add(clientAddress);
//                         System.out.println("All client requests: " + clientRequests);
//                     }

//                     File systemInfoFile = generateSystemInfo();
//                     sendFileToClient(systemInfoFile, out);
//                 }
//             } catch (IOException e) {
//                 System.err.println("Error handling client: " + clientAddress);
//                 e.printStackTrace();
//             }
//         }

//         private File generateSystemInfo() throws IOException {
//             File file = new File("system_info.txt");
//             Process process = Runtime.getRuntime().exec(SYSTEM_INFO_SCRIPT);
//             try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                  PrintWriter writer = new PrintWriter(new FileWriter(file))) {
//                 String line;
//                 while ((line = reader.readLine()) != null) {
//                     writer.println(line);
//                 }
//             } catch (Exception e) {
//                 System.err.println("Error generating system info");
//                 e.printStackTrace();
//             }
//             return file;
//         }

//         private void sendFileToClient(File file, PrintWriter out) throws IOException {
//             try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//                 String line;
//                 while ((line = reader.readLine()) != null) {
//                     out.println(line);
//                 }
//                 out.flush();
//             }
//         }
//     }
// }




import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 1300;
    private static final String SYSTEM_INFO_SCRIPT = "./system.sh";
    private static final String NETWORK_SCRIPT = "./network.sh";
    private static final String LOGIN_SCRIPT = "./login.sh"; // Path to the login script
    private static final List<String> clientRequests = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("Server is running on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                System.out.println("Client connected: " + clientAddress);

                // Run the Network.sh script to test connection
                runShellScript(NETWORK_SCRIPT);

                // Authenticate client before processing requests
                if (authenticateClient(clientSocket)) {
                    // Handle the client request in a separate thread
                    new Thread(new ClientHandler(clientSocket, clientAddress)).start();
                } else {
                    System.out.println("Authentication failed for client: " + clientAddress);
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runShellScript(String scriptPath) {
        try {
            Process process = Runtime.getRuntime().exec(scriptPath);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error running shell script: " + scriptPath);
            e.printStackTrace();
        }
    }

    private static boolean authenticateClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Prompt for username and password
            out.println("Enter username:");
            String username = in.readLine();

            out.println("Enter password:");
            String password = in.readLine();

            // Check if username and password match
            if ("client1".equals(username) && "1234".equals(password)) {
                out.println("Authentication successful!");
                return true;
            } else {
                out.println("Authentication failed. Invalid username or password.");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error during authentication");
            e.printStackTrace();
            return false;
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String clientAddress;

        public ClientHandler(Socket clientSocket, String clientAddress) {
            this.clientSocket = clientSocket;
            this.clientAddress = clientAddress;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String request = in.readLine();

                if ("GET_SYSTEM_INFO".equals(request)) {
                    System.out.println("Processing system info request from " + clientAddress);

                    synchronized (clientRequests) {
                        clientRequests.add(clientAddress);
                        System.out.println("All client requests: " + clientRequests);
                    }

                    File systemInfoFile = generateSystemInfo();
                    sendFileToClient(systemInfoFile, out);
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + clientAddress);
                e.printStackTrace();
            }
        }

        private File generateSystemInfo() throws IOException {
            File file = new File("system_info.txt");
            Process process = Runtime.getRuntime().exec(SYSTEM_INFO_SCRIPT);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            } catch (Exception e) {
                System.err.println("Error generating system info");
                e.printStackTrace();
            }
            return file;
        }

        private void sendFileToClient(File file, PrintWriter out) throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }
                out.flush();
            }
        }
    }
}
