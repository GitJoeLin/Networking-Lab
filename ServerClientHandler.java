package day5_bca;
/*
import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerClientHandler implements Runnable{
    // Maintain data about the client serviced by this thread
    ClientConnectionData client;
    final ArrayList<ClientConnectionData> clientList;
    ArrayList<String> userNames = new ArrayList<>();
    boolean cookie;
    final List<String> cookieWords = Arrays.asList("plough", "passenger", "basketball", "instrument", "competition", "knowledge", "quicksand", "amusements"
            , "observation", "crocodile", "hippopotamus", "librarian", "maintenance", "appointment", "establishment", "psychology"
            , "enthusiasm", "chinchilla", "dormouse", "basilisk", "orangutan", "wolverine", "platypus", "armadillo", "reindeer"
            , "hamburger", "macaroni", "spearmint", "oregano", "mushroom", "marshmallow", "strawberry", "potato", "spaghetti", "honeydew"
            , "cantaloupe", "grapefruit", "watermelon", "sandwich", "helicopter", "houseplant", "aquarium", "magnetism", "emergency");
    String currentWord;

    private int cookieCounter = 0;
    private int randNum;

    public ServerClientHandler(ClientConnectionData client, ArrayList<ClientConnectionData> clientList){
        this.client = client;
        this.clientList = clientList;
        cookie = false;
    }

    public void broadcast(Chat msg){
        try {
            System.out.println("Broadcasting -- " + msg);
            synchronized (clientList) {
                for (ClientConnectionData c : clientList) {
                    c.getOut().writeObject(msg);
                }
            }
        } catch(Exception ex){
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }
    }
    public void cookieGiver(){
        if(!cookie){
            currentWord = cookieWords.get((int)(Math.random()*(cookieWords.size())));
            broadcast(new Chat("Whoever types "+ currentWord + " first gets a cookie!"));
            cookie = true;
        }
    }
    public boolean validName(String name){
        return !name.contains(" ") && name.matches("[A-Za-z0-9]+");
    }

    @Override
    public void run(){
        // TODO Auto-generated method stub
        try {
            ObjectInputStream in = client.getInput();
            ObjectOutputStream out = client.getOut();
            //String userName = in.readLine().trim();
            //client.setUserName(userName);
            //notify all that client has joined
            while(true){
                Chat givename = new Chat(Chat.MSG_HEADER_SERVER, "SUBMITNAME");
                out.writeObject(givename);
                String userName = (String) in.readObject();
                if(userName == null){
                    return;
                }
                else {
                    synchronized (clientList){
                        if(userNames.size()!=0) {
                            if (!userNames.contains(userName) && validName(userName)) {
                                client.setUserName(userName);
                                clientList.add(client);
                                userNames.add(client.getUserName());
                                Welcome hello = new Welcome(client.getUserName());
                                broadcast(hello);
                                StringBuilder sb = new StringBuilder();
                                for(int i = 0; i < userNames.size(); i++){
                                    if(i == userNames.size() - 1){
                                        sb.append(userNames.get(i));
                                    }
                                    else{
                                        sb.append(userNames.get(i));
                                        sb.append(", ");
                                    }
                                }
                                String msg = String.format("NAMES %s", sb.toString());
                                Chat givenames = new Chat(msg);
                                broadcast(givenames);
                                break;
                            }
                        }
                        else{
                            if(validName(userName)) {
                                client.setUserName(userName);
                                clientList.add(client);
                                userNames.add(client.getUserName());
                                Welcome hello = new Welcome(client.getUserName());
                                broadcast(hello);
                                StringBuilder sb = new StringBuilder();
                                for(int i = 0; i < userNames.size(); i++){
                                    if(i == userNames.size() - 1){
                                        sb.append(userNames.get(i));
                                    }
                                    else{
                                        sb.append(userNames.get(i));
                                        sb.append(", ");
                                    }
                                }
                                String msg = String.format("NAMES %s", sb.toString());
                                Chat givenames = new Chat(msg);
                                broadcast(givenames);
                                break;
                            }
                        }
                    }
                }
            }


            cookieGiver();
            String incoming = "";

            while ((incoming = (String) in.readObject()) != null) {
                // handle messages
                //broadcast(String.format("CHAT %s %s", incoming, incoming));

                if(incoming.startsWith("@")){
                    if(incoming.contains(" ")) {
                        int namePrivate = incoming.indexOf(" ");
                        String name = incoming.substring(1, namePrivate);
                        String chat = incoming.substring(namePrivate+1).trim();
                        if (userNames.contains(name)) {
                            PrivateChat secret = new PrivateChat(name, client.getUserName(), chat);
//                            String msg = String.format("PCHAT%s %s (private): %s", name, client.getUserName(), chat);
                            broadcast(secret);
                            cookieCounter++;
                            randNum = (int) ((Math.random() * 5) + 5);
                            if(cookieCounter >= randNum){
                                cookieCounter -= randNum;
                                cookieGiver();
                            }
                        }
                    }
                    else{
                        cookieCounter++;
                    }
                }
                else if(incoming.startsWith("#")){
                    if(currentWord.equals(incoming.substring(1))){
                        CookieChat yum = new CookieChat("You get a cookie!", client.getUserName());
//                        String msg = String.format("COOKIE%s You get a cookie!", client.getUserName());
                        broadcast(yum);
                        int current = client.getCookies();
                        current++;
                        client.setCookies(current);
                        if(current == 1 ){
                            broadcast(new Chat(client.getUserName() + " has " + client.getCookies() + " cookie!"));
                        }
                        else {
                            broadcast(new Chat(client.getUserName() + " has " + client.getCookies() + " cookies!"));
                        }

                        cookie = false;
                        if(cookieCounter >= 10){
                            cookieCounter -= 10;
                            cookieGiver();
                        }
                    }
                }
                else if(incoming.startsWith("/whoishere")){
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < userNames.size(); i++){
                        if(i == userNames.size() - 1){
                            sb.append(userNames.get(i));
                        }
                        else{
                            sb.append(userNames.get(i));
                            sb.append(", ");
                        }
                    }
                    String msg = String.format("NAMES %s", sb.toString());
                    Chat givenames = new Chat(msg);
                    broadcast(givenames);
                }
                else if(incoming.startsWith("/leaderboards")){
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < clientList.size(); i++){
                        if(i == clientList.size() - 1){
                            sb.append(clientList.get(i).getUserName() + "-");
                            sb.append(clientList.get(i).getCookies());
                        }
                        else{
                            sb.append(clientList.get(i).getUserName() + "-");
                            sb.append(clientList.get(i).getCookies());
                            sb.append(", ");
                        }
                    }
                    String msg = String.format("LEADER %s", sb.toString());
                    Chat giveLeaders = new Chat(msg);
                    broadcast(giveLeaders);
                }
                else if(incoming.startsWith("EXIT")){
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < userNames.size(); i++){
                        if(i == userNames.size() - 1){
                            sb.append(userNames.get(i));
                        }
                        else{
                            sb.append(userNames.get(i));
                            sb.append(", ");
                        }
                    }
                    String msg = String.format("NAMES %s", sb.toString());
                    Chat givenames = new Chat(msg);
                    broadcast(givenames);
                    break;
                }
                else{
                    String text = incoming;
                    if(text.length() > 0) {
                        NormalChat msg = new NormalChat(client.getUserName(), text);
//                        String msg = String.format("CHAT %s %s", client.getUserName(), text);
                        // broadcast the message out
                        broadcast(msg);
                        cookieCounter++;
                        randNum = (int) ((Math.random() * 5) + 5);
                        if(cookieCounter >= randNum){
                            cookieCounter -= randNum;
                            cookieGiver();
                        }
                    }
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
            Chat leave = new Chat("EXIT " + client.getUserName());
            broadcast(leave);
            try{
                client.getSocket().close();
            } catch (IOException ex) {}
        }

    }
}
 */

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerClientHandler implements Runnable{
    // Maintain data about the client serviced by this thread
    ClientConnectionData client;
    final ArrayList<ClientConnectionData> clientList;
    final ArrayList<String> userNames;
    boolean cookie;
    final List<String> cookieWords = Arrays.asList("plough", "passenger", "basketball", "instrument", "competition", "knowledge", "quicksand", "amusements"
            , "observation", "crocodile", "hippopotamus", "librarian", "maintenance", "appointment", "establishment", "psychology"
            , "enthusiasm", "chinchilla", "dormouse", "basilisk", "orangutan", "wolverine", "platypus", "armadillo", "reindeer"
            , "hamburger", "macaroni", "spearmint", "oregano", "mushroom", "marshmallow", "strawberry", "potato", "spaghetti", "honeydew"
            , "cantaloupe", "grapefruit", "watermelon", "sandwich", "helicopter", "houseplant", "aquarium", "magnetism", "emergency");
    String currentWord;

    private int cookieCounter = 0;
    private int randNum;
    private Socket socket;

    public ServerClientHandler(ClientConnectionData client, ArrayList<ClientConnectionData> clientList, ArrayList<String> userNames, Socket socket){
        this.client = client;
        this.clientList = clientList;
        this.userNames = userNames;
        cookie = false;
        this.socket = socket;
    }

    public void broadcast(Message msg){
        try {
            System.out.println("Broadcasting -- " + msg.getHeader() + " " + msg.getMessage());
            synchronized (clientList) {
                for (ClientConnectionData c : clientList) {
                    c.getOut().writeObject(msg);
                }
            }
            cookieCounter++;
            if(cookieCounter >= 10){
                cookie = false;
                cookieCounter -= 10;
                cookieGiver();
            }
        } catch(Exception ex){
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }
    }
    public void cookieGiver(){
        if(!cookie){
            currentWord = cookieWords.get((int)(Math.random()*(cookieWords.size())));
            broadcast(new Message("SYSTEM", "Whoever types "+ currentWord + " first gets a cookie!"));
            cookie = true;
        }
    }
    public boolean validName(String name){
        return !name.contains(" ") && name.matches("[A-Za-z0-9]+") && !userNames.contains(name);
    }

    @Override
    public void run(){
        // TODO Auto-generated method stub
        try {
            ObjectOutputStream out = client.getOut();
            ObjectInputStream in = client.getInput();
            //String userName = in.readLine().trim();
            //client.setUserName(userName);
            //notify all that client has joined
            while(true){
                out.writeObject(new Message("SUBMITNAME", ""));
                out.flush();
                String userName = (String) in.readObject();
                if(userName == null){
                    return;
                }
                else {
                    synchronized (clientList){
                        synchronized (userNames) {
                            if (userNames.size() != 0) {
                                if (validName(userName)) {
                                    client.setUserName(userName);
                                    clientList.add(client);
                                    userNames.add(client.getUserName());
                                    broadcast(new Message("WELCOME", client.getUserName()));
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < userNames.size(); i++) {
                                        if (i == userNames.size() - 1) {
                                            sb.append(userNames.get(i));
                                        } else {
                                            sb.append(userNames.get(i));
                                            sb.append(", ");
                                        }
                                    }
                                    broadcast(new Message("NAMES", sb.toString()));
                                    break;
                                }
                            } else {
                                if (validName(userName)) {
                                    client.setUserName(userName);
                                    clientList.add(client);
                                    userNames.add(client.getUserName());
                                    broadcast(new Message("WELCOME", client.getUserName()));
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < userNames.size(); i++) {
                                        if (i == userNames.size() - 1) {
                                            sb.append(userNames.get(i));
                                        } else {
                                            sb.append(userNames.get(i));
                                            sb.append(", ");
                                        }
                                    }
                                    broadcast(new Message("NAMES", sb.toString()));
                                    break;
                                }
                            }
                        }
                    }
                }
            }


            cookieGiver();
            String incoming = "";

            while ((incoming = (String) in.readObject()) != null) {
                synchronized (clientList) {
                    // handle messages
                    //broadcast(String.format("CHAT %s %s", incoming, incoming));

                    if (incoming.startsWith("@")) {
                        synchronized (userNames) {
                            String getmessage = incoming.substring(incoming.lastIndexOf("@"));
                            getmessage = getmessage.substring(getmessage.indexOf(" ") + 1);
                            while (incoming.startsWith("@")) {
                                if (incoming.contains(" ")) {
                                    int namePrivate = incoming.indexOf(" ");
                                    String name = incoming.substring(1, namePrivate);
//                            String chat = incoming.substring(namePrivate + 1).trim();
                                    if (userNames.contains(name)) {
                                        broadcast(new PChat("PCHAT", client.getUserName(), name, getmessage));
                                    }
                                    incoming = incoming.substring(namePrivate + 1);
                                } else {
                                    break;
                                }
                            }
                        }

                    } else if (incoming.startsWith("#")) {
                        if (currentWord.equals(incoming.substring(1))) {
                            broadcast(new Chat("COOKIE", client.getUserName(), " You get a cookie!"));
                            int current = client.getCookies();
                            current++;
                            client.setCookies(current);
                            if (current == 1) {
                                broadcast(new Message("SYSTEM", client.getUserName() + " has " + client.getCookies() + " cookie!"));
                            } else {
                                broadcast(new Message("SYSTEM", client.getUserName() + " has " + client.getCookies() + " cookies!"));
                            }
                        }
                    } else if (incoming.startsWith("/whoishere")) {
                        synchronized (userNames) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < userNames.size(); i++) {
                                if (i == userNames.size() - 1) {
                                    sb.append(userNames.get(i));
                                } else {
                                    sb.append(userNames.get(i));
                                    sb.append(", ");
                                }
                            }
                            broadcast(new Message("NAMES", sb.toString()));
                        }
                    } else if (incoming.startsWith("/leaderboards")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < clientList.size(); i++) {
                            if (i == clientList.size() - 1) {
                                sb.append(clientList.get(i).getUserName() + "-");
                                sb.append(clientList.get(i).getCookies());
                            } else {
                                sb.append(clientList.get(i).getUserName() + "-");
                                sb.append(clientList.get(i).getCookies());
                                sb.append(", ");
                            }
                        }
                        broadcast(new Message("LEADER", sb.toString()));
                    } else if (incoming.startsWith("EXIT")) {
                        synchronized(userNames) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < userNames.size(); i++) {
                                if (i == userNames.size() - 1) {
                                    sb.append(userNames.get(i));
                                } else {
                                    sb.append(userNames.get(i));
                                    sb.append(", ");
                                }
                            }
                            broadcast(new Message("NAMES", sb.toString()));
                            userNames.remove(client.getUserName());
                            break;
                        }
                    } else {
                        String text = incoming;
                        if (text.length() > 0) {
                            broadcast(new Chat("CHAT", client.getUserName(), text));
                            // broadcast the message out
                        }
                    }
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
            broadcast(new Message("EXIT", client.getUserName()));
            try{
                client.getSocket().close();
            } catch (IOException ex) {}
        }

    }
}