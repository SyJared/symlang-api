package org.example.symlangapi.lexer;

import org.example.symlangapi.language.Language;
import java.util.*;

public class Lexer {
    private final String source;
    private final Map<String, TokenType> keywords;
    private final Language language;

    public Lexer(String source) { this(source, Language.ENGLISH); }
    public Lexer(String source, Language language) {
        this.source = source;
        this.language = language;
        this.keywords = language.keywords();
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try { Double.parseDouble(str); return true; }
        catch (NumberFormatException e) { return false; }
    }

    public Token classify(String source) {
        TokenType type = keywords.get(source);
        if (type == null) type = punctuation(source);
        if (type != null) return new Token(type, source);
        return new Token(isNumeric(source) ? TokenType.NUMBER : TokenType.IDENTIFIER, source);
    }

    private TokenType punctuation(String s) {
        return switch (s) {
            case "{" -> TokenType.LEFT_BRACE; case "}" -> TokenType.RIGHT_BRACE;
            case ";" -> TokenType.SEMICOLON; case "=" -> TokenType.EQUAL; case "+" -> TokenType.PLUS;
            case "-" -> TokenType.MINUS; case "/" -> TokenType.DIVIDE; case "*" -> TokenType.TIMES;
            case "<" -> TokenType.LESS; case ">" -> TokenType.GREATER; case "==" -> TokenType.EQUAL_EQUAL;
            case "!=" -> TokenType.BANG_EQUAL; case ">=" -> TokenType.GREATER_EQUAL; case "<=" -> TokenType.LESS_EQUAL;
            case "(" -> TokenType.LEFT_PAREN; case ")" -> TokenType.RIGHT_PAREN;
            case "[" -> TokenType.LEFT_BRACKET; case "]" -> TokenType.RIGHT_BRACKET; case "," -> TokenType.COMMA;
            default -> null;
        };
    }

    public List<Token> lex() {
        List<Token> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int line = 1, column = 1, wordLine = 1, wordColumn = 1;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '"') {
                if (!current.isEmpty()) { tokens.add(new Token(classify(current.toString()).getType(), current.toString(), wordLine, wordColumn)); current.setLength(0); }
                int startLine = line, startColumn = column;
                StringBuilder str = new StringBuilder(); i++; column++;
                while (i < source.length() && source.charAt(i) != '"') {
                    char sc = source.charAt(i++); str.append(sc);
                    if (sc == '\n') { line++; column = 1; } else column++;
                }
                if (i >= source.length()) throw new RuntimeException(language.message("unterminatedString") + " at line " + startLine);
                tokens.add(new Token(TokenType.STRING, str.toString(), startLine, startColumn)); column++;
                continue;
            }
            if (Character.isWhitespace(c)) {
                if (!current.isEmpty()) { tokens.add(new Token(classify(current.toString()).getType(), current.toString(), wordLine, wordColumn)); current.setLength(0); }
                if (c == '\n') { line++; column = 1; } else column++;
                continue;
            }
            if ("(){}[],+-*/=!;<>".indexOf(c) >= 0) {
                if (!current.isEmpty()) { tokens.add(new Token(classify(current.toString()).getType(), current.toString(), wordLine, wordColumn)); current.setLength(0); }
                int startLine = line, startColumn = column;
                String op = String.valueOf(c);
                if (i + 1 < source.length() && "=!<>".indexOf(c) >= 0 && source.charAt(i + 1) == '=') { op += "="; i++; column++; }
                Token classified = classify(op);
                tokens.add(new Token(classified.getType(), op, startLine, startColumn)); column++;
                continue;
            }
            if (current.isEmpty()) { wordLine = line; wordColumn = column; }
            current.append(c); column++;
        }
        if (!current.isEmpty()) tokens.add(new Token(classify(current.toString()).getType(), current.toString(), wordLine, wordColumn));
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
}
