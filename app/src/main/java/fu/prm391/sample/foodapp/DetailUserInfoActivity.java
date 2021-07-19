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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import fu.prm391.sample.foodapp.model.Login;

public class DetailUserInfoActivity extends AppCompatActivity {

    private EditText txtPhone;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtPass;
    private TextView btnEdit;
    private TextView btnUpdate;
    private FirebaseFirestore firestore;

    private Login login;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user_info);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        login = (Login) getIntent().getSerializableExtra("detailInforUser");
        txtPhone = findViewById(R.id.txtPhone);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        btnEdit = findViewById(R.id.btnEdit);
        btnUpdate = findViewById(R.id.btnUpdate);
        txtPhone.setEnabled(false);



        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPhone.setEnabled(true);
            }
        });

        if(login != null){
            firestore = FirebaseFirestore.getInstance();
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = txtPhone.getText().toString();
                    login.setPhoneNumber(phone);
                    firestore.collection("items").document(login.getId()).set(login).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(DetailUserInfoActivity.this,HomeActivity.class);
                            Toast.makeText(DetailUserInfoActivity.this, "Updated", Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }
                    });

                }
            });
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if(login != null){
            txtName.setText(login.getFullName().toString());
            txtPhone.setText(login.getPhoneNumber().toString());
            txtEmail.setText(login.getUserName().toString());
            txtPass.setText(login.getPassword().toString());
        }
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
            txtName.setText(personName);
            txtEmail.setText(personEmail);

//            Glide.with(this).load(String.valueOf(personPhoto)).into(imageView);
        }
    }

    // https://www.youtube.com/watch?v=t-yZUqthDMM&list=LL&index=3
}