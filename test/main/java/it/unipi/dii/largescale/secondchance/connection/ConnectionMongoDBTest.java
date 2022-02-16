package main.java.it.unipi.dii.largescale.secondchance.connection;

import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import org.bson.Document;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionMongoDBTest {

    private static ConnectionMongoDB connMongo;

    @BeforeAll
    public static void openConnection() {
       connMongo = new ConnectionMongoDB();
       connMongo.openConnection();
    }

    @Test
    @DisplayName("shouldLogInUser")
    void shouldLogInUser() {
        Assertions.assertAll(
                () -> assertTrue(connMongo.checkCredentials("Aaliyah","dcc2bd1379fa18c65989882c2222b19c")),
                () -> assertFalse(connMongo.checkCredentials("","")),
                () -> assertFalse(connMongo.checkCredentials("?","?")),
                () -> assertFalse(connMongo.checkCredentials("!","!"))
        );
    }

    @Test
    @DisplayName("shouldRegisterUser")
    void shouldRegisterUser() {
        Assertions.assertAll(
                () -> assertThrows(NullPointerException.class, () -> {
                    connMongo.registerUser(null);
                })
        );
    }


    @Test
    @DisplayName("findUserByUsername")
    void findUserByUsername() {
        Assertions.assertAll(
                () -> assertThrows(NoSuchElementException.class, () -> {
                    connMongo.findUserByUsername("");
                })
        );
    }

    @Test
    @DisplayName("followedUserInsertions")
    void followedUserInsertions() {
        Assertions.assertAll(
                () -> assertThrows(NullPointerException.class, () -> {
                    connMongo.followedUserInsertions(null);
                })
        );
    }

    @Test
    @DisplayName("findUserByFilters")
    void findUserByFilters() {

        ArrayList<Document> arrayTest;

        Assertions.assertThrows(NumberFormatException.class, () -> {
            connMongo.findUserByFilters("","");
        });

        arrayTest =  connMongo.findUserByFilters("India","0");
        Assertions.assertEquals(0, arrayTest.size());
    }


    @Test
    @DisplayName("findInsertionByFilters")
    void findInsertionByFilters() {

        Assertions.assertThrows(NumberFormatException.class, () -> {
            connMongo.findInsertionByFilters("", "", "", "", "", "");
        });
    }

    @Test
    @DisplayName("findInsertionBySeller")
    void findInsertionBySeller() {
        ArrayList<Document> arrayTest;

        arrayTest =  connMongo.findInsertionBySeller("");
        Assertions.assertEquals(0, arrayTest.size());
    }

    @Test
    @DisplayName("buyCurrentInsertion")
    void buyCurrentInsertion() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            connMongo.buyCurrentInsertion("", null);
        });
    }

    @Test
    @DisplayName("verifyUserInDB")
    void verifyUserInDB() {
        Assertions.assertEquals(null, connMongo.verifyUserInDB("", true));
        Assertions.assertEquals(null, connMongo.verifyUserInDB("", false));
    }

    @Test
    @DisplayName("verifyInsertionInDB")
    void verifyInsertionInDB() {
        Assertions.assertEquals(null, connMongo.verifyInsertionInDB("", true));
        Assertions.assertEquals(null, connMongo.verifyInsertionInDB("", false));
    }

    @Test
    @DisplayName("findInsertionDetails")
    void findInsertionDetails() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            connMongo.findInsertionDetails("");
        });
    }

    @Test
    @DisplayName("findMultipleInsertionDetails")
    void findMultipleInsertionDetails() {
        ArrayList<Insertion> arrayTest;

        arrayTest =  connMongo.findMultipleInsertionDetails("");
        Assertions.assertEquals(0, arrayTest.size());
    }

    @Test
    @DisplayName("findInsertionDetailsNeo4J")
    void findInsertionDetailsNeo4J() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            connMongo.findInsertionDetailsNeo4J(null);
        });
    }

    @Test
    @DisplayName("addInsertion")
    void addInsertion() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            connMongo.addInsertion(null);
        });
    }

    @Test
    @DisplayName("getReviewsByUser")
    void getReviewsByUser() {
    }

    @Test
    @DisplayName("deleteInsertionMongo")
    void deleteInsertionMongo() {
    }

}
