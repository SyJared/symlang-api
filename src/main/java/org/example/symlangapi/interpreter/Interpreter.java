package org.example.symlangapi.interpreter;

import org.example.symlangapi.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Interpreter {

    // Safety valve: without this, a submitted `while (true) {}` would hang a
    // server thread forever. This throws a clean error well before that happens.
    private static final int MAX_STEPS = 2_000_000;

    private final Environment globalScope = new Environment();
    private final Consumer<String> printSink;
    private int steps = 0;

    // Default constructor: behaves like your original console version
    public Interpreter() {
        this(System.out::println);
    }

    // API version: pass a sink that collects output into a list instead of printing
    public Interpreter(Consumer<String> printSink) {
        this.printSink = printSink;
    }

    public void interpret(List<Statement> program) {
        for (Statement statement : program) {
            execute(statement, globalScope);
        }
    }

    public Environment getGlobalScope() {
        return globalScope;
    }

    private void tick() {
        steps++;
        if (steps > MAX_STEPS) {
            throw new RuntimeException(
                    "Execution stopped: exceeded " + MAX_STEPS + " steps (possible infinite loop)"
            );
        }
    }

    private void execute(Statement statement, Environment scope) {
        tick();

        if (statement instanceof VariableDeclaration declaration) {
            executeVariableDeclaration(declaration, scope);
            return;
        }

        if (statement instanceof IfStatement ifStatement) {
            executeIfStatement(ifStatement, scope);
            return;
        }

        if (statement instanceof WhileStatement whileStatement) {
            executeWhileStatement(whileStatement, scope);
            return;
        }

        if (statement instanceof Assignment assignment) {
            executeAssignment(assignment, scope);
            return;
        }

        if (statement instanceof IndexAssignment indexAssignment) {
            executeIndexAssignment(indexAssignment, scope);
            return;
        }

        if (statement instanceof FunctionDeclaration functionDeclaration) {
            executeFunctionDeclaration(functionDeclaration, scope);
            return;
        }

        if (statement instanceof ReturnStatement returnStatement) {
            executeReturnStatement(returnStatement, scope);
            return;
        }

        if (statement instanceof ExpressionStatement expressionStatement) {
            evaluate(expressionStatement.getExpression(), scope);
            return;
        }

        throw new RuntimeException("Unknown statement");
    }

    private void executeVariableDeclaration(VariableDeclaration declaration, Environment scope) {
        Object value = evaluate(declaration.getValue(), scope);
        scope.declare(declaration.getName(), value);
    }

    private void executeIfStatement(IfStatement ifStatement, Environment scope) {

        boolean condition = (Boolean) evaluate(ifStatement.getCondition(), scope);

        if (condition) {
            executeBlock(ifStatement.getThenBranch(), new Environment(scope));
        } else if (ifStatement.getElseBranch() != null) {
            executeBlock(ifStatement.getElseBranch(), new Environment(scope));
        }
    }

    private void executeWhileStatement(WhileStatement whileStatement, Environment scope) {
        while ((Boolean) evaluate(whileStatement.getCondition(), scope)) {
            tick();
            executeBlock(whileStatement.getBody(), new Environment(scope));
        }
    }

    private void executeAssignment(Assignment assignment, Environment scope) {
        Object value = evaluate(assignment.getValue(), scope);
        scope.assign(assignment.getName(), value);
    }

    @SuppressWarnings("unchecked")
    private void executeIndexAssignment(IndexAssignment indexAssignment, Environment scope) {

        Object arrayObj = scope.get(indexAssignment.getArrayName());

        if (!(arrayObj instanceof List)) {
            throw new RuntimeException(indexAssignment.getArrayName() + " is not an array");
        }

        List<Object> list = (List<Object>) arrayObj;
        int index = (Integer) evaluate(indexAssignment.getIndex(), scope);
        Object value = evaluate(indexAssignment.getValue(), scope);

        list.set(index, value);
    }

    private void executeFunctionDeclaration(FunctionDeclaration declaration, Environment scope) {
        FunctionValue function = new FunctionValue(declaration, scope);
        scope.declare(declaration.getName(), function);
    }

    private void executeReturnStatement(ReturnStatement returnStatement, Environment scope) {
        Object value = evaluate(returnStatement.getValue(), scope);
        throw new ReturnException(value);
    }

    private void executeBlock(List<Statement> statements, Environment scope) {
        for (Statement statement : statements) {
            execute(statement, scope);
        }
    }

    private Object evaluate(Expression expr, Environment scope) {
        tick();

        if (expr instanceof NumberLiteral number) {
            return evaluateNumber(number);
        }
        if (expr instanceof BooleanLiteral bool) {
            return evaluateBoolean(bool);
        }
        if (expr instanceof StringLiteral string) {
            return evaluateString(string);
        }
        if (expr instanceof ArrayLiteral arrayLiteral) {
            return evaluateArrayLiteral(arrayLiteral, scope);
        }
        if (expr instanceof IndexExpression indexExpression) {
            return evaluateIndex(indexExpression, scope);
        }
        if (expr instanceof VariableReference reference) {
            return evaluateVariable(reference, scope);
        }
        if (expr instanceof BinaryExpression binary) {
            return evaluateBinary(binary, scope);
        }
        if (expr instanceof ComparisonOperation comparison) {
            return evaluateComparison(comparison, scope);
        }
        if (expr instanceof UnaryExpression unary) {
            return evaluateUnary(unary, scope);
        }
        if (expr instanceof FunctionCall call) {
            return evaluateFunctionCall(call, scope);
        }

        throw new RuntimeException("Unknown expression");
    }

    private int evaluateNumber(NumberLiteral number) {
        return Integer.parseInt(number.getValue());
    }

    private boolean evaluateBoolean(BooleanLiteral bool) {
        return bool.getValue();
    }

    private String evaluateString(StringLiteral string) {
        return string.getValue();
    }

    private Object evaluateArrayLiteral(ArrayLiteral arrayLiteral, Environment scope) {
        List<Object> values = new ArrayList<>();
        for (Expression element : arrayLiteral.getElements()) {
            values.add(evaluate(element, scope));
        }
        return values;
    }

    private Object evaluateIndex(IndexExpression indexExpression, Environment scope) {

        Object arrayObj = evaluate(indexExpression.getArray(), scope);

        if (!(arrayObj instanceof List<?> list)) {
            throw new RuntimeException("Cannot index a non-array value");
        }

        int index = (Integer) evaluate(indexExpression.getIndex(), scope);
        return list.get(index);
    }

    private Object evaluateVariable(VariableReference reference, Environment scope) {
        return scope.get(reference.getName());
    }

    private int asInt(Expression expr, Environment scope) {
        return (Integer) evaluate(expr, scope);
    }

    private Object evaluateBinary(BinaryExpression binary, Environment scope) {

        Object leftVal = evaluate(binary.getLeft(), scope);
        Object rightVal = evaluate(binary.getRight(), scope);

        if (binary.getOperator() == org.example.symlangapi.lexer.TokenType.PLUS
                && (leftVal instanceof String || rightVal instanceof String)) {
            return String.valueOf(leftVal) + String.valueOf(rightVal);
        }

        int left = (Integer) leftVal;
        int right = (Integer) rightVal;

        return switch (binary.getOperator()) {
            case PLUS -> left + right;
            case MINUS -> left - right;
            case TIMES -> left * right;
            case DIVIDE -> left / right;
            default -> throw new RuntimeException("Unknown operator");
        };
    }

    private boolean evaluateComparison(ComparisonOperation condition, Environment scope) {
        int left = asInt(condition.getLeft(), scope);
        int right = asInt(condition.getRight(), scope);

        return switch (condition.getCondition()) {
            case EQUAL_EQUAL -> left == right;
            case GREATER -> left > right;
            case GREATER_EQUAL -> left >= right;
            case LESS -> left < right;
            case LESS_EQUAL -> left <= right;
            case BANG_EQUAL -> left != right;
            default -> throw new RuntimeException("Unknown operator");
        };
    }

    private int evaluateUnary(UnaryExpression unary, Environment scope) {
        return -asInt(unary.getOperand(), scope);
    }

    private Object evaluateFunctionCall(FunctionCall call, Environment scope) {

        if (call.getName().equals("print")) {
            return evaluatePrintCall(call, scope);
        }

        Object callee = scope.get(call.getName());

        if (!(callee instanceof FunctionValue function)) {
            throw new RuntimeException(call.getName() + " is not a function");
        }

        FunctionDeclaration declaration = function.getDeclaration();
        List<String> parameters = declaration.getParameters();
        List<Expression> arguments = call.getArguments();

        if (parameters.size() != arguments.size()) {
            throw new RuntimeException(
                    "Expected " + parameters.size() + " arguments but got " + arguments.size()
            );
        }

        Environment callScope = new Environment(function.getClosure());

        for (int i = 0; i < parameters.size(); i++) {
            Object argValue = evaluate(arguments.get(i), scope);
            callScope.declare(parameters.get(i), argValue);
        }

        try {
            executeBlock(declaration.getBody(), callScope);
        } catch (ReturnException returnException) {
            return returnException.getValue();
        }

        return null;
    }

    private Object evaluatePrintCall(FunctionCall call, Environment scope) {

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < call.getArguments().size(); i++) {
            Object value = evaluate(call.getArguments().get(i), scope);
            output.append(stringify(value));

            if (i < call.getArguments().size() - 1) {
                output.append(" ");
            }
        }

        printSink.accept(output.toString());
        return null;
    }

    private String stringify(Object value) {
        if (value == null) {
            return "null";
        }
        return String.valueOf(value);
    }
}