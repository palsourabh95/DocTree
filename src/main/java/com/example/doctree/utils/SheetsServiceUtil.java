package com.example.doctree.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SheetsServiceUtil {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "DocTree";

    public static Sheets getSheetsService() throws Exception {
        Credential credential = GoogleAuthorizeUtil.authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static Sheet getExistingSheet(Sheets sheetsService, String spreadsheetId, String sheetName) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        List<Sheet> sheets = spreadsheet.getSheets();
        Sheet sheet = null;
        boolean flag = false;
        for (Sheet x : sheets) {
            if (x.getProperties().getTitle().equals(sheetName)) {
                return x;

            }
        }
        return null;
    }

    public static void createNewSheet(Sheets sheetsService, String spreadsheetId, String sheetName) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        // Create a single sheet within the spreadsheet
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(sheetName))));

        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    }



    public static Spreadsheet getSpreadsheet(Sheets sheetsService, String spreadsheetId) throws IOException {
        Spreadsheet s = null;
        try {
            // Attempt to retrieve the spreadsheet metadata
            if(sheetsService.spreadsheets().get(spreadsheetId)!=null){
                s = sheetsService.spreadsheets().get(spreadsheetId).execute();
            }
            else{
                Spreadsheet spreadsheet = new Spreadsheet()
                        .setProperties(new SpreadsheetProperties().setTitle(spreadsheetId));
                s = sheetsService.spreadsheets().create(spreadsheet).execute();
            }
            // If there is no error, the spreadsheet exists
            return s;
        } catch (IOException e) {
            // If there is an IOException, the spreadsheet does not exist or the user does not have access
            return null;
        }
    }

}
