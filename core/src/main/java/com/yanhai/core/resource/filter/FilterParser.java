package com.yanhai.core.resource.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FilterParser {

    private final String filterString;
    private int endPos;
    private int currentPos;
    private int markPos;

    public FilterParser(String filterString) {
        super();
        this.filterString = filterString;
        this.endPos = filterString.length();
        this.currentPos = 0;
        this.markPos = 0;
    }

    public Filter parse() throws FilterParserException {
        try {
            return readFilter();
        } catch (Exception e) {
            throw new FilterParserException(e.getMessage(), e);
        }
    }

    private Filter readFilter() {
        final Stack<Node> expressionStack = new Stack<Node>();

        final List<Node> nodes = new ArrayList<>();
        for (String word = readWord(); word != null; word = readWord()) {

            if (word.equalsIgnoreCase("and") || word.equalsIgnoreCase("or")) {

                final OperatorNode currentNode = new OperatorNode(markPos, word.equalsIgnoreCase("and") ? FilterType.AND : FilterType.OR);

                while (!expressionStack.empty() && (expressionStack.peek() instanceof OperatorNode)) {
                    final OperatorNode previousNode = (OperatorNode) expressionStack.peek();

                    if (previousNode.getPrecedence() < currentNode.getPrecedence()) {
                        break;
                    }

                    nodes.add(expressionStack.pop());
                }

                expressionStack.push(currentNode);

            } else if (word.equals("(")) {

                expressionStack.push(new LeftParenthesisNode(markPos));

            } else if (word.equals(")")) {

                while (!expressionStack.empty() && !(expressionStack.peek() instanceof LeftParenthesisNode)) {
                    nodes.add(expressionStack.pop());
                }

                if (expressionStack.empty()) {
                    final String msg = String.format("No opening parenthesis matching closing " + "parenthesis at position %d",
                            markPos);
                    throw new IllegalArgumentException(msg);
                }
                expressionStack.pop();

            } else {

                rewind();
                final int pos = currentPos;
                final Filter filterComponent = readFilterComponent();
                nodes.add(new FilterNode(pos, filterComponent));

            }
        }

        while (!expressionStack.empty()) {
            final Node node = expressionStack.pop();
            if (node instanceof LeftParenthesisNode) {
                final String msg = String.format("No closing parenthesis matching opening " + "parenthesis at position %d",
                        node.getPos());
                throw new IllegalArgumentException(msg);
            }
            nodes.add(node);
        }

        final Stack<FilterNode> filterStack = new Stack<FilterNode>();
        for (Node node : nodes) {
            if (node instanceof OperatorNode) {
                final FilterNode rightOperand = filterStack.pop();
                final FilterNode leftOperand = filterStack.pop();

                final OperatorNode operatorNode = (OperatorNode) node;

                final Filter filter;
                if (operatorNode.getFilterType() == FilterType.AND) {
                    filter = Filter.createAndFilter(leftOperand.getFilterComponent(), rightOperand.getFilterComponent());
                } else {
                    filter = Filter.createOrFilter(leftOperand.getFilterComponent(), rightOperand.getFilterComponent());
                }
                filterStack.push(new FilterNode(leftOperand.getPos(), filter));
            } else {
                filterStack.push((FilterNode) node);
            }
        }

        if (filterStack.size() == 0) {
            final String msg = String.format("Empty filter expression");
            throw new IllegalArgumentException(msg);
        } else if (filterStack.size() > 1) {
            final String msg = String.format("Unexpected characters at position %d", expressionStack.get(1).pos);
            throw new IllegalArgumentException(msg);
        }

        return filterStack.get(0).filterComponent;
    }

    private String readWord() {
        skipWhitespace();
        markPos = currentPos;

        loop:
        while (currentPos < endPos) {
            final char c = filterString.charAt(currentPos);
            switch (c) {
                case '(':
                case ')':
                    if (currentPos == markPos) {
                        currentPos++;
                    }
                    break loop;
                case ' ':
                    break loop;
                default:
                    currentPos++;
                    break;
            }
        }

        if (currentPos == markPos) {
            return null;
        }

        final String word = filterString.substring(markPos, currentPos);

        skipWhitespace();

        return word;
    }

    private Filter readFilterComponent() {
        String attribute = readWord();
        if (attribute == null) {
            final String msg = String.format("End of input at position %d but expected a filter expression", markPos);
            throw new IllegalArgumentException(msg);
        }

        final String filterAttribute = attribute;

        final String operator = readWord();

        final FilterType filterType;
        if (operator.equalsIgnoreCase("eq")) {
            filterType = FilterType.EQUALITY;
        } else if (operator.equalsIgnoreCase("co")) {
            filterType = FilterType.CONTAINS;
        } else if (operator.equalsIgnoreCase("sw")) {
            filterType = FilterType.STARTS_WITH;
        } else if (operator.equalsIgnoreCase("ew")) {
            filterType = FilterType.ENDS_WITH;
        } else if (operator.equalsIgnoreCase("pr")) {
            filterType = FilterType.PRESENCE;
        } else if (operator.equalsIgnoreCase("gt")) {
            filterType = FilterType.GREATER_THAN;
        } else if (operator.equalsIgnoreCase("ge")) {
            filterType = FilterType.GREATER_OR_EQUAL;
        } else if (operator.equalsIgnoreCase("lt")) {
            filterType = FilterType.LESS_THAN;
        } else if (operator.equalsIgnoreCase("le")) {
            filterType = FilterType.LESS_OR_EQUAL;
        } else {
            final String msg = String.format(
                    "Unrecognized attribute operator '%s' at position %d. " + "Expected: eq,co,sw,ew,pr,gt,ge,lt,le", operator,
                    markPos);
            throw new IllegalArgumentException(msg);
        }

        final Object filterValue;
        if (!filterType.equals(FilterType.PRESENCE)) {
            filterValue = readValue();
        } else {
            filterValue = null;
        }

        return new Filter(filterType, filterAttribute, filterValue != null ? filterValue.toString() : null,
                (filterValue != null) && (filterValue instanceof String), null);
    }

    private Object readValue() {
        skipWhitespace();
        markPos = currentPos;

        if (currentPos == endPos) {
            return null;
        }

        if (filterString.charAt(currentPos) == '"') {
            currentPos++;

            final StringBuilder builder = new StringBuilder();
            while (currentPos < endPos) {
                final char c = filterString.charAt(currentPos);
                switch (c) {
                    case '"':
                        currentPos++;
                        skipWhitespace();
                        return builder.toString();
                    default:
                        currentPos++;
                        builder.append(c);
                        break;
                }
            }

            final String msg = String.format("End of input in a string value that began at " + "position %d", markPos);
            throw new IllegalArgumentException(msg);
        } else {
            loop:
            while (currentPos < endPos) {
                final char c = filterString.charAt(currentPos);
                switch (c) {
                    case ' ':
                    case '(':
                    case ')':
                        break loop;

                    case '+':
                    case '-':
                    case '.':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'H':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'm':
                    case 'n':
                    case 'o':
                    case 'p':
                    case 'q':
                    case 'r':
                    case 's':
                    case 't':
                    case 'u':
                    case 'v':
                    case 'w':
                    case 'x':
                    case 'y':
                    case 'z':
                        currentPos++;
                        break;

                    default:
                        final String msg = String.format("Invalid character '%c' in a number or boolean value at " + "position %d", c,
                                currentPos);
                        throw new IllegalArgumentException(msg);
                }
            }
            final String s = filterString.substring(markPos, currentPos);
            skipWhitespace();

            Object value = null;
            try {
                value = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                try {
                    value = Double.parseDouble(s);
                } catch (NumberFormatException e2) {
                    if ("true".equalsIgnoreCase(s)) {
                        value = true;
                    } else if ("false".equalsIgnoreCase(s)) {
                        value = false;
                    }
                }
            }

            if (value == null) {
                final String msg = String.format("Invalid filter value beginning at position %d", markPos);
                throw new IllegalArgumentException(msg);
            }

            return value;
        }
    }

    private void rewind() {
        currentPos = markPos;
    }

    private void skipWhitespace() {
        while (currentPos < endPos && filterString.charAt(currentPos) == ' ') {
            currentPos++;
        }
    }

    class Node {

        private final int pos;

        public Node(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }

        @Override
        public String toString() {
            return "Node [pos=" + pos + "]";
        }

    }

    class LeftParenthesisNode extends Node {

        public LeftParenthesisNode(int pos) {
            super(pos);
        }

        @Override
        public String toString() {
            return "LeftParenthesisNode [pos=" + getPos() + "]";
        }

    }

    class OperatorNode extends Node {

        private final FilterType filterType;

        public OperatorNode(int pos, FilterType filterType) {
            super(pos);
            this.filterType = filterType;
        }

        public FilterType getFilterType() {
            return filterType;
        }

        public int getPrecedence() {
            switch (filterType) {
                case AND:
                    return 2;

                case OR:
                default:
                    return 1;
            }
        }

        @Override
        public String toString() {
            return "OperatorNode [pos=" + this.getPos() + ", filterType=" + filterType + "]";
        }

    }

    class FilterNode extends Node {

        private final Filter filterComponent;

        public FilterNode(int pos, Filter filterComponent) {
            super(pos);
            this.filterComponent = filterComponent;
        }

        public Filter getFilterComponent() {
            return filterComponent;
        }

        @Override
        public String toString() {
            return "FilterNode [pos=" + this.getPos() + ", filterComponent=" + filterComponent + "]";
        }

    }
}
