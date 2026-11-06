package mx.tecnm.leon.TAC;

import mx.tecnm.leon.Parser.Expr.Expr;
import mx.tecnm.leon.Parser.Stmt.Stmt;
import mx.tecnm.leon.Parser.interfaces.ExprVisitor;
import mx.tecnm.leon.Parser.interfaces.StmtVisitor;

import java.util.ArrayList;
import java.util.List;

public class TACGenerator implements StmtVisitor<Void>, ExprVisitor<String> {
    private final List<TACInstruction> instructions = new ArrayList<>();
    private int tempCount = 1;
    private int labelCount = 1;

    public List<TACInstruction> generate(List<Stmt> statements){
        for (var stmt : statements){
            stmt.accept(this);
        }

        return instructions;
    }

    private String newTemp(){
        return "_t" + (tempCount++);
    }

    private String newLabel(){
        return "L" + (labelCount++);
    }

    @Override
    public Void visitWhile(Stmt.WhileStmt stmt) {
        var startLabel = newLabel();
        var endLabel = newLabel();

        instructions.add(new TACInstruction("label", null, null, startLabel));
        var condition = stmt.getCondition().accept(this);
        instructions.add(new TACInstruction("ifFalse", condition, null, endLabel));

        stmt.getBody().accept(this);
        instructions.add(new TACInstruction("goto", null, null, startLabel));
        instructions.add(new TACInstruction("label", null, null, endLabel));

        return null;
    }

    @Override
    public Void visitIf(Stmt.IfStmt stmt) {
        var condition = stmt.getCondition().accept(this);
        var elseLabel = newLabel();

        instructions.add(new TACInstruction("ifFalse", condition, null, elseLabel));
        stmt.getThenBranch().accept(this);

        if (stmt.getElseBranch() != null){
            var endLabel = newLabel();
            instructions.add(new TACInstruction("goto", null, null, endLabel));
            instructions.add(new TACInstruction("label", null, null, elseLabel));
            stmt.getElseBranch().accept(this);
            instructions.add(new TACInstruction("label", null, null, endLabel));
        } else {
            instructions.add(new TACInstruction("label", null, null, elseLabel));
        }

        return null;
    }

    @Override
    public Void visitVar(Stmt.Var stmt) {
        if (stmt.getInitializer() != null){
            var value = stmt.getInitializer().accept(this);
            instructions.add(new TACInstruction("", value, null, stmt.getName().lexeme));
        }

        return null;
    }

    @Override
    public Void visitBlock(Stmt.Block stmt) {
        for (var statement : stmt.getStatements()){
            statement.accept(this);
        }

        return null;
    }

    @Override
    public Void visitPrint(Stmt.Print stmt) {
        var value = stmt.getExpr().accept(this);
        instructions.add(new TACInstruction("print", value, null, null));

        return null;
    }

    @Override
    public Void visitAssignment(Stmt.Assignment stmt) {
        if (stmt.getValue() != null){
            var value = stmt.getValue().accept(this);
            instructions.add(new TACInstruction("", value, null, stmt.getVariable().lexeme));
        }

        return null;
    }

    @Override
    public Void visitVarDeclaration(Stmt.VarDeclaration stmt) {
        for (var variable : stmt.getVariables()){
            variable.accept(this);
        }

        return null;
    }

    @Override
    public String visitBinary(Expr.Binary expr) {
        var left = expr.getLeft().accept(this);
        var right = expr.getRight().accept(this);
        var temp = newTemp();

        instructions.add(new TACInstruction(expr.getToken().lexeme, left, right, temp));
        return temp;
    }

    @Override
    public String visitUnary(Expr.Unary expr) {
        var right = expr.getRight().accept(this);
        var temp = newTemp();

        instructions.add(new TACInstruction(expr.getOp().lexeme, right, null, temp));
        return temp;
    }

    @Override
    public String visitLiteral(Expr.Literal expr) {
        return String.valueOf(expr.getValueOb());
    }

    @Override
    public String visitGrouping(Expr.Grouping expr) {
        return expr.getGrouping().accept(this);
    }

    @Override
    public String visitVariable(Expr.Variable expr) {
        return expr.getName().lexeme;
    }

    @Override
    public String visitRead(Expr.Read expr) {
        var temp = newTemp();
        instructions.add(new TACInstruction("read", null, null, temp));
        return temp;
    }

    @Override
    public String visitLogical(Expr.Logical stmt) {
        var left = stmt.getLeft().accept(this);
        var right = stmt.getRight().accept(this);
        var temp = newTemp();

        instructions.add(new TACInstruction(stmt.getOperator().lexeme, left, right, temp));
        return temp;
    }
}
