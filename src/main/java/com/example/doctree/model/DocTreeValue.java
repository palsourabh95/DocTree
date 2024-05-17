package com.example.doctree.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DocTreeValue {
    List<String> values =  new ArrayList<>();
    Map<String, DocTreeValue> nextVal = new HashMap<>();
}
