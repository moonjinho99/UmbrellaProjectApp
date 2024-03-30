package com.example.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReturnResultActivity extends AppCompatActivity {
    TextView returnText;
    Button goHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_result);

        returnText = (TextView) findViewById(R.id.returntext);
        goHomeBtn = (Button) findViewById(R.id.gohome);

        goHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReturnResultActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
