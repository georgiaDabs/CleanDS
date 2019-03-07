import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.rmi.AlreadyBoundException;
public class Server implements ServerInterface
{
    private  String name;
    private State state;
    private HashMap<Integer, Message> queue;
    private HashMap<Integer,Movie> movies;
    private int correctUpto;
    private double overLoadProb;
    private double offlineProb;
    private int currentCount;
    private Set<Integer> appliedMessages;
    //make server on reg when main is called
    public static void main(String[] args){
        try{
            Server obj=new Server("MovieRating1",0.01,0.05);
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);
            
            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk",37008);
            try{
                registry.bind("MovieRating1",stub);
            }catch(AlreadyBoundException a){
                registry.unbind("MovieRating1");
                registry.bind("MovieRating1",stub);
            }
            System.err.println("Server ready");
        }catch(Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
    //constructor for server
    public Server(String name, double overLoadProb, double offlineProb){
        correctUpto=0;
        this.overLoadProb=overLoadProb;
        this.offlineProb=offlineProb;
        this.name=name;
        this.state=State.ACTIVE;
        movies=new HashMap<Integer,Movie>();
        initiateMovies();
        queue=new HashMap<Integer, Message>();
        initiateRatings();
        appliedMessages=new HashSet<Integer>();
        System.out.println("Number of movies on this server:"+movies.size());
    }

    public boolean ping(){
        if(this.state==State.ACTIVE){
            System.out.println("PING");
            return true;
        }else{
            return false;
        }
    }
    //getters
    public int getCurrentCount(){
        
        return queue.size();
    }

    public String getName(){
        return this.name;
    }

    public State getState(){
        return state;
    }

    public void changeState(){
        if(Math.random()<offlineProb){
            this.state=State.OFFLINE;
            System.out.println("Server offline");
        }else if(Math.random()<overLoadProb){
            this.state=State.OVERLOADED;
            System.out.println("Server overloaded");
        }else{
            this.state=State.ACTIVE;
            System.out.println("Server active");
        }

    }
    //initiate movie list from server
    public void initiateMovies(){
        try{
            Scanner sc=new Scanner(new File("movies.csv"));
            sc.nextLine();
            String currentLine;
            while(sc.hasNext()){
                currentLine=sc.nextLine();
                String[] parts=currentLine.split("\"");

                String id="";
                String nameAndDate="";
                String genres="";
                if(parts.length>1){
                    //System.out.println(parts[1]);
                    if(parts.length>3){
                        String[] awkward=currentLine.split(",");
                        id=awkward[0];
                        nameAndDate=awkward[1];
                        nameAndDate=nameAndDate.substring(1,nameAndDate.length()-1);
                        genres=awkward[2];
                        //System.out.println("Problem Film:"+id+"    "+nameAndDate+"   "+genres);
                    }else{
                        id=parts[0];
                        nameAndDate=parts[1];

                        genres=parts[2];
                        if(genres.length()==0){
                            System.out.println("problem at movie:"+nameAndDate);
                            System.out.println(currentLine);

                            genres=genres.substring(1);
                        }else{
                            String[] parts2=currentLine.split(",");
                            //System.out.println(parts2[0]);
                        }}
                }else{
                    String[] normalParts=currentLine.split(",");
                    id=normalParts[0];
                    nameAndDate=normalParts[1];
                    genres=normalParts[2];
                }
                //System.out.println("ID:"+id+" nameAdn date:"+nameAndDate+" genres:"+genres);
                String dateStr="";
                String name="";

                if(nameAndDate.substring(nameAndDate.length()-1).equals(" ")){
                    dateStr=nameAndDate.substring(nameAndDate.length()-6,nameAndDate.length()-2);
                    //System.out.println("problem sstring");
                    //System.out.println(dateStr);
                }else{
                    String[] nameAndDateSplit=nameAndDate.split("\\(");
                    dateStr=nameAndDateSplit[nameAndDateSplit.length-1];
                    dateStr=dateStr.substring(0,dateStr.length()-1);
                }

                // System.out.println(dateStr);
                // System.out.println("length:"+dateStr.length());
                int date=0;
                try{
                    date=Integer.parseInt(dateStr);

                    name=nameAndDate.substring(0,nameAndDate.length()-7);

                }catch(NumberFormatException a){
                    name=nameAndDate;
                    dateStr="0";

                }
                //System.out.println("ID"+id+"name:"+name+" date:"+date+" genres:"+genres);

                int idInt=0;
                try{
                    idInt=Integer.parseInt(id);
                }catch(NumberFormatException f){
                    id=id.substring(0,id.length()-1);
                    //System.out.println(id);
                    try{
                        idInt=Integer.parseInt(id);
                    }catch(NumberFormatException g){
                        System.out.println("problem line");
                        System.out.println(currentLine);
                    }
                }
                Movie m=new Movie(idInt,name,date,genres);
                // String[] firstPart=parts[1].split("(");
                // System.out.println(parts[1]);
                movies.put(idInt,m);
            }

        }catch(FileNotFoundException e){
            System.out.println("File movies.csv not found in Server");
            e.printStackTrace();
        }
    }

    public void initiateRatings(){
        try{
            Scanner sc=new Scanner(new File("ratings.csv"));
            sc.nextLine();
            String currentLine="";
            int movieId=0;
            int userId=0;
            while(sc.hasNext()){
                try{
                    currentLine=sc.nextLine();
                    String[] parts=currentLine.split(",");
                    userId=Integer.parseInt(parts[0]);
                    movieId=Integer.parseInt(parts[1]);
                    double rating=Double.parseDouble(parts[2]);
                    //System.out.println(movies);
                    movies.get(movieId).addRating(userId,rating);
                }catch(NullPointerException a){
                    //System.out.println(movieId+" not found");
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("rating file not found exception");
        }
    }
    //check to see if valid
    public boolean isMovie(int i){
        return movies.containsKey(i);
    }

    public boolean isMovie(String name){
        boolean found=false;
        for(Movie mov:movies.values()){
            if(mov.getName().equals(name)){
                found=true;
            }
        }
        return found;
    }
    //quey movie
    public String queryMovie(int id){
        Movie m=movies.get(id);
        String str="Movie Name: "+m.getName()+"\n";
        str+="Movie ID: "+m.getID()+"\n";
        str+="Average Rating: "+m.getAverage()+"\n";
        str+="Reviews"+m.getAllReviews()+"\n";
        System.out.println("Returning string about movie:"+id);
        //System.out.println(str);
        return str;
    }
    //update straight to server
    public String updateMovie(int count,int movieId, int userId, double newRating){
        if(count==(correctUpto+1)){
            correctUpto=count;
        }
        String response=implementUpdate( movieId,userId, newRating);
        Message msg=new Message(movies.get(movieId),newRating,userId, MessageType.UPDATE);
        queue.put(count, msg);
        appliedMessages.add(count);

        return response;
    }

    public String implementUpdate(int movieId, int userId, double newRating){
        String response="Updating "+userId+"\'s review for movie "+movieId+" from";

        response+=movies.get(movieId).update(userId,newRating);

        return response;
    }
    //get id from name
    public int getMovieId(String name){
        int id=-1;
        for(Integer mov:movies.keySet()){
            if(movies.get(mov).getName().equals(name)){
                id=mov;
            }
        }
        return id;
    }
    //delete straight to server
    public Result delete(int count, int movieId,int userId){
        if(count==(correctUpto+1)){
            correctUpto=count;
        }
        Result r=Result.FAILED;
        appliedMessages.add(count);
        r=implementDelete( movieId,  userId);
        Message msg=new Message(movies.get(movieId), 0.0, userId,MessageType.DELETE);
        queue.put(count,msg);
        System.out.println("count should now be :"+queue.size());

        return r;
    }

    public Result implementDelete(int movieId,int userId){
        System.out.println("deleting user:"+userId+" review of movie:"+movieId+"from this server");
        Result r=Result.FAILED;
        r=movies.get(movieId).deleteReview(userId);

        return r;
    }
    //add straight to server
    public String add(int count, int movieId, int userId, double rating){
        System.out.println("add called");
        System.out.println("count:"+count+" correctUpTo"+correctUpto);
        if(count==(correctUpto+1)){
            correctUpto=count;
        }
        System.out.println("applied msgs:"+appliedMessages);
        appliedMessages.add(count);
        System.out.println("trying to call imp Add with mId"+movieId+" user"+userId+" double"+rating);
        String response=implementAdd(movieId, userId, rating);
        Message msg=new Message(movies.get(movieId),rating,userId, MessageType.ADD);
        queue.put(count, msg);

        return response;
    }

    public String implementAdd(int movieId, int userId, double rating){
        System.out.println("implement add called");
        String response="Adding rating:"+rating+" to movie:"+movieId+" for user:"+userId;
        //System.out.println(response);
        movies.get(movieId).addRating(userId, rating);
        return response;
    }
    //get message during gossip
    public Result recieveMessage(int count, Message msg){
        System.out.println("recieving msg:"+count);
        Result r=Result.FAILED;
        if(!queue.containsKey(count)){
            queue.put(count,msg);
        }
        return r;
    }
    //gossip with 
    public Result gossipWith(String otherServerName){
        System.out.println("trying to gossip with:"+otherServerName);
        Result r=Result.UNCERTAIN;
        try{
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37008);
            ServerInterface other=(ServerInterface) registry.lookup(otherServerName);
            System.out.println("this server is correct up to"+correctUpto);
            Set<Integer> othersMissing=other.getMissing(correctUpto);
            System.out.println(otherServerName+" is missing "+othersMissing);
            for(Integer i:othersMissing){
                if(queue.containsKey(i)){
                    other.recieveMessage(i,queue.get(i));
                }else{
                    System.out.println("this server is reporting itself as more up to date then it is");
                }
            }
        }catch(NotBoundException e){
            System.out.println("Server:"+otherServerName+" not bound at registry");
            r=Result.FAILED;
        }catch(RemoteException e){
            System.out.println("Remote exception when trying to connect with:"+otherServerName);
            r=Result.FAILED;
        }
        return r;
    }
    //get Missing messages up to currentCount
    public Set<Integer> getMissing(int number){
        Set<Integer> missingMessages=new HashSet<Integer>();
        
        for(int i=(correctUpto+1);i<=number;i++){
            if(!queue.containsKey(i)){
                missingMessages.add(i);
            }
        }
        return missingMessages;
    }
    //update to a certain point
    public Result updateFully(){
        System.out.println("updating fully");
        Result r=Result.SUCCESFUL;
        for(Integer i:queue.keySet()){
            if(!appliedMessages.contains(i)){
                switch(queue.get(i).getType()){
                    case ADD:
                    implementAdd(queue.get(i).getMovieID(),queue.get(i).getUserId(),queue.get(i).getRating());
                    break;
                    case DELETE:
                    implementDelete(queue.get(i).getMovieID(),queue.get(i).getUserId());
                    break;
                    case UPDATE:
                    implementUpdate(queue.get(i).getMovieID(),queue.get(i).getUserId(),queue.get(i).getRating());
                    break;
                }
                
            }
            
        }
        queue=new HashMap<Integer,Message>();
        currentCount=0;
        return r;
    }

    public Result updateTo(int number){
        Result r=Result.SUCCESFUL;
        for(Integer i:queue.keySet()){
            if(i<number&&!appliedMessages.contains(i)){
                switch(queue.get(i).getType()){
                    case ADD:
                    implementAdd(queue.get(i).getMovieID(),queue.get(i).getUserId(),queue.get(i).getRating());
                    break;
                    case DELETE:
                    implementDelete(queue.get(i).getMovieID(),queue.get(i).getUserId());
                    break;
                    case UPDATE:
                    implementUpdate(queue.get(i).getMovieID(),queue.get(i).getUserId(),queue.get(i).getRating());
                    break;
                }
            }
        }
        return r;
    }
}
