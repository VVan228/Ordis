package com.example.vovch.ordis;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyAssets {
    public static void copyAssets(String storage, String externalFilesDir, AssetManager assetManager) {
        String[] files = null;
        try {
            files = assetManager.list("sync/" + storage + "/ru");
        } catch (IOException e) {
            Log.e("tag4me", "Failed to get asset file list.", e);
        }

        String dirPath = externalFilesDir + File.separator + storage;
        File projDir = new File(dirPath);
        if (!projDir.exists())
            projDir.mkdirs();
        dirPath += File.separator + "ru";
        projDir = new File(dirPath);
        if (!projDir.exists())
            projDir.mkdirs();

        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("sync/" + storage + "/ru/" + filename);
                File outFile = new File(externalFilesDir + File.separator + storage + File.separator + "ru", filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag4me", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) { }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) { }
                }
            }
        }
    }

    public static void addDict(String externalFilesDir, String[] strings) throws IOException {
        String gramPath = externalFilesDir + File.separator + "dict" + File.separator + "ru" + File.separator + "search.jsgf";
        String dictPath = externalFilesDir + File.separator + "dict" + File.separator + "ru" + File.separator + "hotwords";
        String phrase = "";

        BufferedWriter bufferWriter = null;
        try {
            FileWriter writer = new FileWriter(dictPath, true);
            bufferWriter = new BufferedWriter(writer);
        }
        catch (IOException e) { Log.d("tag4me", "didnt write" + e.toString()); }

        for (int i = 0; i < strings.length; i++) {
            bufferWriter.write("\n" + strings[i].replace("+", "") + " " + PhonMaker.transcription(strings[i]));

            phrase+=strings[i] + " ";
        }
        bufferWriter.close();
        phrase = phrase.trim();



        BufferedReader file = null;
        try {
             file = new BufferedReader(new FileReader(gramPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer inputBuffer = new StringBuffer();
        String line;
        while ((line = file.readLine()) != null) {
            String[] str = line.split(" ");
            if(!str[0].equals("public")) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }else{
                line = line.replace(";", "|" + phrase.replace("+", "") + ";");
                inputBuffer.append(line);
                break;
            }
        }
        Log.d("tag4me", inputBuffer.toString());
        file.close();

        FileOutputStream fileOut = new FileOutputStream(gramPath);
        fileOut.write(inputBuffer.toString().getBytes());
        fileOut.close();
    }

    public static void editDict(String externalFilesDir, String was, String is) throws IOException {
        String gramPath = externalFilesDir + File.separator + "dict" + File.separator + "ru" + File.separator + "search.jsgf";
        String dictPath = externalFilesDir + File.separator + "dict" + File.separator + "ru" + File.separator + "hotwords";


        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(gramPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer inputBuffer = new StringBuffer();
        String line;
        while ((line = file.readLine()) != null) {
            if(line.split(" ")[0].equals(was.replace("+", "").toLowerCase())){
                inputBuffer.append(is.replace("+", "").toLowerCase());
            }else inputBuffer.append(line.replace(was.replace("+", "") + " " + PhonMaker.transcription(was), is.replace("+", "") + " " + PhonMaker.transcription(is)));
        }
        Log.d("tag4me", inputBuffer.toString());
        file.close();

        FileOutputStream fileOut = new FileOutputStream(gramPath);
        fileOut.write(inputBuffer.toString().getBytes());
        Log.d("tag4me", inputBuffer.toString() + " словарь");
        fileOut.close();



        BufferedReader file2 = null;
        try {
            file2 = new BufferedReader(new FileReader(dictPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer inputBuffer2 = new StringBuffer();
        String line2;
        while ((line2 = file.readLine()) != null) {
            inputBuffer2.append(line2.replace(was.replace("+", ""), is.replace("+", "")));
        }
        Log.d("tag4me", inputBuffer2.toString());
        file2.close();

        FileOutputStream fileOut2 = new FileOutputStream(gramPath);
        fileOut2.write(inputBuffer2.toString().getBytes());
        Log.d("tag4me", inputBuffer2.toString() + " грамматика");
        fileOut2.close();
    }


    static private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
