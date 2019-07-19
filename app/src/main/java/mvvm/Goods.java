package mvvm;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import zheng.androidkits.BR;

/**
 * Created by zheng on 2019/7/18
 */
public class Goods extends BaseObservable {

    @Bindable
    public String name;

    private String details;

    private float price;

    public Goods(String name, String details, float price) {
        this.name = name;
        this.details = details;
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        notifyChange();
    }

    @Bindable
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

}
