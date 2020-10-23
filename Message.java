package day5_bca;

import java.io.Serializable;

public class Message implements Serializable {
    public static final long serialVersionUID = 1L;

    private final String header;
    private final String message;

    public Message(String header, String message){
        this.header = header;
        this.message = message;
    }
    public String getHeader(){
        return header;
    }
    public String getMessage(){
        return message;
    }
}
