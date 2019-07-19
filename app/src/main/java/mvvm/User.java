package mvvm;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import zheng.androidkits.BR;

/**
 * Created by zheng on 2019/7/8
 */
public class User extends BaseObservable {

    private String userName;
    private String userSex;

    public User(String userName, String userSex) {
        this.userName = userName;
        this.userSex = userSex;
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }
}
