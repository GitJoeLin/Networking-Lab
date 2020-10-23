package day5_bca;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static ObjectInputStream socketIn;
    private static ObjectOutputStream out;

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);

        System.out.println("What's the server IP? ");
        String serverip = userInput.nextLine();
        System.out.println("What's the server port? ");
        int port = userInput.nextInt();
        userInput.nextLine();
        try (Socket socket = new Socket(serverip, port)) {
            socketIn = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            // start a thread to listen for server messages
            ClientServerHandler listener = new ClientServerHandler(socketIn);
            Thread t = new Thread(listener);
            t.start();

            System.out.print("Chat sessions has started - ");
            String name = userInput.nextLine().trim();
            out.writeObject(name);
            out.flush();

            String line = userInput.nextLine().trim();
            while (!line.toLowerCase().startsWith("/quit")) {
                out.writeObject(line);
                out.flush();
                line = userInput.nextLine().trim();
            }
            out.writeObject("EXIT");
            out.flush();
            out.close();
            userInput.close();
            socketIn.close();
            socket.close();

        }
    }
}