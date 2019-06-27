package mvp.presenter;

public interface ILoginPresenter {

    void clear();

    void doLogin(String username, String password);

    void setProgressBarVisibility(int visibility);
}
