package main.java.it.unipi.dii.largescale.secondchance.entity;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Insertion {

    String id;
    String brand;
    String category;
    String color;
    String country;
    String description;
    String gender;
    String image_url;
    int interested;
    double price;
    String seller;
    String size;
    String status;
    String timestamp;
    int views;

    public Insertion(){}

    public Insertion(String id, String category, String description,
                     String gender, double price, int interested,
                     int views, String status, String color, String size,
                     String brand, String country, String image_url,
                     String timestamp, String seller){
        this.id = id;
        this.category = category;
        this.description = description;
        this.gender = gender;
        this.price = price;
        this.interested = interested;
        this.views = views;
        this.status = status;
        this.color = color;
        this.size = size;
        this.brand = brand;
        this.country = country;
        this.image_url = image_url;
        this.timestamp = timestamp;
        this.seller = seller;
    }

    public static Insertion toInsertion(Document found) {

        Insertion ins = new Insertion(found.get("_id").toString(), found.getString("category"), found.getString("description"), found.getString("gender"),
                found.getDouble("price"), found.getInteger("interested"), found.getInteger("views"), found.getString("status"), found.getString("color"),
                found.getString("size"), found.getString("brand"), found.getString("country"), found.getString("image_url"), found.getString("timestamp"),
                found.getString("seller"));

        return ins;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setInterested(int interested) {
        this.interested = interested;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCountry() {
        return country;
    }

    public String getGender() {
        return gender;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getInterested() {
        return interested;
    }

    public Integer getViews() {
        return views;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {return id;}

    public String getImage_url() {
        return image_url;
    }

    public String getSeller() {
        return seller;
    }

    public String getSize() {
        return size;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static Document toDocument(Insertion insertion) {

        Document ins = new Document()
                .append("_id", new ObjectId(insertion.getId())).
                append("category", insertion.getCategory()).
                append("description", insertion.getDescription()).
                append("gender", insertion.getGender())
                .append("price", insertion.getPrice())
                .append("interested", insertion.getInterested())
                .append("views", insertion.getViews())
                .append("status", insertion.getStatus())
                .append("color", insertion.getColor())
                .append("size", insertion.getSize())
                .append("country", insertion.getCountry())
                .append("image_url", insertion.getImage_url())
                .append("timestamp", insertion.getTimestamp())
                .append("seller", insertion.getSeller());

        return ins;

    }

    @Override
    public String toString() {
        return "Insertion{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", gender=" + gender +
                ", price=" + price +
                ", interested=" + interested +
                ", views=" + views +
                ", status='" + status + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", brand='" + brand + '\'' +
                ", country='" + country + '\'' +
                ", image_url='" + image_url + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", seller='" + seller + '\'' +
                '}';
    }
}
