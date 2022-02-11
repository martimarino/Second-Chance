import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.Document;
import java.text.*;
import java.util.*;

public class Main {


    public static void modifyRatingWithReviews(MongoDatabase database){

        MongoCollection<Document> user = database.getCollection("user");

        //build users list
        ArrayList<Document> users = new ArrayList<>();
        MongoCursor<Document> cursor;
        cursor = user.find().iterator();
        while(cursor.hasNext())
            users.add(cursor.next());

        int i = 0;
        for (Document d : users) {      //for every user

            //list takes all the embedded reviews
            List<Document> list = null;
            list = d.getList("reviews", Document.class);

            if(list == null)    //if no reviews go to next user
                continue;

            //calculate avg rating of this user
            Double avg = 0.0;
            int sum = 0;

            for(int j=0; j < list.size(); j++) {
                sum += list.get(j).getInteger("rating");
                System.out.println("SUM: " + sum);
            }

            avg = Double.valueOf(sum)/Double.valueOf(list.size());
    System.out.println("AVG: " + avg);

                            // change to do
            // {"rating" = avg}
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("rating", avg);
            // {$set: {"rating": avg}}
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);

            BasicDBObject query = new BasicDBObject();
            query.put("username", d.getString("username"));

            user.updateOne(query, updateObject);

            i++;

    System.out.println("**************************");

        }

    }

    public static void embedReviewIntoUser(MongoDatabase database){

        MongoCollection<Document> user = database.getCollection("user");
        MongoCollection<Document> review = database.getCollection("review");

        // embed review into user

        ArrayList<Document> reviews = new ArrayList<>();
        MongoCursor<Document> cursor;
        cursor = review.find().iterator();
        while(cursor.hasNext())
            reviews.add(cursor.next());

        int i = 0;
        for (Document d : reviews) {
            System.out.println("i: " + i + " " + d);
            Document rew = new Document().append("timestamp", d.getString("timestamp")).append("reviewer", d.getString("reviewer")).append("title", d.getString("title")).append("text", d.getString("text")).append("rating", d.getInteger("rating"));
            BasicDBObject query = new BasicDBObject();

            query.put("username", d.getString("seller"));

            System.out.println("i: " + i + " " + d);
            System.out.println("i: " + i + " " + rew);

            BasicDBObject push_data = new BasicDBObject("$push", new BasicDBObject("reviews", rew));
            user.findOneAndUpdate(query, push_data);
            //review.deleteOne(cur);
            i++;
        }

    }


    public static void main(String[] args) throws ParseException {

        ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("local");

        embedReviewIntoUser(database);
        modifyRatingWithReviews(database);

    }

}
