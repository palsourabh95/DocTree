package com.example.doctree.controller;

import com.example.doctree.service.DocTreeService;
import com.example.doctree.service.LocalWorkBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DocTreeController {

    @Autowired
    DocTreeService docTreeService;

    @Autowired
    LocalWorkBookService localWorkBookService;
    String TEXT = "text";
    String CHANNEL_ID = "channel_id";

    /**
     * /doctree postLink <tag> <link>
     * /doctree getTags
     * /doctree getLinks <tag>
     * /doctree help
     */

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
        System.out.println(bodyMap.get("channel_id"));
        return ResponseEntity.ok(localWorkBookService.mapperFunction(
                bodyMap.get(CHANNEL_ID),
                bodyMap.get(TEXT)));
    }
}
