package fu.prm391.sample.foodapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import fu.prm391.sample.foodapp.adapters.DetailItemsAdapter;
import fu.prm391.sample.foodapp.adapters.PlaceYourOrderAdapter;
import fu.prm391.sample.foodapp.model.DetailOrder;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.RestaurantModel;

public class DetailOrderActivity extends AppCompatActivity {


    private DetailOrder detailOrder;
    private TextView btnBack;
    private TextView txtNameStore;
    private TextView txtAddressStore;
    private TextView tvSubtotalAmount;
    private TextView tvDeliveryChargeAmount;
    private TextView txtDiscount;
    private TextView tvTotalAmount;
    private TextView txtCodeOrder;
    private TextView txtName;
    private TextView txtPhone;
    private TextView txtAddress;
    private TextView txtDate;
    private RecyclerView recyclerviewOrderItems;
    private DetailItemsAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        detailOrder = (DetailOrder) getIntent().getParcelableExtra("detailOrder");
        initEntity();

        txtNameStore.setText(detailOrder.getNameStore());
        txtAddressStore.setText(detailOrder.getAddressStore());
        tvTotalAmount.setText(detailOrder.getAmount().toString()+" $");
        txtCodeOrder.setText(detailOrder.getId());
        txtName.setText(detailOrder.getAccount());
        txtAddress.setText(detailOrder.getAddress());
        txtDate.setText(detailOrder.getDate());
        calculateTotalAmount(detailOrder.getMenuList());
        initRecyclerView(detailOrder.getMenuList());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initEntity(){
        btnBack = findViewById(R.id.btnBack);
        txtNameStore = findViewById(R.id.txtNameStore);
        txtAddressStore = findViewById(R.id.txtAddressStore);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);
        tvDeliveryChargeAmount = findViewById(R.id.tvDeliveryChargeAmount);
        txtDiscount = findViewById(R.id.txtDiscount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        txtCodeOrder = findViewById(R.id.txtCodeOrder);
        txtPhone = findViewById(R.id.txtPhone);
        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtDate = findViewById(R.id.txtDate);
        recyclerviewOrderItems = findViewById(R.id.recyclerviewOrderItems);
    }


    private void calculateTotalAmount(List<Menu> Menus){
        float subTotalAmount = 0f;
        float subTototalAmountbeforDisscount = 0f;
        for (Menu menu : Menus){
            subTototalAmountbeforDisscount += ((menu.getPrice() * menu.getTotalInCart()));
            subTotalAmount = ((menu.getPrice() * menu.getTotalInCart()) -  ((menu.getPrice() * menu.getSale())/100)*menu.getTotalInCart()) + subTotalAmount;
        }
        txtDiscount.setText(String.format("%.2f",subTototalAmountbeforDisscount - subTotalAmount) + " $");
        tvSubtotalAmount.setText(String.format("%.2f",subTototalAmountbeforDisscount) + " $");
        tvDeliveryChargeAmount.setText(5 +" $");
        subTotalAmount += 4;
        tvTotalAmount.setText(String.format("%.2f",subTotalAmount) + " $");
    }

    private void initRecyclerView(List<Menu> menus){
        recyclerviewOrderItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailItemsAdapter(menus);
        recyclerviewOrderItems.setAdapter(adapter);
    }
}