import java.rmi.Remote;
import java.rmi.RemoteException;
public interface FrontEndInterface extends Remote
{
    public void ping() throws RemoteException, NoneOnlineException;
    public void changeStates() throws RemoteException;
    public String sendRating(double rating,int userId, int movieID) throws NoneOnlineException,RemoteException;
    public String sendRating(double rating,int userId, String movieName) throws NoneOnlineException,RemoteException;
    public String queryMovie(int movieID) throws RemoteException, NoneOnlineException;
     public String queryMovie(String movieName) throws RemoteException, NoneOnlineException;
     public Result deleteReview(int movieId, int userId) throws  RemoteException, NoneOnlineException;
     public String updateMovie(int movieId, int userId, double newRating) throws RemoteException, NoneOnlineException;
     public Result deleteReview(String moviename, int userId) throws  RemoteException, NoneOnlineException;
     public String updateMovie(String moviename, int userId, double newRating) throws RemoteException,NoneOnlineException;
     public boolean isMovie(String name) throws RemoteException, NoneOnlineException;
     public boolean isMovie(int id) throws RemoteException, NoneOnlineException;
     public void initiateStubs() throws RemoteException, NoneOnlineException;
}
