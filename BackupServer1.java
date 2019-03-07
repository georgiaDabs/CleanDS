import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.rmi.AlreadyBoundException;
public class BackupServer1
{
    static Registry registry;
    public static void main(String[] args){
        try{
            Server obj=new Server("MovieRating2",0.05,0.01);
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Get registry
             registry = LocateRegistry.getRegistry("mira2.dur.ac.uk",37008);
            try{
                registry.bind("MovieRating2",stub);
            }catch(AlreadyBoundException a){
                registry.unbind("MovieRating2");
                registry.bind("MovieRating2",stub);
            }
            System.err.println("Server ready");
        }catch(Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
