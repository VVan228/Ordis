package com.example.vovch.ordis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PhonMaker {
    static HashMap<String, String> softhard_cons = new HashMap<String, String>();
    static HashMap<String, String> other_cons = new HashMap<String, String>();
    static HashMap<String, String> vowels = new HashMap<String, String>();

    static String[] others = new String[]{
            "#", "+", "-", "ь", "ъ"
    };
    static String[] startsyl = {
            "#", "ъ", "ь", "а", "я", "о", "ё", "у", "ю", "э", "е", "и", "ы", "-"
    };
    static String[] softletters = {
            "я", "ё", "ю", "и", "ь", "е"
    };

    static String transcription(String str) {
        setSets();
        return convert(str)
                .replaceAll("[#,.]","")
                .replaceAll("\\[", "")
                .replaceAll("\\]", "")
                .trim();
    }

    static void pallatize(String[] phones){
        for (int i = 0; i < phones.length; i++) {
            if(softhard_cons.containsKey(Character.toString(phones[i].charAt(0)))){
                if(Arrays.asList(softletters).contains(Character.toString(phones[i+1].charAt(0)))){
                    phones[i] = softhard_cons.get(Character.toString(phones[i].charAt(0))) + "j";
                }else{
                    phones[i] = softhard_cons.get(Character.toString(phones[i].charAt(0)));
                }
            }
            if(other_cons.containsKey(Character.toString(phones[i].charAt(0)))){
                phones[i] = other_cons.get(Character.toString(phones[i].charAt(0)));
            }
        }
    }

    static String[] convert_vowels(String[] phones){
        ArrayList<String> new_phones = new ArrayList<>();
        String prev = "";
        for (String phone:phones) {
            if(Arrays.asList(startsyl).contains(prev)){
                if(phone.charAt(0)=='я'||phone.charAt(0)=='ю'||phone.charAt(0)=='е'||phone.charAt(0)=='ё'){
                    new_phones.add("j");
                }
            }
            if(vowels.containsKey(Character.toString(phone.charAt(0)))){
                new_phones.add(vowels.get(Character.toString(phone.charAt(0))) + Character.toString(phone.charAt(1)));
            }else{
                new_phones.add(phone);
            }
            prev = Character.toString(phone.charAt(0));
        }
        return new_phones.toArray(new String[new_phones.size()]);
    }

    static String convert(String stressword){
        String phon = "#" + stressword + "#";
        String[] phones = new String[phon.length()];
        for (int i = 0; i < phon.length(); i++) {
            phones[i] = Character.toString(phon.charAt(i));
        }
        ArrayList<String> stress_phones = new ArrayList<>();
        int stress = 0;
        for(String phone : phones){
            if(phone.equals("+")){
                stress = 1;
            }else {
                stress_phones.add(phone + stress);
                stress = 0;
            }
        }

        String[] stressPhones = stress_phones.toArray(new String[stress_phones.size()]);
        pallatize(stressPhones);
        phones = convert_vowels(stressPhones);
        String [] result = new String[phones.length-2];
        for (int i = 1; i < phones.length-1; i++) {
            result[i-1] = phones[i];
        }
        return Arrays.toString(filter(result));
    }

    static String[] filter(String[] str){
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            if(!str[i].equals("ь0")){
                if(!str[i].equals("ъ0")){
                    result.add(str[i]);
                }
            }
        }
        return result.toArray(new String[0]);
    }

    static void setSets(){
        softhard_cons.put("б", "b");
        softhard_cons.put("в", "v");
        softhard_cons.put("г", "g");
        softhard_cons.put("Г", "g");
        softhard_cons.put("д", "d");
        softhard_cons.put("з", "z");
        softhard_cons.put("к", "k");
        softhard_cons.put("л", "l");
        softhard_cons.put("м", "m");
        softhard_cons.put("н", "n");
        softhard_cons.put("п", "p");
        softhard_cons.put("р", "r");
        softhard_cons.put("с", "s");
        softhard_cons.put("т", "t");
        softhard_cons.put("ф", "f");
        softhard_cons.put("х", "h");

        other_cons.put("ж", "zh");
        other_cons.put("ц", "c");
        other_cons.put("ч", "ch");
        other_cons.put("ш", "sh");
        other_cons.put("щ", "sch");
        other_cons.put("й", "j");

        vowels.put("а", "a");
        vowels.put("я", "a");
        vowels.put("у", "u");
        vowels.put("ю", "u");
        vowels.put("о", "o");
        vowels.put("ё", "o");
        vowels.put("э", "e");
        vowels.put("е", "e");
        vowels.put("и", "i");
        vowels.put("ы", "y");
    }
}
