package mx.tecnm.leon.Lexer;

public class Token {
    public String lexeme;
    public TokenType type;
    public Object literal;

    public Token(String lexeme, TokenType type, Object literal){
        this.lexeme = lexeme;
        this.type = type;
        this.literal = literal;
    }


    @Override
    public String toString() {
        return "Token{" +
                "lexeme='" + lexeme + '\'' +
                ", type=" + type +
                '}';
    }
}
