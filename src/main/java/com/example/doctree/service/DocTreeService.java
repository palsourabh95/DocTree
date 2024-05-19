package com.example.doctree.service;

import com.example.doctree.model.DocTreeValue;
import com.example.doctree.utils.SheetsServiceUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    public String updateSheet(String spreadsheetId, String sheetName, String link){
        try{
            Sheets sheetsService =  SheetsServiceUtil.getSheetsService();
            if(SheetsServiceUtil.getExistingSheet(sheetsService, spreadsheetId, sheetName) == null){
                SheetsServiceUtil.createNewSheet(sheetsService, spreadsheetId, sheetName);
            }
            Sheet sheet = SheetsServiceUtil.getExistingSheet(sheetsService, spreadsheetId, sheetName);

            List<List<Object>> data = Arrays.asList(
                    Arrays.asList(link)
            );
            ValueRange body = new ValueRange().setValues(data);
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, sheet+"!A1", body)
                    .setValueInputOption("RAW")
                    .execute();
            return "Link Successfully updated";
        }
        catch (Exception e){
            return "Failed to update link in the doc";
        }

    }




}
