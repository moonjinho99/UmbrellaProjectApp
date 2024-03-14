package com.example.umbrella;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReturnResultActivity extends AppCompatActivity {
    TextView returnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_result);

        returnText = (TextView) findViewById(R.id.returntext);
    }
}
