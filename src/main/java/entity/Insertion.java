package main.java.entity;

public class Insertion {

    String id;
    String category;
    String description;
    String gender;
    double price;
    int interested;
    int views;
    String status;
    String color;
    String size;
    String brand;
    String country;
    String image_url;
    String timestamp;
    String seller;
    String sold;

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

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getSold() {
        return this.sold;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

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
                ", sold='" + sold + '\'' +
                '}';
    }
}
