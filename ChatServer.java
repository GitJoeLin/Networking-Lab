package day5_bca;

import day7_serialize.RandomizedServer;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.Buffer;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
public class ChatServer {
    public static final int PORT = 54321;
    private static final ArrayList<ClientConnectionData> clientList = new ArrayList<>();


    public static void main(String[] args) throws Exception{
        ExecutorService pool =   Executors.newFixedThreadPool(100);
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Chat Server started.");
            System.out.println("Local IP: " + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Local Port: " + serverSocket.getLocalPort());

            while(true){
                try {
                    Socket socket = serverSocket.accept();
                    System.out.printf("Connected to %s: %d on local port %d\n",
                            socket.getInetAddress(), socket.getPort(), socket.getLocalPort());

                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    String name = socket.getInetAddress().getHostName();
                    ClientConnectionData client = new ClientConnectionData(socket, in, out, name);

                    //handle client business in another thread
                    pool.execute(new ServerClientHandler(client, clientList));

                } catch(IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}
 */

public class ChatServer {
    public static final int PORT = 54321;
    private static final ArrayList<ClientConnectionData> clientList = new ArrayList<>();
    private static final ArrayList<String> userNames = new ArrayList<>();



    public static void main(String[] args) throws Exception{
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Chat Server started.");
            System.out.println("Local IP: " + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Local Port: " + serverSocket.getLocalPort());

            ExecutorService pool = Executors.newFixedThreadPool(100);
            while(true){
                try {
                    Socket socket = serverSocket.accept();
                    System.out.printf("Connected to %s: %d on local port %d\n",
                            socket.getInetAddress(), socket.getPort(), socket.getLocalPort());

//                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    String name = socket.getInetAddress().getHostName();
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    ClientConnectionData client = new ClientConnectionData(socket, in, out, name);

                    //handle client business in another thread
                    pool.execute(new ServerClientHandler(client, clientList, userNames, socket));

                } catch(IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}