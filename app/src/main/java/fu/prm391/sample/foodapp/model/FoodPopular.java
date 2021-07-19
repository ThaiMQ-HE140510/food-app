package fu.prm391.sample.foodapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class FoodPopular implements Parcelable {
    private String name;
    private float price;
    private String url;
    private String type;
    private float rating;
    private int saved;
    private int order;
    private String describle;

    public FoodPopular() {
    }

    public FoodPopular(String name, float price, String url, String type, float rating, int saved, int order, String describle) {
        this.name = name;
        this.price = price;
        this.url = url;
        this.type = type;
        this.rating = rating;
        this.saved = saved;
        this.order = order;
        this.describle = describle;
    }

    protected FoodPopular(Parcel in) {
        name = in.readString();
        price = in.readFloat();
        rating = in.readFloat();
        saved = in.readInt();
        order = in.readInt();
        url = in.readString();
        type = in.readString();
        describle = in.readString();
    }

    public static final Creator<FoodPopular> CREATOR = new Creator<FoodPopular>() {
        @Override
        public FoodPopular createFromParcel(Parcel in) {
            return new FoodPopular(in);
        }

        @Override
        public FoodPopular[] newArray(int size) {
            return new FoodPopular[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(price);
        dest.writeString(url);
        dest.writeString(describle);
        dest.writeString(type);
        dest.writeInt(order);
        dest.writeInt(saved);
        dest.writeFloat(rating);
    }
}
