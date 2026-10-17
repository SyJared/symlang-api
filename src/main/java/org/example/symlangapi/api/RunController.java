package org.example.symlangapi.api;

import org.example.symlangapi.ast.Statement;
import org.example.symlangapi.interpreter.FunctionValue;
import org.example.symlangapi.interpreter.Interpreter;
import org.example.symlangapi.lexer.Lexer;
import org.example.symlangapi.lexer.Token;
import org.example.symlangapi.language.Language;
import org.example.symlangapi.parser.Parser;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://symmon-portfolio.vercel.app")
public class RunController {

    @PostMapping("/run")
    public RunResponse run(@RequestBody RunRequest request) {

        List<String> output = new ArrayList<>();

        Language language;
        String lang = request.getLang() == null || request.getLang().isBlank() ? "en" : request.getLang();
        if (lang.equalsIgnoreCase("en")) language = Language.ENGLISH;
        else if (lang.equalsIgnoreCase("tl")) language = Language.TAGALOG;
        else return new RunResponse(output, Map.of(), "Unsupported language: " + lang + " (supported: en, tl)");

        try {
            String source = request.getCode();

            Lexer lexer = new Lexer(source, language);
            List<Token> tokens = lexer.lex();

            Parser parser = new Parser(tokens, language);
            List<Statement> program = parser.parseProgram();

            Interpreter interpreter = new Interpreter(output::add, language);
            interpreter.interpret(program);

            Map<String, Object> rawVariables = interpreter.getGlobalScope().snapshot();
            Map<String, Object> variables = sanitize(rawVariables);

            return new RunResponse(output, variables, null);

        } catch (StackOverflowError e) {
            return new RunResponse(output, Map.of(), language.message("stackOverflow"));
        } catch (RuntimeException e) {
            return new RunResponse(output, Map.of(), e.getMessage());
        }
    }

    // Converts runtime values into JSON-safe forms. Functions become their
    // toString() label instead of their raw AST — nobody viewing the output
    // needs to see the function's internal declaration/closure structure.
    private Map<String, Object> sanitize(Map<String, Object> variables) {

        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            result.put(entry.getKey(), sanitizeValue(entry.getValue()));
        }

        return result;
    }

    private Object sanitizeValue(Object value) {

        if (value instanceof FunctionValue function) {
            return function.toString();
        }

        if (value instanceof List<?> list) {
            List<Object> sanitizedList = new ArrayList<>();
            for (Object item : list) {
                sanitizedList.add(sanitizeValue(item));
            }
            return sanitizedList;
        }

        return value;
    }
}
