package org.example.symlangapi.parser;

import org.example.symlangapi.ast.*;
import org.example.symlangapi.lexer.Token;
import org.example.symlangapi.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    private Token peek(){
        return tokens.get(current);
    }

    private void advance() {
        if (current < tokens.size() - 1) {
            current++;
        }
    }

    private Token match(TokenType expected) {

        Token currentToken = peek();

        if (currentToken.getType() == expected) {
            advance();
            return currentToken;
        }

        throw new RuntimeException(
                "Expected " + expected + " but found " + currentToken.getType()
        );
    }

    private Expression parsePrimary() {

        Token token = peek();

        if (token.getType() == TokenType.NUMBER) {
            advance();
            return new NumberLiteral(token.getValue());
        }

        if (token.getType() == TokenType.STRING) {
            advance();
            return new StringLiteral(token.getValue());
        }

        if (token.getType() == TokenType.TRUE) {
            advance();
            return new BooleanLiteral(true);
        }

        if (token.getType() == TokenType.FALSE) {
            advance();
            return new BooleanLiteral(false);
        }

        if (token.getType() == TokenType.LEFT_BRACKET) {
            advance();

            List<Expression> elements = new ArrayList<>();
            if (peek().getType() != TokenType.RIGHT_BRACKET) {
                elements.add(parseExpression());
                while (peek().getType() == TokenType.COMMA) {
                    advance();
                    elements.add(parseExpression());
                }
            }

            match(TokenType.RIGHT_BRACKET);
            return new ArrayLiteral(elements);
        }

        if (token.getType() == TokenType.IDENTIFIER) {
            advance();

            if (peek().getType() == TokenType.LEFT_PAREN) {
                return parseFunctionCallArguments(token.getValue());
            }

            return new VariableReference(token.getValue());
        }

        if (token.getType() == TokenType.LEFT_PAREN) {
            advance();
            Expression expr = parseExpression();
            match(TokenType.RIGHT_PAREN);
            return expr;
        }

        throw new RuntimeException("Expected primary expression");
    }

    private Expression parseUnary() {

        if (peek().getType() == TokenType.MINUS) {
            TokenType operator = peek().getType();
            advance();

            Expression operand = parseUnary();
            return new UnaryExpression(operator, operand);
        }

        return parsePostfix();
    }

    private Expression parsePostfix() {

        Expression expr = parsePrimary();

        while (peek().getType() == TokenType.LEFT_BRACKET) {
            advance();
            Expression index = parseExpression();
            match(TokenType.RIGHT_BRACKET);
            expr = new IndexExpression(expr, index);
        }

        return expr;
    }

    private Expression parseMultiplication() {

        Expression left = parseUnary();

        while (isMultiplicativeOperator(peek().getType())) {
            TokenType operator = peek().getType();
            advance();

            Expression right = parseUnary();
            left = new BinaryExpression(left, operator, right);
        }

        return left;
    }

    private Expression parseAddition() {

        Expression left = parseMultiplication();

        while (isAdditiveOperator(peek().getType())) {
            TokenType operator = peek().getType();
            advance();

            Expression right = parseMultiplication();
            left = new BinaryExpression(left, operator, right);
        }

        return left;
    }

    private Expression parseComparison() {

        Expression left = parseAddition();

        while (isComparisonOperator(peek().getType())) {
            TokenType condition = peek().getType();
            advance();

            Expression right = parseAddition();
            left = new ComparisonOperation(left, condition, right);
        }

        return left;
    }

    private Expression parseExpression() {
        return parseComparison();
    }

    private boolean isAdditiveOperator(TokenType type) {
        return type == TokenType.PLUS || type == TokenType.MINUS;
    }

    private boolean isMultiplicativeOperator(TokenType type) {
        return type == TokenType.TIMES || type == TokenType.DIVIDE;
    }

    private boolean isComparisonOperator(TokenType type) {
        return type == TokenType.GREATER
                || type == TokenType.LESS
                || type == TokenType.GREATER_EQUAL
                || type == TokenType.LESS_EQUAL
                || type == TokenType.EQUAL_EQUAL
                || type == TokenType.BANG_EQUAL;
    }

    // --- Statements ---

    private Statement parseStatement() {

        if (peek().getType() == TokenType.LET) {
            return parseVariableDeclaration();
        }

        if (peek().getType() == TokenType.IF) {
            return parseIfStatement();
        }

        if (peek().getType() == TokenType.WHILE) {
            return parseWhileStatement();
        }

        if (peek().getType() == TokenType.FUNCTION) {
            return parseFunctionDeclaration();
        }

        if (peek().getType() == TokenType.RETURN) {
            return parseReturnStatement();
        }

        if (peek().getType() == TokenType.IDENTIFIER) {
            return parseIdentifierStatement();
        }

        throw new RuntimeException("Expected statement but found " + peek().getType());
    }

    private VariableDeclaration parseVariableDeclaration() {

        match(TokenType.LET);
        Token name = match(TokenType.IDENTIFIER);
        match(TokenType.EQUAL);
        Expression value = parseExpression();
        match(TokenType.SEMICOLON);

        return new VariableDeclaration(name.getValue(), value);
    }

    private IfStatement parseIfStatement() {

        match(TokenType.IF);
        match(TokenType.LEFT_PAREN);
        Expression condition = parseExpression();
        match(TokenType.RIGHT_PAREN);

        List<Statement> thenBranch = parseBlock();

        List<Statement> elseBranch = null;
        if (peek().getType() == TokenType.ELSE) {
            advance();
            elseBranch = parseBlock();
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private WhileStatement parseWhileStatement() {

        match(TokenType.WHILE);
        match(TokenType.LEFT_PAREN);
        Expression condition = parseExpression();
        match(TokenType.RIGHT_PAREN);

        List<Statement> body = parseBlock();

        return new WhileStatement(condition, body);
    }

    private FunctionDeclaration parseFunctionDeclaration() {

        match(TokenType.FUNCTION);
        Token name = match(TokenType.IDENTIFIER);

        match(TokenType.LEFT_PAREN);
        List<String> parameters = new ArrayList<>();

        if (peek().getType() != TokenType.RIGHT_PAREN) {
            parameters.add(match(TokenType.IDENTIFIER).getValue());

            while (peek().getType() == TokenType.COMMA) {
                advance();
                parameters.add(match(TokenType.IDENTIFIER).getValue());
            }
        }

        match(TokenType.RIGHT_PAREN);

        List<Statement> body = parseBlock();

        return new FunctionDeclaration(name.getValue(), parameters, body);
    }

    private ReturnStatement parseReturnStatement() {

        match(TokenType.RETURN);
        Expression value = parseExpression();
        match(TokenType.SEMICOLON);

        return new ReturnStatement(value);
    }

    private Statement parseIdentifierStatement() {

        Token name = peek();
        advance();

        if (peek().getType() == TokenType.LEFT_PAREN) {
            FunctionCall call = parseFunctionCallArguments(name.getValue());
            match(TokenType.SEMICOLON);
            return new ExpressionStatement(call);
        }

        if (peek().getType() == TokenType.LEFT_BRACKET) {
            advance();
            Expression index = parseExpression();
            match(TokenType.RIGHT_BRACKET);
            match(TokenType.EQUAL);
            Expression value = parseExpression();
            match(TokenType.SEMICOLON);
            return new IndexAssignment(name.getValue(), index, value);
        }

        match(TokenType.EQUAL);
        Expression value = parseExpression();
        match(TokenType.SEMICOLON);

        return new Assignment(name.getValue(), value);
    }

    private FunctionCall parseFunctionCallArguments(String name) {

        match(TokenType.LEFT_PAREN);
        List<Expression> arguments = new ArrayList<>();

        if (peek().getType() != TokenType.RIGHT_PAREN) {
            arguments.add(parseExpression());

            while (peek().getType() == TokenType.COMMA) {
                advance();
                arguments.add(parseExpression());
            }
        }

        match(TokenType.RIGHT_PAREN);

        return new FunctionCall(name, arguments);
    }

    private List<Statement> parseBlock() {

        match(TokenType.LEFT_BRACE);

        List<Statement> statements = new ArrayList<>();
        while (peek().getType() != TokenType.RIGHT_BRACE) {
            statements.add(parseStatement());
        }

        match(TokenType.RIGHT_BRACE);

        return statements;
    }

    public List<Statement> parseProgram() {

        List<Statement> statements = new ArrayList<>();

        while (peek().getType() != TokenType.EOF) {
            statements.add(parseStatement());
        }

        return statements;
    }
}