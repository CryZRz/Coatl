package mx.tecnm.leon.Parser;

import mx.tecnm.leon.Lexer.Token;
import mx.tecnm.leon.Lexer.TokenType;
import mx.tecnm.leon.Parser.Exceptions.ParseError;
import mx.tecnm.leon.Parser.Expr.Expr;
import mx.tecnm.leon.Parser.Stmt.Stmt;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<Stmt>();
        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    /*
    * statement  → declaration
         | assignment ";"
         | if_statement
         | while_statement
         | compound_statement {}
         | print_statement
    * */
    private Stmt statement() {
        if (match(TokenType.INT, TokenType.BOOL, TokenType.REAL)) {
            return declaration();
        }
        if (match(TokenType.IF)) {
            return ifStatement();
        }
        if (match(TokenType.WHILE)) {
            return whileStatement();
        }
        if (match(TokenType.LEFT_BRACE)) {
            return blockStatement();
        }
        if (match(TokenType.PRINT)) {
            return printStatement();
        }

        return assigmentStatement();
    }

    /*
     *  identifier "=" expression ";"
     * */
    private Stmt assigmentStatement() {
        var variable = consume(TokenType.IDENTIFIER, "Expected variable name");
        consume(TokenType.EQUAL, "Expected '=' after defintion");

        var exp = expression();
        consume(TokenType.SEMICOLON, "Expected ';' ");
        return new Stmt.Assignment(variable, exp);
    }

    /*
     *  expression → logical_or
     * */
    private Expr expression() {
        return or();
    }

    private Stmt printStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '(' ");
        var expr = expression();
        consume(TokenType.RIGHT_PAREN, "Exprewcted ')'");
        consume(TokenType.SEMICOLON, "EXpected ';'");

        return new Stmt.Print(expr);
    }

    private Stmt blockStatement() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()){
            statements.add(statement());
        }

        consume(TokenType.RIGHT_BRACE, "Expected '}'");

        return new Stmt.Block(statements);
    }

    private Stmt whileStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '('");
        var expr = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')'");

        var stmt = statement();

        return new Stmt.WhileStmt(expr, stmt);
    }

    private Stmt ifStatement() {
        Stmt stmtElse = null;
        consume(TokenType.LEFT_PAREN, "Expected '(' ");
        var expr = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')'");

        var stmtThem = statement();

        if (match(TokenType.ELSE)){
            stmtElse = statement();
        }

        return new Stmt.IfStmt(expr, stmtThem, stmtElse);
    }

    /*
    * declaration → type identifier_init_list ";"
    * */
    private Stmt declaration() {
        var type = previous();
        var stmt = identifierInitList(type);
        consume(TokenType.SEMICOLON, "Expected ';'");
        return stmt;
    }

    /*
    * identifier_init_list → identifier_init ("," identifier_init)*
    * */
    private Stmt identifierInitList(Token type) {
        List<Stmt.Var> statements = new ArrayList<>();
        statements.add(identifierInit(type));
        while (match(TokenType.COMMA)){
            statements.add(identifierInit(type));
        }

        return new Stmt.VarDeclaration(statements);
    }

    private Stmt.Var identifierInit(Token type) {
        var variable = consume(TokenType.IDENTIFIER, "Expected variable name");
        if (match(TokenType.EQUAL)){
            var expr = expression();
            return new Stmt.Var(type, variable, expr);
        }

        return new Stmt.Var(type, variable, null);
    }

    /*
     *  logical_or → logical_and ("or" logical_and)*
     * */
    private Expr or() {
        var expr = and();
        while (match(TokenType.OR)){
            var operator = previous();
            var right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    /*
     *  logical_and → equality (("and" | "&&") equality)*
     * */
    private Expr and() {
        var expr = equality();

        while(match(TokenType.AND)){
            var operator = previous();
            var right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    /*
     *  equality → comparison (("==" | "!=") comparison)*
     * */
    private Expr equality() {
        var expr = comparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)){
            var operator = previous();
            var right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /*
     * comparison → additive (("<" | "<=" | ">" | ">=") additive)*
     * */
    private Expr comparison() {
        var expr = additive();

        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)){
            var operator = previous();
            var right = additive();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /*
    * additive → multiplicative (("+" | "-") multiplicative)*
    * */
    private Expr additive(){
        var expr = multiplicative();

        while (match(TokenType.PLUS, TokenType.MINUS)){
            var operator = previous();
            var right = multiplicative();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /*
    * multiplicative → unary (("*" | "/") unary)*
    * */
    private Expr multiplicative(){
        var expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)){
            var operator = previous();
            var right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /*
    * unary → ("not" | "-") unary
              | primary
    *
    * */
    private Expr unary(){
        if (match(TokenType.NOT, TokenType.MINUS)){
            var operator = previous();
            var right = unary();

            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    /*
    * primary → identifier
         | number
         | boolean
         | read_expression
         | "(" expression ")"
    *
    * */
    private Expr primary(){
        if (match(TokenType.IDENTIFIER)){
            return new Expr.Variable(previous());
        }
        if (match(TokenType.INTEGER_LITERAL, TokenType.REAL_LITERAL)){
            return new Expr.Literal(previous(), previous().literal);
        }
        if (match(TokenType.TRUE)){
            return new Expr.Literal(previous(), true);
        }
        if (match(TokenType.FALSE)){
            return new Expr.Literal(previous(), false);
        }
        if (match(TokenType.READ)){
            return readExpr();
        }
        if (match(TokenType.LEFT_PAREN)){
            var expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expected ')'");
            return new Expr.Grouping(expr);
        }

        throw new ParseError("Expected expression");
    }

    private Expr readExpr(){
        return null;
    }

    private boolean match(TokenType... types){
        for (var type : types){
            if (check(type)){
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String error){
        if (check(type)){
            return advance();
        }

        throw new ParseError(error);
    }

    private boolean check(TokenType tokenType){

        if (isAtEnd()){
            return false;
        }

        return peek().type == tokenType;
    }

    private Token advance(){
        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    private Token previous(){
        return this.tokens.get(current-1);
    }

    private Token peek(){
        return this.tokens.get(current);
    }

    private boolean isAtEnd(){
        return peek().type == TokenType.EOF;
    }

}
