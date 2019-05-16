package com.example.vovch.ordis.Actions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Order1 {
    LocationManager locationManager;
    String OPEN_WEATHER_MAP_APPID = "d82b785794b1010bdc6ffbac143b154a";

    @SuppressLint("MissingPermission")
    public void run(Context context) {
        String[] PERMISSIONS = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        if(!hasPermissions(context, PERMISSIONS)){
            Toast toast = Toast.makeText(context,
                    "у приложения нет нужных разрешений",
                    Toast.LENGTH_SHORT);
            toast.show();

        }

        final double MyLat;
        final double MyLong;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled)
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (network_enabled)
            net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gps_loc != null && net_loc != null) {
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;
        } else {
            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }

        if(finalLoc==null){
            Toast toast = Toast.makeText(context,
                    "местоположение не поределено",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }else{
            MyLat = finalLoc.getLatitude();
            MyLong = finalLoc.getLongitude();
        }


        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String apiURL = "https://api.openweathermap.org/data/2.5/weather";


        new Thread()
        {
            public void run() {
                URL url = null;
                try {
                    url = new URL(apiURL + "?lat=" + MyLat + "&lon=" + MyLong + "&APPID=" + OPEN_WEATHER_MAP_APPID);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                InputStream stream = null;
                String res = "";
                try {
                    stream = (InputStream) url.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scanner scan = new Scanner(stream);
                while (scan.hasNextLine()) res +=scan.nextLine();
                Gson gson = new Gson();
                OpenWeatherMap op = gson.fromJson(res, OpenWeatherMap.class);
                Log.d("tag4me", op.toString());

            }
        }.start();
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
class OpenWeatherMap {
    weather[] weather;
    coord coord;
    String base;
    main main;
    wind wind;
    clouds clouds;
    long dt;
    sys sys;
    long id;
    String name;
    int code;

    @Override
    public String toString() {
        return "weather = " + Arrays.toString(weather) +
                ", coord = " + coord +
                ", base = '" + base + '\'' +
                ", main = " + main +
                ", wind = " + wind +
                ", clouds = " + clouds +
                ", dt = " + dt +
                ", sys = " + sys +
                ", id = " + id +
                ", name = '" + name + '\'' +
                ", code = " + code;
    }
}
class weather{
    int id;
    String main;
    String description;

    @Override
    public String toString() {
        return "id=" + id +
                ", main='" + main + '\'' +
                ", description='" + description + '\'';
    }
}
class coord{
    double lon;
    double lat;

    @Override
    public String toString() {
        return "lon=" + lon +
                ", lat=" + lat;
    }
}
class main{
    double temp;
    double pressure;
    int humidity;
    double temp_max;
    double temp_min;
    double sea_level;
    double grnd_level;

    @Override
    public String toString() {
        return "temp=" + temp +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", temp_max=" + temp_max +
                ", temp_min=" + temp_min +
                ", sea_level=" + sea_level +
                ", grnd_level=" + grnd_level;
    }
}
class wind{
    double speed;
    double deg;

    @Override
    public String toString() {
        return "speed=" + speed +
                ", deg=" + deg;
    }
}
class clouds{
    int all;

    @Override
    public String toString() {
        return "all=" + all;
    }
}
class rain{
    double h1;
    double h3;

    @Override
    public String toString() {
        return "h1=" + h1 +
                ", h3=" + h3;
    }
}
class snow{
    double h1;
    double h3;

    @Override
    public String toString() {
        return "h1=" + h1 +
                ", h3=" + h3;
    }
}
class sys{
    double messege;
    String country;
    long sunrise;
    long sunset;

    @Override
    public String toString() {
        return "messege=" + messege +
                ", country='" + country + '\'' +
                ", sunrise=" + sunrise +
                ", sunset=" + sunset;
    }
}

