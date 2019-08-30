package com.qp.dev.parentalcontrol;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.qp.dev.parentalcontrol.Pattern.PasswordActivity;
import com.qp.dev.parentalcontrol.Pattern.PasswordSet;
import com.qp.dev.parentalcontrol.Services.AlarmReceiver;
import com.qp.dev.parentalcontrol.Services.AppCheckServices;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Context context;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public static int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView( R.layout.activity_splash);
        checkPermissions();
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                OverlayPermissionDialogFragment dialogFragment = new OverlayPermissionDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "Overlay Permission");
            }else if(!hasUsageStatsPermission()){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                UsageAcessDialogFragment dialogFragment = new UsageAcessDialogFragment();
                ft.add(dialogFragment, null);
                ft.commitAllowingStateLoss();
            } else {
                startService();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState );
    }

    public void startService(){
        startService(new Intent(SplashActivity.this, AppCheckServices.class));
        try {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
            int interval = (86400 * 1000) / 4;
            if (manager != null) {
                manager.cancel(pendingIntent);
            }
            assert manager != null;
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPreferences = getSharedPreferences( AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        final boolean isPasswordSet = sharedPreferences.getBoolean(AppLockConstants.IS_PASSWORD_SET, false);
        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                if (isPasswordSet) {
                    Intent i = new Intent(SplashActivity.this, PasswordActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this, PasswordSet.class);
                    startActivity(i);
                }
                finish();
            }
        }, SPLASH_TIME_OUT );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        Log.d("SplashActivity", "cp 1");
        checkPermissions();
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        assert appOps != null;
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static class OverlayPermissionDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder( Objects.requireNonNull( getActivity() ) );
            builder.setMessage(R.string.ovarlay_permission_description)
                    .setTitle("Overlay Permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + Objects.requireNonNull( getActivity() ).getPackageName()));
                            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static class UsageAcessDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder( Objects.requireNonNull( getActivity() ) );

            builder.setMessage(R.string.usage_data_access_description)
                    .setTitle("Usage Access Permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(
                                    new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                                    MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                        }
                    });

            return builder.create();
        }
    }
}