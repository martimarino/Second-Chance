package main.java.entity;

public class Review {

    String reviewer;
    String seller;
    String text;
    String timestamp;
    String title;
    int rating;


    public String getReviewer() {
        return reviewer;
    }

    public Review(String reviewer, String seller, String text, String timestamp, String title, int rating) {
        this.reviewer = reviewer;
        this.seller = seller;
        this.text = text;
        this.timestamp = timestamp;
        this.title = title;
        this.rating = rating;
    }

    public String getSeller() {
        return seller;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public int getRating() {
        return rating;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewer='" + reviewer + '\'' +
                ", seller='" + seller + '\'' +
                ", text='" + text + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                '}';
    }
}