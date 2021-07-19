package fu.prm391.sample.foodapp.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Message implements Serializable {
    @Exclude private String id;
    private String message;
    private String user;
    private String nameItem;
    private String date;
    private int numberOfReport;

    public Message() {
    }

    public Message(String message, String user) {
        this.message = message;
        this.user = user;
    }

    public Message(String id, String message, String user, int numberOfReport) {
        this.id = id;
        this.message = message;
        this.user = user;
        this.numberOfReport = numberOfReport;
    }

    public int getNumberOfReport() {
        return numberOfReport;
    }

    public void setNumberOfReport(int numberOfReport) {
        this.numberOfReport = numberOfReport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

