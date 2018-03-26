package io.restyql.model;

import java.util.HashMap;

public class RestySchema {
    private HashMap<String, HashMap> restyMap = new HashMap<>();

    public HashMap get(String key) {
        return getRestyMap().get(key);
    }

    public void put(String key, HashMap value) {
        this.getRestyMap().put(key, value);
    }

    public String toString() {
        return getRestyMap().toString();
    }

    public HashMap<String, HashMap> getRestyMap() {
        return restyMap;
    }

    public void setRestyMap(HashMap<String, HashMap> restyMap) {
        this.restyMap = restyMap;
    }
}
