package fu.prm391.sample.foodapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import fu.prm391.sample.foodapp.adapters.ProductAdsAdapter;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.Menu;

public class DetailItemActivity extends AppCompatActivity {


    private TextView txtNameStore;
    private TextView txtNameItem;
    private TextView txtAddress;
    private TextView txtRating;
    private TextView txtSave;
    private TextView txtOrder;
    private ImageView imgDetail;
    private ProductAdsAdapter adapterRes;
    private TextView btnViewMore;

    private Login login;
    private FirebaseFirestore firestore;
    private CollectionReference reference;
    private Menu menu;
    private GoogleSignInAccount acct;
    private GoogleSignInClient mGoogleSignInClient;


    private int[] icons = {R.drawable.tasty,R.drawable.hand,R.drawable.sandwich,R.drawable.stars,R.drawable.food_package};
    private String[] Dess = {"Delicious","Worth lots of money","eat full","Fresh and clean", "Nice packaging"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        menu = getIntent().getParcelableExtra("menu");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        login = (Login) getIntent().getSerializableExtra("login");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);


        txtNameStore = findViewById(R.id.txtNameStore);
        txtNameItem = findViewById(R.id.txtNameItem);
        txtAddress = findViewById(R.id.txtAddress);
        txtRating = findViewById(R.id.txtRating);
        txtSave = findViewById(R.id.txtSave);
        txtOrder = findViewById(R.id.txtOrder);
        imgDetail = findViewById(R.id.imgDetail);

    // for comment item, chạy lần đầu tiên
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("comments");

        txtNameStore.setText(name + " - ");
        txtAddress.setText(address);
        txtNameItem.setText(menu.getName());
        txtRating.setText(menu.getRating() + "");
        txtSave.setText("("+menu.getSave() + "+)");
        txtOrder.setText("(" +menu.getOrder() + "+)");
        Glide.with(imgDetail)
                .load(menu.getUrl())
                .apply(new RequestOptions().transform(new CenterCrop()).transform(new RoundedCorners(15))).into(imgDetail);

        initRecyclerView();
        btnViewMore = findViewById(R.id.btnViewMore);
        btnViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferDataToCommentActivity();
            }
        });
    }


    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        adapterRes = new ProductAdsAdapter(icons,Dess);
        recyclerView.setAdapter(adapterRes);
    }

    private void transferDataToCommentActivity(){
        Intent intent = new Intent(DetailItemActivity.this,CommentActivity.class);
        intent.putExtra("user",login);
        intent.putExtra("menu",menu);
        startActivity(intent);

    }
}