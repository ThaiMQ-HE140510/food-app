package fu.prm391.sample.foodapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import fu.prm391.sample.foodapp.R;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.RestaurantModel;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder> implements Filterable{

    // class con của MainActivity

    private List<RestaurantModel> restaurantModelList;
    private List<RestaurantModel> listFilter;
    private RestaurantListClickListener clickListener;
    private Login login;

    public RestaurantListAdapter(List<RestaurantModel> restaurantModelList, RestaurantListClickListener clickListener, Login login){
        this.restaurantModelList = restaurantModelList;
        this.clickListener = clickListener;
        this.listFilter = new ArrayList<>(restaurantModelList);
        this.login = login;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row,parent,false);
        return new MyViewHolder(view);
    }

    // set giá trị cho các component
    @Override
    public void onBindViewHolder(@NonNull RestaurantListAdapter.MyViewHolder holder, int position) {
        holder.restaurantName.setText(restaurantModelList.get(position).getName());
    //    holder.restaurantAddress.setText("Address: " +restaurantModelList.get(position).getAddress());
    //    holder.restaurantHours.setText("Today's hours: "+restaurantModelList.get(position).getHours().getTodaysHours());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(restaurantModelList.get(position), login);
            }
        });
//        holder.thumbImage.setImageResource(imagesProduct[position]);
        Glide.with(holder.thumbImage)
                .load(restaurantModelList.get(position).getImage())
                .apply(new RequestOptions().transform(new CenterCrop()).transform(new RoundedCorners(20))).into(holder.thumbImage);
    }

//    @Override
//    public int getItemCount() {
//        return restaurantModelList.size();
//    }

    // change size on list filter Search
    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<RestaurantModel> list = new ArrayList<>();
                if(constraint.toString().isEmpty()){
                    listFilter = restaurantModelList;
                } else {
                    for (RestaurantModel restaurantModel : restaurantModelList){
                        if(restaurantModel.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                            list.add(restaurantModel);
                        }
                    }
                    listFilter = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listFilter;
                return filterResults;
            }
            // run on a ui thread
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listFilter = ((List<RestaurantModel>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    // get Filter for function searchs


    // lấy ra các component trên màn hình
    static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView restaurantName;
    //    private TextView restaurantAddress;
    //    private TextView restaurantHours;
        private ImageView thumbImage;
        public MyViewHolder(View view){
            super(view);
            restaurantName = view.findViewById(R.id.restaurantName);
        //    restaurantAddress = view.findViewById(R.id.restaurantAddress);
        //    restaurantHours = view.findViewById(R.id.restaurantHours);
            thumbImage = view.findViewById(R.id.thumbImage);
        }
    }
    public interface RestaurantListClickListener{
        public void onItemClick(RestaurantModel restaurantModel, Login login);
    }
}