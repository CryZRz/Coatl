package mx.tecnm.leon;

import mx.tecnm.leon.exceptions.LexicalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String source;
    private int start;
    private int current;
    private final Map<String, TokenType> keywords = new HashMap<>();
    private final List<Token> tokens = new ArrayList<>();

    public Lexer(String source){
        this.source = source;
        initKeywords();
    }

    private void initKeywords() {
        this.keywords.put("if", TokenType.IF);
        this.keywords.put("int", TokenType.INT);
        this.keywords.put("real", TokenType.REAL);
        this.keywords.put("bool", TokenType.BOOL);
        this.keywords.put("else", TokenType.ELSE);
        this.keywords.put("while", TokenType.WHILE);
        this.keywords.put("print", TokenType.PRINT);
        this.keywords.put("read", TokenType.READ);
        this.keywords.put("true", TokenType.TRUE);
        this.keywords.put("false", TokenType.FALSE);
        this.keywords.put("and", TokenType.AND);
        this.keywords.put("or", TokenType.OR);
        this.keywords.put("not", TokenType.NOT);
    }

    public List<Token> generateTokens() throws LexicalException {

        while(!isEnd()){
            start = current;
            generateToken();
        }

        tokens.add(new Token("", TokenType.EOF));
        return tokens;
    }

    private void generateToken() throws LexicalException {
        var c = advance();

        switch (c){
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;
            case '/': addToken(TokenType.SLASH); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case ',': addToken(TokenType.COMMA); break;
            case '\n':
            case ' ':
            case '\t':
            case '\r': break;

            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case '!': {
                if (match('=')){
                    addToken(TokenType.BANG_EQUAL);
                    break;
                }else{
                    throw new LexicalException("Error: operador desconocido " + source.substring(start, current));
                }

            }
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '#' : {
                while (!isEnd() && peek() != '\n')
                    advance();
                break;
            }

            default:
                if (isAlpha(c)){
                    identifier();
                } else if (isNumeric(c)){
                    number();
                }else{
                    throw new LexicalException("Error: carácter desconocido " + c);
                }
        }
    }

    private boolean match(char c){
        if (isEnd())
            return false;

        if (source.charAt(current) != c)
            return false;

        current++;
        return true;
    }

    private void number() throws LexicalException {
        while (!isEnd() && isNumeric(peek()))
            advance();

        if (peek() == '.'){
            advance();

            if (!isNumeric(peek()))
                throw new LexicalException("Error: numero invalido " + source.substring(start, current));

            while (!isEnd() && isNumeric(peek()))
                advance();

            addToken(TokenType.REAL_LITERAL);
        }else{
         addToken(TokenType.INTEGER_LITERAL);
        }
    }

    private void identifier(){
        while (!isEnd() && isAlphaNumeric(peek()))
            advance();

        var lexeme = source.substring(start, current);
        addToken(keywords.getOrDefault(lexeme, TokenType.IDENTIFIER));
    }

    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isNumeric(char c){
        return (c >= '0' && c <= '9');
    }

    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isNumeric(c);
    }

    private void addToken(TokenType type){
        var lexeme = source.substring(start, current);
        tokens.add(new Token(lexeme, type));
    }

    private boolean isEnd(){
        return current >= source.length();
    }

    private char peek(){
        if (isEnd())
            return '\0';

        return source.charAt(current);
    }

    private char advance(){
        return source.charAt(current++);
    }

}
