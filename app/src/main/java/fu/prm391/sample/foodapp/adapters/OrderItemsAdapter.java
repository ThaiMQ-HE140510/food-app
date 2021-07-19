package fu.prm391.sample.foodapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fu.prm391.sample.foodapp.R;
import fu.prm391.sample.foodapp.model.DetailOrder;
import fu.prm391.sample.foodapp.model.Menu;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.MyViewHolder> {



    private List<DetailOrder> orderItems;
    private IDetailOrderItemsClickListener clickListener;

    public OrderItemsAdapter(List<DetailOrder> orderItems, IDetailOrderItemsClickListener clickListener){
        this.orderItems = orderItems;
        this.clickListener = clickListener;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public OrderItemsAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items_reculer,parent,false);
        return new OrderItemsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  OrderItemsAdapter.MyViewHolder holder, int position) {
        holder.txtDate.setText(orderItems.get(position).getDate());
        holder.txtCart.setText(orderItems.get(position).getTotalDishes() + " dishes");
        holder.txtName.setText(orderItems.get(position).getNameStore() + " - " + orderItems.get(position).getAddressStore());
        holder.txtPrice.setText(orderItems.get(position).getAmount() + "$ (spot cash)");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.itemClick(orderItems.get(position));
            }
        });
    }
    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView txtDate;
        private TextView txtName;
        private TextView txtPrice;
        private TextView txtCart;
        public MyViewHolder(View view){
            super(view);
            txtDate = view.findViewById(R.id.txtDate);
            txtName = view.findViewById(R.id.txtName);
            txtPrice = view.findViewById(R.id.txtPrice);
            txtCart = view.findViewById(R.id.txtCart);
        }
    }
    public interface IDetailOrderItemsClickListener{
        public void itemClick(DetailOrder detailOrder);
    }
}


