package NetworkingLab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
    public void cookieGiver(){
        if(!cookie){
            currentWord = cookieWords.get((int)(Math.random()*(cookieWords.size())));
            broadcast("Whoever types "+ currentWord + " first gets a cookie!");
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
            BufferedReader in = client.getInput();
            PrintWriter out = client.getOut();
            //String userName = in.readLine().trim();
            //client.setUserName(userName);
            //notify all that client has joined
            while(true){
                out.println("SUBMITNAME");
                String userName = in.readLine();
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
                                broadcast(String.format("WELCOME %s", client.getUserName()));
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
                                broadcast(msg);
                                break;
                            }
                        }
                        else{
                            if(validName(userName)) {
                                client.setUserName(userName);
                                clientList.add(client);
                                userNames.add(client.getUserName());
                                broadcast(String.format("WELCOME %s", client.getUserName()));
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
                                broadcast(msg);
                                break;
                            }
                        }
                    }
                }
            }


            cookieGiver();
            String incoming = "";

            while ((incoming = in.readLine()) != null) {
                // handle messages
                //broadcast(String.format("CHAT %s %s", incoming, incoming));

                if(incoming.startsWith("@")){
                    if(incoming.contains(" ")) {
                        int namePrivate = incoming.indexOf(" ");
                        String name = incoming.substring(1, namePrivate);
                        String chat = incoming.substring(namePrivate+1).trim();
                        if (userNames.contains(name)) {
                            String msg = String.format("PCHAT%s %s (private): %s", name, client.getUserName(), chat);
                            broadcast(msg);
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
                    if(currentWord.equals(incoming.substring(2))){
                        String msg = String.format("COOKIE%s You get a cookie!", client.getUserName());
                        broadcast(msg);
                        int current = client.getCookies();
                        current++;
                        client.setCookies(current);
                        if(current <=1 ){
                            broadcast(client.getUserName() + " has " + client.getCookies() + " cookie!");
                        }
                        broadcast(client.getUserName() + " has " + client.getCookies() + " cookies!");

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
                    broadcast(msg);
                }
                else if(incoming.startsWith("EXIT")){
                    break;
                }
                else{
                    String text = incoming;
                    if(text.length() > 0) {
                        String msg = String.format("CHAT %s %s", client.getUserName(), text);
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
            broadcast(String.format("EXIT %s", client.getUserName()));
            try{
                client.getSocket().close();
            } catch (IOException ex) {}
        }

    }
}