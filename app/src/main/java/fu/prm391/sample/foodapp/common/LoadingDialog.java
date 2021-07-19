package fu.prm391.sample.foodapp.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import fu.prm391.sample.foodapp.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

     public LoadingDialog(Activity activity){
        this.activity = activity;
    }

    public void loadingAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_loading,null));
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }
    public void dissmissDialog(){
        alertDialog.dismiss();
    }
}
