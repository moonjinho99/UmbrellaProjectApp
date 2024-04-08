package com.example.umbrella;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.umbrella.dto.UmbrellaDTO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RentalGridListAdapter extends BaseAdapter {
    ArrayList<UmbrellaDTO> items = new ArrayList<UmbrellaDTO>();

    public static String returnUmbName = "";
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
            convertView = inflater.inflate(R.layout.rental_grid_item,parent,false);
        }

        ImageView umbrella_img = convertView.findViewById(R.id.rental_umb_img);
        TextView name = convertView.findViewById(R.id.locknum);
        TextView price = convertView.findViewById(R.id.price);
        Button returnBtn = convertView.findViewById(R.id.returnBtn);

        name.setText(Integer.toString(listitem.getUmbrellacode()));
        price.setText(Integer.toString(listitem.getPrice()));

        String img_name = listitem.getPhoto();

        Picasso.get()
                .load("http://172.30.1.61:8000/img?img_name="+img_name)
                .error(R.drawable.ic_launcher_background)
                .into(umbrella_img);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnUmbName = name.getText().toString();
                QRCodeScannerUtil.startScan((Activity) context);
//                Toast.makeText(context,"반납하기",Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

}
