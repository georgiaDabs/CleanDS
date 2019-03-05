import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class NoneOnlineException extends Exception
{
    DateFormat df;
    Date dateobj;
    public NoneOnlineException(){
        df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        dateobj = new Date();
    }

    public String getTime(){
        String errorString="All servers were offline or overloaded at "+df.format(dateobj);
        return errorString;
    }
}
