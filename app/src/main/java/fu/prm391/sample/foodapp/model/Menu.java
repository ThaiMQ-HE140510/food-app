package fu.prm391.sample.foodapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Menu implements Parcelable {
    private int typeOfSubmit;
    private String name;
    private float price;
    private int totalInCart;
    private String url;
    private float rating;
    private int saved;
    private int order;
    private String describle;
    private String type;
    private float sale;
    private float priceSale;


    public Menu(String name, float price, int totalInCart, String url, float rating, int save, String type) {
        this.name = name;
        this.price = price;
        this.totalInCart = totalInCart;
        this.url = url;
        this.rating = rating;
        this.saved = save;
        this.order = order;
        this.describle = describle;
        this.type = type;
    }

    public Menu() {
    }

    public Menu(int typeOfSubmit, String name, float price, String url, float rating, int saved, int order, String describle, String type, float sale) {
        this.typeOfSubmit = typeOfSubmit;
        this.name = name;
        this.price = price;
        this.url = url;
        this.rating = rating;
        this.saved = saved;
        this.order = order;
        this.describle = describle;
        this.type = type;
        this.sale = sale;
    }

    protected Menu(Parcel in) {
        name = in.readString();
        price = in.readFloat();
        url = in.readString();
        totalInCart = in.readInt();
        rating = in.readFloat();
        saved = in.readInt();
        order= in.readInt();
        describle = in.readString();
        type = in.readString();
        sale = in.readFloat();
    }


    public int getTypeOfSubmit() {
        return typeOfSubmit;
    }

    public void setTypeOfSubmit(int typeOfSubmit) {
        this.typeOfSubmit = typeOfSubmit;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public float getSale() {
        return sale;
    }

    public void setSale(float sale) {
        this.sale = sale;
    }

    public float getPriceSale() {
        return priceSale;
    }

    public void setPriceSale(float priceSale) {
        this.priceSale = priceSale;
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };


    public int getTotalInCart() {
        return totalInCart;
    }

    public void setTotalInCart(int totalInCart) {
        this.totalInCart = totalInCart;
    }

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

    public int getSave() {
        return saved;
    }

    public void setSave(int save) {
        this.saved = save;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDescible() {
        return describle;
    }

    public void setDescible(String descible) {
        this.describle = descible;
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
        dest.writeInt(totalInCart);
        dest.writeFloat(rating);
        dest.writeInt(saved);
        dest.writeInt(order);
        dest.writeString(describle);
        dest.writeString(type);
        dest.writeFloat(sale);
    }
}