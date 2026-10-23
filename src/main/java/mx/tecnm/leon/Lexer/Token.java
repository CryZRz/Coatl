package mx.tecnm.leon.Lexer;

public class Token {
    public String lexeme;
    public TokenType type;

    public Token(String lexeme, TokenType type){
        this.lexeme = lexeme;
        this.type = type;
    }


    @Override
    public String toString() {
        return "Token{" +
                "lexeme='" + lexeme + '\'' +
                ", type=" + type +
                '}';
    }
}
