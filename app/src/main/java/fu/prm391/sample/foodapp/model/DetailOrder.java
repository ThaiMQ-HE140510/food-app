package fu.prm391.sample.foodapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class DetailOrder implements Parcelable{

    @Exclude private String id;
    private String nameStore;
    private String addressStore;
    private Float amount;
    private int totalDishes;
    private String date;
    private List<Menu> menuList;
    private String account;
    private String address;

    public DetailOrder() {
    }

    protected DetailOrder(Parcel in) {
        nameStore = in.readString();
        addressStore = in.readString();
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readFloat();
        }
        totalDishes = in.readInt();
        date = in.readString();
        menuList = in.createTypedArrayList(Menu.CREATOR);
        account = in.readString();
        address = in.readString();
        id = in.readString();
    }

    public static final Creator<DetailOrder> CREATOR = new Creator<DetailOrder>() {
        @Override
        public DetailOrder createFromParcel(Parcel in) {
            return new DetailOrder(in);
        }

        @Override
        public DetailOrder[] newArray(int size) {
            return new DetailOrder[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameStore() {
        return nameStore;
    }

    public void setNameStore(String nameStore) {
        this.nameStore = nameStore;
    }

    public String getAddressStore() {
        return addressStore;
    }

    public void setAddressStore(String addressStore) {
        this.addressStore = addressStore;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public int getTotalDishes() {
        return totalDishes;
    }

    public void setTotalDishes(int totalDishes) {
        this.totalDishes = totalDishes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameStore);
        dest.writeString(addressStore);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(amount);
        }
        dest.writeInt(totalDishes);
        dest.writeString(date);
        dest.writeTypedList(menuList);
        dest.writeString(account);
        dest.writeString(address);
        dest.writeString(id);
    }
}
