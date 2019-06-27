package zheng.androidkits;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp.CallBackUtil;
import okhttp.OkhttpUtil;
import okhttp3.Call;

public class OkHttpActivity extends AppCompatActivity {

    @BindView(R.id.okhttp_get)
    Button btn_get;

    @BindView(R.id.okhttp_post)
    Button btn_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.okhttp_get)
    public void onGetClick() {
        OkhttpUtil.okHttpGet("https://www.baidu.com/", new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                com.orhanobut.logger.Logger.t("OkHttpActivity").d("get response " + response);
            }
        });
    }

    @OnClick(R.id.okhttp_post)
    public void onPostClick() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "title");
        params.put("desc", "desc");
        OkhttpUtil.okHttpPost("https://www.baidu.com/", params, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                com.orhanobut.logger.Logger.t("OkHttpActivity").d("post response " + response);
            }
        });
    }
}
