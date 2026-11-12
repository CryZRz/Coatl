package mx.tecnm.leon.Parser.interfaces;

import mx.tecnm.leon.Parser.Expr.Expr;

public interface ExprVisitor<T> {
    T visitBinary(Expr.Binary expr);
    T visitUnary(Expr.Unary expr);
    T visitLiteral(Expr.Literal expr);
    T visitGrouping(Expr.Grouping expr);
    T visitVariable(Expr.Variable expr);
    T visitRead(Expr.Read expr);
    T visitLogical(Expr.Logical stmt);
}
