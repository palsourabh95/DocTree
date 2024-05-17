package com.example.doctree.controller;

import com.example.doctree.service.DocTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DocTreeController {

    @Autowired
    DocTreeService docTreeService;
    String TEXT = "text";

    @PostMapping("slack/events")
    public ResponseEntity<String> postName(@RequestBody String requestBody){
        String[] requestBodyTokens = requestBody.split("&");
        Map<String, String> bodyMap = Arrays.stream(requestBodyTokens)
                .map(str -> str.split("="))
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> {
                            try {
                                return URLDecoder.decode(arr[1], "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                return arr[1];
                            }
                        }
                ));
        String[] textSplit = bodyMap.get(TEXT).split(" ");
        String validateArgs =  docTreeService.validateArguments(textSplit);
        if(validateArgs != null){
            return ResponseEntity.ok(validateArgs);
        }
        return ResponseEntity.ok("Done");
    }
}
