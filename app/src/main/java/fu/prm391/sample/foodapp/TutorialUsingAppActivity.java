package fu.prm391.sample.foodapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TutorialUsingAppActivity extends AppCompatActivity {


    private TextView btnBack;
    private TextView txtCreateAccount;
    private TextView txtOrder;
    private ListView listViewAccount;
    private ListView listViewProduct;
    private int imagesAccount[] = {R.drawable.create_account_1,R.drawable.create_account_2};
    private int imagesProduct[] = {R.drawable.order_p_1,R.drawable.order_p_2,R.drawable.order_p_3};
    private String[] steps = {"Step 1: Select product store",
            "Step 2: Click on the cart icon to buy the product",
            "Step 3: Fill in all the information in the field and Click Button Place your order."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_using_app);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        btnBack = findViewById(R.id.btnBack);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        txtOrder = findViewById(R.id.txtOrder);

        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialogAccount(Gravity.CENTER,R.layout.dialog_create_account_help);
            }
        });
        txtOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelpAppDialogProduct(Gravity.CENTER,R.layout.dialog_order_product_help);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void openHelpAppDialogAccount(int gravity, int dialogId){

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

        listViewAccount = dialog.findViewById(R.id.listImage);
        CustomAdapterCreateAccount customAdapter = new CustomAdapterCreateAccount();
        listViewAccount.setAdapter(customAdapter);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void openHelpAppDialogProduct(int gravity, int dialogId){

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

        listViewProduct = dialog.findViewById(R.id.listImage);
        CustomAdapterOrderProduct customAdapter = new CustomAdapterOrderProduct();
        listViewProduct.setAdapter(customAdapter);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // https://www.youtube.com/watch?v=tLVz5wmNyrw
    // custom item in ListView
    class CustomAdapterCreateAccount extends BaseAdapter {

        @Override
        public int getCount() {
            return imagesAccount.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.custom_layout_image,null);
            ImageView imageView = view.findViewById(R.id.imageView);
            imageView.setImageResource(imagesAccount[position]);
            return view;
        }
    }
    class CustomAdapterOrderProduct extends BaseAdapter {

        @Override
        public int getCount() {
            return imagesProduct.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.custom_layout_order_product,null);
            ImageView imageView = view.findViewById(R.id.imageView);
            TextView textView = view.findViewById(R.id.txtSteps);
            textView.setText(steps[position]);
            imageView.setImageResource(imagesProduct[position]);
            return view;
        }
    }
}