package mvvm;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import java.util.Random;
import zheng.androidkits.R;
import zheng.androidkits.databinding.ActivityDatabinding2Binding;

/**
 * Created by zheng on 2019/7/19
 */
public class MainBindingActivity2 extends Activity {

    private ObservableGoods observableGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDatabinding2Binding activityDatabinding2Binding = DataBindingUtil.setContentView(this, R.layout.activity_databinding2);
        observableGoods = new ObservableGoods("john", 33, "decripetion");
        activityDatabinding2Binding.setObservableGoods(observableGoods);
        activityDatabinding2Binding.setObservableGoodsHandler(new ObservableGoodsHandler());
    }

    public class ObservableGoodsHandler {

        public void changeGoodsName() {
            observableGoods.getName().set("code" + new Random().nextInt(100));
        }

        public void changeGoodsDetails() {
            observableGoods.getName().set("code" + new Random().nextInt(100));
            observableGoods.getDetailes().set("hi" + new Random().nextInt(100));
            observableGoods.getPrice().set(new Random().nextInt(100));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
