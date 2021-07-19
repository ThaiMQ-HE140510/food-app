package fu.prm391.sample.foodapp;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import fu.prm391.sample.foodapp.databinding.ActivitySignUpBinding;
import fu.prm391.sample.foodapp.model.CountryData;
import fu.prm391.sample.foodapp.model.Login;

public class SignUpActivity extends AppCompatActivity {


    private EditText txtFullName;
    private EditText txtUsername;
    private EditText txtPassword;
    private TextView btnSignup;
    private TextView btnSignin;
    private ImageView profileIv;

    private FirebaseFirestore firestore;

    /* phone authentication
     build binding
    private ActivityMainBinding binding; */

    // if code send fail, will used to resent code OTP
    // https://www.youtube.com/watch?v=W8eGh6vKKR8
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;
    private String mVerificationId; // will hold OTP/Verification code
    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth firebaseAuth;
    // progress dialog
    private ProgressDialog pd;
    private ActivitySignUpBinding binding;

    // permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLARY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    // permisstion arrays


    private String[] cameraPermistions;
    private String[] storagePermistions;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();


        // for phone authentication

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.phoneLl.setVisibility(View.VISIBLE);
        binding.codeLl.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();

        // init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull  String verificationId, @NonNull  PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG,"onCodeSent: "+verificationId);
                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();
                // hide phone layout, show code layout
                binding.phoneLl.setVisibility(View.GONE);
                binding.codeLl.setVisibility(View.VISIBLE);
                Toast.makeText(SignUpActivity.this, "Verification code sent...", Toast.LENGTH_SHORT).show();
                binding.codeSentDescription.setText("Please ty the verification code we sent \nto "+binding.phoneEt.getText().toString().trim());


            }
        };

        binding.phoneContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneEt.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(SignUpActivity.this,"Please enter phone number...",Toast.LENGTH_SHORT).show();
                }else{
                    startPhoneVerification(phone);
                }
            }
        });

        binding.resentCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneEt.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(SignUpActivity.this,"Please enter phone number...",Toast.LENGTH_SHORT).show();
                }else{
                    resentVerificationCode(phone,forceResendingToken);
                }
            }
        });

        txtUsername = findViewById(R.id.txtUsername);
        btnSignin = findViewById(R.id.btnSignin);
        txtFullName = findViewById(R.id.txtFullName);
        txtPassword = findViewById(R.id.txtPassword);
        profileIv = findViewById(R.id.profileIv);
        btnSignup = findViewById(R.id.btnSignup);

        // https://www.youtube.com/watch?v=qCZFOlCQLL8&list=LL&index=3&t=1197s
        firestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firestore.collection("items");

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeOTP = binding.codeEt.getText().toString().trim();
                Map<String,Object> objectMap = new HashMap<>();
                if(!isValidateConditionDataInfo()){
                    return;
                }
                else if(TextUtils.isEmpty(codeOTP)){
                    Toast.makeText(SignUpActivity.this,"Please enter verification code...",Toast.LENGTH_SHORT).show();
                }

                else{
                    verifyPhoneNumberWithCode(mVerificationId,codeOTP);
                }
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // https://www.youtube.com/watch?v=wUete9RNmsc
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();

            }
        });
        // init permisstion array
        cameraPermistions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermistions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }


    private void startPhoneVerification(String phone) {
        pd.setMessage("Verify phone number");
        pd.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mcallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void resentVerificationCode(String phone,PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resenting code");
        pd.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mcallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Verifying code");
        pd.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");
        firestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firestore.collection("items");
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // successfully
                        Map<String,Object> objectMap = new HashMap<>();
                        objectMap.put("fullName",txtFullName.getText().toString());
                        objectMap.put("userName",txtUsername.getText().toString());
                        objectMap.put("password",txtPassword.getText().toString());
                        objectMap.put("phoneNumber",binding.phoneEt.getText().toString().trim());
                        objectMap.put("access",0);
                        objectMap.put("addressHome","");
                        objectMap.put("addressCompany","");
                        reference.add(objectMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(SignUpActivity.this, "Add successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Add fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                        pd.dismiss();
                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(SignUpActivity.this,"Logged In as: "+phone,Toast.LENGTH_SHORT).show();
                        // start profile activity
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        // failed sigining in
                        pd.dismiss();
                        Toast.makeText(SignUpActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImagePickDialog() {
        String[] options = {"camera","Gallaty"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    if(which == 0){
                        if(checkCameraPermission()){
                            pickFormCamera();

                        }
                        else{
                            requestCameraPermistion();
                        }

                    }
                    else{
                        if(checkStoragePermission()){
                            pickFromGallary();
                        }
                        else{
                            requestStoragePermission();
                        }

                    }
            }
        }).show();
    }

    private void pickFromGallary(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLARY_CODE);
    }

    private void pickFormCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp_Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermistions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermistion(){
        ActivityCompat.requestPermissions(this,cameraPermistions,CAMERA_REQUEST_CODE);
    }

    private boolean isValidateConditionDataInfo(){
        if(TextUtils.isEmpty(txtFullName.getText().toString())){
            txtFullName.setError("Please input your full name: ");
            return false;
        }
        else if(TextUtils.isEmpty(txtUsername.getText().toString())){
            txtUsername.setError("Please input your username: ");
            return false;
        }
        else if(TextUtils.isEmpty(txtPassword.getText().toString())){
            txtPassword.setError("Please input your password: ");
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        // permisstion allowed
                        pickFormCamera();

                    }else{
                        Toast.makeText(this, "Camera permissions are necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        // permisstion allowed
                        pickFromGallary();

                    }else{
                        Toast.makeText(this, "Storage permission is necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLARY_CODE){
                image_uri =  data.getData();
                profileIv.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                profileIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}