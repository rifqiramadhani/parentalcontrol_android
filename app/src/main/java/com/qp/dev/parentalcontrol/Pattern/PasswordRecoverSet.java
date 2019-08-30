package com.qp.dev.parentalcontrol.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.qp.dev.parentalcontrol.HomeActivity;
import com.qp.dev.parentalcontrol.R;
import com.qp.dev.parentalcontrol.Utils.AppLockConstants;

import java.util.ArrayList;
import java.util.List;

public class PasswordRecoverSet extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    Spinner questionsSpinner;
    EditText answer;
    Button confirmButton;
    int questionNumber = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_password_recover_set );

        confirmButton = (Button) findViewById( R.id.confirmButton);
        questionsSpinner = (Spinner) findViewById(R.id.questionsSpinner);
        answer = (EditText) findViewById(R.id.answer);

        sharedPreferences = getSharedPreferences( AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        List<String> list = new ArrayList<String>();
        list.add("Pilih Pertanyaan Keamanan?");
        list.add("Siapa nama belakang ibumu?");
        list.add("Apa nama kota kelahiran kamu?");
        list.add("Apa nama panggilan ketika kamu kecil?");
        list.add("Apa nama kota dimana orang tua kamu bertemu?");
        list.add("Apa nama depan dari sepupu tertua kamu?");
        list.add("Apa nama hewan peliharaan kamu yang pertama?");

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionsSpinner.setAdapter(stringArrayAdapter);

        questionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionNumber = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Buat inputan pertanyaan keamanan
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionNumber != 0 && !answer.getText().toString().isEmpty()) { //dari package Utils
                    // check pola
                    editor.putBoolean(AppLockConstants.IS_PASSWORD_SET, true);
                    editor.apply();

                    editor.putString(AppLockConstants.ANSWER, answer.getText().toString());
                    editor.commit();
                    editor.putInt(AppLockConstants.QUESTION_NUMBER, questionNumber);
                    editor.commit();

                    Intent home = new Intent(PasswordRecoverSet.this, HomeActivity.class);
                    startActivity(home);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Mohon Pilih Pertanyaan dan Tulis Jawaban", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PasswordRecoverSet.this, PasswordSet.class);
        startActivity(i);
        finish();
    }
}