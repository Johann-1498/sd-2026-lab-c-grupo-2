
import java.net.*;
import java.io.*;
import java.util.*;

// Cliente de chat en consola
public class Client {

    private String notif = " *** ";

    // Streams y socket
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private String server, username;
    private int port;

    Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    // Inicia la conexión con el servidor
    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch(Exception e) {
            display("Error connecting to server: " + e);
            return false;
        }

        display("Connected to " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            // IMPORTANTE: primero output, luego input
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            display("Error creating streams: " + e);
            return false;
        }

        new ListenFromServer().start();

        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            display("Login error: " + e);
            disconnect();
            return false;
        }

        return true;
    }

    // Muestra mensajes en consola
    private void display(String msg) {
        System.out.println(msg);
    }

    // Envía mensajes al servidor
    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch(IOException e) {
            display("Error sending message: " + e);
        }
    }

    // Cierra conexión
    private void disconnect() {
        try { if(sInput != null) sInput.close(); } catch(Exception e) {}
        try { if(sOutput != null) sOutput.close(); } catch(Exception e) {}
        try { if(socket != null) socket.close(); } catch(Exception e) {}
    }

    public static void main(String[] args) {

        int port = 1500;
        String server = "localhost";
        String username = "Anonymous";

        Scanner scan = new Scanner(System.in);
        System.out.print("Enter username: ");
        username = scan.nextLine();

        Client client = new Client(server, port, username);

        if(!client.start()) return;

        System.out.println("Connected. Commands: MESSAGE | WHOISIN | LOGOUT");

        while(true) {
            System.out.print("> ");
            String input = scan.nextLine();

            if(input.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
                break;
            }
            else if(input.equalsIgnoreCase("WHOISIN")) {
                client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
            }
            else {
                client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, input));
            }
        }

        scan.close();
        client.disconnect();
    }

    // Hilo que escucha mensajes del servidor
    class ListenFromServer extends Thread {
        public void run() {
            while(true) {
                try {
                    String msg = (String) sInput.readObject();
                    System.out.println(msg);
                    System.out.print("> ");
                }
                catch(IOException e) {
                    display(notif + "Server disconnected: " + e + notif);
                    break;  
                }
                catch(ClassNotFoundException e) {}
            }
        }
    }
}