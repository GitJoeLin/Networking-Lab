package day5_bca;

import java.io.Serializable;

public class Chat extends Message implements Serializable {
    public static final long serialVersionUID = 1L;

    private String userName;

    public Chat(String header, String userName, String msg){
        super(header, msg);
        this.userName = userName;
    }
    public String getUserName(){
        return userName;
    }
}
