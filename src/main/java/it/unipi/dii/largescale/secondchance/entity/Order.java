package main.java.it.unipi.dii.largescale.secondchance.entity;

public class Order {

    String user;
    String timestamp;
    Insertion insertion;
    boolean reviewed;

    public Order(String user, String timestamp, Insertion insertion){

        this.user = user;
        this.timestamp = timestamp;
        this.insertion = insertion;
        this.reviewed = false;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Insertion getInsertion() {
        return insertion;
    }

    public void setInsertion(Insertion insertion) {
        this.insertion = insertion;
    }

    @Override
    public String toString() {
        return "Order{" +
                ", buyer='" + user + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", insertion='" + insertion + '\'' +
                '}';
    }
}
