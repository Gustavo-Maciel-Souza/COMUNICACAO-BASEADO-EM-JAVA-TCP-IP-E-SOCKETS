import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static String userName;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Monitoramento Ambiental - Chat");
        JTextArea messageArea = new JTextArea(20, 50);
        JTextField inputField = new JTextField(40);
        JButton sendButton = new JButton("Enviar");
        JButton pollutionButton = new JButton("Relatar Poluição");
        JButton statusButton = new JButton("Status Ambiental");
        JButton alertButton = new JButton("🚨 Alerta");

        messageArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);
        panel.add(pollutionButton);
        panel.add(statusButton);
        panel.add(alertButton);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            userName = JOptionPane.showInputDialog("Digite seu nome e cargo (ex: João - Fiscal Ambiental):");
            out.println(userName);

            sendButton.addActionListener(e -> sendMessage(out, inputField.getText()));
            pollutionButton.addActionListener(e -> sendMessage(out, "🚨 Relatório de Poluição: " + inputField.getText()));
            statusButton.addActionListener(e -> sendMessage(out, "🌍 Status Ambiental: " + inputField.getText()));
            alertButton.addActionListener(e -> sendMessage(out, "⚠️ ALERTA: " + inputField.getText()));

            inputField.addActionListener(e -> sendMessage(out, inputField.getText()));

            String message;
            while ((message = in.readLine()) != null) {
                messageArea.append(message + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(PrintWriter out, String message) {
        if (!message.trim().isEmpty()) {
            out.println(message);
        }
    }
}
