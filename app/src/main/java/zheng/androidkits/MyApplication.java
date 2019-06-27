package zheng.androidkits;

import android.app.Application;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("androidkit"); //设置log的级别为NONE即可关闭日志打印。logLevel(LogLevel.NONE)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
