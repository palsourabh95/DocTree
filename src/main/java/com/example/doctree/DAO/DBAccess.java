package com.example.doctree.DAO;

import java.util.HashMap;
import java.util.Map;

public class DBAccess {

    DBAccess dao = null;
    private Map<String, String> tagToLinkMapping = new HashMap<>();

    private DBAccess(){
    }

    public DBAccess getInstance(){
        if(dao == null){
            dao = new DBAccess();
        }
        return dao;
    }


}
