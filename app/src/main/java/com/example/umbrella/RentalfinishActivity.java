package com.example.umbrella;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RentalfinishActivity extends AppCompatActivity {

    TextView finishText;
    Button gomainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rentalfinish);

        finishText = (TextView) findViewById(R.id.finishtext);
        gomainBtn = (Button) findViewById(R.id.gomain);

        gomainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RentalfinishActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}