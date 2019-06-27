package permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by zheng on 2019/1/7
 * 动态权限申请(android 6.0以上)
 * <p>
 * Android6.0中对权限分为了一般权限和危险权限。
 * 一般权限：只要在清单文件中注册即可使用。
 * 危险权限：除了要在清单文件中注册外，还要对用户进行请求权限弹窗提醒，才可以使用。
 * <p>
 * 关于日历的权限：
 * <uses-permission android:name="android.permission.READ_CALENDAR"/>
 * <uses-permission android:name="android.permission.WRITE_CALENDAR"/>
 * <p>
 * 关于相机的权限：
 * <uses-permission android:name="android.permission.CAMERA"/>
 * <p>
 * 关于联系人的权限：
 * <uses-permission android:name="android.permission.READ_CONTACTS"/>
 * <uses-permission android:name="android.permission.WRITE_CONTACTS"/>
 * <uses-permission android:name="android.permission.GET_CONTACTS"/>
 * <p>
 * 关于位置的权限：
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 * <p>
 * 关于电话的权限：
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 * <uses-permission android:name="android.permission.CALL_PHONE"/>
 * <uses-permission android:name="android.permission.READ_CALL_LOG"/>
 * <uses-permission android:name="android.permission.WRITE_CALL_LOG"/>
 * <uses-permission android:name="android.permission.USE_SIP"/>
 * <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
 * <p>
 * 关于传感器的权限：
 * <uses-permission android:name="android.permission.BODY_SENSORS"/>
 * <p>
 * 关于短信的权限：
 * <uses-permission android:name="android.permission.SEND_SMS"/>
 * <uses-permission android:name="android.permission.RECEIVE_SMS"/>
 * <uses-permission android:name="android.permission.READ_SMS"/>
 * <uses-permission android:name="android.permission.RECEIVE_WAP_PUSH"/>
 * <uses-permission android:name="android.permission.RECEIVE_MMS"/>
 * <uses-permission android:name="android.permission.READ_CELL_BROADCASTS"/>
 * <p>
 * 关于SD卡的权限
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 */

public class BasePermissionsActivity extends Activity {

    private static String TAG = BasePermissionsActivity.class.getSimpleName();

    private static int REQUEST_CODE = 0;

    public void requestPermission(String[] permissions, int requestCode) {
        this.REQUEST_CODE = requestCode;
        if (checkPermissions(permissions)) {
            permissinSucceed(requestCode);
        } else {
            List<String> needPermissions = getPermissions(permissions);
            ActivityCompat.requestPermissions(this, needPermissions.toArray(new String[needPermissions.size()]), requestCode);
        }
    }

    private boolean verificationPermissions(int[] results) {
        for (int result : results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private List<String> getPermissions(String[] permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                permissionList.add(permission);
            }
        }
        return permissionList;
    }

    /**
     * 检查所有的权限是否都已授权
     *
     * @param permissions
     * @return
     */
    private boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(BasePermissionsActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (verificationPermissions(grantResults)) {
                permissinSucceed(requestCode);
            } else {
                permissionFailing(requestCode);
                showFaiingDialog();
            }
        }
    }

    private void showFaiingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("消息")
                .setMessage("当前应用无此权限，该功能暂时无法使用。如若需要，请单击确定按钮进行权限授权！")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSettings();
                    }
                }).show();

    }

    public void permissionFailing(int code) {
        Log.d(TAG, "获取权限失败=" + code);
    }

    public void permissinSucceed(int code) {
        Log.d(TAG, "获取权限成功=" + code);
    }

    private void startSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
