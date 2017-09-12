package com.yanhai.core.resource;

import java.util.HashMap;
import java.util.Map;

public class SimpleAttributeNameMapper implements AttributeNameMapper {

    private Map<String, String> paramsMap;

    public SimpleAttributeNameMapper(Map<String, String> paramsMap) {
        super();
        this.paramsMap = paramsMap == null ? new HashMap<>() : paramsMap;
    }

    public void addMapper(String key, String value) {
        this.paramsMap.put(key, value);
    }

    @Override
    public String mapToInternal(String attr) {
        String result = attr;
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public String[] mapToInternal(String[] attr) {
        String[] result = new String[attr.length];
        int pos = 0;
        for (String s : attr) {
            result[pos++] = mapToInternal(s);
        }
        return result;
    }

    @Override
    public String mapFromInternal(String attr) {
        String result = attr;
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            result = result.replaceAll(entry.getValue(), entry.getKey());
        }
        return result;
    }

    @Override
    public String[] mapFromInternal(String[] attr) {
        String[] result = new String[attr.length];
        int pos = 0;
        for (String s : attr) {
            result[pos++] = mapFromInternal(s);
        }
        return result;
    }

}
