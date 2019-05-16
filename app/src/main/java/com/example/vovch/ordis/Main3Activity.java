package com.example.vovch.ordis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main3Activity extends AppCompatActivity{
    int currentPosition = 0;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ListView lv = findViewById(R.id.lv2);
        FileReader fr = null;
        try {
            fr = new FileReader(getExternalFilesDir(null) + File.separator + "dict" + File.separator + "ru" + File.separator + "orders.json");
        } catch (FileNotFoundException e) {
            Log.d("tag4me", e.toString());
        }
        Gson gson = new Gson();
        OrderInfo[] orderInfo = gson.fromJson(fr, OrderInfo[].class);
        final Adapter adapter = new Adapter(orderInfo, this);
        lv.setAdapter(adapter);



        LayoutInflater li = LayoutInflater.from(this);
        final View dialogView = li.inflate(R.layout.create_order_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(dialogView);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                EditText ed = dialogView.findViewById(R.id.createOrderDialog);
                                Intent intent = new Intent();
                                intent.putExtra("name", adapter.getItem(currentPosition).getName());
                                intent.putExtra("description", adapter.getItem(currentPosition).getDescription());
                                intent.putExtra("pronouncing", ed.getText().toString().toLowerCase());
                                intent.putExtra("location", adapter.getItem(currentPosition).getLocation());

                                setResult(1, intent);

                                finish();
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        alertDialog = mDialogBuilder.create();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                alertDialog.show();
            }
        });
    }

}

class Adapter extends BaseAdapter {

    OrderInfo[] orderInfo;
    Context context;

    public Adapter(OrderInfo[] orderInfo, Context context) {
        this.orderInfo = orderInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderInfo.length;
    }

    @Override
    public OrderInfo getItem(int position) {
        return orderInfo[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.orders_list_item,
                            null);
        }
        OrderInfo of = orderInfo[position];
        ((TextView)view.findViewById(R.id.name)).setText(of.name);
        ((TextView)view.findViewById(R.id.description)).setText(of.description);
        return view;
    }

}
