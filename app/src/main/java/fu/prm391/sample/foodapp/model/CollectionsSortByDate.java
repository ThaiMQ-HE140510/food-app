package fu.prm391.sample.foodapp.model;

import java.util.Comparator;

public class CollectionsSortByDate implements Comparator<DetailOrder> {

    @Override
    public int compare(DetailOrder o1, DetailOrder o2) {
        return o2.getDate().compareTo(o1.getDate());
    }
}
