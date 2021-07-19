package fu.prm391.sample.foodapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import fu.prm391.sample.foodapp.adapters.MenuListAdapter;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.RestaurantModel;

// muốn search cần implements Filterable

public class RestaurantMenuActivity extends AppCompatActivity implements MenuListAdapter.MenuListClickListener, MenuListAdapter.ItemDetailClickListener {

    // add thêm class con là MenuListAdapter
    // trong class con MenuListAdapter đã xử lí từng phần nhỏ, sau đó add vào layout lớn
    private List<Menu> menuList = new ArrayList<>();
    private TextView buttonCheckout;
    private LinearLayout btnCreateNew;
    private MenuListAdapter menuListAdapter;
    private List<Menu> intemsInCartList;
    private int totalItemInCart = 0;
    private Login login;
    private SearchView searchView;
    public static int REQUEST_CODE = 27;
    private RestaurantModel restaurantModel;
    private String location;
    private GoogleSignInAccount acct;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);
        // get RestaurantModel from HomeActivity
        restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
       // menu = getIntent().getParcelableExtra("menuCreate");

        // get userLogin from HomeActivity
        login = (Login) getIntent().getSerializableExtra("userInfoLogin");
        location = getIntent().getStringExtra("location");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);
        initRecyclerView();
        // get user info from Homactivity


        btnCreateNew = findViewById(R.id.btnCreateNew);
        btnCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantMenuActivity.this,CreateItemActivity.class);
                intent.putExtra("restaurant",restaurantModel);
                startActivity(intent);
            }
        });

        buttonCheckout = findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intemsInCartList == null || intemsInCartList.size() <= 0){
                    Toast.makeText(RestaurantMenuActivity.this, "Please add some item to cart", Toast.LENGTH_SHORT).show();
                    return;
                }
                // chuyển sang màn hình PlaceYourOrderActivity và đóng gói dữ liệu
                restaurantModel.setMenus(intemsInCartList);
                Intent intent = new Intent(RestaurantMenuActivity.this,PlaceYourOrderActivity.class);
                intent.putExtra("RestaurantModel",restaurantModel);
                // sent user info to RestaurantMenuActivity
                intent.putExtra("userInfoLogin",login);
                intent.putExtra("location",location);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        // get login with google


        if(login != null){
            if(login.getAccess() == 0){
                btnCreateNew.setVisibility(View.GONE);
                buttonCheckout.setVisibility(View.VISIBLE);
            } else{
                buttonCheckout.setVisibility(View.GONE);
                btnCreateNew.setVisibility(View.VISIBLE);
            }
        }
        if(acct != null){
                btnCreateNew.setVisibility(View.GONE);
                buttonCheckout.setVisibility(View.VISIBLE);
        }
        if(login == null && acct == null){
            buttonCheckout.setVisibility(View.GONE);
            btnCreateNew.setVisibility(View.VISIBLE);
        }
        if(intemsInCartList == null || intemsInCartList.size() == 0){
            buttonCheckout.setVisibility(View.GONE);
        }
        else{
            buttonCheckout.setVisibility(View.VISIBLE);
        }
    }

    // RecyclerView create on activity_restaurant_menu.xml
    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // chia layout làm 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        menuList = restaurantModel.getMenus();
//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(itemDecoration);
        menuListAdapter = new MenuListAdapter(menuList,this,restaurantModel.getName(),restaurantModel.getAddress(),this,restaurantModel,login,this,acct);
        recyclerView.setAdapter(menuListAdapter);
    }

    private void setTextCart(){
        totalItemInCart = 0;
        for (Menu m : intemsInCartList){
            totalItemInCart = totalItemInCart + m.getTotalInCart();
        }
        if(intemsInCartList.size() == 0 || intemsInCartList == null){
            buttonCheckout.setVisibility(View.GONE);
        }
        buttonCheckout.setText(totalItemInCart + " - Items");
    }

    // sent user info to PlaceYourOrderActivity
    @Override
    public void onAddToCartClick(Menu menu) {
        if(intemsInCartList == null){
            intemsInCartList = new ArrayList<>();
        }
        buttonCheckout.setVisibility(View.VISIBLE);
        intemsInCartList.add(menu);
        setTextCart();
    }

    @Override
    public void onUpdateCartClick(Menu menu) {
        if(intemsInCartList.contains(menu)){
            int index = intemsInCartList.indexOf(menu);
            intemsInCartList.remove(index);
            intemsInCartList.add(index,menu);
            setTextCart();
        }
    }

    @Override
    public void onRemoveCartClick(Menu menu) {
        if(intemsInCartList.contains(menu)){
            intemsInCartList.remove(menu);
            setTextCart();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                // to do something
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            // to do something
            finish();
        }
    }
    // option for function search
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bg,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // action_search chính là trỏ đến item trong menu_bg.xml
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                menuListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onItemClick(Menu menu, String name, String address, Login login) {
        // đóng gói dữ liệu tranfer
        Intent intent = new Intent(RestaurantMenuActivity.this,DetailItemActivity.class);
        intent.putExtra("menu", menu);
        // sent user infor to  RestaurantMenuActivity
        intent.putExtra("name",name);
        intent.putExtra("address",address);
        intent.putExtra("login",login);
        // Gửi dữ liệu đi 1 chiều
        startActivity(intent);
    }
}