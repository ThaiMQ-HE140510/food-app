package fu.prm391.sample.foodapp;
import fu.prm391.sample.foodapp.adapters.FoodPopularAdapter;
import fu.prm391.sample.foodapp.adapters.IntroductionAdapter;
import fu.prm391.sample.foodapp.adapters.RestaurantListAdapter;
import fu.prm391.sample.foodapp.common.LoadingDialog;
import fu.prm391.sample.foodapp.model.FoodPopular;
import fu.prm391.sample.foodapp.model.Introduction;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.RestaurantModel;
import me.relex.circleindicator.CircleIndicator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // add thêm class con là  RestaurantListAdapter
    private TextView btnSignup;
    private EditText txtUsername;
    private EditText txtPassword;
    private TextView btnLogin;

    private TextView sign_in_button;
    private int RC_SIGN_IN = 0;
    private CollectionReference reference;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore firestore;


    private Timer timer;
    private List<Introduction> list;
    private IntroductionAdapter adapter;
    private ViewPager viewPager;
    private CircleIndicator indicator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        btnSignup = findViewById(R.id.btnSignup);
        sign_in_button = findViewById(R.id.sign_in_button);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("items");

        list = getListIntroduction();
        initViewPager(list);
        autoSlideImages();

        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.loadingAlertDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadingDialog.dissmissDialog();
                    }
                },1000);
                adapterScreen();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // for option signin with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // https://www.youtube.com/watch?v=t-yZUqthDMM&list=LL&index=3
        // https://www.youtube.com/watch?v=qCZFOlCQLL8&list=LL&index=4&t=1217s
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.loadingAlertDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadingDialog.dissmissDialog();
                    }
                },1000);
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });
    }

    // for option signin with google
    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    // for option signin with google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void adapterScreen(){
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Login login;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    String user = txtUsername.getText().toString().trim();
                    String pass = txtPassword.getText().toString().trim();
                    QuerySnapshot snapshots = task.getResult();
                    for (QueryDocumentSnapshot doc : snapshots){
                        if(doc.get("userName").toString().equals(user) && doc.get("password").toString().equals(pass)){
                            login = new Login();
                            login.setId(doc.getId());
                            login.setFullName(doc.get("fullName").toString());
                            login.setPhoneNumber(doc.get("phoneNumber").toString());
                            login.setUserName(doc.get("userName").toString());
                            login.setPassword(doc.get("password").toString());
                            login.setAccess(Integer.parseInt(doc.get("access").toString()));
                            login.setAddressHome(doc.get("addressHome").toString());
                            login.setAddressCompany(doc.get("addressCompany").toString());
                        }
                    }
                    if(login != null){
                        // sent user info to HomeActivity
                        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                        intent.putExtra("inforUser",login);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private List<Introduction> getListIntroduction(){
        List<Introduction> introductions = new ArrayList<>();
        introductions.add(new Introduction("Fresh Food","Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum",R.drawable.img1));
        introductions.add(new Introduction("Fast Delivery","Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum",R.drawable.img2));
        introductions.add(new Introduction("Easy Payment","Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum",R.drawable.img3));
        return introductions;
    }

    private void initViewPager(List<Introduction> list){

        indicator = findViewById(R.id.circle_indicator);
        viewPager = findViewById(R.id.recyclerFoodPopular);
        adapter = new IntroductionAdapter(list);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());
    }

    private void autoSlideImages(){
        if(list == null || list.isEmpty() || viewPager == null){
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
                        int totalItem = list.size() - 1;
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
    // for option signin with google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

}