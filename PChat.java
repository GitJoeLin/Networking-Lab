package day5_bca;

import java.io.Serializable;

public class PChat extends Message implements Serializable {
    public static final long serialVersionUID = 1L;

    private final String sender;
    private final String recipient;

    public PChat(String header, String sender, String recipient, String msg){
        super(header, msg);
        this.sender = sender;
        this.recipient = recipient;
    }
    public String getSender(){
        return sender;
    }
    public String getRecipient(){
        return recipient;
    }
}
