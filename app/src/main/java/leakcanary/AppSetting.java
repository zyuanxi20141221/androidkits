package leakcanary;

import android.content.Context;

/**
 * 此单例模式如果在调用getInstance(Context context)方法的时候传入的context参数是Activity、Service等上下文，就会导致内存泄露
 */
public class AppSetting {

    private static AppSetting appSetting;
    private Context context;

    private AppSetting(Context context) {
        this.context = context;
        //全局的上下文Application Context就是应用程序的上下文，和单例的生命周期一样长，这样就避免了内存泄漏。
        //this.context = context.getApplicationContext();
    }

    public static AppSetting getInstance(Context context) {
        if (appSetting == null) {
            appSetting = new AppSetting(context);
        }
        return appSetting;
    }
}
