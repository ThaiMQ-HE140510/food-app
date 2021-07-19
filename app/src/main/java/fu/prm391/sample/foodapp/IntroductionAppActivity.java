package fu.prm391.sample.foodapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class IntroductionAppActivity extends AppCompatActivity {


    private TextView btnBack;
    private TextView txtAppfood;
    private TextView txtTiming;
    private TextView txtDistance;
    private TextView txtDriver;
    private TextView txtRate;
    private TextView txtRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction_app);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        btnBack = findViewById(R.id.btnBack);

        txtAppfood = findViewById(R.id.txtAppfood);
        txtTiming = findViewById(R.id.txtTiming);
        txtDistance = findViewById(R.id.txtDistance);
        txtDriver = findViewById(R.id.txtDriver);
        txtRate = findViewById(R.id.txtRate);
        txtRoute = findViewById(R.id.txtRoute);


        txtAppfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialog(Gravity.BOTTOM,R.layout.dialog_appfood_help);
            }
        });
        txtTiming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialog(Gravity.CENTER,R.layout.dialog_timing_help);
            }
        });
        txtDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialog(Gravity.CENTER,R.layout.dialog_distance_help);
            }
        });
        txtDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialog(Gravity.CENTER,R.layout.dialog_drive_help);
            }
        });
        txtRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialog(Gravity.CENTER,R.layout.dialog_rate_help);
            }
        });
        txtRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialog(Gravity.CENTER,R.layout.dialog_rout_help);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void openHelpAppDialog(int gravity, int dialogId){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogId);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = gravity;
        window.setAttributes(layoutParams);
        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        }else{
            dialog.setCancelable(false);
        }

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}