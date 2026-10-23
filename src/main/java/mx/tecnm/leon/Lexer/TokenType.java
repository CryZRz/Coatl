package mx.tecnm.leon.Lexer;

public enum TokenType {
    INT,
    REAL,
    BOOL,
    IF,
    ELSE,
    WHILE,
    PRINT,
    READ,
    TRUE,
    FALSE,
    AND,
    OR,
    NOT,

    BANG_EQUAL,
    EQUAL,
    EQUAL_EQUAL,
    GREATER,
    GREATER_EQUAL,
    LESS,
    LESS_EQUAL,

    IDENTIFIER, INTEGER_LITERAL, REAL_LITERAL,

    PLUS,
    MINUS,
    STAR,
    SLASH,
    SEMICOLON,
    COMMA,
    LEFT_BRACE,
    RIGHT_BRACE,
    LEFT_PAREN,
    RIGHT_PAREN,

    EOF
}
