package com.hgb.oaiddemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private TextView tvContent;
    protected PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionHelper = new PermissionHelper(this);
        tvContent = findViewById(R.id.tv_content);

        final StringBuilder idsBuilder = new StringBuilder();
        permissionHelper.requestPermissions("请给权限",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {
                        idsBuilder.append("IMEI: ").append(DevicesUtil.getIMEI(MainActivity.this)).append("\n");
                        tvContent.setText(idsBuilder.toString());
                    }

                    @Override
                    public void doAfterDenied(String... permission) {

                    }
                }, Manifest.permission.READ_PHONE_STATE);

        //获取OAID等设备标识符
        MdidHelper mdidHelper = new MdidHelper((oaid, vaid, aaid, isSupport) -> {
            if (isSupport) {
                idsBuilder.append("OAID: ").append(oaid).append("\n");
                idsBuilder.append("VAID: ").append(vaid).append("\n");
                idsBuilder.append("AAID: ").append(aaid).append("\n");

                idsBuilder.append("AndroidId: ").append(DevicesUtil.getAndroidId(MainActivity.this)).append("\n");
                idsBuilder.append("MAC: ").append(DevicesUtil.getMacAddress()).append("\n");
                if (PermissionHelper.hasPermissions(MainActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                    idsBuilder.append("IMEI: ").append(DevicesUtil.getIMEI(MainActivity.this)).append("\n");
                }
                runOnUiThread(() -> tvContent.setText(idsBuilder.toString()));
            }
        });
        mdidHelper.getDeviceIds(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
