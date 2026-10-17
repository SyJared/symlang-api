package org.example.symlangapi;

import org.example.symlangapi.api.RunController;
import org.example.symlangapi.api.RunRequest;
import org.example.symlangapi.api.RunResponse;
import org.example.symlangapi.ast.*;
import org.example.symlangapi.interpreter.Interpreter;
import org.example.symlangapi.language.Language;
import org.example.symlangapi.lexer.*;
import org.example.symlangapi.parser.Parser;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LanguageSupportTests {
    @Test void lexerRecognizesBothKeywordSetsAndUnicodeNames() {
        String[] english = {"let", "if", "else", "while", "function", "return", "true", "false"};
        String[] tagalog = {"itakda", "kung", "kundi", "habang", "gawain", "ibalik", "totoo", "mali"};
        TokenType[] types = {TokenType.LET, TokenType.IF, TokenType.ELSE, TokenType.WHILE, TokenType.FUNCTION, TokenType.RETURN, TokenType.TRUE, TokenType.FALSE};
        for (int i = 0; i < types.length; i++) {
            assertEquals(types[i], new Lexer(english[i], Language.ENGLISH).lex().get(0).getType());
            assertEquals(types[i], new Lexer(tagalog[i], Language.TAGALOG).lex().get(0).getType());
        }
        assertEquals(TokenType.IDENTIFIER, new Lexer("let", Language.TAGALOG).lex().get(0).getType());
        assertEquals("ñandú", new Lexer("ñandú").lex().get(0).getValue());
    }

    @Test void parserBuildsEachStatementTypeForEnglishAndTagalog() {
        List<Statement> en = parse("let x = 1; if (true) {} else {} while (false) {} function f(a) { return a; } x = f(1); x[0] = 2; print(x);");
        assertInstanceOf(VariableDeclaration.class, en.get(0)); assertInstanceOf(IfStatement.class, en.get(1));
        assertInstanceOf(WhileStatement.class, en.get(2)); assertInstanceOf(FunctionDeclaration.class, en.get(3));
        assertInstanceOf(Assignment.class, en.get(4)); assertInstanceOf(IndexAssignment.class, en.get(5)); assertInstanceOf(ExpressionStatement.class, en.get(6));
        List<Statement> tl = parse("itakda x = 1; kung (totoo) {} kundi {} habang (mali) {} gawain f(a) { ibalik a; } x = f(1); ilimbag(x);");
        assertEquals(6, tl.size());
        assertInstanceOf(VariableDeclaration.class, tl.get(0)); assertInstanceOf(IfStatement.class, tl.get(1));
        assertInstanceOf(WhileStatement.class, tl.get(2)); assertInstanceOf(FunctionDeclaration.class, tl.get(3));
        assertInstanceOf(ReturnStatement.class, ((FunctionDeclaration) tl.get(3)).getBody().get(0));
        assertInstanceOf(Assignment.class, tl.get(4)); assertInstanceOf(ExpressionStatement.class, tl.get(5));
    }

    @Test void bothLanguagesRunFactorialWithIdenticalResults() {
        String en = "function factorial(n) { if (n <= 1) { return 1; } else { return n * factorial(n - 1); } } let answer = factorial(5); print(\"5! =\", answer);";
        String tl = "gawain factorial(n) { kung (n <= 1) { ibalik 1; } kundi { ibalik n * factorial(n - 1); } } itakda answer = factorial(5); ilimbag(\"5! =\", answer);";
        RunResponse english = run(en, "en"), tagalog = run(tl, "tl");
        assertNull(english.getError()); assertNull(tagalog.getError());
        assertEquals(english.getOutput(), tagalog.getOutput()); assertEquals(List.of("5! = 120"), tagalog.getOutput());
        assertEquals(english.getVariables(), tagalog.getVariables()); assertEquals(120, tagalog.getVariables().get("answer"));
    }

    @Test void errorsAreClearAndPrintIsNotAvailableInTagalog() {
        assertTrue(run("print(1);", "tl").getError().contains("not a function"));
        RunResponse badLanguage = run("", "xx"); assertTrue(badLanguage.getError().contains("Unsupported language"));
        assertTrue(run("print(\"unfinished);", "en").getError().contains("Unterminated string"));
    }

    @Test void englishConstructorAndInfiniteLoopSafetyRemainAvailable() {
        List<String> out = new ArrayList<>();
        Interpreter interpreter = new Interpreter(out::add);
        interpreter.interpret(parse("let x = 1; print(x);"));
        assertEquals(List.of("1"), out);
        assertThrows(RuntimeException.class, () -> new Interpreter(s -> {}).interpret(parse("while (true) {}")));
    }

    private List<Statement> parse(String source) { return parse(source, Language.ENGLISH); }
    private List<Statement> parse(String source, Language lang) { return new Parser(new Lexer(source, lang).lex(), lang).parseProgram(); }
    private RunResponse run(String source, String lang) { RunRequest request = new RunRequest(); request.setCode(source); request.setLang(lang); return new RunController().run(request); }
}
