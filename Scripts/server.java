import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private static final int PORT = 1300;
    private static final ArrayList<String> clientInfo = new ArrayList<>();
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        System.out.println("Server is running on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                // Run Network.sh script
                Runtime.getRuntime().exec("./Network.sh");

                String request;
                while ((request = in.readLine()) != null) {
                    if (request.equals("REQUEST_SYSTEM_INFO")) {
                        lock.lock();
                        try {
                            System.out.println("Processing request for system info.");
                            Runtime.getRuntime().exec("./system.sh");
                            File systemInfoFile = new File("system_info.txt");
                            BufferedReader fileReader = new BufferedReader(new FileReader(systemInfoFile));
                            String line;
                            while ((line = fileReader.readLine()) != null) {
                                out.println(line);
                            }
                            fileReader.close();
                            out.println("END_OF_FILE");
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        System.out.println("Received invalid request from client.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
