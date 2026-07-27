package org.example.symlangapi.lexer;

import java.util.*;

public class Lexer {
    private String source;

    public Lexer(String source) {
        this.source = source;
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("let", TokenType.LET);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("{", TokenType.LEFT_BRACE);
        keywords.put("}", TokenType.RIGHT_BRACE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("return", TokenType.RETURN);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put(";", TokenType.SEMICOLON);
        keywords.put("=", TokenType.EQUAL);
        keywords.put("+", TokenType.PLUS);
        keywords.put("-", TokenType.MINUS);
        keywords.put("/", TokenType.DIVIDE);
        keywords.put("*", TokenType.TIMES);
        keywords.put("<", TokenType.LESS);
        keywords.put(">", TokenType.GREATER);
        keywords.put("==", TokenType.EQUAL_EQUAL);
        keywords.put("!=", TokenType.BANG_EQUAL);
        keywords.put(">=", TokenType.GREATER_EQUAL);
        keywords.put("<=", TokenType.LESS_EQUAL);
        keywords.put("(", TokenType.LEFT_PAREN);
        keywords.put(")", TokenType.RIGHT_PAREN);
        keywords.put("[", TokenType.LEFT_BRACKET);
        keywords.put("]", TokenType.RIGHT_BRACKET);
        keywords.put(",", TokenType.COMMA);
        keywords.put("function", TokenType.FUNCTION);
    }

    public Token classify(String source) {

        TokenType type = keywords.get(source);

        if (type != null) {
            return new Token(type, source);
        }

        if (isNumeric(source)) {
            return new Token(TokenType.NUMBER, source);
        }

        return new Token(TokenType.IDENTIFIER, source);
    }

    public List<Token> lex() {

        List<Token> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < source.length(); i++) {

            char c = source.charAt(i);

            if (c == '"') {
                if (!current.isEmpty()) {
                    tokens.add(classify(current.toString()));
                    current.setLength(0);
                }
                StringBuilder str = new StringBuilder();
                i++;
                while (i < source.length() && source.charAt(i) != '"') {
                    str.append(source.charAt(i));
                    i++;
                }
                tokens.add(new Token(TokenType.STRING, str.toString()));
                continue;
            }

            if (Character.isWhitespace(c)) {
                if (!current.isEmpty()) {
                    tokens.add(classify(current.toString()));
                    current.setLength(0);
                }
                continue;
            }

            if (c == '(' || c == ')' ||
                    c == '{' || c == '}' ||
                    c == '[' || c == ']' ||
                    c == ',' ||
                    c == '+' || c == '-' ||
                    c == '*' || c == '/' ||
                    c == '=' || c == '!' ||
                    c == ';' ||
                    c == '<' || c == '>'){

                if (!current.isEmpty()) {
                    tokens.add(classify(current.toString()));
                    current.setLength(0);
                }

                if (c == '=' && i + 1 < source.length() && source.charAt(i + 1) == '=') {
                    tokens.add(classify("=="));
                    i++;
                    continue;
                }
                if (c == '!' && i + 1 < source.length() && source.charAt(i + 1) == '=') {
                    tokens.add(classify("!="));
                    i++;
                    continue;
                }
                if (c == '>' && i + 1 < source.length() && source.charAt(i + 1) == '=') {
                    tokens.add(classify(">="));
                    i++;
                    continue;
                }
                if (c == '<' && i + 1 < source.length() && source.charAt(i + 1) == '=') {
                    tokens.add(classify("<="));
                    i++;
                    continue;
                }

                tokens.add(classify(String.valueOf(c)));
                continue;
            }

            current.append(c);
        }

        if (!current.isEmpty()) {
            tokens.add(classify(current.toString()));
        }

        tokens.add(new Token(TokenType.EOF, ""));

        return tokens;
    }
}