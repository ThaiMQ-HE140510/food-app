package fu.prm391.sample.foodapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fu.prm391.sample.foodapp.R;

public class ProductAdsAdapter extends RecyclerView.Adapter<ProductAdsAdapter.MyViewHolder> {

    private int[] icons;
    private String[] Dess;

    public ProductAdsAdapter(int[] icons,String[] Dess){
        this.icons = icons;
        this.Dess = Dess;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_detail_rate,parent,false);
        return new ProductAdsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ProductAdsAdapter.MyViewHolder holder, int position) {
        holder.txtDes.setText(Dess[position]);
        holder.icon.setImageResource(icons[position]);
    }
    @Override
    public int getItemCount() {
        return Dess.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView txtDes;
        private ImageView icon;
        public MyViewHolder(View view){
            super(view);
            icon = view.findViewById(R.id.icon);
            txtDes = view.findViewById(R.id.txtDes);
        }
    }
}
