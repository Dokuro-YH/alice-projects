package com.yanhai.core.resource.jdbc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.util.StringUtils;

import com.yanhai.core.resource.AttributeNameMapper;
import com.yanhai.core.resource.SimpleAttributeNameMapper;
import com.yanhai.core.resource.filter.Filter;
import com.yanhai.core.resource.filter.FilterParserException;

public class SimpleSearchQueryConverter implements SearchQueryConverter {

    private static final Logger log = LoggerFactory.getLogger(SimpleSearchQueryConverter.class);

    private AttributeNameMapper mapper = new SimpleAttributeNameMapper(Collections.emptyMap());

    private boolean dbCaseInsensitive = false;

    public SimpleSearchQueryConverter() {
    }

    public SimpleSearchQueryConverter(AttributeNameMapper mapper, boolean dbCaseInsensitive) {
        this.mapper = mapper;
        this.dbCaseInsensitive = dbCaseInsensitive;
    }

    public boolean isDbCaseInsensitive() {
        return dbCaseInsensitive;
    }

    public void setDbCaseInsensitive(boolean dbCaseInsensitive) {
        this.dbCaseInsensitive = dbCaseInsensitive;
    }

    public void setAttributeNameMapper(AttributeNameMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ProcessedFilter convert(String filter, String sortBy, boolean ascending) {
        return convert(filter, sortBy, ascending, mapper);
    }

    @Override
    public ProcessedFilter convert(String filter, String sortBy, boolean ascending, AttributeNameMapper mapper) {
        String paramPrefix = generateParameterPrefix(filter);
        Map<String, Object> values = new HashMap<>();
        String where = StringUtils.hasText(filter) ? getWhereClause(filter, sortBy, ascending, values, mapper, paramPrefix)
                : null;
        return new ProcessedFilter(where, values, paramPrefix, StringUtils.hasText(sortBy));
    }

    protected String generateParameterPrefix(String filter) {
        if (filter == null) {
            return null;
        }
        while (true) {
            String s = new RandomValueStringGenerator().generate().toLowerCase();
            if (!filter.contains(s)) {
                return "__" + s + "_";
            }
        }
    }

    private String getWhereClause(String filterString, String sortBy, boolean ascending, Map<String, Object> values,
                                  AttributeNameMapper mapper, String paramPrefix) {
        try {
            Filter filter = convertFilter(filterString);
            String whereClause = createWhereClause(filter, values, mapper, paramPrefix);

            if (sortBy != null) {
                sortBy = mapper.mapToInternal(sortBy);
                whereClause += ORDER_BY + sortBy + (ascending ? " ASC" : " DESC");
            }
            return whereClause;
        } catch (FilterParserException e) {
            log.debug("Unable to parse " + filterString, e);
            throw new IllegalArgumentException("Invalid Filter:" + filterString + " Message:" + e.getMessage());
        }
    }

    private String createWhereClause(Filter filter, Map<String, Object> values, AttributeNameMapper mapper,
                                     String paramPrefix) {
        switch (filter.getFilterType()) {
            case AND:
                return "(" + createWhereClause(filter.getFilterComponents().get(0), values, mapper, paramPrefix) + " AND "
                        + createWhereClause(filter.getFilterComponents().get(1), values, mapper, paramPrefix) + ")";
            case OR:
                return "(" + createWhereClause(filter.getFilterComponents().get(0), values, mapper, paramPrefix) + " OR "
                        + createWhereClause(filter.getFilterComponents().get(1), values, mapper, paramPrefix) + ")";
            case EQUALITY:
                return comparisonClause(filter, "=", values, "", "", paramPrefix);
            case CONTAINS:
                return comparisonClause(filter, "LIKE", values, "%", "%", paramPrefix);
            case STARTS_WITH:
                return comparisonClause(filter, "LIKE", values, "", "%", paramPrefix);
            case PRESENCE:
                return getAttributeName(filter, mapper) + " IS NOT NULL";
            case GREATER_THAN:
                return comparisonClause(filter, ">", values, "", "", paramPrefix);
            case GREATER_OR_EQUAL:
                return comparisonClause(filter, ">=", values, "", "", paramPrefix);
            case LESS_THAN:
                return comparisonClause(filter, "<", values, "", "", paramPrefix);
            case LESS_OR_EQUAL:
                return comparisonClause(filter, "<=", values, "", "", paramPrefix);
            default:
                return null;
        }
    }

    protected String comparisonClause(Filter filter, String comparator, Map<String, Object> values, String valuePrefix,
                                      String valueSuffix, String paramPrefix) {
        String pName = getParamName(values, paramPrefix);
        String paramName = ":" + pName;
        if (filter.getFilterType() == null) {
            return getAttributeName(filter, mapper) + " IS NULL";
        } else if (filter.isQuoteFilterValue()) {
            Object value = getStringOrDate(filter.getFilterValue());
            if (value instanceof String) {

                values.put(pName, valuePrefix + value + valueSuffix);
                if (isDbCaseInsensitive()) {
                    return "" + getAttributeName(filter, mapper) + " " + comparator + " " + paramName + "";
                } else {
                    return "LOWER(" + getAttributeName(filter, mapper) + ") " + comparator + " LOWER(" + paramName + ")";
                }
            } else {
                values.put(pName, value);
                return getAttributeName(filter, mapper) + " " + comparator + " " + paramName;
            }
        } else {
            try {
                values.put(pName, Double.parseDouble(filter.getFilterValue()));
            } catch (NumberFormatException x) {
                if ("true".equalsIgnoreCase(filter.getFilterValue())) {
                    values.put(pName, Boolean.TRUE);
                } else if ("false".equalsIgnoreCase(filter.getFilterValue())) {
                    values.put(pName, Boolean.FALSE);
                } else {
                    throw new IllegalArgumentException(
                            "Invalid non quoted value [" + filter.getFilterAttribute() + " : " + filter.getFilterValue() + "]");
                }
            }
            return getAttributeName(filter, mapper) + " " + comparator + " " + paramName;
        }
    }

    protected Object getStringOrDate(String s) {
        try {
            DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return TIMESTAMP_FORMAT.parse(s);
        } catch (ParseException x) {
            return s;
        }
    }

    protected String getAttributeName(Filter filter, AttributeNameMapper mapper) {
        String name = filter.getFilterAttribute();
        return mapper.mapToInternal(name);
    }

    protected String getParamName(Map<String, Object> values, String paramPrefix) {
        return paramPrefix + values.size();
    }

    private Filter convertFilter(String filterString) throws FilterParserException {
        try {
            return Filter.parse(filterString);
        } catch (FilterParserException e) {
            log.debug("Attempting legacy scim filter conversion for [" + filterString + "]", e);
            filterString = filterString.replace("'", "\"");
            return Filter.parse(filterString);
        }
    }

    @Override
    public String map(String attribute) {
        return StringUtils.hasText(attribute) ? mapper.mapToInternal(attribute) : attribute;
    }

}
