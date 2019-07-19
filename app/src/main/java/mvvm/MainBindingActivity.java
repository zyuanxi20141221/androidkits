package mvvm;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.Random;
import zheng.androidkits.BR;
import zheng.androidkits.R;
import zheng.androidkits.databinding.ActivityDatabindingBinding;

/**
 * Created by zheng on 2019/7/8
 */
public class MainBindingActivity extends Activity {

    private final String TAG = MainBindingActivity.class.getSimpleName();
    private Goods goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDatabindingBinding activityDatabindingMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_databinding);
        User user = new User("zheng", "男");

        goods = new Goods("code", "hi", 24);

        activityDatabindingMainBinding.setUser(user);
        activityDatabindingMainBinding.setClick(new Click());

        activityDatabindingMainBinding.setGoods(goods);
        activityDatabindingMainBinding.setGoodsHandler(new GoodsHandler());
        activityDatabindingMainBinding.setUserPresenter(new UserPresenter());

        goods.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.name) {
                    Log.e(TAG, "BR.name");
                } else if (propertyId == BR._all) {
                    Log.e(TAG, "BR._all");
                } else {
                    Log.e(TAG, "未知");
                }
            }
        });
    }

    public class UserPresenter {

        public void onUserNameClick(User user) {
            Toast.makeText(MainBindingActivity.this, "UserName：" + user.getUserName(), Toast.LENGTH_SHORT).show();
        }
    }

    public class GoodsHandler {

        public void changeGoodsName() {
            goods.setName("code" + new Random().nextInt(100));
            goods.setPrice(new Random().nextInt(100));
        }

        public void changeGoodsDetail() {
            goods.setDetails("hi" + new Random().nextInt(100));
            goods.setPrice(new Random().nextInt(100));
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
