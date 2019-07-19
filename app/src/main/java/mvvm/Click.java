package mvvm;

import android.util.Log;
import android.view.View;

/**
 * Created by zheng on 2019/7/8
 */
public class Click {

    private String tag = "click";

    public void test(View v) {
        Log.e(tag, "点击事件触发了");
    }
}
