package zheng.androidkits;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import asynctask.AsyncTaskActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fragment.FragmentActivity;
import leakcanary.LeakcanaryActivity;
import listview.ListViewActivity;
import mvp.view.LoginActivity;
import recycleview.RecycleViewActivity;
import video.SimplePlayActivity;
import video.VideoInfo;
import view.bottomdialog.CustomeViewActivity;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); //通过注解的方式来对Android View进行绑定.
    }

    @OnClick(R.id.take_permission)
    public void onPermissionClick() {
        intent = new Intent(this, PermissionActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_video)
    public void onVideoClick() {
        VideoInfo info = new VideoInfo("aaa", "http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4");
        intent = new Intent(this, SimplePlayActivity.class);
        intent.putExtra("videoInfo", info);
        startActivity(intent);
    }

    @OnClick(R.id.take_request)
    public void onRequestClick() {
        intent = new Intent(this, RequestActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_customview)
    public void onCustomViewClick() {
        intent = new Intent(this, CustomeViewActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_okhttp)
    public void onOkHttpClick() {
        intent = new Intent(this, OkHttpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_opensdk)
    public void onOpenSdkClick() {
        intent = new Intent(this, OpenSdkActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_mvp_pattern)
    public void onAndroidMVPClick() {
        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_asynctask)
    public void onAndroidAsyncTaskClick() {
        intent = new Intent(this, AsyncTaskActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_leakcanary)
    public void onAndroidLeakcanaryClick() {
        intent = new Intent(this, LeakcanaryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_listview)
    public void onAndroidListViewClick() {
        intent = new Intent(this, ListViewActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_recycleview)
    public void onAndroidRecycleViewClick() {
        intent = new Intent(this, RecycleViewActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_fragment)
    public void onFragmentClick() {
        intent = new Intent(this, FragmentActivity.class);
        startActivity(intent);
    }
}
