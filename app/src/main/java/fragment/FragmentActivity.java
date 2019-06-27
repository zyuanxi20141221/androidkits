package fragment;

import android.os.Bundle;
import com.orhanobut.logger.Logger;
import zheng.androidkits.R;

public class FragmentActivity extends android.support.v4.app.FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("FragmentActivity onCreate");
        setContentView(R.layout.activity_fragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, Fragment1.newInstance("fragment test"), "f1")
//                .addToBackStack("fname") //addToBackStack("fname")是可选的。FragmentManager拥有回退栈（BackStack），
                                           //类似于Activity的任务栈，如果添加了该语句，就把该事务加入回退栈，当用户点击返回按钮，
                                           // 会回退该事务（回退指的是如果事务是add(frag1)，那么回退操作就是remove(frag1)）
                                           //如果没添加该语句，用户点击返回按钮会直接销毁Activity
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("FragmentActivity onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.d("FragmentActivity onSaveInstanceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("FragmentActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("FragmentActivity onDestroy");
    }
}
