package fu.prm391.sample.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fu.prm391.sample.foodapp.adapters.FoodPopularAdapter;
import fu.prm391.sample.foodapp.adapters.RestaurantListAdapter;
import fu.prm391.sample.foodapp.model.FoodPopular;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.RestaurantModel;
import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends AppCompatActivity implements RestaurantListAdapter.RestaurantListClickListener, LocationListener {

    private RestaurantListAdapter adapterRes;
    private FoodPopularAdapter adapterPopular;
    private EditText searchKey;
    private ImageView imageView;
    private ImageView imgEmail;
    private TextView txtAddress;

    private ViewPager viewPager;
    private CircleIndicator indicator;

    private ImageView gpsBtn;
    private static final int LOCATION_REQUEST_CODE = 100;
    private String[] locationPermissions;
    private LocationManager locationManager;
    private double latitude, longitude;


    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();;
    private CollectionReference collection = firestore.collection("Restaurans");
    private List<RestaurantModel> restaurantModelList;
    private List<FoodPopular> foodPopulars;
    private Timer timer;

    private Login login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        searchKey = findViewById(R.id.txtSearch);
        imageView = findViewById(R.id.imageView);
        imgEmail = findViewById(R.id.imgEmail);
        txtAddress = findViewById(R.id.txtAddress);
        gpsBtn = findViewById(R.id.gpsBtn);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        // get user info from MainActivity
        login = (Login) getIntent().getSerializableExtra("inforUser");

        foodPopulars = getFoodPolular();
        getRestaurantModelListFromFirebase();


        initRecyclerViewPopular(foodPopulars);
        autoSlideImages();


        // getLocation
        getLocation();
        // search filter
        searchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterRes.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialog(Gravity.CENTER,R.layout.dialog_notifi_email);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserInfoActivity.class);
                intent.putExtra("userInfo",login);
                startActivity(intent);
            }
        });

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        // https://www.youtube.com/watch?v=wUete9RNmsc
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }


    private void getLocation(){
        if(checkLocationPermistion()){
            detectLocation();
        }else{
            requestLocationPermission();
        }
    }

    private void insertRestaurantModelListDataToFirebase(){
        firestore = FirebaseFirestore.getInstance();
        collection = firestore.collection("Restaurans");
        restaurantModelList = getRestaurantData();
        for(int i = 0;i<restaurantModelList.size();i++){
            Map<String,Object> objectMap = new HashMap<>();
            objectMap.put("name",restaurantModelList.get(i).getName());
            objectMap.put("address",restaurantModelList.get(i).getAddress());
            objectMap.put("delivery_charge",restaurantModelList.get(i).getDelivery_charge());
            objectMap.put("image",restaurantModelList.get(i).getImage());
            objectMap.put("menus",restaurantModelList.get(i).getMenus());
            collection.add(objectMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private void getRestaurantModelListFromFirebase(){
        List<RestaurantModel> listModel = new ArrayList<>();
        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshots = task.getResult();
                    for (QueryDocumentSnapshot doc : snapshots){
                        RestaurantModel model = new RestaurantModel();
                        model.setId(doc.getId().toString());
                        model.setName(doc.get("name").toString());
                        model.setAddress(doc.get("address").toString());
                        model.setDelivery_charge(Float.parseFloat(doc.get("delivery_charge").toString()));
                        model.setImage(doc.get("image").toString());
                        List<Menu> menus = new ArrayList<>();
                        List<HashMap<String,Object>> menusListHashMap = (List<HashMap<String, Object>>) doc.get("menus");
                        for (int i=0;i<menusListHashMap.size();i++){
                            Menu menu = new Menu();
                            HashMap<String,Object> menuHashmap = menusListHashMap.get(i);
                            menu.setName(menuHashmap.get("name").toString());
                            menu.setPrice(Float.parseFloat(menuHashmap.get("price").toString()));
                            menu.setTotalInCart(Integer.parseInt(menuHashmap.get("totalInCart").toString()));
                            menu.setUrl(menuHashmap.get("url").toString());
                            menu.setRating(Float.parseFloat(menuHashmap.get("rating").toString()));
                            menu.setSaved(Integer.parseInt(menuHashmap.get("saved").toString()));
                            menu.setOrder(Integer.parseInt(menuHashmap.get("order").toString()));
                            menu.setDescrible(menuHashmap.get("describle").toString());
                            menu.setType(menuHashmap.get("type").toString());
                            menu.setSale(Float.parseFloat(menuHashmap.get("sale").toString()));
                            menu.setPriceSale(Float.parseFloat(menuHashmap.get("priceSale").toString()));
                            menus.add(menu);
                        }
                        model.setMenus(menus);
                        listModel.add(model);
                    }
                    initRecyclerView(listModel);
                }
            }
        });
    }

    private void findAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            String address = addresses.get(0).getAddressLine(0);// complete address
            txtAddress.setText(address);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermistion(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
    }

    private void detectLocation() {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG   ).show();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    // put data to screen
    private void initRecyclerView(List<RestaurantModel> restaurantModelList){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        adapterRes = new RestaurantListAdapter(restaurantModelList, this,login);
        recyclerView.setAdapter(adapterRes);
    }

    private void initRecyclerViewPopular(List<FoodPopular> foodPopulars){

        indicator = findViewById(R.id.circle_indicator);
        viewPager = findViewById(R.id.recyclerFoodPopular);
        adapterPopular = new FoodPopularAdapter(foodPopulars);
        viewPager.setAdapter(adapterPopular);
        indicator.setViewPager(viewPager);
        adapterPopular.registerDataSetObserver(indicator.getDataSetObserver());
//        RecyclerView recyclerView = findViewById(R.id.recyclerFoodPopular);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
//        adapterPopular = new FoodPopularAdapter(foodPopulars);
//        recyclerView.setAdapter(adapterPopular);
    }

    private void autoSlideImages(){
        if(foodPopulars == null || foodPopulars.isEmpty() || viewPager == null){
            return;
        }
        // init timer
        if(timer == null){
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = viewPager.getCurrentItem();
                        int totalItem = foodPopulars.size() - 1;
                        if(currentItem < totalItem){
                            currentItem++;
                            viewPager.setCurrentItem(currentItem);
                        }else{
                            viewPager.setCurrentItem(0);
                        }
                    }
                });

            }
        },500,2500);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    // get list data from file json data
    private List<RestaurantModel> getRestaurantData(){
        InputStream is = getResources().openRawResource(R.raw.restaurant);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1){
                writer.write(buffer, 0,n);
            }
        }
        catch (Exception ex){

        }
        String jsonStr = writer.toString();
        Gson gson = new Gson();
        RestaurantModel[] restaurantModels =  gson.fromJson(jsonStr,RestaurantModel[].class);
        List<RestaurantModel> restList = Arrays.asList(restaurantModels);
        return restList;
    }

    private List<FoodPopular> getFoodPolular(){
        InputStream is = getResources().openRawResource(R.raw.food_popular);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1){
                writer.write(buffer, 0,n);
            }
        }
        catch (Exception ex){

        }
        String jsonStr = writer.toString();
        Gson gson = new Gson();
        FoodPopular[] foodPopular  =  gson.fromJson(jsonStr,FoodPopular[].class);
        List<FoodPopular> foodPopulars = Arrays.asList(foodPopular);
        return foodPopulars;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // loaction detect

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        findAddress();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // gps location disablle
        Toast.makeText(this, "Please enable location...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        // permisstion allowed
                        detectLocation();

                    }else{
                        Toast.makeText(this, "Location permission is necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // khi chọn vào 1 cửa hàng ăn, sẽ đóng gói dữ liệu của cửa hàng đó và gửi đi xử lí
    @Override
    public void onItemClick(RestaurantModel restaurantModel, Login login) {
        // đóng gói dữ liệu tranfer
        Intent intent = new Intent(HomeActivity.this,RestaurantMenuActivity.class);
        intent.putExtra("RestaurantModel",restaurantModel);
        // sent user infor to  RestaurantMenuActivity
        intent.putExtra("userInfoLogin",login);
        intent.putExtra("location",txtAddress.getText().toString());
        // Gửi dữ liệu đi 1 chiều
        startActivity(intent);
    }

    private void openHelpAppDialog(int gravity, int dialogId){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogId);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = gravity;
        window.setAttributes(layoutParams);
        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        }else{
            dialog.setCancelable(false);
        }
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}