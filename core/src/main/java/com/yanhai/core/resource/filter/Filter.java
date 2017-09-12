package com.yanhai.core.resource.filter;

import java.util.Arrays;
import java.util.List;

public class Filter {

    private final FilterType filterType;

    private final String filterAttribute;

    private final String filterValue;

    private final boolean quoteFilterValue;

    private final List<Filter> filterComponents;

    public Filter(FilterType filterType, String filterAttribute, String filterValue, boolean quoteFilterValue,
                  List<Filter> filterComponents) {
        super();
        this.filterType = filterType;
        this.filterAttribute = filterAttribute;
        this.filterValue = filterValue;
        this.quoteFilterValue = quoteFilterValue;
        this.filterComponents = filterComponents;
    }

    public static Filter createAndFilter(Filter... filterComponents) {
        return new Filter(FilterType.AND, null, null, false, Arrays.asList(filterComponents));
    }

    public static Filter createOrFilter(Filter... filterComponents) {
        return new Filter(FilterType.OR, null, null, false, Arrays.asList(filterComponents));
    }

    public static Filter parse(String filterString) throws FilterParserException {
        FilterParser filterParser = new FilterParser(filterString);
        return filterParser.parse();
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public String getFilterAttribute() {
        return filterAttribute;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public boolean isQuoteFilterValue() {
        return quoteFilterValue;
    }

    public List<Filter> getFilterComponents() {
        return filterComponents;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        toString(builder);
        return builder.toString();
    }

    public void toString(final StringBuilder builder) {
        switch (filterType) {
            case AND:
            case OR:
                builder.append('(');

                for (int i = 0; i < filterComponents.size(); i++) {
                    if (i != 0) {
                        builder.append(' ');
                        builder.append(filterType);
                        builder.append(' ');
                    }

                    builder.append(filterComponents.get(i));
                }

                builder.append(')');
                break;

            case PRESENCE:
                builder.append(filterAttribute);
                builder.append(' ');
                builder.append(filterType);
                break;

            default:
                builder.append(filterAttribute);
                builder.append(' ');
                builder.append(filterType);
                builder.append(' ');
                builder.append(filterValue);
                break;
        }
    }
}
