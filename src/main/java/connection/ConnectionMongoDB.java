package main.java.connection;

import com.mongodb.client.*;
import main.java.entity.*;
import main.java.utils.*;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.ConnectionString;

import java.io.IOException;

public class ConnectionMongoDB{

    private MongoClient mongoClient;
    private MongoDatabase db;

    public void openConnection(){
        ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
        mongoClient = MongoClients.create(uri);
        db = mongoClient.getDatabase("local");
    }

    public void closeConnection() {
        mongoClient.close();
    }

    public boolean logInUser(String username, String password) throws IOException {

        this.openConnection();
        if(!userAlreadyPresent(username, password)) {
            System.out.println("Username or Password wrong, try again");
            this.closeConnection();
            return false;
        } else{
            System.out.println("FOUND!!!!");
        }
        this.closeConnection();
        return true;
    }

    public boolean registerUser(User u) {

        this.openConnection();

        if(userAlreadyPresent(u.getUsername(), u.getPassword())) {
            Utility.infoBox("Please, choose another username and try again.", "Error", "Username already used!");
            return false;
        }

        MongoCollection<Document> myColl = db.getCollection("user");
        Document user = new Document("address", u.getAddress())
                .append("city", u.getCity())
                .append("country", u.getCountry())
                .append("email", u.getEmail())
                .append("name", u.getName())
                .append("password", u.getPassword())
                .append("username", u.getUsername())
                .append("suspended", u.getSuspended());
        myColl.insertOne(user);
        this.closeConnection();
        return true;
    }

    private boolean userAlreadyPresent(String username, String password) {

        MongoCollection<Document> myColl = db.getCollection("user");
        MongoCursor<Document> cursor  = myColl.find(and(eq("username", username),
                eq("password", password))).iterator();
        if(!cursor.hasNext())
            return false;
        return true;
    }

    public User findUserDetails(String username)
    {
        this.openConnection();
        User logUser = new User();
        MongoCollection<Document> myColl = db.getCollection("user");
        Document user = myColl.find(eq("username", username)).first();
        logUser.setUsername(user.getString("username"));
        logUser.setEmail(user.getString("email"));
        logUser.setAddress(user.getString("address"));
        logUser.setCity(user.getString("city"));
        logUser.setCountry(user.getString("country"));
        this.closeConnection();
        return logUser;
    }
}