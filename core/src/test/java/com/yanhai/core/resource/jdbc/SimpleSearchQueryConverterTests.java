package com.yanhai.core.resource.jdbc;

import com.yanhai.core.resource.AttributeNameMapper;
import com.yanhai.core.resource.SimpleAttributeNameMapper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SimpleSearchQueryConverterTests {

    @Test
    public void testConverter() throws Exception {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("isLocked", "is_locked");
        AttributeNameMapper mapper = new SimpleAttributeNameMapper(paramsMap);
        SimpleSearchQueryConverter c = new SimpleSearchQueryConverter(mapper, true);
        SearchQueryConverter.ProcessedFilter filter = c.convert("username eq 'alice' and version lt 10 and isLocked eq true", "username", true);

        System.out.println(filter);
    }
}
