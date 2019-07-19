package mvvm;

import android.databinding.ObservableField;
import android.databinding.ObservableFloat;

/**
 * Created by zheng on 2019/7/18
 */
public class ObservableGoods {

    private ObservableField<String> name;

    private ObservableFloat price;

    private ObservableField<String> detailes;

    public ObservableGoods(String name, float price, String detailes) {
        this.name = new ObservableField<>(name);
        this.price = new ObservableFloat(price);
        this.detailes = new ObservableField<>(detailes);
    }

    public ObservableField<String> getName() {
        return name;
    }

    public void setName(ObservableField<String> name) {
        this.name = name;
    }

    public ObservableFloat getPrice() {
        return price;
    }

    public void setPrice(ObservableFloat price) {
        this.price = price;
    }

    public ObservableField<String> getDetailes() {
        return detailes;
    }

    public void setDetailes(ObservableField<String> detailes) {
        this.detailes = detailes;
    }
}
