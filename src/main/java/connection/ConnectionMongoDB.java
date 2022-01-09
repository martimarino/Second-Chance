package main.java.connection;

import com.mongodb.client.*;
import main.java.entity.*;
import main.java.utils.*;
import org.bson.Document;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import com.mongodb.ConnectionString;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class ConnectionMongoDB{

    private MongoClient mongoClient;
    private MongoDatabase db;
    MongoCursor<Document> cursor;

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
                .append("suspended", u.getSuspended())
                .append("rating", u.getRating());
        myColl.insertOne(user);
        this.closeConnection();
        return true;
    }

    private static Consumer<Document> printDocuments() {
        return doc -> System.out.println(doc.toJson());
    }

    public Document findUserByUsername(String username) {

        this.openConnection();
        ArrayList<Document> users = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("user");
        cursor  = myColl.find(eq("username", username)).iterator();
        if(!cursor.hasNext()) {
            this.closeConnection();
            Utility.infoBox("There is no user with this username.", "Error", "Username not found!");
            return null;
        }

        if (cursor.hasNext())
        {
            this.closeConnection();
            return cursor.next();
        }

        return null;
    }

    private boolean userAlreadyPresent(String username, String password) {

        MongoCollection<Document> myColl = db.getCollection("user");
        cursor = myColl.find(and(eq("username", username),
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
        logUser.setName(user.getString("name"));
        logUser.setEmail(user.getString("email"));
        logUser.setAddress(user.getString("address"));
        logUser.setCity(user.getString("city"));
        logUser.setCountry(user.getString("country"));
        logUser.setSuspended(user.getString("suspended"));
        this.closeConnection();
        return logUser;
    }

    public ArrayList<Document> followedUserInsertions(ArrayList<String> usList, int k) {

        this.openConnection();
        ArrayList<Document> insertions = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("insertion");
        System.out.println("insertion id: " + usList);
        for(int i = 0; i < usList.size(); i++) {
            Document d = myColl.find(eq("uniq_id", usList.get(i))).first();
            insertions.add(d);
        }

        this.closeConnection();
        return insertions;
    }

    public ArrayList<Document> findViralInsertions(int k) {

        this.openConnection();
        ArrayList<Document> insertions = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("insertion");
        Bson match = match(eq("sold", "N"));
        Bson sort = sort(descending("interested"));
        Bson project = project(fields(excludeId(), include("seller"), include("image_url"), include("status"), include("interested"), include("price"), include("uniq_id")));
        Bson limit = limit(k);
        myColl.aggregate(Arrays.asList(sort,project ,limit));
        AggregateIterable<Document> r = myColl.aggregate(Arrays.asList(match, sort,project ,limit));

        for (Document document : r) {
            insertions.add(document);
        }
        this.closeConnection();
        return insertions;
    }

    public ArrayList<Document> findUserByFilters(String country,String rating) {

        this.openConnection();
        MongoCollection<Document> myColl = db.getCollection("user");
        ArrayList<Document> users = new ArrayList<>();

        if(country.equals("country") && !rating.equals("rating"))
        {
             cursor  = myColl.find(eq("rating", rating)).iterator();
        }
        else if(!country.equals("country") && rating.equals("rating"))
        {
             cursor  = myColl.find(eq("country", country)).iterator();
        }
        else{
             cursor  = myColl.find(and(eq("country", country),
                    eq("rating", rating))).iterator();
        }

        while(cursor.hasNext())
        {
            users.add(cursor.next());
        }

        this.closeConnection();
        return users;

    }

    private ArrayList<Document> partialSearch(int index, MongoCollection<Document> myColl, ArrayList<Document> insertions, String size, String price, String gender, String status, String category, String color) {

        switch (index) {
            case 0:
                cursor  = myColl.find(eq("size", size)).iterator();
                break;
            case 1:
                String[] split = price.split("-");
                System.out.println("FASCIA: " + split[0]);
                if(split.length == 1) {
                    cursor  = myColl.find(gte("price", Double.parseDouble(split[0]))).iterator();
                } else {
                    System.out.println("FASCIA: " + split[1]);
                    cursor  = myColl.find(and(gte("price", Double.parseDouble(split[0])),
                            lte("price", Double.parseDouble(split[1])))).iterator();
                }
                break;
            case 2:
                cursor  = myColl.find(eq("gender", gender)).iterator();
                break;
            case 3:
                cursor  = myColl.find(eq("status", status)).iterator();
                break;
            case 4:
                cursor  = myColl.find(eq("category", category)).iterator();
                break;
            case 5:
                cursor  = myColl.find(eq("color", color)).iterator();
                break;
            default:
                break;
        }
        while(cursor.hasNext())
            insertions.add(cursor.next());

        return insertions;
    }

    public ArrayList<Document> findInsertionByFilters(String size, String price, String gender, String status, String category, String color) {

        this.openConnection();
        ArrayList<Document> insertions = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("insertion");

        //the following variables are 1 if the relative filter is applied
        int sizeFilterOn, priceFilterOn, genderFilterOn, statusFilterOn, categoryFilterOn, colorFilterOn;
        sizeFilterOn = (size.equals("size")) ? 0 : 1;
        priceFilterOn = (price.equals("price")) ? 0 : 1;
        genderFilterOn = (gender.equals("gender")) ? 0 : 1;
        statusFilterOn = (status.equals("status")) ? 0 : 1;
        categoryFilterOn = (category.equals("category")) ? 0 : 1;
        colorFilterOn = (color.equals("color")) ? 0 : 1;

        int[] filter = {sizeFilterOn, priceFilterOn, genderFilterOn,
                statusFilterOn,categoryFilterOn, colorFilterOn};

        for(int i = 0; i < 6; i++) {
            if(filter[i] == 1)
            partialSearch(i, myColl, insertions, size, price, gender, status, category, color);
        }

        this.closeConnection();
        return insertions;
    }


    public ArrayList<Document> findInsertionBySeller(String seller) {

        this.openConnection();
        ArrayList<Document> insertions = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("insertion");

        cursor = myColl.find(eq("seller", seller)).iterator();
        while (cursor.hasNext())
            insertions.add(cursor.next());

        this.closeConnection();
        return insertions;

    }

    public ArrayList<Document> findInsertionByBrand(String brand) {

        this.openConnection();
        ArrayList<Document> insertions = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("insertion");

        cursor = myColl.find(eq("brand", brand)).iterator();
        while (cursor.hasNext())
            insertions.add(cursor.next());

        this.closeConnection();
        return insertions;
    }

    public Insertion findInsertion(String insertion_id) {

        this.openConnection();

        Insertion insertion = new Insertion();
        MongoCollection<Document> myColl = db.getCollection("insertion");
        Document insertion_found = myColl.find(eq("uniq_id", insertion_id)).first();

        insertion.setBrand(insertion_found.getString("brand"));
        insertion.setCountry(insertion_found.getString("country"));
        insertion.setCategory(insertion_found.getString("category"));
        insertion.setColor(insertion_found.getString("color"));
        insertion.setDescription(insertion_found.getString("description"));
        insertion.setGender(insertion_found.getString("gender"));
        insertion.setImage_url(insertion_found.getString("image_url"));
        insertion.setInterested(insertion_found.getInteger("interested"));
        insertion.setPrice(insertion_found.getDouble("price"));
        insertion.setViews(insertion_found.getInteger("views"));
        insertion.setSeller(insertion_found.getString("seller"));
        insertion.setSize(insertion_found.getString("size"));
        insertion.setStatus(insertion_found.getString("status"));
        insertion.setTimestamp(insertion_found.getString("timestamp"));

        this.closeConnection();
        return insertion;

    }
}