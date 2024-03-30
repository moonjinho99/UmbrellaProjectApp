package com.example.umbrella;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.umbrella.dto.LockerDto;
import com.example.umbrella.dto.UmbrellaDTO;

import java.util.ArrayList;

public class GridListAdapter extends BaseAdapter {
    ArrayList<UmbrellaDTO> items = new ArrayList<UmbrellaDTO>();
    Context context;

    public void addItem(UmbrellaDTO item){
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        UmbrellaDTO listitem = items.get(position);

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item,parent,false);
        }

        TextView name = convertView.findViewById(R.id.locknum);
        TextView price = convertView.findViewById(R.id.price);
        Button rentalBtn = convertView.findViewById(R.id.rentalBtn);

        if(listitem.getRentalStatus() == 3)
        {
            rentalBtn.setEnabled(false);
            rentalBtn.setText("대여중");
        }

        if(listitem.getRentalStatus() == 3 || listitem.getRentalStatus() == 1)
        {
            name.setText(Integer.toString(listitem.getUmbrellacode()));
            price.setText(Integer.toString(listitem.getPrice()));
        }

        rentalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UmbrellaDetail.class);
                intent.putExtra("locknum",name.getText().toString());
                intent.putExtra("umbrella_code",listitem.getUmbrellacode());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
