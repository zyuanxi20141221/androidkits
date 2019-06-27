package zheng.androidkits;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import retrofit.GetRequest_Interface;
import retrofit.PostRequest_Interface;
import retrofit.Translation;
import retrofit.Translation1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit使用
 */
public class RequestActivity extends AppCompatActivity implements View.OnClickListener {

    Button getButton;
    Button postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        getButton = findViewById(R.id.btn_get);
        postButton = findViewById(R.id.btn_post);
        initEvent();
    }

    private void initEvent() {
        getButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
    }

    private void get_request() {
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //创建网络请求接口的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
        //对发送请求进行封装
        Call<Translation> call = request.getCall();
        //发送网络请求(异步)
        call.enqueue(new Callback<Translation>() {
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                Log.v("RequestActivity", response.body().toString());
            }

            @Override
            public void onFailure(Call<Translation> call, Throwable throwable) {

            }
        });
    }

    private void post_request() {
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fanyi.youdao.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //创建网络请求接口的实例
        PostRequest_Interface request = retrofit.create(PostRequest_Interface.class);
        //对发送请求进行封装
        Call<Translation1> call = request.getCall("I come from china");
        //发送网络请求(异步)
        call.enqueue(new Callback<Translation1>() {
            @Override
            public void onResponse(Call<Translation1> call, Response<Translation1> response) {
                Log.v("RequestActivity", "翻译是：" + response.body().toString());
            }

            @Override
            public void onFailure(Call<Translation1> call, Throwable throwable) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                get_request();
                break;
            case R.id.btn_post:
                post_request();
                break;
        }
    }
}
