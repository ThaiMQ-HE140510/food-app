package fu.prm391.sample.foodapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import fu.prm391.sample.foodapp.R;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Message> messages;
    private Context context;
    private FirebaseFirestore firestore;

    public void setData(List<Message> list,Context context){
        this.messages = list;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_messsage,parent,false);
        return new MessageAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MessageAdapter.MyViewHolder holder, int position) {

        firestore = FirebaseFirestore.getInstance();

        Message message = messages.get(position);
        if(message == null){
            return;
        }
        else{
            holder.txtMessage.setText(message.getMessage());
            holder.txtUser.setText(message.getUser());
            holder.txtDate.setText(message.getDate());
        }
        holder.btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportComment(Gravity.BOTTOM,message);
            }
        });
    }

    private void reportComment(int gravity,Message message){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_report);
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
        TextView btnRp = dialog.findViewById(R.id.btnRp);
        btnRp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int numberOfReport = message.getNumberOfReport();
                numberOfReport += 1;
                message.setNumberOfReport(numberOfReport);
                firestore.collection("comments").document(message.getId().toString()).set(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Thank you! We have received feedback...", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
                if(message.getNumberOfReport() > 3){
                    deleteComment(message);
                }
                dialog.dismiss();
            }
        });
        
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteComment(Message message){
        firestore.collection("comments").document(message.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        if(messages.size() == 0){
               return 0;
        }
        else{
               return messages.size();
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView txtUser;
        private TextView txtMessage;
        private TextView txtDate;
        private ImageView btnReport;
        public MyViewHolder(View view){
            super(view);
            txtUser = view.findViewById(R.id.txtUser);
            txtMessage = view.findViewById(R.id.txtMessage);
            txtDate = view.findViewById(R.id.txtDate);
            btnReport = view.findViewById(R.id.btnReport);
        }
    }
}
