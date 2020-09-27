package day5_bca;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerClientHandler implements Runnable{
    // Maintain data about the client serviced by this thread
    ClientConnectionData client;
    final ArrayList<ClientConnectionData> clientList;

    public ServerClientHandler(ClientConnectionData client, ArrayList<ClientConnectionData> clientList){
        this.client = client;
        this.clientList = clientList;
    }

    public void broadcast(String msg){
        try {
            System.out.println("Broadcasting -- " + msg);
            synchronized (clientList) {
                for (ClientConnectionData c : clientList) {
                    c.getOut().println(msg);
                }
            }
        } catch(Exception ex){
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }
    }
    @Override
    public void run(){
        // TODO Auto-generated method stub
        try {
            BufferedReader in = client.getInput();
            String userName = in.readLine().trim();
            client.setUserName(userName);
            //notify all that client has joined
            broadcast(String.format("WELCOME %s", client.getUserName()));

            String incoming = "";

            while ( (incoming = in.readLine()) != null) {
                // handle messages
                if(incoming.startsWith("CHAT")){
                    String chat = incoming.substring(4).trim();
                    if(chat.length() > 0) {
                        String msg = String.format("CHAT %s %s", client.getName(), chat);
                        // broadcast the message out
                        broadcast(msg);
                    }
                } else if(incoming.startsWith("QUIT")){
                    break;
                }
            }
        } catch (Exception ex){
            if (ex instanceof SocketException){
                System.out.println("Caught socket ex for " + client.getName());
            } else{
                System.out.println(ex);
                ex.printStackTrace();
            }
        } finally{
            //Remove client from clientList, notify all
            synchronized (clientList){
                clientList.remove(client);
            }
            System.out.println(client.getUserName() + " has left.");
            broadcast(String.format("EXIT %s", client.getUserName()));
            try{
                client.getSocket().close();
            } catch (IOException ex) {}
        }

    }
}
