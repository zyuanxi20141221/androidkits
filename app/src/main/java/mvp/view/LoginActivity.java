package mvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mvp.presenter.ILoginPresenter;
import mvp.presenter.LoginPresenterCompl;
import zheng.androidkits.R;

public class LoginActivity extends Activity implements ILoginView {

    @BindView(R.id.action_login_button)
    Button btnLogin;
    @BindView(R.id.action_clear_button)
    Button btnClear;

    @BindView(R.id.text_username)
    AutoCompleteTextView txt_username;
    @BindView(R.id.text_password)
    EditText txt_password;

    @BindView(R.id.login_progress)
    ProgressBar loginProgressBar;

    ILoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginPresenter = new LoginPresenterCompl(this);
        loginPresenter.setProgressBarVisibility(View.INVISIBLE);
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

    @OnClick(R.id.action_login_button)
    public void onLoginButonClick() {
        loginPresenter.setProgressBarVisibility(View.VISIBLE);
        loginPresenter.doLogin(txt_username.getText().toString(), txt_password.getText().toString());
    }

    @OnClick(R.id.action_clear_button)
    public void onClearButtonClick() {
        loginPresenter.clear();
    }

    @Override
    public void onClearText() {
        txt_username.setText("");
        txt_password.setText("");
    }

    @Override
    public void onLoginResult(boolean result, int code) {
        loginPresenter.setProgressBarVisibility(View.INVISIBLE);
        if (result) {
            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Login Fail, code = " + code, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSetProgressBarVisibility(int visibility) {
        loginProgressBar.setVisibility(visibility);
    }
}
