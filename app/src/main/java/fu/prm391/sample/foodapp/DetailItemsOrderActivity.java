package fu.prm391.sample.foodapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import fu.prm391.sample.foodapp.adapters.OrderItemsAdapter;
import fu.prm391.sample.foodapp.model.DetailOrder;
import fu.prm391.sample.foodapp.model.Menu;

public class DetailItemsOrderActivity extends AppCompatActivity implements OrderItemsAdapter.IDetailOrderItemsClickListener {


    private OrderItemsAdapter adapter;
    private List<DetailOrder> orderItems;
    private TextView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_items_order);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        // get list order items from UserInfoActivity
        orderItems = (List<DetailOrder>) getIntent().getSerializableExtra("orderItems");
        initRecyclerView(orderItems);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void initRecyclerView(List<DetailOrder> orders){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemsAdapter(orders,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void itemClick(DetailOrder detailOrder) {
        Intent intent = new Intent(DetailItemsOrderActivity.this,DetailOrderActivity.class);
        intent.putExtra("detailOrder", detailOrder);
        startActivity(intent);
    }
}