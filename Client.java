import java.util.Scanner;
import java.util.InputMismatchException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.RemoteException;
public class Client
{
    private static FrontEndInterface stub;
    public static void main(String[] args){
        try {

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37009);

            // Lookup the remote object "Hello" from registry
            // and create a stub for it
            stub = (FrontEndInterface) registry.lookup("FrontEndServer");

            // Invoke a remote method

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        Scanner sc=new Scanner(System.in);

        boolean running =true;
        while(running){
            try{
                boolean inputCorrect=false;
                System.out.println("Welcome to the Movie Ratings Server press the key of what you'd like to do");
                System.out.println("1. Get Movie Ratings");
                System.out.println("2. Send a Movie Rating");
                System.out.println("3. Update a Movie Rating");
                System.out.println("4. Delete a Movie Rating");
                System.out.println("5. Exit");
                int response=0;
                while(!inputCorrect){
                    if(sc.hasNextInt()){
                        response=sc.nextInt();
                        if(response>0&&response<=6){
                            inputCorrect=true;
                        }else{
                            System.out.println("Not a 1,2,3,4 or 5 try again");
                        }
                    }else{
                        sc.next();
                        System.out.println("Not an integer try again");
                    }
                }
                if(response==5){
                    System.out.println("Thankyou for using the server");
                    running=false;
                }
                if(response==1){
                    getRating();
                }else if(response==2){
                    sendRating();
                }else if(response==3){
                    updateRatings();
                }else if(response==4){
                    deleteRating();
                }/* else if(response==5){
                //addMovie();
                }*/
            }catch(NoneOnlineException e){
                boolean waitLoop=true;
                while(waitLoop){
                    System.out.println(e.getTime());
                    System.out.println("would you like to wait for a server to come back online or exit?");
                    System.out.println("1. wait");
                    System.out.println("2. exit");
                    int errorResponse=0;
                    boolean inputCorrect=false;
                    while(!inputCorrect){
                        if(sc.hasNextInt()){
                            errorResponse=sc.nextInt();
                            if(errorResponse>0&&errorResponse<=2){
                                inputCorrect=true;
                            }
                        }
                    }
                    if(errorResponse==1){
                        try{
                            stub.ping();
                            waitLoop=false;
                        }catch(NoneOnlineException a){
                            System.out.println("Still offline");
                        }catch(RemoteException r){
                            System.out.println("RemoteException when waiting for server to change");
                        }
                    }else if(errorResponse==2){
                        waitLoop=false;
                        running=false;
                    }
                }
            }
        }

    }

    public static void updateRatings() throws NoneOnlineException{
        Scanner sc=new Scanner(System.in);
        boolean current=true;
        while(current){
            System.out.println("How would you like to get the film to update?");
            System.out.println("1.by ID");
            System.out.println("2.by name");
            System.out.println("3. Back to main menu");
            boolean inputCorrect=false;
            int  response=0;
            while(!inputCorrect){
                if(sc.hasNextInt()){
                    response=sc.nextInt();
                    if(response>0&&response<=3){
                        inputCorrect=true;
                    }else{
                        sc.next();
                        System.out.println("please enter a 1, 2 or 3");  
                    }
                }else{
                    sc.next();
                    System.out.println("Not an integer try again");
                }
            }
            if(response==1){
                try{
                    System.out.println("please enter the movie id");
                    int movieID=0;
                    inputCorrect=false;
                    while(!inputCorrect){
                        if(sc.hasNextInt()){
                            movieID=sc.nextInt();
                            inputCorrect=true;
                        }else{
                            sc.next();
                            System.out.println("Not an integer try again");
                        }
                    }

                    System.out.println("which users review would you like to update?");
                    int userID=0;
                    inputCorrect=false;
                    while(!inputCorrect){
                        if(sc.hasNextInt()){
                            userID=sc.nextInt();
                            inputCorrect=true;
                        }else{
                            sc.next();
                            System.out.println("Not an integer try again");
                        }
                    }
                    System.out.println("what would you like to update this rating to?");
                    double newRating=0.0;
                    inputCorrect=false;
                    while(!inputCorrect){
                        if(sc.hasNextDouble()){
                            newRating=sc.nextDouble();
                            if(newRating>0&&newRating<=5.0){
                                inputCorrect=true;
                            }else{
                                System.out.println("not in range of 0 to 5 try again");
                            }
                        }else{
                            sc.next();
                            System.out.println("Not a double try again");
                        }
                    }
                    String serverResponse=stub.updateMovie(movieID, userID, newRating);
                    System.out.println(serverResponse);

                }catch(RemoteException r){
                    System.out.println("Remote Exception in update block");
                    r.printStackTrace();
                }
            }else if(response==2){
                System.out.println("please enter the movie name");
                try{
                    String movieName=sc.next();
                    System.out.println("Which user's review would you like to update?");
                    int userId=0;
                    inputCorrect=false;
                    while(!inputCorrect){
                        if(sc.hasNextInt()){
                            userId=sc.nextInt();
                            inputCorrect=true;
                        }else{
                            sc.next();
                            System.out.println("Not an integer try again");
                        }
                    }
                    System.out.println("What would you like to change the review to (please enter a double)?");
                    double newRating=0.0;
                    inputCorrect=false;
                    while(!inputCorrect){
                        if(sc.hasNextDouble()){
                            newRating=sc.nextDouble();
                            inputCorrect=true;
                        }else{
                            sc.next();
                            System.out.println("Not a double try again");
                        }
                    }
                    String serverResponse=stub.updateMovie(movieName,userId, newRating);
                    System.out.println(serverResponse);
                }catch(RemoteException r){
                    System.out.println("Remote exception in update block");
                    r.printStackTrace();
                }
            }else if(response==3){
                current=false;
            }
        }
    }

    public static void getRating() throws NoneOnlineException{
        Scanner sc=new Scanner(System.in);
        boolean current=true;

        while(current){
            System.out.println("How would you like to get the rating");
            System.out.println("1. By Movie ID");
            System.out.println("2. By Movie Name");
            System.out.println("3. Back To Main Menu");
            int response=0;
            boolean inputCorrect=false;
            while(!inputCorrect){
                if(sc.hasNextInt()){
                    response=sc.nextInt();
                    if(response>0&&response<=3){
                        inputCorrect=true;
                    }else{

                        System.out.println("not a 1, 2 or 3 try again");
                        sc.next();
                    }
                }else{
                    System.out.println("not an integer try again");
                }
            }
            if(response==3){
                current=false;
            }else if(response==2){
                System.out.println("Enter name:");
                String input=sc.next();
                try{
                    String serverResponse=stub.queryMovie(input);
                    System.out.println(serverResponse);
                }catch(RemoteException e){
                    System.out.println("Remote exception at query movie");
                    e.printStackTrace();
                }catch(NullPointerException a){
                    System.out.println("input was:"+input);
                    System.out.println("Server is:"+stub);
                }
            }else if(response==1){
                System.out.println("Enter ID:");
                int input=0;
                inputCorrect=false;
                while(!inputCorrect){
                    if(sc.hasNextInt()){
                        input=sc.nextInt();
                        inputCorrect=true;
                    }else{
                        sc.next();
                        System.out.println("Not an integer try again");
                    }
                }
                try{
                    System.out.println("sending query to movie with id:"+input);
                    String serverResponse=stub.queryMovie(input);
                    System.out.println(serverResponse);
                }catch(RemoteException e){
                    System.out.println("Remote exception at query movie");
                    e.printStackTrace();
                }
            }

        }
    }

    public static void deleteRating() throws NoneOnlineException{
        Scanner sc=new Scanner(System.in);

        boolean current=true;
        while(current){
            boolean inputCorrect=false;
            System.out.println("How wil you choose the film rating to delete?");
            System.out.println("1.by ID");
            System.out.println("2.by name");
            System.out.println("3. Back to main menu");
            int response=0;
            while(!inputCorrect){
                if(sc.hasNextInt()){
                    response=sc.nextInt();
                    if(response<=3&&response>0){
                        inputCorrect=true;
                    }else{
                        System.out.println("Not a 1, 2 or 3 try again");
                    }

                }else{
                    System.out.println("not a 1, 2 or 3 try again");
                    sc.next();
                }
            }
            if(response==1){
                inputCorrect=false;
                System.out.println("Enter id:");
                int id=-1;
                while(!inputCorrect){
                    if(sc.hasNextInt()){
                        id=sc.nextInt();
                        inputCorrect=true;
                    }else{
                        System.out.println("not an integer try again");
                    }
                }
                try{
                    if(stub.isMovie(id)){
                        System.out.println("which users review would you like to delete?");
                        inputCorrect=false;

                        int userId=-1;
                        while(!inputCorrect){
                            if(sc.hasNextInt()){
                                userId=sc.nextInt();
                                inputCorrect=true;
                            }else{
                                System.out.println("Not an integer try again");
                                sc.next();
                            }
                        }
                        Result r=stub.deleteReview(id,userId);
                        System.out.println(r);

                    }else{
                        System.out.println("Not a movie try again");
                    }
                }catch(RemoteException a){
                    System.out.println("Lost connection with server");
                }
            }else if(response==3){
                current=false;
            }else if(response==2){
                System.out.println("Enter name:");

                String name=sc.next();
                try{
                    if(stub.isMovie(name)){
                        System.out.println("which users review would you like to delete?");
                        inputCorrect=false;
                        int userId=-1;
                        while(!inputCorrect){
                            if(sc.hasNextInt()){
                                userId=sc.nextInt();
                                inputCorrect=true;
                            }else{
                                sc.next();
                                System.out.println("Not an integer try again");
                            }
                        }
                        System.out.println(stub.deleteReview(name,userId));
                    }
                }catch(RemoteException a){
                    System.out.println("remote exception getting id");
                }
            }
        }
    }

    public static void sendRating() throws NoneOnlineException{
        Scanner sc=new Scanner(System.in);
        boolean current=true;
        while(current){
            boolean inputCorrect=false;
            System.out.println("How will you choose the film to rate");
            System.out.println("1. By Movie ID");
            System.out.println("2. By Movie Name");
            System.out.println("3. Back To Main Menu");
            int response=0;
            while(!inputCorrect){
                if(sc.hasNextInt()){
                    response=sc.nextInt();
                    inputCorrect=true;
                }else{
                    sc.next();
                    System.out.println("Not an integer try again");
                }
            }

            if(response==3){
                current=false;
                break;
            }else if(response==1){
                System.out.println("Please enter the Movie id");
                inputCorrect=false;

                int id=-1;
                while(!inputCorrect){
                    if(sc.hasNextInt()){
                        id=sc.nextInt();
                        inputCorrect=true;
                    }else{
                        sc.next();
                        System.out.println("Not an integer try again");
                    }
                }
                try{
                    System.out.println("Enter the user id for this rating");
                    inputCorrect=false;

                    int userID=0;
                    while(!inputCorrect){if(sc.hasNextInt()){
                            userID=sc.nextInt();
                            inputCorrect=true;
                        }else{
                            System.out.println("Not an integer try again");
                            sc.next();
                        }
                    }
                    System.out.println("Enter the rating as a double");
                    inputCorrect=false;
                    double rating=0.0;
                    while(!inputCorrect){
                        if(sc.hasNextDouble()){
                            rating=sc.nextDouble();
                            if(rating>0&&rating<=5.0){
                                inputCorrect=true;}else{
                                System.out.println("not in range of 0 to 5 try again");
                            }
                        }else{
                            System.out.println("Not a double try again");
                            sc.next();
                        }
                    }
                    String serverResponse=stub.sendRating(rating,userID,id);
                    System.out.println(serverResponse);
                }catch(RemoteException r){
                    System.out.println("remote exception at get movie for review");
                }
            }else if(response==2){
                System.out.println("Please enter the name");
                String name=sc.next();
                try{
                    if(stub.isMovie(name)){
                        System.out.println("please enter the user id for the rating");
                        int userId=0;
                        inputCorrect=false;
                        while(!inputCorrect){
                            if(sc.hasNextInt()){
                                userId=sc.nextInt();
                                inputCorrect=true;
                            }else{
                                System.out.println("Not an integer try again");
                                sc.next();
                            }
                        }
                        System.out.println("please enter the rating as a double");
                        inputCorrect=false;
                        double rating=0.0;
                        while(!inputCorrect){
                            if(sc.hasNextDouble()){
                               
                                rating=sc.nextDouble();
                                if(rating>0&&rating<=5.0){
                                inputCorrect=true;}else{System.out.println("not in range 0 to 5 try again");}
                            }else{
                                System.out.println("Not a double try again");
                                sc.next();
                            }
                        }
                        String serverResponse=stub.sendRating(rating,userId,name);
                        System.out.println(serverResponse);
                    }
                }catch(RemoteException e){
                    System.out.println("lost connection with front end");
                }
            }

        }
    }

}