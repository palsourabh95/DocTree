package com.example.doctree.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DocTree {
    private Map<String, DocTreeValue> docTreeValueMap;
}
