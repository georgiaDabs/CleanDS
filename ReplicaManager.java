import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
public class ReplicaManager implements FrontEndInterface
{
    public ServerInterface current;
    public  ArrayList<ServerInterface> backups;
    public int currentCount;
    //constructor
    public ReplicaManager(){
        currentCount=0;
    }
    //main
    public static void main(String[] args){

        try {
            ReplicaManager obj=new ReplicaManager();
            obj.current=null;
            obj.backups=new ArrayList<ServerInterface>();
            FrontEndInterface thisStub = (FrontEndInterface) UnicastRemoteObject.exportObject(obj, 0);
            thisStub.initiateStubs();
            // Get registry
            Registry registry = LocateRegistry.createRegistry(37009);
            try{
                registry.bind("FrontEndServer",thisStub);
            }catch(AlreadyBoundException a){
                registry.unbind("FrontEndServer");
                registry.bind("FrontEndServer",thisStub);
            }

            // Lookup the remote object "Hello" from registry
            // and create a stub for itls

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void initiateStubs(){

        try {

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37008);
            current = (ServerInterface) registry.lookup("MovieRating1");
            System.out.println("Current server state:"+current.getState());
            ServerInterface replica2=(ServerInterface) registry.lookup("MovieRating2");
            System.out.println("Backup1 server state:"+replica2.getState());
            backups.add(replica2);
            ServerInterface replica3=(ServerInterface) registry.lookup("MovieRating3");
            System.out.println("Backup2 server state:"+replica3.getState());
            backups.add(replica3);
            // Lookup the remote object "Hello" from registry
            // and create a stub for itls

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            //e.printStackTrace();
        }

    }

    public int resetStubs() throws NoneOnlineException{
        boolean online=false;
        int numberOnline=0;
        try{
            if(current.getState()==State.ACTIVE){
                System.out.println("current is active");
                online=true;
                numberOnline++;
            }else{
                System.out.println("current is:"+current.getState());
            }
        }catch(RemoteException e){
            System.out.println("current is unreachable");
        }
        try{
            if(backups.get(0).getState()==State.ACTIVE){
                System.out.println("backup1 is active");

                numberOnline++;
                if(online==false){
                    System.out.println("swapping current with backup 1");
                    ServerInterface temp=current;
                    current=backups.get(0);
                    backups.set(0,temp);
                    System.out.println("Current is now:"+current.getName());
                }
                online=true;
            }else{
                System.out.println("current is:"+current.getState());
            }
        }catch(RemoteException e){
            System.out.println("current is unreachable");
        }
        try{
            if(backups.get(1).getState()==State.ACTIVE){
                System.out.println("backup1 is active");

                numberOnline++;
                if(online==false){
                    System.out.println("swapping current with backup 1");
                    ServerInterface temp=current;
                    current=backups.get(1);
                    backups.set(1,temp);
                    System.out.println("Current is now:"+current.getName());
                }
                online=true;
            }else{
                System.out.println("current is:"+current.getState());
            }
        }catch(RemoteException e){
            System.out.println("current is unreachable");
        }
        return numberOnline;
    }

    public void changeStates(){
        try{
            current.changeState();
        }catch(RemoteException e){
            System.out.println("remote exception");
        }
        try{
            backups.get(0).changeState();
        }catch(RemoteException e){
            System.out.println("remote exception");
        }
        try{
            backups.get(1).changeState();
        }catch(RemoteException e){
            System.out.println("remote exception");
        }
    }

    public String sendRating(double rating,int userId, int movieID) throws NoneOnlineException{
        String response="";
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }

        try{

            response=current.add(currentCount,movieID,userId,rating);
            currentCount++;
        }catch(RemoteException e){
            System.out.println("current offline");

        }
        return response;
    }

    public void allOffline(){}

    public String queryMovie(int movieID) throws NoneOnlineException{
        String response="";
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }
        try{
            gossip();
            String query=current.queryMovie(movieID);
        }catch(RemoteException e){
            System.out.println("current offline");

        }
        return response;
    }

    public void gossip(){

    }

    public String queryMovie(String movieName) throws NoneOnlineException{
        String response="";
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }
        try{
                response=current.queryMovie(current.getMovieId(movieName));
            
        }catch(RemoteException e){

        }
        return response;
    }

    public Result deleteReview(int movieId, int userId){
        Result r=Result.UNCERTAIN;
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }
        try{
            r=current.delete(currentCount,movieId,userId);
            currentCount++;
        }catch(RemoteException e){
            System.out.println("getting remote exception");
        }
        return r;
    }

    public String updateMovie(int movieId, int userId, double newRating){
        String response="";
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }
        try{
            response=current.updateMovie(currentCount,movieId,userId,newRating);
            currentCount++;
        }catch(RemoteException e){
            System.out.println("remoteException e");
        }
        return response;
    }

    public Result deleteReview(String movieName, int userId){
        Result r=Result.UNCERTAIN;
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }
        try{
            
                r=current.delete(currentCount,current.getMovieId(movieName),userId);
                currentCount++;
            
        }catch(RemoteException e){
            System.out.println("remoteException e");
        }
        return r;
    }

    public String updateMovie(String moviename, int userId, double newRating){
        String response="";
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }
        try{
            
                response=current.updateMovie(currentCount,current.getMovieId(moviename),userId,newRating);
                currentCount++;
            
        }catch(RemoteException e){
            System.out.println("remoteException e");
        }
        return response;
    }

    public boolean isMovie(String name){
        boolean is=false;
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }
        try{
            is=current.isMovie(name);
        }catch(RemoteException e){
            System.out.println("RemoteException");
        }
        return is;
    }

    public boolean isMovie(int id){
        boolean is=false;
        try{
            if(!current.ping()){
                if(!backups.get(1).ping()||!backups.get(0).ping()){
                    allOffline();
                }
            }
        }catch(RemoteException a){
            try{
                if(!backups.get(0).ping()){
                    if(!backups.get(1).ping()){
                        allOffline();
                    }
                }
            }catch(RemoteException b){
                try{if(!backups.get(1).ping()){
                        allOffline();
                    }
                }catch(RemoteException c){
                    allOffline();
                }
            }
        }
        try{
            is=current.isMovie(id);
        }catch(RemoteException e){
            System.out.println("RemoteException");
        }
        return is;
    }

    public int mostUpToDate(ArrayList<ServerInterface> servers){
        int count=0;
        int best=-1;
        for(int i=0;i<servers.size();i++){
            try{
            if(servers.get(i).getCurrentCount()>count){
                best=i;
            }}catch(RemoteException e){
                System.out.println("RemoteException");
            }
        }
        return best;
    }
}
