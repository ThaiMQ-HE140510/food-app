package fu.prm391.sample.foodapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.RestaurantModel;

public class OrderSuccessActivity extends AppCompatActivity {


    private TextView buttonDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);
        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        // set Title for Acction bar
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(false);
        buttonDone = findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderSuccessActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}