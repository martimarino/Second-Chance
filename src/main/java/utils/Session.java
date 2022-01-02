package main.java.utils;

import main.java.entity.User;

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
        else
            session.logUser = new User(username);
    }

}