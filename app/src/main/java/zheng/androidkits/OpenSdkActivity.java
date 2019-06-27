package zheng.androidkits;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenSdkActivity extends AppCompatActivity {

    @BindView(R.id.btn_glid)
    Button btn_glid;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensdk);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_glid)
    public void onGlidClick() {
        intent = new Intent(this, GlidActivity.class);
        startActivity(intent);
    }
}
