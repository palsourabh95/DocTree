package com.example.doctree.service;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.doctree.utils.ArgsUtils.validateArguments;

@Component
public class LocalWorkBookService {

    String userHome = null;
    String absolutePath = null;

    public LocalWorkBookService(){
        userHome = System.getProperty("user.home");
        absolutePath = userHome + "/Desktop/DumpDocTreeSheets/";
        if(!(new File(absolutePath).exists())){
            new File(absolutePath).mkdirs();
        }

        System.out.println("absolute path : " + absolutePath);
    }

    private Workbook getWorkBook(String filePath){
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            return WorkbookFactory.create(fileInputStream);
        }
        catch (Exception e){
            System.out.println("Unable to open the workbook");
            throw new RuntimeException("Unable to open the workbook");
        }
    }

    public String mapperFunction(String channelId, String input){
        String[] inputArray = input.split(" ");
        if(inputArray.length == 0){
            return "Invalid Input";
        }
        return switch (inputArray[0].toLowerCase()) {
            case "help" :
                if(inputArray.length>1){
                    yield "/doctree Help expects no arguments. \nBe in your limits, Bruh!!!";
                }
                yield helpCommand();
            case "gettags" :
                if(inputArray.length>1){
                    yield "/doctree gettags expects no arguments. \nBe in your limits, Bruh!!!";
                }
                yield getTagsFromSpreadSheet(channelId);
            case "getlinks" :
                if(inputArray.length>2){
                    yield "/doctree getlinks expects one arguments i.e  <tags>. \nBe in your limits, Bruh!!!";
                }
                yield  getLinksFromSheet(channelId, inputArray[1]);
            case "postlink" :
                if(inputArray.length>3){
                    yield "/doctree postlink expects one arguments i.e  <tags> <link>. \nBe in your limits, Bruh!!!";
                }
                yield updateSheet(channelId, inputArray[1], inputArray[2]);
            default :
                yield "Invalid argument for DocTree command";
        };
    }

    public String updateSheet(String spreadsheetId, String sheetName, String link){
        String validateArgs = validateArguments(new String[]{sheetName, link});
        if(validateArgs!=null){
            return validateArgs;
        }

        String filePath = absolutePath + spreadsheetId+".xlsx"; // Path to your workbook

        Workbook workbook;
        Sheet sheet = null;

        File file = new File(filePath);

        try{
            if(!file.exists()){
                file.createNewFile();
                Workbook tempWorkBook = new XSSFWorkbook();
                FileOutputStream fileOut = new FileOutputStream(filePath);
                tempWorkBook.write(fileOut);
                tempWorkBook.close();
                fileOut.close();
            }

            workbook = getWorkBook(filePath);
            sheet = getSheetBySheetName(workbook, sheetName);
            appendDataToSheet(sheet, link);
            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.close();
            System.out.println("Workbook updated for channel " + spreadsheetId);
            workbook.close();
            fos.close();
        }
        catch (Exception e){
            return "Failed to update link in the doc";
        }
        return "Link Successfully updated";
    }

    public String getLinksFromSheet(String spreadSheetId, String sheetName){
        if(!(new File(absolutePath + spreadSheetId + ".xlsx").exists())){
            return "No Tags exist for this channel";
        }
        Workbook workbook = getWorkBook(absolutePath + spreadSheetId + ".xlsx");
        for(int i=0;i< workbook.getNumberOfSheets();i++){
            Sheet sheet = workbook.getSheetAt(i);
            if(sheet.getSheetName().equals(sheetName)){
                List<String> response = new ArrayList<>();
                for(int j=0;j<sheet.getPhysicalNumberOfRows();j++){
                    Row row = sheet.getRow(j);
                    Cell cell = row.getCell(0);
                    response.add(cell.getStringCellValue());
                }
                if(response.isEmpty()){
                    return "No links to display for the tag : "+ sheetName;
                }
                return breakListIntoString(response);
            }
        }
        return "No such tag exist";
    }

    public String getTagsFromSpreadSheet(String spreadSheetId){
        if(!(new File(absolutePath + spreadSheetId + ".xlsx").exists())){
            return "No Tags exist for this channel";
        }
        Workbook workbook = getWorkBook(absolutePath + spreadSheetId + ".xlsx");
        if(workbook.getNumberOfSheets() == 0){
            return "No tags to display";
        }
        List<String> response = new ArrayList<>();
        for(int i=0;i< workbook.getNumberOfSheets();i++)
            {
                response.add(workbook.getSheetName(i));
            }
        return breakListIntoString(response);
    }

    private Sheet getSheetBySheetName(Workbook workbook, String sheetName){
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if(sheet.getSheetName().equals(sheetName)){
                return sheet;
            }
        }
        return workbook.createSheet(sheetName);
    }

    private void appendDataToSheet(Sheet sheet, String link){
        int rowCount = sheet.getPhysicalNumberOfRows();
        Row row = sheet.createRow(rowCount);
        Cell cell = row.createCell(0);
        cell.setCellValue(link);
    }

    public String helpCommand(){
        return breakListIntoString(new ArrayList<>(){{
            add("DocTree is a comprehensive app which enables you to classify and list your links which you post in your channel");
            add("/doctree getTags                       - Get the list of tags Corresponding to the channel");
            add("/doctree getLinks <tag>                - Get the links posted corresponding to the channel");
            add("/doctree postLink <tag> <link>         - Post a link to the given tag corresponding to the channel");
        }});
    }

    public String breakListIntoString(List<String> stringList){
        return String.join("\n", stringList);
    }



}
