package fu.prm391.sample.foodapp.adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import fu.prm391.sample.foodapp.R;
import fu.prm391.sample.foodapp.model.DetailOrder;
import fu.prm391.sample.foodapp.model.Menu;

public class DetailItemsAdapter extends RecyclerView.Adapter<DetailItemsAdapter.MyViewHolder> {



    private List<Menu> menus;

    public DetailItemsAdapter(List<Menu> menus){
        this.menus = menus;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public DetailItemsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_order_item_adapter,parent,false);
        return new DetailItemsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailItemsAdapter.MyViewHolder holder, int position) {
        holder.menuName.setText(menus.get(position).getName());
//        holder.txtPrice.setText("$"+String.format("%.2f", menus.get(position).getPrice()));
        holder.menuQty.setText(menus.get(position).getTotalInCart() + "x");

        if(menus.get(position).getSale() == 0){
            holder.txtPriceSale.setVisibility(View.GONE);
            holder.txtPrice.setTextColor(Color.rgb(46,49, 49));
            holder.txtPrice.setText(String.format("%.2f",menus.get(position).getPrice()) + " $");
        }else{
            float subTotalAmount = menus.get(position).getPrice() -  (menus.get(position).getPrice() * menus.get(position).getSale())/100;
            holder.txtPriceSale.setVisibility(View.VISIBLE);
            holder.txtPrice.setTextColor(Color.rgb(149, 165, 166));
            holder.txtPrice.setPaintFlags(holder.txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtPrice.setText(String.format("%.2f",menus.get(position).getPrice()) + " $");
            holder.txtPriceSale.setText(String.format("%.2f",subTotalAmount) + " $");
        }

        Glide.with(holder.thumbImage)
                .load(menus.get(position).getUrl())
                .apply(new RequestOptions().transform(new CenterCrop()).transform(new RoundedCorners(30))).into(holder.thumbImage);
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView menuName;
        private TextView txtPrice;
        private TextView txtPriceSale;
        private TextView menuQty;
        private ImageView thumbImage;


        public MyViewHolder(View view){
            super(view);
            menuName = view.findViewById(R.id.menuName);
            txtPrice = view.findViewById(R.id.txtPrice);
            menuQty = view.findViewById(R.id.menuQty);
            thumbImage = view.findViewById(R.id.thumbImage);
            txtPriceSale = view.findViewById(R.id.txtPriceSale);
        }
    }
}