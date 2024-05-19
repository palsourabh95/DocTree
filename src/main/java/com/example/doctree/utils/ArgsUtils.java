package com.example.doctree.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class ArgsUtils {

    public static String validateArguments(String[] textSplit){
        try{
            String hyperLink = textSplit[1];
            if( (hyperLink.charAt(0) == '<') && (hyperLink.charAt(hyperLink.length()-1) == '>')){
                textSplit[1] = hyperLink.substring(1, hyperLink.length()-1);
            }

            System.out.println("Text1 : " + textSplit[0] + ":" + textSplit[1]);
            if(!Pattern.compile("\\bhttps?://\\S+\\b")
                    .matcher(textSplit[1])
                    .find()){
                return "Invalid HyperLink";
            }

//            URL url = new URL(textSplit[1]);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("HEAD");
//            int responseCode = connection.getResponseCode();
//            System.out.println("Response Code :"+ responseCode);
//            if (!(responseCode >= 200 && responseCode < 300)){
//                connection.disconnect();
//                return "HyperLink Inaccessible";
//            }
        }
        catch (Exception e){
        }
        return null;
    }

}
