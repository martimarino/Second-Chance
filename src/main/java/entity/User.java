package main.java.entity;

import main.java.connection.*;
import main.java.utils.*;

public class User implements GeneralUser {

    String email;
    String username;
    String password;
    String name;
    String country;
    String city;
    String address;
    String suspended;
    String rating;
    double balance;

    public User(String email, String username, String password, String name, String country, String city, String address, String suspended, String rating, double balance) {

        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.suspended = suspended;
        this.rating = rating;
        this.balance = balance;
    }

    public User()
    {
        this.username = null;
        this.password = null;
        this.name = null;
        this.country = null;
        this.city = null;
        this.address = null;
        this.email = null;
    }
    public User(String username)
    {
        ConnectionMongoDB conn = new ConnectionMongoDB();
        User user = conn.findUserDetails(username);
        if(user.getSuspended().equals("Y"))
            Utility.infoBox("You can't login because your account has been suspended.", "Error", "Account suspended!");
        else {
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.country = user.getCountry();
            this.address = user.getAddress();
            this.city = user.getCity();
            this.name = user.getName();
            this.suspended = user.getSuspended();
            this.rating = user.getRating();
            this.balance = user.getBalance();
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSuspended(String suspended) { this.suspended = suspended; }

    public void setRating(String rating) { this.rating = rating; }

    public void setBalance(Double balance){ this.balance = balance;}

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getSuspended() { return suspended; }

    public String getRating() { return rating; }

    public Double getBalance() {  return this.balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                        ", suspended'" + suspended + '\'' +
                '}';
    }


}