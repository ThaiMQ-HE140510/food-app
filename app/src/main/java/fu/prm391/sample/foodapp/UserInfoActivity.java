package fu.prm391.sample.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fu.prm391.sample.foodapp.model.CollectionsSortByDate;
import fu.prm391.sample.foodapp.model.DetailOrder;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.RestaurantModel;

public class UserInfoActivity extends AppCompatActivity {

    private TextView btnClickInfo;
    private Login login;
    private CollectionReference reference;
    private FirebaseFirestore firestore;
    private GoogleSignInAccount acct;
    private GoogleSignInClient mGoogleSignInClient;
    private String email;
    private CircleMenu circleMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        login = (Login) getIntent().getSerializableExtra("userInfo");
        btnClickInfo = findViewById(R.id.btnClickInfo);

 // https://www.youtube.com/watch?v=m3uUph2Rb7Q&t=148s
        circleMenu = findViewById(R.id.circle_menu);
        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.mipmap.menu, R.mipmap.close)
                .addSubMenu(Color.parseColor("#88b9f4"), R.mipmap.home)
                .addSubMenu(Color.parseColor("#7cd15d"), R.mipmap.location)
                .addSubMenu(Color.parseColor("#f45930"), R.mipmap.signout)
                .addSubMenu(Color.parseColor("#a55fb1"), R.mipmap.cart)
                .addSubMenu(Color.parseColor("#fb905a"), R.mipmap.help).setOnMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int index) {
                switch (index){
                    case 0:
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },1000);
                        break;
                    case 1:
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(UserInfoActivity.this,SavedAddressActivity.class);
                                intent.putExtra("userLogin", login);
                                startActivity(intent);
                            }
                        },1000);
                        break;
                    case 2:
                        Handler handler5 = new Handler();
                        handler5.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                signOut();
                            }
                        },1000);
                        break;
                    case 3:
                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handleItemsOrderOfUser();
                            }
                        },1000);

                        break;
                    case 4:
                        Handler handler3 = new Handler();
                        handler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent1 = new Intent(UserInfoActivity.this,SupportAppActivity.class);
                                startActivity(intent1);
                            }
                        },1000);
                        break;
                }

            }
        });



        // get login with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if(login != null){
            email = login.getUserName().toString();
        }
        acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct != null) {
            /*
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
            */
            String personEmail = acct.getEmail();
            email = personEmail;
        }

        btnClickInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this,DetailUserInfoActivity.class);
                intent.putExtra("detailInforUser",login);
                startActivity(intent);
            }
        });
    }

    // sent list order Items to DetailItemsOrderActivity
    private void handleItemsOrderOfUser(){
        List<DetailOrder> items = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("orderItems");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshots = task.getResult();
                    for (QueryDocumentSnapshot doc : snapshots){
                        if(doc.get("user").toString().equals(email)){
                            DetailOrder model = new DetailOrder();
                            model.setId(doc.getId());
                            model.setAccount(doc.get("user").toString());
                            model.setNameStore(doc.get("nameStore").toString());
                            model.setAddressStore(doc.get("addressStore").toString());
                            model.setAmount(Float.parseFloat(doc.get("amount").toString()));
                            model.setTotalDishes(Integer.parseInt(doc.get("totalDishes").toString()));
                            model.setDate(doc.get("date").toString());
                            model.setAddress(doc.get("address").toString());
                            List<Menu> menuList = new ArrayList<>();
                            List<HashMap<String,Object>> menusListHashMap = (List<HashMap<String, Object>>) doc.get("menuList");
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
                                menuList.add(menu);
                            }
                            model.setMenuList(menuList);
                            items.add(model);
                        }
                    }
                    Collections.sort(items, new CollectionsSortByDate());
                    Intent intent = new Intent(UserInfoActivity.this,DetailItemsOrderActivity.class);
                    intent.putExtra("orderItems", (Serializable) items);
                    startActivity(intent);
                }
            }
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UserInfoActivity.this, "Sign out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserInfoActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}