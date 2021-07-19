package fu.prm391.sample.foodapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import fu.prm391.sample.foodapp.R;
import fu.prm391.sample.foodapp.model.Menu;


// class con của RestaurantMenuActivity
public class PlaceYourOrderAdapter extends RecyclerView.Adapter<PlaceYourOrderAdapter.MyViewHolder> {



    private List<Menu> menuList;

    public PlaceYourOrderAdapter(List<Menu> menuList){
        this.menuList = menuList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PlaceYourOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_order_recycler_row,parent,false);
        return new MyViewHolder(view);

    }

    // set giá trị cho các component
    @Override
    public void onBindViewHolder(@NonNull PlaceYourOrderAdapter.MyViewHolder holder, int position) {
        holder.menuName.setText(menuList.get(position).getName());
        holder.menuPrice.setText("$"+String.format("%.2f", menuList.get(position).getPrice()));
        holder.menuQty.setText(menuList.get(position).getTotalInCart() + "x");
        if(menuList.get(position).getSale() == 0){
            holder.icLeft.setVisibility(View.GONE);
            holder.icRight.setVisibility(View.GONE);
            holder.txtSale.setVisibility(View.GONE);
            holder.icDisccount.setVisibility(View.GONE);
        }
        holder.txtSale.setText(menuList.get(position).getSale() +"%");
        Glide.with(holder.thumbImage)
                .load(menuList.get(position).getUrl())
                .apply(new RequestOptions().transform(new CenterCrop()).transform(new RoundedCorners(30))).into(holder.thumbImage);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }
    // lấy ra các component trên màn hình
    static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView menuName;
        private TextView menuPrice;
        private TextView menuQty;
        private ImageView thumbImage;
        private ImageView icDisccount;
        private TextView icLeft;
        private TextView txtSale;
        private TextView icRight;
        public MyViewHolder(View view){
            super(view);
            menuName = view.findViewById(R.id.menuName);
            menuPrice = view.findViewById(R.id.menuPrice);
            menuQty = view.findViewById(R.id.menuQty);
            thumbImage = view.findViewById(R.id.thumbImage);
            icDisccount = view.findViewById(R.id.icDisccount);
            icLeft = view.findViewById(R.id.icLeft);
            txtSale = view.findViewById(R.id.txtSale);
            icRight = view.findViewById(R.id.icRight);
        }
    }
}