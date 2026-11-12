package mx.tecnm.leon.Parser.interfaces;

import mx.tecnm.leon.Parser.Expr.Expr;
import mx.tecnm.leon.Parser.Stmt.Stmt;

public interface StmtVisitor<T> {
    T visitWhile(Stmt.WhileStmt stmt);
    T visitIf(Stmt.IfStmt stmt);
    T visitVar(Stmt.Var stmt);
    T visitBlock(Stmt.Block stmt);
    T visitPrint(Stmt.Print stmt);
    T visitAssignment(Stmt.Assignment stmt);
    T visitVarDeclaration(Stmt.VarDeclaration stmt);
}
