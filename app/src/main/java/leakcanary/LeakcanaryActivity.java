package leakcanary;

import android.app.Activity;
import android.os.Bundle;
import com.orhanobut.logger.Logger;
import zheng.androidkits.R;

public class LeakcanaryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leakcanary);
        LeakThread leakThread = new LeakThread();
        leakThread.start();
    }

    class LeakThread extends Thread {
        @Override
        public void run() {
            try {
                AppSetting.getInstance(LeakcanaryActivity.this);
                Thread.sleep(6 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("LeakcanaryActivity onDestroy");
    }
}
