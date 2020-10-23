package day5_bca;

import java.io.*;
import java.net.Socket;
/*
public class ClientConnectionData {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream out;
    private String name;
    private String userName;
    private int cookies;

    public ClientConnectionData(Socket socket, ObjectInputStream input, ObjectOutputStream out, String name) {
        this.socket = socket;
        this.input = input;
        this.out = out;
        this.name = name;
        this.userName = userName;
        this.cookies = 0;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public int getCookies() {
        return cookies;
    }

    public void setCookies(int cookies) {
        this.cookies = cookies;
    }

}


 */
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnectionData {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream out;
    private String name;
    private String userName;
    private int cookies;

    public ClientConnectionData(Socket socket, ObjectInputStream input, ObjectOutputStream out, String name) {
        this.socket = socket;
        this.input = input;
        this.out = out;
        this.name = name;
        this.userName = userName;
        this.cookies = 0;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public int getCookies() {
        return cookies;
    }

    public void setCookies(int cookies) {
        this.cookies = cookies;
    }

}