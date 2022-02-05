package main.java.it.unipi.dii.largescale.secondchance.connection;


import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionMongoDBTest {

    private static ConnectionMongoDB connMongo;

    @BeforeAll
    public static void openConnection() {
       connMongo = new ConnectionMongoDB();
       connMongo.openConnection();
    }


    @Test
    @DisplayName("The user should not log in.")
    void shouldLogInUser() {
        Assertions.assertAll(
                () -> assertTrue(connMongo.checkCredentials("Aaliyah","dcc2bd1379fa18c65989882c2222b19c")),
                () -> assertFalse(connMongo.checkCredentials("","")),
                () -> assertFalse(connMongo.checkCredentials("?","?")),
                () -> assertFalse(connMongo.checkCredentials("!","!"))
        );
    }

    @Test
    @DisplayName("The user should not register.")
    void shouldRegisterUser() {
        Assertions.assertAll(
                () -> assertThrows(NullPointerException.class, () -> {
                    connMongo.registerUser(null);
                })
        );
    }
}