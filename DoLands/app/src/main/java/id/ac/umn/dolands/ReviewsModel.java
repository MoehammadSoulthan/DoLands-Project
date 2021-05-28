package id.ac.umn.dolands;

public class ReviewsModel {
    public String imageUrl, username, message;

    public ReviewsModel () {}

    public ReviewsModel(String imageUrl, String username, String message) {
        this.imageUrl = imageUrl;
        this.username = username;
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
