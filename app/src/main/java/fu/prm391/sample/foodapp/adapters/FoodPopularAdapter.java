package fu.prm391.sample.foodapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import fu.prm391.sample.foodapp.R;
import fu.prm391.sample.foodapp.model.FoodPopular;
import fu.prm391.sample.foodapp.model.RestaurantModel;

public class FoodPopularAdapter extends PagerAdapter {

    private List<FoodPopular> foodPopulars;

    public FoodPopularAdapter(List<FoodPopular> foodPopulars) {
        this.foodPopulars = foodPopulars;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.food_popular,container,false);
        ImageView imageView = view.findViewById(R.id.imagePopular);
                Glide.with(imageView)
                .load(foodPopulars.get(position).getUrl())
                .apply(new RequestOptions().transform(new CenterCrop()).transform(new RoundedCorners(30))).into(imageView);
                container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if(foodPopulars != null){
            return foodPopulars.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull  View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull  ViewGroup container, int position, @NonNull  Object object) {
       container.removeView((View)object);
    }
}
