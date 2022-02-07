package main.java.it.unipi.dii.largescale.secondchance.connection;

import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class ConnectionNeo4jDB implements AutoCloseable
{
    private Driver driver;
    String uri = "neo4j://127.0.0.1:7687";
    String user = "neo4j";
    String password = "2nd-chance";
    public static ConnectionNeo4jDB connNeo;

    public void open()
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close()
    {
        driver.close();
    }

    public boolean addUser(final User u)
    {
        this.open();
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (u:User {username: $username, country: $country})",
                        parameters("username", u.getUsername(),
                                "country", u.getCountry()));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            this.close();
        }
    }

    public boolean addInsertion(final Insertion i)
    {
        this.open();
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (i:Insertion {uniq_id: $uniq_id, category: $category," +
                                "gender: $gender})",
                        parameters( "uniq_id", i.getId(), "category", i.getCategory(),
                                "gender", i.getGender()));
                return null;
            });
            this.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Utility.printTerminal("Cannot create new insertion node");
            return false;
        }
    }

    public void followUser(String follower, String followed) {
        this.open();
        System.out.println("USER_FOLLOWER: " + follower + "USER_FOLLOWED : " + follower);
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (u:User),(v) " +
                                "WHERE u.username = $username1 AND v.username = $username2 " +
                                "CREATE (u)-[:FOLLOWS]->(v)",
                        parameters( "username1", follower, "username2", followed));
                return null;
            });
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unfollowUser(String unfollower, String unfollowed) {
        this.open();
        try ( Session session = driver.session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (u:User)-[rel:FOLLOWS]->(v)  " +
                                "WHERE u.username = $username1 AND v.username = $username2 " +
                                "DELETE rel",
                        parameters( "username1", unfollower, "username2", unfollowed));
                return null;
            });
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getSuggestedUsers(String username, String country, int k) {
        this.open();
        ArrayList<String> suggestions = new ArrayList<>();
        try (Session session = driver.session()) {

            List<String> similar = session.readTransaction((TransactionWork<List<String>>) tx -> {

                Result result = tx.run( "MATCH (u:User)-[:FOLLOWS]->(m)-[:FOLLOWS]->(others) " +

                                "WHERE u.username = $username AND u.country = $country AND others.country = $country " +
                                "AND NOT (u)-[:FOLLOWS]->(others) " +
                                "RETURN others.username as SuggUsers " +
                                "LIMIT $k",
                        parameters("username", username,
                                "country", country,
                                "k", k));
                while (result.hasNext()) {
                    Record r = result.next();
                    suggestions.add(r.get("SuggUsers").asString());
                }
                return suggestions;
            });
            Utility.printTerminal("NEO4j\n" + similar);
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

            List<String> insertions = session.readTransaction((TransactionWork<List<String>>) tx -> {

                Result result = tx.run( "MATCH (u:User)-[:FOLLOWS]->(m)-[:POSTED]->(i:Insertion) " +
                                "WHERE u.username = $username " +
                                "RETURN i.uniq_id as SuggIns " +
                                "LIMIT $k",
                        parameters("username", username,
                                "k", k));

                while (result.hasNext()) {
                    Record r = result.next();
                    System.out.println("SUGGINS: " + r.get("SuggIns").asString());
                    followed.add(r.get("SuggIns").asString());
                }
                return followed;
            });
            Utility.printTerminal("NEO4j\n" + insertions);
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return followed;

    }

    public boolean likeInsertion(String username, String insertion_id) {

        this.open();
        System.out.println("INSERTION NEO : " + insertion_id);

        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (u:User {username: $username})" +
                                "CREATE (u)-[rel:INTERESTED]->(i: Insertion {uniq_id: $id})", parameters("username", username,
                                "id", insertion_id));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            this.close();
        }
    }

    public boolean dislikeInsertion(String username, String insertion_id) {

        this.open();

        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (u:User { username: $username})-[r:INTERESTED]->(i :Insertion { uniq_id: $id})\n" +
                                "DELETE r", parameters("username", username,
                                "id", insertion_id));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            this.close();
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
            this.close();
            return relation;
        }
    }

    public boolean deleteInsertion(String uniq_id) {
        this.open();

        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (i:Insertion{uniq_id : $uniq_id}) DETACH DELETE i;",
                        parameters("uniq_id", uniq_id));
                return null;
            });
            return true;
        }catch(Exception e)
        {
            return false;
        }finally{
            this.close();
        }
    }

    public boolean checkIfFollows(String us1, String us2) {

        this.open();
        Boolean check;

        try(Session session = driver.session()) {
            check = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run(
                        "MATCH (a:User {username: $username1})-[r:FOLLOWS]->(b:User {username: $username2}) " +
                                "RETURN r",
                        parameters( "username1", us1,
                                "username2", us2));

                return result.hasNext();
            });
            this.close();
            return check;
        }
    }

    public void followUnfollowButton(String text, String us1, String us2) {

        switch (text) {
            case "Follow":
                followUser(us1, us2);
                break;

            case "Unfollow":
                unfollowUser(us1, us2);
                break;

            default:
                break;
        }

    }

    public boolean createPostedRelationship(String node1, String node2) {

        this.open();
        try(Session session = driver.session()) {
                session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (u:User),(i:Insertion) " +
                                "WHERE u.username = $username AND i.uniq_id = $id " +
                                "CREATE (u)-[:POSTED]->(i)",
                        parameters( "username", node1,
                                "id", node2));
                return null;
            });
            this.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utility.printTerminal("Cannot create POSTED relationship");
        return false;

    }

    /* ********** USER SOCIAL FUNCTIONALITIES ********** */

    public ArrayList<String> retrieveFollowersByUser(String user) {

        this.open();
        ArrayList<String> followers = new ArrayList<>();

        try (Session session = driver.session()) {

            List<String> follow = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run( "MATCH (u:User) <- [r:FOLLOWS] - (u1:User) " +
                                "WHERE u.username = $username " +
                                "RETURN u1.username as name ",
                        parameters( "username", user));

                while(result.hasNext())
                {
                    Record r = result.next();
                    followers.add(r.get("name").asString());
                }
                return followers;
            });
            if(!follow.isEmpty())
                Utility.printTerminal(follow.get(0));
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return followers;
    }

    public ArrayList<String> retrieveFollowingByUser(String user) {

        this.open();
        ArrayList<String> following = new ArrayList<>();

        try (Session session = driver.session()) {

            List<String> follow = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run( "MATCH (u:User) - [r:FOLLOWS] -> (u1:User) " +
                                "WHERE u.username = $username " +
                                "RETURN u1.username as name ",
                        parameters( "username", user));

                while(result.hasNext())
                {
                    Record r = result.next();
                    following.add(r.get("name").asString());
                }
                return following;
            });
            if(!follow.isEmpty())
                Utility.printTerminal(follow.get(0));
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return following;
    }

    public ArrayList<String> retrieveFollowedInsertionByUser(String user) {

        this.open();
        ArrayList<String> followed_ins = new ArrayList<>();

        try (Session session = driver.session()) {

            List<String> follow = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run( "MATCH (u:User) - [r:INTERESTED] -> (i:Insertion)" +
                                "WHERE u.username = $username " +
                                "RETURN i.uniq_id as uniq_id",
                        parameters( "username", user));

                while(result.hasNext())
                {
                    Record r = result.next();
                    followed_ins.add(r.get("uniq_id").asString());
                }
                return followed_ins;
            });
            if(!followed_ins.isEmpty())
                Utility.printTerminal("*************** NEO4j FOLLOWED INSERTIONS ***************\n"
                                        + followed_ins.get(0));
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return followed_ins;
    }

    public boolean deleteInsertionNeo4J(String id) {
        this.open();

        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (u:Insertion {uniq_id: $id})" +
                                "DETACH DELETE u", parameters("id", id));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            this.close();
        }
    }
}
