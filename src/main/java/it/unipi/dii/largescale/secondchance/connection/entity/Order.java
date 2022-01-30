package main.java.it.unipi.dii.largescale.secondchance.connection.entity;

public class Order {

    String buyer;
    String timestamp;
    Insertion insertion;
    boolean reviewed;

    public Order(String buyer, String timestamp, Insertion insertion){

        this.buyer = buyer;
        this.timestamp = timestamp;
        this.insertion = insertion;
        this.reviewed = false;
    }


    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
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
                ", buyer='" + buyer + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", insertion='" + insertion + '\'' +
                '}';
    }
}
