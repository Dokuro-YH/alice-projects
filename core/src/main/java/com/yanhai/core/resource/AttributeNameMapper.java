package com.yanhai.core.resource;

public interface AttributeNameMapper {

    void addMapper(String key, String value);

    String mapToInternal(String attr);

    String[] mapToInternal(String[] attr);

    String mapFromInternal(String attr);

    String[] mapFromInternal(String[] attr);
}
