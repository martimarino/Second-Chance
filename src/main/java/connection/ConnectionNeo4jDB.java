package main.java.connection;

import main.java.entity.*;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class ConnectionNeo4jDB implements AutoCloseable {
    private Driver driver;
    String uri = "neo4j://127.0.0.1:7687";
    String user = "neo4j";
    String password = "2nd-chance";

    public void open() {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void addUser(final User u) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (u:User {username: $username, country: $country})",
                        parameters("username", u.getUsername(),
                                "country", u.getCountry()));
                return null;
            });
        }
    }

    public void addInsertion(final Insertion i) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (i:Insertion {uniq_id: $uniq_id, category: $category," +
                                "gender: $gender})",
                        parameters("uniq_id", i.getId(), "category", i.getCategory(),
                                "gender", i.getGender()));
                return null;
            });
        }
    }

    public void followUser(User follower, User followed) {

    }

    public void unfollowUser(User unfollower, User unfollowed) {

    }

    public void likeAnInsertion(User u, Insertion i) {

    }

    public ArrayList<String> getSuggestedUsers(String username, String country, int k) {
        this.open();
        ArrayList<String> suggestions = new ArrayList<>();
        try (Session session = driver.session()) {

            String u = "72q0jrBM81n7vySAL";
            String c = "Austria";

            List<String> similar = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run("MATCH (u:User)-[:FOLLOWS]->(m)<-[:FOLLOWS]-(others) " +
                                "WHERE u.username = $username AND u.country = $country AND others.country = $country " +
                                "AND NOT (u)-[:FOLLOWS]->(others) " +
                                "RETURN others.username as SuggUsers " +
                                "LIMIT $k",
                        parameters("username", u,
                                "country", c,
                                "k", k));
/*
                List<String> similar = session.readTransaction((TransactionWork<List<String>>) tx -> {
                    Result result = tx.run( "MATCH (u:User)-[:FOLLOWS]->(m)<-[:FOLLOWS]-(others) " +
                                "WHERE u.username = $username AND u.country = $country AND others.country = $country " +
                                "AND NOT (u)-[:FOLLOWS]->(others) " +
                                "RETURN others.username as SuggUsers " +
                                "LIMIT $k",
                            parameters( "username", username,
                                    "country", country,
                                    "k", k));
*/
                while (result.hasNext()) {
                    Record r = result.next();
                    suggestions.add(r.get("SuggUsers").asString());
                }
                return suggestions;
            });
            System.out.println("*************** NEO4j ***************");
            System.out.println(similar);
            System.out.println("*************************************");
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }


    public ArrayList<String> getFollowedInsertions(String username, int k) {

        this.open();
        ArrayList<String> followed = new ArrayList<>();
        try (Session session = driver.session()) {

            String u = "72q0jrBM81n7vySAL";
            //String c = "Austria";

            List<String> insertions = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run("MATCH (u:User)-[:FOLLOWS]->(m)-[:POSTED]-(i:Insertion) " +
                                "WHERE u.username = $username " +
                                "RETURN i.uniq_id as SuggIns " +
                                "LIMIT $k",
                        parameters("username", u,
                                "k", k));

                while (result.hasNext()) {
                    Record r = result.next();
                    followed.add(r.get("SuggIns").asString());
                }
                return followed;
            });
            System.out.println("*************** NEO4j ***************");
            System.out.println(insertions);
            System.out.println("*************************************");
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return followed;

    }

    public void setFavouriteInsertion(String username, String insertion_id) {

        this.open();

        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (u:User {username: $username})" +
                                "CREATE (u)-[rel:INTERESTED]->(i: Insertion {uniq_id: $id})", parameters("username", username,
                                "id", insertion_id));
                return null;
            });

            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dislikeInsertion(String username, String insertion_id) {

        this.open();

        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (u:User { username: $username})-[r:INTERESTED]->(i :Insertion { uniq_id: $id})\n" +
                                "DELETE r", parameters("username", username,
                                "id", insertion_id));
                return null;
            });

            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean showIfInterested(String username, String insertion_id) {

        this.open();

        try (Session session = driver.session()) {
            Boolean relation = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run("MATCH (u:User { username: $username})-[r:INTERESTED]->(i :Insertion { uniq_id: $id})\n" +
                        "RETURN r", parameters("username", username,
                        "id", insertion_id));

                return result.hasNext();
            });
            System.out.println(relation);

            return relation;
        }
    }
}
