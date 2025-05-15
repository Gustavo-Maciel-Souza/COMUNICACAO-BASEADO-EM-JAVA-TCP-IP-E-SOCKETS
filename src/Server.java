import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static final String LOG_FILE = "inspection_log.txt";

    public static void main(String[] args) {
        System.out.println("Servidor iniciado...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                out.println("Digite seu nome e cargo (ex: João - Fiscal Ambiental):");
                clientName = in.readLine();
                broadcastMessage("🔔 " + clientName + " entrou no chat de monitoramento ambiental.");

                String message;
                while ((message = in.readLine()) != null) {
                    String formattedMessage = "[" + clientName + "] " + message;
                    broadcastMessage(formattedMessage);
                    logMessage(formattedMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        private void broadcastMessage(String message) {
            System.out.println("Recebido: " + message);
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }

        private void logMessage(String message) {
            try (PrintWriter logWriter = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                logWriter.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
