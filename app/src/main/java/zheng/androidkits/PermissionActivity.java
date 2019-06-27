package zheng.androidkits;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.BasePermissionsActivity;

/**
 * Create by zheng on 2019/1/7
 * 动态权限申请
 */

public class PermissionActivity extends BasePermissionsActivity {

    @BindView(R.id.take_phone)
    Button mtake_phone;

    @BindView(R.id.take_photo)
    Button mtake_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_permission);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.take_phone)
    public void takePhoneOnclick() {
        requestPermission(new String[]{Manifest.permission.CALL_PHONE}, 1);
    }

    @OnClick(R.id.take_photo)
    public void takePhotoOnclick() {
        requestPermission(new String[]{Manifest.permission.CAMERA}, 2);
    }

    @Override
    public void permissionFailing(int code) {
        super.permissionFailing(code);
    }

    @Override
    public void permissinSucceed(int code) {
        super.permissinSucceed(code);
        switch (code) {
            case 1:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:15855759639"));
                startActivity(intent);
                break;
            case 2:
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent2);
                break;
        }
    }
}
