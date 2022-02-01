package main.java.it.unipi.dii.largescale.secondchance.utils;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import org.bson.Document;

public class Session{

    private User logUser;
    private static Session session = null;

    public Session(){}

    public static Session getInstance(){

        if(session == null)
            session = new Session();
        return session;
    }

    public void setLogUser(Document user, Boolean isAdmin){

        if(session == null)
            throw new RuntimeException("Session not already active");
        else {
            if(isAdmin)
                session.logUser = User.fromDocumentAdmin(user);
            else
                session.logUser = User.fromDocument(user);
        }
    }

    public static User getLogUser() {

        if(session == null)
            throw new RuntimeException("Session not already active");
        else
            return session.logUser;
    }

    public User getLoggedUser() {
        if(session == null)
            throw new RuntimeException("Session is not active.");
        else
            return session.logUser;
    }

    public void getLogoutUser() {

        logUser = null;
        session = null; // [1]
    }

}


// [1]: After this, if there is no reference to the object
//      it will be deleted by the garbage collector