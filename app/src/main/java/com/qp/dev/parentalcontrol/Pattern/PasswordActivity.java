package com.qp.dev.parentalcontrol.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qp.dev.parentalcontrol.HomeActivity;
import com.qp.dev.parentalcontrol.Preference.SharedPreference;
import com.qp.dev.parentalcontrol.R;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;
import com.takwolf.android.lock9.Lock9View;

import java.util.Objects;

public class PasswordActivity extends AppCompatActivity {
    Lock9View lock9View;
    Button confirmButton;
    TextView textView, a, b;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    String enteredPassword;
    SharedPreference sharedPreference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    Button forgetPassword;
    String[] hasil;
    String gabung;
    final String TAG = PasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = getApplicationContext();
        setContentView( R.layout.activity_password);
        lock9View = findViewById(R.id.lock_9_view);
        forgetPassword = findViewById(R.id.forgetPassword);
        confirmButton = findViewById(R.id.confirmButton);
        textView = findViewById(R.id.textView);
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
                if (Objects.requireNonNull( sharedPreferences.getString( AppLockConstants.PASSWORD, "" ) ).matches(password)) {
                    Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PasswordActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Pola Tidak Cocok, Mohon Diulang", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(PasswordActivity.this, PasswordReset.class);
                startActivity(i);

            }
        });
    }
}