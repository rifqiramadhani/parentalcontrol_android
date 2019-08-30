package com.qp.dev.parentalcontrol.Services;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.qp.dev.parentalcontrol.Pattern.PasswordReset;
import com.qp.dev.parentalcontrol.Preference.SharedPreference;
import com.qp.dev.parentalcontrol.R;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;
import com.takwolf.android.lock9.Lock9View;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppCheckServices extends Service {
//    public static final String TAG = "AppCheckServices";
    private Context context = null;
    private Timer timer;
    ImageView imageView;
    Button confirmButton;
    private WindowManager windowManager;
    private Dialog dialog;
    public static String currentApp = "";
    public static String previousApp = "";
    String enteredPassword;
    SharedPreference sharedPreference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    List<String> pakageName;
    String[] hasil;
    String gabung;
    final String TAG = AppCheckServices.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sharedPreference = new SharedPreference();
        if (sharedPreference != null) {
            pakageName = sharedPreference.getLocked(context);
        }
        timer = new Timer("AppCheckServices");
        timer.schedule(updateTask, 1000L, 1000L);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageView = new ImageView(this);
        imageView.setVisibility( View.GONE);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
        params.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
        windowManager.addView(imageView, params);
    }

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            if (sharedPreference != null) {
                pakageName = sharedPreference.getLocked(context);
            }
            if (isConcernedAppIsInForeground()) {
                Log.d("isConcernedAppIsInFrgnd", "true");
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            if (!currentApp.matches(previousApp)) {
                                showUnlockDialog();
                                previousApp = currentApp;
                            }else {
                                Log.d("AppCheckService", "Aplikasi sedang berjalan");
                            }
                        }
                    });
                }
            } else {
                Log.d("isConcernedAppIsInFrgnd", "false");
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            hideUnlockDialog();
                        }
                    });
                }
            }
        }
    };

    void showUnlockDialog() {
        showDialog();
    }

    void hideUnlockDialog() {
        previousApp = "";
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showDialog() {
        if (context == null)
            context = getApplicationContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate( R.layout.popup_unlock, null, false);
        Lock9View lock9View = promptsView.findViewById(R.id.lock_9_view);
        Button forgetPassword = promptsView.findViewById( R.id.forgetPassword);
        sharedPreferences = getSharedPreferences( AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                String[] alphabets = password.split( "" ); //buat array dari pattern2 dipecah tiap string [0,1,2,3,..,8,9]
                hasil = new String[10];
                //konversi karakter
                for (int j = 0; j < hasil.length; j++) {
                    hasil[j] = "";
                }
                //parsing
                int i = 0;
                for (String alphabet : alphabets) {
                    Log.d( TAG, ": geser ke " + i + " dot " + alphabet );
                    hasil[i] = alphabet;
                    i += 1;
                }

                Log.d( TAG, "Hasil array: " + hasil[2] ); // jumlah array alphabet termasuk 0, 3 dot pattern menjadi 4

                for (int j = 1; j < hasil.length; j++) {
//                    if (hasil[j].equals( "0" )) {
//                        hasil[j] = "ABCDE";
//                        editor.putString( "dot0", hasil[j] );
//                    }
                    if (hasil[j].equals( "1" )) {
                        hasil[j] = "ABCDE";
                        editor.putString( "dot1", hasil[j] );
                    }
                    if (hasil[j].equals( "2" )) {
                        hasil[j] = "FGHIJ";
                        editor.putString( "dot2", hasil[j] );
                    }
                    if (hasil[j].equals( "3" )) {
                        hasil[j] = "KLMNO";
                        editor.putString( "dot3", hasil[j] );
                    }
                    if (hasil[j].equals( "4" )) {
                        hasil[j] = "PQRST";
                        editor.putString( "dot4", hasil[j] );
                    }
                    if (hasil[j].equals( "5" )) {
                        hasil[j] = "UVWXY";
                        editor.putString( "dot5", hasil[j] );
                    }
                    if (hasil[j].equals( "6" )) {
                        hasil[j] = "ZABCD";
                        editor.putString( "dot6", hasil[j] );
                    }
                    if (hasil[j].equals( "7" )) {
                        hasil[j] = "EFGHI";
                        editor.putString( "dot7", hasil[j] );
                    }
                    if (hasil[j].equals( "8" )) {
                        hasil[j] = "JKLMN";
                        editor.putString( "dot8", hasil[j] );
                    }
                    if (hasil[j].equals( "9" )) {
                        hasil[j] = "OPQRS";
                        editor.putString( "dot9", hasil[j] );
                    }
                    if (!hasil[j].equals( "" )) {
                        Log.d( TAG, "idx j: " + j + " - value: " + hasil[j] );
                    }
                }

//              Proses Enkripsi Vigenere Cipher
                String gabung = hasil[1] + hasil[2] + hasil[3] + hasil[4] + hasil[5] + hasil[6] + hasil[7] + hasil[8] + hasil[9];
                Log.d( TAG, "concate : " + gabung );

                int count_gabung = gabung.length();
                Log.d( TAG, "jumlah_karkt : " + count_gabung );

                // String text = "";
                String kunci = "ABCDE"; // Cari karakter kunci random dengan metode
                password = this.encrypt( gabung, kunci );
                editor.apply();
//                if (password.matches(sharedPreference.getPassword(context))) {
                if (Objects.requireNonNull( sharedPreferences.getString( AppLockConstants.PASSWORD, "" ) ).matches(password)) {
//                    Toast.makeText(getApplicationContext(), "Login berhasil", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Pola Kunci", Toast.LENGTH_SHORT).show();
                }
            }
            // Algoritma Enkripsi
            private String encrypt(String pattern_char, final String key) {
                //            StringBuilder res = new StringBuilder();
                String res = "";
                String coba = "";
                for (int i = 0, j = 0; i < pattern_char.length(); i++) {
                    char c = pattern_char.charAt( i );
                    char k = key.charAt( j );
                    if (c < 'A' || c > 'Z')
                        continue;
                    //                res.append( (char) ((c + key.charAt( j ) - 2 * 'A') % 26 + 'A') );
                    //                res = new StringBuilder( String.valueOf( res ) ).append( (char) (((key.charAt( j ) + c) % 26) + 65) ).toString();
//                    if (c==65){
                    res = res + (char) (((c + k) % 26) + 65);
//                    }
//                    else {
//                        res = res + (char) (((c + k) % 26) + 65 + 1);
//                    }
//                    Log.d( TAG, "charac : " + c );
                    Log.d( TAG, "rumus : " + c + " + " + k);
                    Log.d( TAG, "result : " + res );
//                    coba = "A" + "B";
                    j = ++j % key.length();
                }
                Log.d( TAG, "plainteks : " + pattern_char );
                Log.d( TAG, "encrypted : " + res );
                return res;
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AppCheckServices.this, PasswordReset.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                dialog.dismiss();
            }
        });

        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        Objects.requireNonNull( dialog.getWindow() ).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
                return true;
            }
        });
        dialog.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // check buat service tetap berjalan
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
        }
        return START_STICKY;
    }

    public boolean isConcernedAppIsInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
        if (Build.VERSION.SDK_INT <= 20) {
            if (task.size() > 0) {
                ComponentName componentInfo = task.get(0).topActivity;
                for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                    if (componentInfo.getPackageName().equals(pakageName.get(i))) {
                        currentApp = pakageName.get(i);
                        return true;
                    }
                }
            }
        } else {
            String mpackageName = manager.getRunningAppProcesses().get(0).processName;
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            assert usage != null;
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) {
                    Log.d(TAG,"isEmpty Yes");
                    mpackageName = "";
                }else {
                    mpackageName = Objects.requireNonNull( runningTask.get( runningTask.lastKey() ) ).getPackageName();
                    Log.d(TAG,"isEmpty No : "+mpackageName);
                }
            }

            for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                Log.d("AppCheckService", "pakageName Size" + pakageName.size());
                if (mpackageName.equals(pakageName.get(i))) {
                    currentApp = pakageName.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        if (imageView != null) {
            windowManager.removeView(imageView);
        }
        // window manager
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}