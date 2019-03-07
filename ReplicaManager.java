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

    public ArrayList<Integer> resetStubs() throws NoneOnlineException{
        boolean online=false;
        ArrayList<Integer> onlineServers=new ArrayList<Integer>();
        try{
            if(current.getState()==State.ACTIVE){
                System.out.println("current is active");
                online=true;
                onlineServers.add(0);
            }else{
                System.out.println("current is:"+current.getState());
            }
        }catch(RemoteException e){
            System.out.println("current is unreachable");
        }
        try{
            if(backups.get(0).getState()==State.ACTIVE){
                System.out.println("backup1 is active");

                if(online==false){
                    System.out.println("swapping current with backup 1");
                    ServerInterface temp=current;
                    current=backups.get(0);
                    backups.set(0,temp);
                    System.out.println("Current is now:"+current.getName());
                    onlineServers.add(0);
                }else{
                    onlineServers.add(1);
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

                if(online==false){
                    System.out.println("swapping current with backup 1");
                    ServerInterface temp=current;
                    current=backups.get(1);
                    backups.set(1,temp);
                    System.out.println("Current is now:"+current.getName());
                    onlineServers.add(0);
                }else{
                    onlineServers.add(2);
                }
                online=true;
            }else{
                System.out.println("current is:"+current.getState());
            }
        }catch(RemoteException e){
            System.out.println("current is unreachable");
        }
        return onlineServers;
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
    public String sendRating(double rating, int userId, String movieName) throws NoneOnlineException{
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
            response=current.add(currentCount,current.getMovieId(movieName),userId,rating);
            currentCount++;
        }catch(RemoteException e){

        }
        return response;
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

    public void allOffline() throws NoneOnlineException{
        System.out.println("All servers offline or overloaded");
        changeStates();
        throw new NoneOnlineException();
    }

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

    public void gossip() throws NoneOnlineException{
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
            //reset stubs so if any are online they will become current and get how many are online
            ArrayList<Integer> onlineServers=resetStubs();
            if(onlineServers.size()==1){

            }else if(onlineServers.size()==2){
                int upToDateNumber1=0;
                int upToDateNumber2=1;
                while(upToDateNumber1!=upToDateNumber2){
                    
                    ArrayList<ServerInterface> servers=new ArrayList<ServerInterface>();
                    servers.add(current);
                    servers.add(backups.get(onlineServers.get(1)-1));

                    int mostUpToDate=mostUpToDate(servers);
                    try{
                        if(mostUpToDate==0){
                            current.gossipWith(backups.get(onlineServers.get(1)-1).getName());
                            backups.get(onlineServers.get(1)-1).updateTo(current.getCurrentCount());
                        }else{
                            backups.get(onlineServers.get(1)-1).gossipWith(current.getName());
                            current.updateTo(backups.get(onlineServers.get(1)-1).getCurrentCount());
                        }
                        
                    }catch(RemoteException e){
                        System.out.println("Remote exception trying to get current to gossip with backup "+onlineServers.get(1));
                    }
                    try{
                        upToDateNumber1=current.getCurrentCount();
                        upToDateNumber2=backups.get(onlineServers.get(1)-1).getCurrentCount();
                    }catch(RemoteException e){
                        System.out.println("Remote Exception gettingcurrent counts SHOULDN'T HAPPEN");
                    }
                }
            }else if(onlineServers.size()==3){
                int upToDate1=0;
                int upToDate2=1;
                int upToDate3=2;
                while(upToDate1!=currentCount&&upToDate2!=currentCount&&upToDate3!=currentCount){
                    try{
                        current.gossipWith(backups.get(0).getName());
                        current.gossipWith(backups.get(1).getName());
                    }catch(RemoteException e){
                        System.out.println("RemoteException with gossiping when all 3 should be on SHOULDN'T HAPPEN");
                    }
                    try{
                        upToDate1=current.getCurrentCount();
                        upToDate2=backups.get(0).getCurrentCount();
                        upToDate3=backups.get(1).getCurrentCount();
                    }catch(RemoteException e){
                        System.out.println("remote excpetion with getting new up to date numbers SHOULDN'T HAPPEN");
                    }
                }
            }else if(onlineServers.isEmpty()){
                allOffline();
            }
        }catch(NoneOnlineException e){
            allOffline();
        }
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

    public Result deleteReview(int movieId, int userId) throws NoneOnlineException{
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

    public String updateMovie(int movieId, int userId, double newRating) throws NoneOnlineException{
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

    public Result deleteReview(String movieName, int userId) throws NoneOnlineException{
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

    public String updateMovie(String moviename, int userId, double newRating) throws NoneOnlineException{
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

    public boolean isMovie(String name) throws NoneOnlineException{
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
    public void ping() throws NoneOnlineException{
        System.out.println("PING");
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
    }
    public boolean isMovie(int id) throws NoneOnlineException{
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
