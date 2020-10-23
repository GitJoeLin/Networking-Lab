package day5_bca;

/*
import java.io.BufferedReader;
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
            Chat incoming;
            String userName = "";

            while( (incoming = (Chat) socketIn.readObject()) != null) {
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                if(incoming.getMsgHeader()== Chat.MSG_HEADER_SERVER && incoming.getMessage().equals("SUBMITNAME")){
                    System.out.println("Enter your username: ");
                }
                else if(incoming.getMsgHeader()==Chat.MSG_HEADER_WELCOME){
                    System.out.println(incoming.getMessage() + " has joined.");
                    userName = incoming.getMessage();
                }
                else if(incoming.getMsgHeader()==Chat.MSG_HEADER_NORMAL){
                    System.out.println(incoming.getMessage());
//                    int posname = incoming.indexOf(":");
//                    if(!(incoming.substring(5, posname).equals(userName))){
//                        System.out.println(incoming.substring(5));
//                    }
                }
                else if(incoming.getMsgHeader()==Chat.MSG_HEADER_PRIVATE){
                    PrivateChat secret = (PrivateChat) incoming;
                    if(secret.getRecipient().equals(userName)){
                        System.out.println(secret.getMessage());
                    }
//                    int posname = incoming.indexOf(":");
//                    if(!(incoming.substring(6, posname).equals(userName))){
//                        System.out.println(incoming.substring(6+userName.length()));
//                    }
                }
                else if(incoming.getMsgHeader()==Chat.MSG_HEADER_COOKIE){
                    CookieChat cookietype = (CookieChat) incoming;
                    if(cookietype.getWinner().equals(userName)){
                        cookie++;
                        if(cookie<=1){
                            System.out.println("You have " + cookie + " cookie!");
                        }
                        else{
                            System.out.println("You have " + cookie + " cookies!");
                        }

                    }
//                    int posname = incoming.indexOf(" ");
//                    if(!(incoming.substring(7, posname).equals(userName))){
//                        System.out.println(incoming.substring(7+userName.length()));
//                        cookie++;
//                        if(cookie<=1) {
//                            System.out.println("You have " + cookie + " cookie!");
//                        }
//                        else{
//                            System.out.println("You have " + cookie + " cookies!");
//                        }
//                    }
                }
                else if(incoming.getMsgHeader()==Chat.MSG_HEADER_SERVER && incoming.getMessage().getHeader().equals("EXIT")){
                    int posname = incoming.getMessage().indexOf(" ");
                    System.out.println(incoming.getMessage().substring(posname+1) + " has left.");
                }
                else{
                    System.out.println(incoming);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
        } finally{
            System.out.println("Client Listener exiting");
        }
    }
}

 */
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