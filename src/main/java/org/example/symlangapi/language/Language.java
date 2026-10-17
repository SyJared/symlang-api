package org.example.symlangapi.language;

import org.example.symlangapi.lexer.TokenType;
import java.util.LinkedHashMap;
import java.util.Map;

/** Language-specific words and diagnostics; operators and punctuation are shared. */
public enum Language {
    ENGLISH("print", words("let", "if", "else", "while", "function", "return", "true", "false"), "Expected ", " but found ", "Expected primary expression", "Expected statement but found ", "Unknown statement", "Unknown expression", "Unknown operator", "Cannot index a non-array value", " is not an array", " is not a function", "Expected ", " arguments but got ", "Execution stopped: exceeded ", " steps (possible infinite loop)", "Stack overflow — likely unbounded recursion", "Unterminated string", "Unexpected end of input"),
    TAGALOG("ilimbag", words("itakda", "kung", "kundi", "habang", "gawain", "ibalik", "totoo", "mali"), "Inaasahan ang ", " ngunit nakita ang ", "Inaasahan ang pangunahing expression", "Inaasahan ang pahayag ngunit nakita ang ", "Hindi kilalang pahayag", "Hindi kilalang expression", "Hindi kilalang operator", "Hindi maaaring mag-index ng hindi-array na halaga", " ay hindi array", " ay hindi function", "Inaasahan ang ", " argument ngunit nakuha ang ", "Huminto ang pagpapatakbo: lumampas sa ", " hakbang (posibleng walang katapusang loop)", "Stack overflow — maaaring walang katapusang recursion", "Hindi naisara ang string", "Hindi inaasahang katapusan ng input");

    private final String printName;
    private final Map<String, TokenType> keywords;
    private final Map<String, String> messages;

    Language(String printName, Map<String, TokenType> keywords, String... m) {
        this.printName = printName; this.keywords = Map.copyOf(keywords);
        String[] keys = {"expectedPrefix", "expectedMiddle", "primary", "statementPrefix", "unknownStatement", "unknownExpression", "unknownOperator", "indexNonArray", "notArraySuffix", "notFunctionSuffix", "argumentsPrefix", "argumentsMiddle", "stepPrefix", "stepSuffix", "stackOverflow", "unterminatedString", "unexpectedEof"};
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < keys.length; i++) values.put(keys[i], m[i]);
        this.messages = Map.copyOf(values);
    }
    public String printName() { return printName; }
    public Map<String, TokenType> keywords() { return keywords; }
    public String message(String key) { return messages.get(key); }

    // Canonical keyword spellings for both languages live in this table.
    private static Map<String, TokenType> words(String let, String iff, String els, String wh, String fn, String ret, String yes, String no) {
        Map<String, TokenType> m = new LinkedHashMap<>();
        m.put(let, TokenType.LET); m.put(iff, TokenType.IF); m.put(els, TokenType.ELSE); m.put(wh, TokenType.WHILE);
        m.put(fn, TokenType.FUNCTION); m.put(ret, TokenType.RETURN); m.put(yes, TokenType.TRUE); m.put(no, TokenType.FALSE);
        return m;
    }
}
