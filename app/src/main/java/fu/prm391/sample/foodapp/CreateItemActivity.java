package fu.prm391.sample.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.RestaurantModel;

public class CreateItemActivity extends AppCompatActivity {


    private EditText txtName;
    private EditText txtPrice;
    private EditText txtImage;
    private EditText txtDescribe;
    private EditText txtType;
    private EditText txtSale;
    private TextView btnCreate;
    private FirebaseFirestore firestore;
    private RestaurantModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);


        txtName = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        txtImage = findViewById(R.id.txtImage);
        txtDescribe = findViewById(R.id.txtDescribe);
        txtType = findViewById(R.id.txtType);
        txtSale = findViewById(R.id.txtSale);
        btnCreate = findViewById(R.id.btnCreate);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        model = getIntent().getParcelableExtra("restaurant");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore = FirebaseFirestore.getInstance();

                String name = (txtName.getText().toString());
                float price = (Float.parseFloat(txtPrice.getText().toString()));
                String url = (txtImage.getText().toString());
                String description = (txtDescribe.getText().toString());
                String type = (txtType.getText().toString());
                Float sale = (Float.parseFloat(txtSale.getText().toString()));
                int order = (200);
                int typrOfSave = 1;
                int saved = 123;
                Float rating = (float) 12.6;
                Menu menu = new Menu(typrOfSave,name,price,url,rating,saved,order,description,type,sale);
                model.getMenus().add(menu);
                firestore.collection("Restaurans").document(model.getId().toString()).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {
                        Toast.makeText(CreateItemActivity.this, "Product Updated", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateItemActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}