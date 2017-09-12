package com.yanhai.core.resource.filter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yanhai
 */
public class FilterParserTests {

    @Test
    public void testSingletonParser() throws Exception {
        FilterParser parser = new FilterParser("a eq \"1\"");

        Filter parse = parser.parse();

        assertEquals("a EQUALITY 1", parse.toString());
    }

    @Test
    public void testQuoteValueParser() throws Exception {
        FilterParser parser = new FilterParser("a eq 1.2");

        Filter parse = parser.parse();

        assertEquals("a EQUALITY 1.2", parse.toString());
    }

    @Test
    public void testAndParser() throws Exception {
        FilterParser parser = new FilterParser("a eq \"1\" and b co \"2\" and c co \"3\"");

        Filter parse = parser.parse();

        assertEquals("((a EQUALITY 1 AND b CONTAINS 2) AND c CONTAINS 3)", parse.toString());
    }

    @Test
    public void testOrParser() throws Exception {
        FilterParser parser = new FilterParser("a eq \"1\" or b co \"2\" or c co \"3\"");

        Filter parse = parser.parse();

        assertEquals("((a EQUALITY 1 OR b CONTAINS 2) OR c CONTAINS 3)", parse.toString());
    }

    @Test
    public void testAndOrParser() throws Exception {
        FilterParser parser = new FilterParser("a eq \"1\" and b co \"2\" or c co \"3\"");

        Filter parse = parser.parse();

        assertEquals("((a EQUALITY 1 AND b CONTAINS 2) OR c CONTAINS 3)", parse.toString());
    }

    @Test
    public void testParenthesisParser() throws Exception {
        FilterParser parser = new FilterParser("a eq \"1\" and (b co \"2\" or c co \"3\")");

        Filter parse = parser.parse();

        assertEquals("(a EQUALITY 1 AND (b CONTAINS 2 OR c CONTAINS 3))", parse.toString());
    }
}
