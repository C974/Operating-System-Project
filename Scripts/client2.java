// import java.io.*;
// import java.net.*;
// import java.util.concurrent.TimeUnit;

// public class Client2 {
//     private static final String SERVER_IP = "192.168.15.132"; // Server IP
//     private static final int SERVER_PORT = 1300; // Server port
//     private static final String SYSTEM_INFO_FILE = "system_info_client2.txt";

//     public static void main(String[] args) {
//         while (true) {
//             try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
//                  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//                 System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);

//                 // Request system information
//                 out.println("GET_SYSTEM_INFO");
//                 System.out.println("Request sent: GET_SYSTEM_INFO");

//                 // Read and save the system information file
//                 try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(SYSTEM_INFO_FILE))) {
//                     String line;
//                     System.out.println("Receiving system information from the server...");
//                     while ((line = in.readLine()) != null) {
//                         fileWriter.write(line);
//                         fileWriter.newLine();
//                         System.out.println(line); // Display content on terminal
//                     }
//                 }

//                 System.out.println("System information saved to: " + SYSTEM_INFO_FILE);

//             } catch (IOException e) {
//                 System.err.println("Error connecting to the server: " + e.getMessage());
//                 e.printStackTrace();
//             }

//             // Wait for 5 minutes before the next request
//             try {
//                 System.out.println("Waiting for 5 minutes before the next request...");
//                 TimeUnit.MINUTES.sleep(5);
//             } catch (InterruptedException e) {
//                 System.err.println("Sleep interrupted: " + e.getMessage());
//             }
//         }
//     }
// }



import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class Client2 {
    private static final String SERVER_IP = "192.168.15.132"; // Server IP
    private static final int SERVER_PORT = 1300; // Server port
    private static final String SYSTEM_INFO_FILE = "system_info_client2.txt";

    public static void main(String[] args) {
        try {
            // Run search.sh and clientinfo.sh initially
            System.out.println("Running search.sh...");
            runShellScript("./search.sh");

            System.out.println("Running clientinfo.sh...");
            runShellScript("./clientinfo.sh");

            // Loop to request system information every 5 minutes
            while (true) {
                // Connect to the server and request system information
                connectAndRequestSystemInfo();

                // Wait 5 minutes before the next request
                System.out.println("Waiting for 5 minutes before the next request...");
                TimeUnit.MINUTES.sleep(5);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void connectAndRequestSystemInfo() throws IOException {
        // Connect to the server
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
        }
    }

    private static void runShellScript(String scriptPath) {
        try {
            Process process = Runtime.getRuntime().exec(scriptPath);
            process.waitFor();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // Display output of the script
                }
            }

            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println(errorLine); // Display errors, if any
                }
            }
        } catch (Exception e) {
            System.err.println("Error running shell script: " + scriptPath);
            e.printStackTrace();
        }
    }
}
