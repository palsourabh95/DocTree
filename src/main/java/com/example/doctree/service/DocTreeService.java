package com.example.doctree.service;

import com.example.doctree.model.DocTree;
import com.example.doctree.model.DocTreeValue;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DocTreeService {

    DocTreeValue docTreeValue;

    public String validateArguments(String[] textSplit){
        for(String str : textSplit){
            System.out.println(str);
        }
        try{
            if(textSplit.length != 2){
                return "DocTree expects two argument [<tag> <hyperlink>]";
            }
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

            URL url = new URL(textSplit[1]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code :"+ responseCode);
            if (!(responseCode >= 200 && responseCode < 300)){
                connection.disconnect();
                return "HyperLink Inaccessible";
            }
        }
        catch (Exception e){
        }
        return null;
    }

    public boolean validateHyperlinkRegex(String hyperLink){
        return Pattern.compile("\\bhttps?://\\S+\\b")
                .matcher(hyperLink)
                .find();
    }

    public boolean validateHyperLinkAccessibility(String hyperLink){
        try{
            URL url = new URL(hyperLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 300);
        }
        catch (Exception e){
        }
        return true;
    }

    public String addResourceLinks(String resourceLevel, String link){
        String[] levelArr = resourceLevel.split("\\.");
        if(levelArr.length ==1){
            return "FAIL";
        }
        DocTreeValue docTree = docTreeValue.getNextVal().get(levelArr[0]);
        Map<String, DocTreeValue> docTreeValueMap = docTree.getNextVal();
        for( int i=1;i< levelArr.length;i++){
            //leaf node
            if(levelFound(docTreeValueMap,levelArr[i]) && i==levelArr.length-1){
               DocTreeValue docTreeValue = docTreeValueMap.get(levelArr[i]);
               List<String> currVal = docTreeValue.getValues();
               currVal.add(link);
               docTreeValue.setValues(currVal);
               docTreeValueMap.put(levelArr[i],docTreeValue);
               return "Success";
            }else if(levelFound(docTreeValueMap,levelArr[i])){
                docTreeValueMap = docTreeValueMap.get(levelArr[i]).getNextVal();
            }
        }
        return "Fail, level not found, please create it";
    }

    private Boolean levelFound(Map<String, DocTreeValue> docTreeValueMap, String level){
        if(docTreeValueMap.containsKey(level))
                return true;
        return false;
    }

    public void addNode(){
        docTreeValue =  new DocTreeValue();
        DocTreeValue docTreeValue2 =  new DocTreeValue();
        Map<String,DocTreeValue> map= new HashMap<>();
        Map<String,DocTreeValue> map1= new HashMap<>();
        map.put("Prospect",new DocTreeValue());
        map.put("Goals",new DocTreeValue());
        docTreeValue2.setNextVal(map);
        map1.put("Channel1",docTreeValue2);
        docTreeValue.setNextVal(map1);
    }

    public DocTreeValue getValue(){
        return docTreeValue;
    }
}
