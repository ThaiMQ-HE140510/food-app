package fu.prm391.sample.foodapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fu.prm391.sample.foodapp.adapters.PlaceYourOrderAdapter;
import fu.prm391.sample.foodapp.model.DetailOrder;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.RestaurantModel;

public class PlaceYourOrderActivity extends AppCompatActivity {

    private RecyclerView cartItemsRecyclerView;
    private TextView tvSubtotalAmount;
    private TextView tvDeliveryChargeAmount;
    private TextView tvTotalAmount;
    private LinearLayout buttonPlaceYourOrder;
    private PlaceYourOrderAdapter placeYourOrderAdapter;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore firestore;
    private Login login;
    private String userName;
    private String location;
    private TextView txtAddres;
    private TextView btnGetMoreItem;
    private TextView btnNoteLocation;
    private TextView btnNote;
    private TextView txtDiscount;
    private TextView txtTotalPrice;
    private TextView txtAddMoreLocation;
    private TextView txtNote;
    private LinearLayout layout_note;
    private RestaurantModel restaurantModel;
    private String detail;
    private String site;
    private String note;
    private int count;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_your_order);
        // get RestaurantModel from RestaurantMenuActivity
        restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        location = getIntent().getStringExtra("location");

        //get user login info by google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        // get user info from RestaurantMenuActivity
        login = (Login) getIntent().getSerializableExtra("userInfoLogin");

        if(login != null){
            userName = login.getUserName();
        }
        if(acct != null){
            userName = acct.getEmail();
        }
        // set Title for Acction bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);
        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);
        tvDeliveryChargeAmount = findViewById(R.id.tvDeliveryChargeAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnNoteLocation = findViewById(R.id.btnNoteLocation);
        btnNote = findViewById(R.id.btnNote);
        txtAddres = findViewById(R.id.txtAddress);
        btnGetMoreItem = findViewById(R.id.btnGetMore);
        txtAddMoreLocation = findViewById(R.id.txtAddMoreLocation);
        txtNote = findViewById(R.id.txtNote);
        layout_note = findViewById(R.id.layout_note);

        txtAddres.setText(location);

        btnGetMoreItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonPlaceYourOrder = findViewById(R.id.buttonPlaceYourOrder);
       // switchDelivery = findViewById(R.id.switchDelivery);

        buttonPlaceYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaceYourOrderClick(restaurantModel);
                addOrderItems(restaurantModel.getMenus(),userName);
            }
        });

        initRecyclerView(restaurantModel);
        caculateTotalAmount(restaurantModel);


        txtAddMoreLocation.setVisibility(View.GONE);
        btnNoteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocationInfo(Gravity.BOTTOM);
                if(txtAddMoreLocation == null){
                    txtAddMoreLocation.setVisibility(View.GONE);
                }
            }
        });

        layout_note.setVisibility(View.GONE);
        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote(Gravity.BOTTOM);
            }
        });
    }

    // add order items in firebase
    public void addOrderItems(List<Menu> menus, String userName){
        SimpleDateFormat sdf = new SimpleDateFormat("dd - MM - yyyy");
        String currentDateandTime = sdf.format(new Date());
        firestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firestore.collection("orderItems");
                Map<String,Object> objectMap = new HashMap<>();
                objectMap.put("user",userName);
                objectMap.put("nameStore",restaurantModel.getName());
                objectMap.put("addressStore", restaurantModel.getAddress());
                objectMap.put("amount",totalAmount(restaurantModel));
                objectMap.put("totalDishes",totalDishes(restaurantModel));
                objectMap.put("date",currentDateandTime);
                objectMap.put("menuList",restaurantModel.getMenus());
                objectMap.put("moreLocation",txtAddMoreLocation.getText().toString());
                objectMap.put("note",txtNote.getText().toString());
                objectMap.put("address",location);
                reference.add(objectMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(PlaceYourOrderActivity.this, "Add successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceYourOrderActivity.this, "Add fail", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void onPlaceYourOrderClick(RestaurantModel restaurantModel){

        // start seccess activity
            Intent intent = new Intent(PlaceYourOrderActivity.this,OrderSuccessActivity.class);
            intent.putExtra("RestaurantModel",restaurantModel);
            startActivityForResult(intent,RestaurantMenuActivity.REQUEST_CODE);
    }

    private void initRecyclerView(RestaurantModel restaurantModel){
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeYourOrderAdapter = new PlaceYourOrderAdapter(restaurantModel.getMenus());
        cartItemsRecyclerView.setAdapter(placeYourOrderAdapter);
    }

    private void caculateTotalAmount(RestaurantModel restaurantModel){
        float subTotalAmount = 0f;
        float subTototalAmountbeforDisscount = 0f;
        for (Menu menu :restaurantModel.getMenus()){
            subTototalAmountbeforDisscount += ((menu.getPrice() * menu.getTotalInCart()));
            subTotalAmount = ((menu.getPrice() * menu.getTotalInCart()) -  ((menu.getPrice() * menu.getSale())/100)*menu.getTotalInCart()) + subTotalAmount;
        }
            txtDiscount.setText(String.format("%.2f",subTototalAmountbeforDisscount - subTotalAmount) + " $");
            tvSubtotalAmount.setText(String.format("%.2f",subTototalAmountbeforDisscount) + " $");
            tvDeliveryChargeAmount.setText(String.format("%.2f",restaurantModel.getDelivery_charge()) + " $");
            subTotalAmount += restaurantModel.getDelivery_charge();
            tvTotalAmount.setText(String.format("%.2f",subTotalAmount) + " $");
            txtTotalPrice.setText("$" + String.format("%.2f",subTotalAmount));
    }

    private float totalAmount(RestaurantModel restaurantModel){
        float subTotalAmount = 0f;
        for (Menu menu :restaurantModel.getMenus()){
            subTotalAmount = ((menu.getPrice() * menu.getTotalInCart()) -  ((menu.getPrice() * menu.getSale())/100)*menu.getTotalInCart()) + subTotalAmount;
        }
        subTotalAmount += restaurantModel.getDelivery_charge();
        return subTotalAmount;
    }

    private int totalDishes(RestaurantModel restaurantModel){
        int total = 0;
        for (Menu menu :restaurantModel.getMenus()){
            total += menu.getTotalInCart();
        }
        return total;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RestaurantMenuActivity.REQUEST_CODE){
            setResult(Activity.RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void addLocationInfo(int gravity){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_more_info_location);
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
        TextView txtStoreName = dialog.findViewById(R.id.txtStoreName);
        TextView txtDelivery = dialog.findViewById(R.id.txtDelivery);
        TextView txtDetail = dialog.findViewById(R.id.txtDetail);
        TextView txtSite = dialog.findViewById(R.id.txtSite);
        TextView btnConfirm = dialog.findViewById(R.id.btnConfirm);
        txtStoreName.setText(restaurantModel.getAddress());
        txtDelivery.setText(location);

        txtDetail.setText(detail);
        txtSite.setText(site);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 detail = txtDetail.getText().toString();
                 site = txtSite.getText().toString();
                if(TextUtils.isEmpty(detail) && TextUtils.isEmpty(site)){
                    txtAddMoreLocation.setVisibility(View.GONE);
                }
                else{
                    String detailLocation = detail + ", " + site;
                    txtAddMoreLocation.setVisibility(View.VISIBLE);
                    if(!TextUtils.isEmpty(detail) && !TextUtils.isEmpty(site)){
                        txtAddMoreLocation.setText(detailLocation);
                    }
                    if(!TextUtils.isEmpty(detail) && TextUtils.isEmpty(site)){
                        txtAddMoreLocation.setText(detail);
                    }
                    if(TextUtils.isEmpty(detail) && !TextUtils.isEmpty(site)){
                        txtAddMoreLocation.setText(site);
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void addNote(int gravity){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_note);
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
        TextView txtNoteDetail = dialog.findViewById(R.id.txtNoteDetail);
        TextView txtCount = dialog.findViewById(R.id.txtCount);
        TextView btnComplete = dialog.findViewById(R.id.btnComplete);

        txtNoteDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                count = Integer.parseInt(String.valueOf(s.length()));
                txtCount.setText(count + "");
                if(count == 0 || count > 320){
                    btnComplete.setBackgroundResource(R.drawable.not_complete_bg);
                }
                else{
                    btnComplete.setBackgroundResource(R.drawable.register_bg);
                }

            }
        });

        txtNoteDetail.setText(note);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note = txtNoteDetail.getText().toString();
                if(TextUtils.isEmpty(txtNoteDetail.getText().toString())){
                    layout_note.setVisibility(View.GONE);
                }
                else{
                    layout_note.setVisibility(View.VISIBLE);
                    txtNote.setText(txtNoteDetail.getText().toString());
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}