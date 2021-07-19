package fu.prm391.sample.foodapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import fu.prm391.sample.foodapp.R;
import fu.prm391.sample.foodapp.model.Introduction;

public class IntroductionAdapter extends PagerAdapter {

    private List<Introduction> list;

    public IntroductionAdapter(List<Introduction> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.intro_adapter,container,false);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        ImageView imageView = view.findViewById(R.id.imageView);
        txtTitle.setText(list.get(position).getTitle());
        txtDescription.setText(list.get(position).getDescription());
        imageView.setImageResource(list.get(position).getImage());
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull  View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull  Object object) {
        container.removeView((View)object);
    }
}
