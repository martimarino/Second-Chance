package main.java.utils;

import main.java.entity.*;

public class Session{

    private User logUser;
    private static Session session = null;

    public Session(){}

    public static Session getInstance(){

        if(session == null)
            session = new Session();
        return session;
    }

    public void setLogUser(String username){

        if(session == null)
            throw new RuntimeException("Session not already active");
        else {
            session.logUser = new User(username);
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