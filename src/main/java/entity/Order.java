package main.java.entity;

public class Order {

    String seller;
    String buyer;
    String image;
    double price;
    String timestamp;


    public Order(String seller, String buyer, String image, double price, String timestamp){

        this.seller = seller;
        this.buyer = buyer;
        this.image = image;
        this.price = price;
        this.timestamp = timestamp;

    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Order{" +
                " seller='" + seller + '\'' +
                ", buyer='" + buyer + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
