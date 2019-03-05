import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;
public class Movie implements Serializable
{
    public HashMap<Integer,Double> ratings;
    public String name;
    public int year;
    public ArrayList<String> genreList;
    public double averageRating;
    public int id;
    public Movie(int ID,String name, int year, String genres){
        this.name=name;
        this.year=year;
        this.id=ID;
        String[] genreArray=genres.split("|");
        genreList=new ArrayList<String>();
        for(String g:genreArray){
            genreList.add(g);
        }
        ratings=new HashMap<Integer, Double>();
        averageRating=0.0;
    }
    public Result deleteReview(int userId){
        if(ratings.containsKey(userId)){
            ratings.remove(userId);
            return Result.SUCCESFUL;
        }
        return Result.FAILED;
    }
    public String update(int userId, double newRating){
        String response=ratings.get(userId)+" to "+newRating;
        ratings.put(userId,newRating);
        return response;
    }
    public String getAllReviews(){
        String str="";
        for(Integer i:ratings.keySet()){
            str+=("User "+i+" gave this movie a rating of "+ratings.get(i)+"\n");
        }
        return str;
    }
    public int getID(){
        return this.id;
    }
    public void addRating(int userNumber, Double rating){
        int size=ratings.size();
        averageRating=((averageRating*size)+rating)/(size+1);
        ratings.put(userNumber,rating);
    }
    public double getAverage(){
        return averageRating;
    }
    public String getName(){
        return name;
    }
}
