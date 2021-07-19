package fu.prm391.sample.foodapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import fu.prm391.sample.foodapp.model.Login;

public class SupportAppActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView txtIntro;
    private TextView txtUsermMnual;
    private TextView btnEmailSupport;
    private TextView txtEmailAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_app);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        btnBack = findViewById(R.id.btnBack);
        txtIntro = findViewById(R.id.txtIntro);
        txtUsermMnual = findViewById(R.id.txtUsermMnual);
        btnEmailSupport = findViewById(R.id.btnEmailSupport);
        txtEmailAdmin = findViewById(R.id.txtEmailAdmin);

        txtIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupportAppActivity.this,IntroductionAppActivity.class);
                startActivity(intent);
            }
        });
        txtUsermMnual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupportAppActivity.this,TutorialUsingAppActivity.class);
                startActivity(intent);
            }
        });

        btnEmailSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://mail.google.com/mail/u/0/#inbox?compose=XBcJlJnXvwCssnlggVkzMVXZgFBbCrVPnKfndPXRvpQBVdrkMrHsRjVTWCTJpQBGlRpNDlDvJqSRPCNQ");
            }
        });
        txtEmailAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://www.facebook.com/odndo/");
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void goToUrl(String url){
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}