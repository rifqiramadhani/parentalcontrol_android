package com.qp.dev.parentalcontrol.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qp.dev.parentalcontrol.HomeActivity;
import com.qp.dev.parentalcontrol.R;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;
import com.takwolf.android.lock9.Lock9View;

import java.util.Objects;

public class FragmentPola extends Fragment {
    Lock9View lock9View;
    Button confirmButton, retryButton;
    TextView textView;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    String enteredPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String[] hasil;
    String gabung;
    final String TAG = FragmentPola.class.getSimpleName();

    public static FragmentPola newInstance() {
        FragmentPola f = new FragmentPola();
        return (f);
    }

    public FragmentPola() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.activity_password_set, container, false);
        lock9View = (Lock9View) v.findViewById(R.id.lock_9_view);
        confirmButton = (Button) v.findViewById(R.id.confirmButton);
        retryButton = (Button) v.findViewById(R.id.retryButton);
        textView = (TextView) v.findViewById(R.id.textView);
        confirmButton.setEnabled(false);
        retryButton.setEnabled(false);
        sharedPreferences = Objects.requireNonNull( getActivity() ).getSharedPreferences( AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(AppLockConstants.PASSWORD, enteredPassword);
                editor.apply();

                editor.putBoolean(AppLockConstants.IS_PASSWORD_SET, true);
                editor.commit();

                Intent i = new Intent(getActivity(), HomeActivity.class);
                Objects.requireNonNull( getActivity() ).startActivity(i);
                getActivity().finish();
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
//                    if (hasil[j].equals( "0" )) { //statement menggunakan ==
//                        hasil[j] = "ABCDE";
//                        editor.putString( "dot0", hasil[j] );
//                    }
                    if (hasil[j].equals( "1" )) {
                        hasil[j] = "FGHIJ";
                        editor.putString( "dot1", hasil[j] );
                    }
                    if (hasil[j].equals( "2" )) {
                        hasil[j] = "KLMNO";
                        editor.putString( "dot2", hasil[j] );
                    }
                    if (hasil[j].equals( "3" )) {
                        hasil[j] = "PQRST";
                        editor.putString( "dot3", hasil[j] );
                    }
                    if (hasil[j].equals( "4" )) {
                        hasil[j] = "UVWXY";
                        editor.putString( "dot4", hasil[j] );
                    }
                    if (hasil[j].equals( "5" )) {
                        hasil[j] = "ZABCD";
                        editor.putString( "dot5", hasil[j] );
                    }
                    if (hasil[j].equals( "6" )) {
                        hasil[j] = "EFGHI";
                        editor.putString( "dot6", hasil[j] );
                    }
                    if (hasil[j].equals( "7" )) {
                        hasil[j] = "JKLMN";
                        editor.putString( "dot7", hasil[j] );
                    }
                    if (hasil[j].equals( "8" )) {
                        hasil[j] = "OPQRS";
                        editor.putString( "dot8", hasil[j] );
                    }
                    if (hasil[j].equals( "9" )) {
                        hasil[j] = "TUVWX";
                        editor.putString( "dot8", hasil[j] );
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
                        Toast.makeText(getActivity(), "Pola Kunci tidak cocok - Mohon Diulang", Toast.LENGTH_SHORT).show();
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
        });
        return v;
    }

}