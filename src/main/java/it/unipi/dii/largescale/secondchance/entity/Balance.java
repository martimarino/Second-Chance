package main.java.it.unipi.dii.largescale.secondchance.entity;

import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import org.bson.Document;

public class Balance {

    public static Balance balance;
    String username;
    Double credit;

    public Balance(String username, Double credit){
        this.username = username;
        this.credit = credit;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public String getUsername() {
        return username;
    }

    public Double getCredit() {
        return this.credit;
    }

    public Document toDocument() {
        Document doc = new Document().append("username", this.getUsername())
                .append("credit", this.getCredit());

        return doc;
    }

}
