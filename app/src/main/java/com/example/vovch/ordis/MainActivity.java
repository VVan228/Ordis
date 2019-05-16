package com.example.vovch.ordis;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vovch.ordis.Actions.Order1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Scanner;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.PocketSphinx;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;


public class MainActivity extends AppCompatActivity implements RecognitionListener{
    SpeechRecognizer rec;
    //какие-то нужные строки
    public static String KWS_SEARCH = "hotword";
    public static String COMMAND_SEARCH = "cqommand";
    public static String PHRASE = "ордис";
    TextView txt;
    public static SQLiteDatabase db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final DBHelper dbhelper = new DBHelper(this);
        db = dbhelper.getWritableDatabase();
        Log.d("tag4me", PhonMaker.transcription("последовательность"));

        FloatingActionButton fab = findViewById(R.id.fab);
        if(txt==null) txt = findViewById(R.id.txt);

        //проверка доступа к микрофону и памяти
        String[] PERMISSIONS = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }



        //скопировать языковую модель и грамматику с словарём
        File acoust = new File(getExternalFilesDir(null) + File.separator + "hmm" + File.separator + "ru");
        if(!acoust.exists()) {
            CopyAssets.copyAssets("hmm", getExternalFilesDir(null).getAbsolutePath(), getAssets());
            CopyAssets.copyAssets("dict", getExternalFilesDir(null).getAbsolutePath(), getAssets());
        }
        else{
            Log.d("tag4me", "alreaday created");
        }

        //настройка распознователя
        recSetup();

        //начало прослушки
        if(rec!=null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "established",
                    Toast.LENGTH_SHORT);
            toast.show();
            rec.startListening(KWS_SEARCH);

        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "not established",
                    Toast.LENGTH_SHORT);
            toast.show();
        }


        //на всякий случай
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBeginningOfSpeech() {
        txt.setText("начало речи");
    }

    @Override
    public void onEndOfSpeech() {
        txt.setText("конец речи");
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if(hypothesis==null){
            return;
        }
        //если ждет пока его позовут
        if(rec.getSearchName().equals(KWS_SEARCH)){
            System.out.println("распознано");
            Toast toast = Toast.makeText(getApplicationContext(),
                    "KWS_SEARCH",
                    Toast.LENGTH_SHORT);
            toast.show();
            //функция stop запускает onResult
            rec.stop();
        }else{
            Cursor c = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE pronouncing = '" + hypothesis.getHypstr() + "'", null);
            if (c.getCount()>0){
                c.moveToFirst();
                try {
                    Method mt = Class.forName(c.getString(4)/*location*/).getDeclaredMethod("run", Context.class);
                    mt.invoke(Class.forName(c.getString(4)).newInstance(), this);
                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(this,"ошибка: " + e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    Log.d("tag4me", e.toString());
                }
                rec.stop();
            }else Log.d("tag4me", "can't find order " + hypothesis.getHypstr());
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if(rec.getSearchName().equals(KWS_SEARCH)) {
            //слушать грамматику (всегда пытается угадать)
            rec.startListening(COMMAND_SEARCH, 3000);
        }else{
            Log.d("tag4me", "начало прослушивания");
            rec.startListening(KWS_SEARCH);
        }
        txt.setText("конец речи");
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {
        //если не услышал грамматику
        txt.setText("все");
        rec.startListening(KWS_SEARCH);
    }



    private void recSetup(){
        rec = null;

        File acoust = new File(getExternalFilesDir(null) + File.separator + "hmm" + File.separator + "ru");
        File dict = new File(getExternalFilesDir(null) + File.separator + "dict" + File.separator + "ru" + File.separator + "hotwords");
        File jsgf = new File(getExternalFilesDir(null) + File.separator + "dict" + File.separator + "ru" + File.separator + "search.jsgf");

        if(acoust.isFile()) System.out.println("YES IT IS" + Arrays.toString(dict.listFiles()) + " YES");
        else System.out.println("NO IT ISNT " + Arrays.toString(acoust.listFiles()) + " NO");

        try {
            rec = defaultSetup()
                    .setAcousticModel(acoust)
                    .setDictionary(dict)
                    .setBoolean("-remove_noise", false)
                    .setKeywordThreshold(1e-5f)
                    .getRecognizer();
            rec.addListener(this);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("проблема в " + e);
        }

        if(rec!=null) {
            rec.addListener(this);
            try {
                rec.addKeyphraseSearch(KWS_SEARCH, PHRASE);
                rec.addGrammarSearch(COMMAND_SEARCH, jsgf);
                System.out.println("поиск по слову умный дом установлен");
            } catch (NullPointerException e) {
                System.out.println("поиск не установлен");
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}