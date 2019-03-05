import java.io.Serializable;
public class Message implements Serializable
{
    private Movie m;
    private double rating;
    private int userId;
    private MessageType type;
    public Message(Movie m, double rating, int userId, MessageType type){
        this.type=type;
        this.m=m;
        this.rating=rating;
        this.userId=userId;
    }
    public int getUserId(){
        return userId;
    }
    public int getMovieID(){
        return this.m.getID();
    }
    public double getRating(){
        return this.rating;
    }
    public MessageType getType(){
        return this.type;
    }
    public String getName(){
        return this.m.getName();
    }
}