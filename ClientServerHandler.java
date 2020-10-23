package day5_bca;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.Buffer;

public class ClientServerHandler implements Runnable {
    ObjectInputStream socketIn;
    int cookie = 0;

    public ClientServerHandler(ObjectInputStream socketIn) {
        this.socketIn = socketIn;
    }

    @Override
    public void run() {
        try {
            Message incoming;
            String userName = "";

            while( (incoming = (Message) socketIn.readObject()) != null) {
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                if(incoming.getHeader().equals("SUBMITNAME")){
                    System.out.println("Enter your username: ");
                }
                else if(incoming.getHeader().equals("WELCOME")){
                    if(userName.isEmpty()) {
                        userName = incoming.getMessage();
                    }
                    System.out.println(userName + " has joined.");
                }
                else if(incoming.getHeader().equals("CHAT")){
                    Chat came = (Chat)incoming;
                    String sender = came.getUserName();
                    String msg = incoming.getMessage();
                    if(!sender.equals(userName)){
                        System.out.println(sender + ": " + msg);
                    }
                }
                else if(incoming.getHeader().equals("PCHAT")){
                    PChat came = (PChat)incoming;
                    String sender = came.getSender();
                    String recipient = came.getRecipient();
                    String msg = came.getMessage();
                    if(recipient.equals(userName)) {
                        if (!(sender.equals(userName))) {
                            System.out.println(sender + "(private): " + msg);
                        }
                    }
                }
                else if(incoming.getHeader().equals("NAMES")){
                    String msg = incoming.getMessage();
                    System.out.println("All Connected Users: " + msg + "\n");
                }
                else if(incoming.getHeader().equals("LEADER")){
                    String msg = incoming.getMessage();
                    System.out.println("Leaderboards: \n" + msg + "\n");
                }
                else if(incoming.getHeader().equals("COOKIE")){
                    Chat came = (Chat)incoming;
                    String getting = came.getUserName();
                    String msg = came.getMessage();
                    if(getting.equals(userName)){
                        System.out.println(msg);
                        cookie++;
                        if(cookie<=1) {
                            System.out.println("You have " + cookie + " cookie!");
                        }
                        else{
                            System.out.println("You have " + cookie + " cookies!");
                        }
                    }
                }
                else if(incoming.getHeader().equals("EXIT")){
                    String msg = incoming.getMessage();
                    System.out.println(msg + " has left.");
                }
                else{
                    String msg = incoming.getMessage();
                    System.out.println(msg);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
        } finally{
            System.out.println("Client Listener exiting");
        }
    }
}