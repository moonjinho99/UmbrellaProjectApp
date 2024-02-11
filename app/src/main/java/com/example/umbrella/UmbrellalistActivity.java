package com.example.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.umbrella.dto.UmbrellaDTO;

import java.util.List;

public class UmbrellalistActivity extends AppCompatActivity {

    TextView name;
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umbrellalist);

        name = (TextView) findViewById(R.id.name);
        gridView = (GridView) findViewById(R.id.umbrellaGridView);

        GridListAdapter adapter = new GridListAdapter();

        Intent intent = getIntent();

        String addname = intent.getStringExtra("name");

        name.setText(addname);

        List<UmbrellaDTO> lists = MainActivity.umbrellaList;

        for(int i=0; i<lists.size(); i++)
        {
            adapter.addItem(lists.get(i));
        }

        gridView.setAdapter(adapter);

    }

}