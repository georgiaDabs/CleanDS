
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
public interface ServerInterface extends Remote
{
    public boolean ping() throws RemoteException;
    public String queryMovie(int id) throws RemoteException;
    public String getName() throws RemoteException;
   public State getState() throws RemoteException;
   public void changeState() throws RemoteException;
   public boolean isMovie(int i) throws RemoteException;
   public String updateMovie(int count,int movieId,int userId, double newRating) throws RemoteException;
   public boolean isMovie(String name) throws RemoteException;
   public int getMovieId(String name) throws RemoteException; 
   public int getCurrentCount() throws RemoteException;
   public Result delete(int count, int movieId, int userId) throws RemoteException;
   public String add(int count, int movieId, int userId, double rating) throws RemoteException;
   public Set<Integer> getMissing(int upTo) throws RemoteException;
   public Result gossipWith(String otherServerName) throws RemoteException;
   public Result recieveMessage(int number, Message msg) throws RemoteException;
   public Result updateTo(int number) throws RemoteException;
   public Result updateFully() throws RemoteException;
}
