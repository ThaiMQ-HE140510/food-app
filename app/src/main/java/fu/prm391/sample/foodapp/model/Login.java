package fu.prm391.sample.foodapp.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Login implements Serializable {
    @Exclude private String id;
    private String userName;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String addressHome;
    private String addressCompany;
    private String dateTime;
    private int access;

    public Login() {
    }

    public Login(String userName, String password, String fullName, String phoneNumber,String addressHome,String addressCompany,String dateTime,int access) {
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.addressHome = addressHome;
        this.addressCompany = addressCompany;
        this.dateTime = dateTime;
        this.access = access;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAddressHome() {
        return addressHome;
    }

    public void setAddressHome(String addressHome) {
        this.addressHome = addressHome;
    }

    public String getAddressCompany() {
        return addressCompany;
    }

    public void setAddressCompany(String addressCompany) {
        this.addressCompany = addressCompany;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
