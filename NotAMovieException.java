
public class NotAMovieException extends Exception
{
    public String getMessage(){
        return "movie does not exist on this server";
    }
}