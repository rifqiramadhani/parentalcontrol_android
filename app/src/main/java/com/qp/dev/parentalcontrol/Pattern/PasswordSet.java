package com.qp.dev.parentalcontrol.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qp.dev.parentalcontrol.R;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;
import com.takwolf.android.lock9.Lock9View;

public class PasswordSet extends AppCompatActivity {
    Lock9View lock9View;
    Button confirmButton, retryButton;
    TextView textView;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    String enteredPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    String[] hasil;
    String gabung;
    final String TAG = PasswordSet.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = getApplicationContext();
        setContentView( R.layout.activity_password_set);
        lock9View = findViewById(R.id.lock_9_view);
        confirmButton = findViewById(R.id.confirmButton);
        retryButton = findViewById(R.id.retryButton);
        textView = findViewById(R.id.textView);
        confirmButton.setEnabled(false);
        retryButton.setEnabled(false);
        sharedPreferences = getSharedPreferences( AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(AppLockConstants.PASSWORD, enteredPassword);
                editor.apply();

                Intent i = new Intent(PasswordSet.this, PasswordRecoverSet.class);
                startActivity(i);
                finish();
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnteringFirstTime = true;
                isEnteringSecondTime = false;
                textView.setText("Buat Pola Kunci");
                confirmButton.setEnabled(false);
                retryButton.setEnabled(false);
            }
        });

        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                retryButton.setEnabled(true);
                long start = (SystemClock.elapsedRealtime());
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
                long stop = (SystemClock.elapsedRealtime());
                long last = (stop - start);
                Log.d(TAG, "Waktu berjalan: " + " \n mulai: " + start + "\n berhenti: " + stop + "\n lama waktu: " + last);

                if (isEnteringFirstTime) {
                    enteredPassword = password;
                    isEnteringFirstTime = false;
                    isEnteringSecondTime = true;
                    textView.setText("Buat Ulang Pola Kunci");
                } else if (isEnteringSecondTime) {
                    if (enteredPassword.matches(password)) {
                        confirmButton.setEnabled(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Pola Kunci tidak cocok - Mohon Diulang", Toast.LENGTH_SHORT).show();
                        isEnteringFirstTime = true;
                        isEnteringSecondTime = false;
                        textView.setText("Buat Pola Kunci");
                        retryButton.setEnabled(false);
                    }
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

//            private String encrypt(String message, final String mappedKey) {
//                int[][] table = createVigenereTable();
//                String encryptedText = "";
//                for (int i = 0, j = 0; i < message.length(); i++) {
//                    if(message.charAt(i) == (char)32 && mappedKey.charAt(j) == (char)32){
//                        encryptedText += " ";
//                    } else {
//                        //accessing element at table[i][j] position to replace it with letter in message
//                        encryptedText += (char)table[(int)message.charAt(i)-65][(int)mappedKey.charAt(j)-65];
//                        Log.d( TAG, "charac : " + encryptedText );
//                    }
//                    j = ++j % mappedKey.length();
//                }
//                return encryptedText;
//            }
//
//            private int[][] createVigenereTable() {
//                // creating 26x26 table containing alphabets
//                int[][] tableArr = new int[26][26];
//                for (int i = 0; i < 26; i++) {
//                    for (int j = 0; j < 26; j++) {
//                        int temp;
//                        if((i+65)+j > 90){
//                            temp = ((i+65)+j) -26;
//                            tableArr[i][j] = temp;
//                        } else {
//                            temp = (i+65)+j;
//                            tableArr[i][j] = temp;
//                        }
//                    }
//                }
//                return tableArr;
//            }
        });
    }
}