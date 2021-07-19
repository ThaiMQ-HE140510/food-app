package fu.prm391.sample.foodapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import fu.prm391.sample.foodapp.model.Login;

public class SavedAddressActivity extends AppCompatActivity {

    private Login login;
    private TextView btnBack;
    private EditText txtAddressHome;
    private EditText txtAddressCompany;
    private LinearLayout btnAdd;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_address);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        login = (Login) getIntent().getSerializableExtra("userLogin");
        btnBack = findViewById(R.id.btnBack);
        txtAddressHome = findViewById(R.id.txtAddressHome);
        txtAddressCompany = findViewById(R.id.txtAddressCompany);
        btnAdd = findViewById(R.id.btnAdd);
        firestore = FirebaseFirestore.getInstance();
        btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String addressHome = txtAddressHome.getText().toString();
                    String addressCompany = txtAddressCompany.getText().toString();
                    login.setAddressHome(addressHome);
                    login.setAddressCompany(addressCompany);
                    firestore.collection("items").document(login.getId()).set(login).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SavedAddressActivity.this, "Updated", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        if(login != null){
            txtAddressHome.setText(login.getAddressHome().toString());
            txtAddressCompany.setText(login.getAddressCompany().toString());
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}