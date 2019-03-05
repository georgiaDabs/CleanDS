import java.rmi.Remote;
import java.rmi.RemoteException;
public interface FrontEndInterface extends Remote
{
    public void changeStates() throws RemoteException;
    public String sendRating(double rating,int userId, int movieID) throws NoneOnlineException,RemoteException;
     public String queryMovie(int movieID) throws RemoteException, NoneOnlineException;
     public String queryMovie(String movieName) throws RemoteException, NoneOnlineException;
     public Result deleteReview(int movieId, int userId) throws  RemoteException;
     public String updateMovie(int movieId, int userId, double newRating) throws RemoteException;
     public Result deleteReview(String moviename, int userId) throws  RemoteException;
     public String updateMovie(String moviename, int userId, double newRating) throws RemoteException;
     public boolean isMovie(String name) throws RemoteException;
     public boolean isMovie(int id) throws RemoteException;
     public void initiateStubs() throws RemoteException;
}
