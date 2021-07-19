package fu.prm391.sample.foodapp.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.invoke.LambdaConversionException;
import java.util.ArrayList;
import java.util.List;

import fu.prm391.sample.foodapp.CreateItemActivity;
import fu.prm391.sample.foodapp.DetailItemActivity;
import fu.prm391.sample.foodapp.HomeActivity;
import fu.prm391.sample.foodapp.R;
import fu.prm391.sample.foodapp.RestaurantMenuActivity;
import fu.prm391.sample.foodapp.TutorialUsingAppActivity;
import fu.prm391.sample.foodapp.model.Login;
import fu.prm391.sample.foodapp.model.Menu;
import fu.prm391.sample.foodapp.model.RestaurantModel;


// class con của RestaurantMenuActivity
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MyViewHolder> implements Filterable {



    private List<Menu> menuList;
    private List<Menu> temps;
    private String resName;
    private RestaurantModel model;
    private Login login;
    private String resAddress;
    private MenuListClickListener clickListener;
    private ItemDetailClickListener detailClickListener;
    private FirebaseFirestore firestore;
    private Context context;
    private GoogleSignInAccount account;

    public MenuListAdapter(List<Menu> menuList, MenuListClickListener clickListener, String resName, String resAddress, ItemDetailClickListener detailClickListener,RestaurantModel model,Login login,Context context,GoogleSignInAccount account){
        this.menuList = menuList;
        this.resName = resName;
        this.resAddress = resAddress;
        this.detailClickListener = detailClickListener;
        this.clickListener = clickListener;
        this.temps = new ArrayList<>(menuList);
        this.model = model;
        this.login = login;
        this.context = context;
        this.account = account;
    }

    public void updateData(List<Menu> menuList){
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_recycler_row,parent,false);
        return new MyViewHolder(view);

    }

    // set giá trị cho các component
    @Override
    public void onBindViewHolder(@NonNull MenuListAdapter.MyViewHolder holder, int position) {
        holder.menuName.setText(menuList.get(position).getName());
        if(menuList.get(position).getSale() != 0){
         //   holder.txtSale.setText(menuList.get(position).getSale() + "%");
            Float price = menuList.get(position).getPrice() - (menuList.get(position).getPrice()*menuList.get(position).getSale()/100);
            holder.menuPriceSale.setText(String.format("%.2f",price) + " $");
            holder.menuPrice.setText(menuList.get(position).getPrice()+ " $");
            holder.icRight.setVisibility(View.VISIBLE);
            holder.menuPriceSale.setVisibility(View.VISIBLE);
            holder.menuPrice.setTextColor(Color.rgb(149, 165, 166));
            holder.menuPrice.setPaintFlags(holder.menuPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.menuPrice.setText(menuList.get(position).getPrice()+ " $");
            holder.icRight.setVisibility(View.GONE);
            holder.menuPriceSale.setVisibility(View.GONE);
            holder.menuPrice.setTextColor(Color.rgb(46,49, 49));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailClickListener.onItemClick(menuList.get(position),resName,resAddress,login);
            }
        });
        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu menu  = menuList.get(position);
                menu.setTotalInCart(1);
                clickListener.onAddToCartClick(menu);
                holder.addMoreLayout.setVisibility(View.VISIBLE);
                holder.addToCartButton.setVisibility(View.GONE);
                holder.tvCount.setText(menu.getTotalInCart()+"");
            }
        });
        holder.imageMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu menu  = menuList.get(position);
                int total = menu.getTotalInCart();
                total--;
                if(total > 0 ) {
                    menu.setTotalInCart(total);
                    holder.tvCount.setText(total +"");
                    clickListener.onUpdateCartClick(menu);
                } else {
                    holder.addMoreLayout.setVisibility(View.GONE);
                    holder.addToCartButton.setVisibility(View.VISIBLE);
                    menu.setTotalInCart(total);
                    clickListener.onRemoveCartClick(menu);
                }
            }
        });
        holder.imageAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu menu  = menuList.get(position);
                int total = menu.getTotalInCart();
                total++;
                // giới hạn số lượng sản phẩm mua
                if(total <= 20 ) {
                    menu.setTotalInCart(total);
                    clickListener.onUpdateCartClick(menu);
                    holder.tvCount.setText(total +"");
                }
            }
        });
        Glide.with(holder.thumbImage)
                .load(menuList.get(position).getUrl())
                .apply(new RequestOptions().transform(new CenterCrop()).transform(new RoundedCorners(20))).into(holder.thumbImage);

        firestore = FirebaseFirestore.getInstance();

        holder.btnDeleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog .Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure to Delete item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Menu menu = menuList.get(position);
                        menuList.remove(menu);
                        model.setMenus(menuList);
                        firestore.collection("Restaurans").document(model.getId().toString()).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Delete Success", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
            }
        });
        holder.btnUpdateCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu menu = menuList.get(position);
                updateItem(Gravity.CENTER,menu);
            }
        });

        if(login != null){
            if(login.getAccess() == 0){
                holder.addToCartButton.setVisibility(View.VISIBLE);
                holder.btnDeleteCart.setVisibility(View.GONE);
                holder.btnUpdateCart.setVisibility(View.GONE);
            }else{
                holder.addToCartButton.setVisibility(View.GONE);
                holder.btnDeleteCart.setVisibility(View.VISIBLE);
                holder.btnUpdateCart.setVisibility(View.VISIBLE);
            }
        }
        if(account != null){
            holder.addToCartButton.setVisibility(View.VISIBLE);
            holder.btnDeleteCart.setVisibility(View.GONE);
            holder.btnUpdateCart.setVisibility(View.GONE);
        }
        if(login == null && account == null){
            holder.addToCartButton.setVisibility(View.GONE);
            holder.btnDeleteCart.setVisibility(View.VISIBLE);
            holder.btnUpdateCart.setVisibility(View.VISIBLE);
        }
    }

    private void updateItem(int gravity,Menu menu){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_item_layout);
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

         EditText txtName = dialog.findViewById(R.id.txtName);
         EditText txtPrice = dialog.findViewById(R.id.txtPrice);
         EditText txtImage = dialog.findViewById(R.id.txtImage);
         EditText txtDescribe = dialog.findViewById(R.id.txtDescribe);
         EditText txtType = dialog.findViewById(R.id.txtType);
         EditText txtSale = dialog.findViewById(R.id.txtSale);

         txtName.setText(menu.getName().toString());
         txtPrice.setText(menu.getPrice() + "");
         txtImage.setText(menu.getUrl().toString());
         txtDescribe.setText(menu.getDescrible().toString());
         txtType.setText(menu.getType().toString());
         txtSale.setText(menu.getSale() + "");

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView btnUpdate = dialog.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.setName(txtName.getText().toString());
                menu.setPrice(Float.parseFloat(txtPrice.getText().toString()));
                menu.setUrl(txtImage.getText().toString());
                menu.setDescrible(txtDescribe.getText().toString());
                menu.setType(txtType.getText().toString());
                menu.setSale(Float.parseFloat(txtSale.getText().toString()));
                model.setMenus(menuList);
                firestore.collection("Restaurans").document(model.getId().toString()).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notifyDataSetChanged();
                    }
                });

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    // Filter result for action search
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Menu> list = new ArrayList<>();
                if(constraint.toString().isEmpty()){
                    list.addAll(temps);
                } else {
                    for (Menu menu : temps){
                        if(menu.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                            list.add(menu);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }
            // run on a ui thread
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                menuList.clear();
                menuList.addAll( (List<Menu>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    // lấy ra các component trên màn hình
    static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView menuName;
        private TextView menuPrice;
        private TextView addToCartButton;
        private ImageView thumbImage;
        private ImageView imageMinus;
        private ImageView imageAddOne;
        private TextView tvCount;
        private LinearLayout addMoreLayout;
        private TextView menuPriceSale;
        private ImageView icRight;
        private ImageView btnUpdateCart;
        private ImageView btnDeleteCart;

        public MyViewHolder(View view){
            super(view);
            menuName = view.findViewById(R.id.menuName);
            menuPrice = view.findViewById(R.id.menuPrice);
            addToCartButton = view.findViewById(R.id.addToCartButton);
            thumbImage = view.findViewById(R.id.thumbImage);
            imageMinus = view.findViewById(R.id.imageMinus);
            imageAddOne = view.findViewById(R.id.imageAddOne);
            tvCount = view.findViewById(R.id.tvCount);
            icRight = view.findViewById(R.id.icRight);
            btnUpdateCart = view.findViewById(R.id.btnUpdateCart);
            btnDeleteCart = view.findViewById(R.id.btnDeleteCart);
            menuPriceSale = view.findViewById(R.id.menuPriceSale);
            addMoreLayout = view.findViewById(R.id.addMoreLayout);
        }
    }

    public interface MenuListClickListener{
        public void onAddToCartClick(Menu menu);
        public void onUpdateCartClick(Menu menu);
        public void onRemoveCartClick(Menu menu);
    }

    public interface ItemDetailClickListener{
        public void onItemClick(Menu menu,String name,String address,Login login);
    }
}