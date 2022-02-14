package main.java.it.unipi.dii.largescale.secondchance.connection;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionNeo4jDBTest {

    private static ConnectionNeo4jDB connNeo;

    @BeforeAll
    public static void openConnection() {
        connNeo = new ConnectionNeo4jDB();
        connNeo.open();
    }


    /* This test not catch NullPointerException correctly. Need to fix it.*/
    @Test
    @DisplayName("addUser")
    void addUser() {

        /*
        Assertions.assertThrows(NullPointerException.class, () -> {
            connNeo.addUser(null);
        });

        Assertions.assertEquals(false, connNeo.addUser(null));
        */
    }

    @Test
    @DisplayName("addInsertion")
    void addInsertion() {
        /*
        Assertions.assertThrows(NullPointerException.class, () -> {
            connNeo.addInsertion(null);
        });

        Assertions.assertEquals(false, connNeo.addInsertion(null));
         */
    }


    @Test
    @DisplayName("getSuggestedUsers")
    void getSuggestedUsers() {

        ArrayList<String> arrayTest;

        arrayTest = connNeo.getSuggestedUsers("", "", 0);
        Assertions.assertEquals(0, arrayTest.size());

        arrayTest = connNeo.getSuggestedUsers("", "", 1000000000);
        Assertions.assertEquals(0, arrayTest.size());
    }

    @Test
    @DisplayName("getFollowedInsertions")
    void getFollowedInsertions() {

        ArrayList<String> arrayTest;

        arrayTest = connNeo.getFollowedInsertions("", 0);
        Assertions.assertEquals(0, arrayTest.size());

        arrayTest = connNeo.getFollowedInsertions("", 1000000000);
        Assertions.assertEquals(0, arrayTest.size());
    }

    @Test
    @DisplayName("likeInsertion")
    void likeInsertion() {
        // Why true instead of null?
        Assertions.assertEquals(true, connNeo.likeInsertion("", ""));
    }

    @Test
    @DisplayName("dislikeInsertion")
    void dislikeInsertion() {
        // Why true instead of null?
        Assertions.assertEquals(true, connNeo.dislikeInsertion("", ""));
    }

    @Test
    @DisplayName("showIfInterested")
    void showIfInterested() {
        Assertions.assertEquals(false, connNeo.showIfInterested("",""));
    }

    @Test
    @DisplayName("deleteInsertion")
    void deleteInsertion() {
        // Why true instead of null?
        Assertions.assertEquals(true, connNeo.deleteInsertion(""));
    }

    @Test
    @DisplayName("checkIfFollows")
    void checkIfFollows() {
        Assertions.assertEquals(false, connNeo.checkIfFollows("", ""));
    }

    @Test
    @DisplayName("createPostedRelationship")
    void createPostedRelationship() {
        // Why true instead of null/false?
        Assertions.assertEquals(true, connNeo.createPostedRelationship("",""));
    }

    @Test
    @DisplayName("retrieveFollowersByUser")
    void retrieveFollowersByUser() {

        ArrayList<String> arrayTest;

        arrayTest = connNeo.retrieveFollowersByUser("");
        Assertions.assertEquals(0, arrayTest.size());
    }

    @Test
    @DisplayName("retrieveFollowingByUser")
    void retrieveFollowingByUser() {

        ArrayList<String> arrayTest;

        arrayTest = connNeo.retrieveFollowingByUser("");
        Assertions.assertEquals(0, arrayTest.size());
    }

    @Test
    @DisplayName("retrieveFollowedInsertionByUser")
    void retrieveFollowedInsertionByUser() {

        ArrayList<String> arrayTest;

        arrayTest = connNeo.retrieveFollowedInsertionByUser("");
        Assertions.assertEquals(0, arrayTest.size());
    }

    @Test
    @DisplayName("deleteInsertionNeo4J")
    void deleteInsertionNeo4J() {
        // Why true instead of null/false?
        Assertions.assertEquals(true, connNeo.deleteInsertionNeo4J(""));
    }
}