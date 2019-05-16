package com.example.vovch.ordis;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;

public class Main2Activity extends AppCompatActivity {
    Cursor c = null;
    public static SQLiteDatabase db;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        String name = data.getStringExtra("name");

        String Query = "Select * from " + DBHelper.TABLE_NAME + " where " + "name" + " = '" + name + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            String description = data.getStringExtra("description");
            String pronouncing = data.getStringExtra("pronouncing");
            String location = data.getStringExtra("location");
            String[] prons = pronouncing.split(" ");
            try {
                CopyAssets.addDict(getExternalFilesDir(null).toString(), prons);
                db.execSQL("INSERT INTO " + DBHelper.TABLE_NAME + "(name, pronouncing, description, location) VALUES (" + "'" + name + "', '" + pronouncing.replace("+", "") + "', '" + description + "', '"  + location+ "');");
            } catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "error " + e.toString(),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "already exists",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.close();
        c = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME, null);
        userAdapter.changeCursor(c);
        userAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView lv = findViewById(R.id.lv);
        String[] headers = new String[]{
                "_id", "name", "pronouncing", "description"
        };
        final DBHelper dbhelper = new DBHelper(this);
        db = dbhelper.getWritableDatabase();
        c = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME, null);
        userAdapter = new SimpleCursorAdapter(this,
                R.layout.settings_list_item,
                c,
                headers,
                new int[]{R.id.textId, R.id.textName, R.id.textPronouncing, R.id.textDescription},
                0);
        lv.setAdapter(userAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()!=R.id.add){
            finish();
            return true;
        }else{
            Intent intent = new Intent(this, Main3Activity.class);
            startActivityForResult(intent, 1);
            return true;
        }
    }


}
