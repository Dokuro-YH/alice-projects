package com.yanhai.core.util;

import java.util.Collections;
import java.util.List;

public class PagingUtils {

    private PagingUtils() {
    }

    public static <T> List<T> subList(List<T> input, int startIndex, int count) {
        int fromIndex = startIndex - 1;
        int toIndex = fromIndex + count;

        if (toIndex >= input.size()) {
            toIndex = input.size();
        }

        if (fromIndex >= toIndex) {
            return Collections.emptyList();
        }

        return input.subList(fromIndex, toIndex);
    }
}
