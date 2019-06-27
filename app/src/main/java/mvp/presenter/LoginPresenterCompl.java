package mvp.presenter;

import android.os.Handler;
import android.os.Looper;
import mvp.model.IUser;
import mvp.model.UserModel;
import mvp.view.ILoginView;

public class LoginPresenterCompl implements ILoginPresenter {

    ILoginView loginView;
    IUser user;
    Handler handler;

    public LoginPresenterCompl(ILoginView loginView) {
        this.loginView = loginView;
        initUser();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void clear() {
        loginView.onClearText();
    }

    @Override
    public void doLogin(String username, String password) {
        final int code = user.checkUserValidity(username, password);
        handler.postDelayed(new Runnable() { //模拟比较耗时的网络请求
            @Override
            public void run() {
                loginView.onLoginResult(code == 0 ? true : false, code);
            }
        }, 3000);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        loginView.onSetProgressBarVisibility(visibility);
    }

    private void initUser() {
        user = new UserModel("android mvp", "mvp");
    }
}
