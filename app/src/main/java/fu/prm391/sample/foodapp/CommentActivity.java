package fu.prm391.sample.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fu.prm391.sample.foodapp.adapters.MessageAdapter;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.Message;

public class CommentActivity extends AppCompatActivity {

    private MessageAdapter messageAdapter;
    private RecyclerView recyclerViewMessage;
    private List<Message> messages = new ArrayList<>();

    private EditText textEditMessage;
    private ImageView btnSend;
    private Login login;
    private FirebaseFirestore firestore;
    private CollectionReference reference;
    private Menu menu;
    private String username;
    private GoogleSignInAccount acct;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        textEditMessage = findViewById(R.id.textEditMessage);
        btnSend = findViewById(R.id.btnSend);

        menu = getIntent().getParcelableExtra("menu");
        login = (Login) getIntent().getSerializableExtra("user");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);


        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("comments");
        handleListMessageFromFirebase();

        if(login != null){
            username = login.getFullName();
        }
        if(acct != null){
            username = acct.getDisplayName();
        }


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                SimpleDateFormat sdf = new SimpleDateFormat("dd - MM - yyyy");
                String currentDateandTime = sdf.format(new Date());
                CollectionReference reference = firestore.collection("comments");
                Map<String,Object> objectMap = new HashMap<>();
                objectMap.put("user",messages.get(messages.size() - 1).getUser().toString());
                objectMap.put("message", messages.get(messages.size() - 1).getMessage());
                objectMap.put("nameItem", menu.getName());
                objectMap.put("date",currentDateandTime);
                objectMap.put("numberOfReport",0);
                reference.add(objectMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CommentActivity.this, "Add fail", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    private void handleListMessageFromFirebase(){
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("comments");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Message> messageList = new ArrayList<>();
                    QuerySnapshot snapshots = task.getResult();
                    for (QueryDocumentSnapshot doc : snapshots){
                        if(doc.get("nameItem").toString().equals(menu.getName())){
                            Message message = new Message();
                            message.setMessage(doc.get("message").toString());
                            message.setNameItem(doc.get("nameItem").toString());
                            message.setUser(doc.get("user").toString());
                            message.setDate(doc.get("date").toString());
                            message.setNumberOfReport(Integer.parseInt(doc.get("numberOfReport").toString()));
                            message.setId(doc.getId());
                            messageList.add(message);
                        }
                    }
                    messages = messageList;
                    initRecyclerViewMessage(messageList);
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    private void sendMessage() {
        String comment = textEditMessage.getText().toString().trim();
        if(comment == null || TextUtils.isEmpty(comment)){
            return;
        }
        else{
            messages.add(new Message(comment,username));
            recyclerViewMessage.scrollToPosition(messages.size() -1);
            textEditMessage.setText("");
            messageAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerViewMessage(List<Message> list){
        recyclerViewMessage = findViewById(R.id.recyclerviewComments);
        recyclerViewMessage.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter();
        messageAdapter.setData(list,this);
        recyclerViewMessage.setAdapter(messageAdapter);
    }
}