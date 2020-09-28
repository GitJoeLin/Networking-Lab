package NetworkingLab;

import java.io.BufferedReader;
import java.net.Socket;
import java.nio.Buffer;

public class ClientServerHandler implements Runnable {
    BufferedReader socketIn;

    public ClientServerHandler(BufferedReader socketIn) {
        this.socketIn = socketIn;
    }

    @Override
    public void run() {
        try {
            String incoming = "";
            String userName = "";

            while( (incoming = socketIn.readLine()) != null) {
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                if(incoming.startsWith("SUBMITNAME")){
                    System.out.println("Enter your username: ");
                }
                else if(incoming.startsWith("WELCOME")){
                    System.out.println(incoming.substring(8) + " has joined.");
                    userName = incoming.substring(8);
                }
                else if(incoming.startsWith("CHAT")){
                    int posname = incoming.indexOf(":");
                    if(!(incoming.substring(5, posname).equals(userName))){
                        System.out.println(incoming.substring(5));
                    }
                }
                else if(incoming.startsWith("PCHAT"+userName)){
                    int posname = incoming.indexOf(":");
                    if(!(incoming.substring(5, posname).equals(userName))){
                        System.out.println(incoming.substring(6+userName.length()));
                    }
                }
                else if(incoming.startsWith("EXIT")){
                    int posname = incoming.indexOf(" ");
                    System.out.println(incoming.substring(posname+1) + " has left.");
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
        } finally{
            System.out.println("Client Listener exiting");
        }
    }
}