package day5_bca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

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
    Timer timer = new Timer();

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
                else if (userName.startsWith("* ")){
                    userName = userName.substring(2);
                    synchronized (clientList){
                        if(userNames.size()!=0) {
                            if (!userNames.contains(userName) && validName(userName)) {
                                client.setUserName(userName);
                                clientList.add(client);
                                userNames.add(client.getUserName());
                                broadcast(String.format("WELCOME %s", client.getUserName()));
                                break;
                            }
                        }
                        else{
                            if(validName(userName)) {
                                client.setUserName(userName);
                                clientList.add(client);
                                userNames.add(client.getUserName());
                                broadcast(String.format("WELCOME %s", client.getUserName()));
                                break;
                            }
                        }
                    }
                }
            }



            String incoming = "";
            cookieGiver();
            while ( (incoming = in.readLine()) != null) {
                // handle messages
                if(incoming.startsWith("* ")){
                    String chat = incoming.substring(2).trim();
                    if(chat.length() > 0) {
                        String msg = String.format("CHAT %s: %s", client.getUserName(), chat);
                        // broadcast the message out
                        broadcast(msg);
                    }
                }
                else if(incoming.startsWith("@")){
                    if(incoming.contains("* ")) {
                        int namePrivate = incoming.indexOf("*");
                        String name = incoming.substring(1, namePrivate-1);
                        String chat = incoming.substring(namePrivate+2).trim();
                        if (userNames.contains(name)) {
                            String msg = String.format("PCHAT%s %s (private): %s", name, client.getUserName(), chat);
                            broadcast(msg);
                        }
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
                        cookieGiver();
                    }
                }
                else if(incoming.startsWith("QUIT")){
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
