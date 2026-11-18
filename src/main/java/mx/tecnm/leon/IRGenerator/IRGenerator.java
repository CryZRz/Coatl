package mx.tecnm.leon.IRGenerator;

import mx.tecnm.leon.IRGenerator.Instruction.Instruction;
import mx.tecnm.leon.Lexer.TokenType;
import mx.tecnm.leon.Parser.Expr.Expr;
import mx.tecnm.leon.Parser.Stmt.Stmt;
import mx.tecnm.leon.Parser.interfaces.ExprVisitor;
import mx.tecnm.leon.Parser.interfaces.StmtVisitor;
import mx.tecnm.leon.Semantic.Type;

import java.util.List;
import java.util.Map;

public class IRGenerator implements StmtVisitor<Void>, ExprVisitor<IRValue> {
    private final List<Stmt> stmts;
    private final IRBuilder irBuilder = new IRBuilder();
    private final Map<Expr, Type> expressionTypes;

    public IRGenerator(List<Stmt> stmts, Map<Expr, Type> expressionTypes){
        this.stmts = stmts;
        this.expressionTypes = expressionTypes;
    }

    public List<Instruction> generate(){
        for (var statement : stmts){
            statement.accept(this);
        }

        return irBuilder.getInstructions();
    }

    @Override
    public IRValue visitBinary(Expr.Binary expr) {
        var left = expr.left.accept(this);
        var operator = expr.operator;
        var right = expr.right.accept(this);

        var temp = irBuilder.newTemp(expressionTypes.get(expr));

        irBuilder.emitBinary(temp, left, operator.type, right);

        return temp;
    }

    @Override
    public IRValue visitUnary(Expr.Unary expr) {
        var operator = expr.op;
        var right = expr.right.accept(this);

        var temp = irBuilder.newTemp(expressionTypes.get(expr));

        irBuilder.emitUnary(temp, operator.type, right);

        return temp;
    }

    @Override
    public IRValue visitLiteral(Expr.Literal expr) {
        if (expr.value.type == TokenType.TRUE || expr.value.type == TokenType.FALSE){
            return new IRValue(
                    expr.value.type == TokenType.TRUE ? "1" : "0",
                    ValueType.CONSTANT,
                    expressionTypes.get(expr)
            );
        }

        return new IRValue(
                expr.value.lexeme,
                ValueType.CONSTANT,
                expressionTypes.get(expr)
                );
    }

    @Override
    public IRValue visitGrouping(Expr.Grouping expr) {
        return expr.grouping.accept(this);
    }

    @Override
    public IRValue visitVariable(Expr.Variable expr) {
        return new IRValue(
                expr.name.lexeme,
                ValueType.VARIABLE,
            expressionTypes.get(expr)
        );
    }

    @Override
    public IRValue visitRead(Expr.Read expr) {
        var temp = irBuilder.newTemp(expressionTypes.get(expr) );

        irBuilder.emitRead(temp);

        return temp;
    }

    @Override
    public IRValue visitLogical(Expr.Logical expr) {
        var left = expr.left.accept(this);
        var right = expr.right.accept(this);
        var operator = expr.operator;

        var temp = irBuilder.newTemp(expressionTypes.get(expr));
        irBuilder.emitLogic(temp, left, operator.type, right);

        return temp;
    }

    @Override
    public Void visitWhile(Stmt.WhileStmt stmt) {
        var startLabel = irBuilder.newJump();
        var endLabel = irBuilder.newJump();

        irBuilder.emitLabel(startLabel);
        var condition = stmt.condition.accept(this);

        irBuilder.emitIfFalse(condition, endLabel);
        stmt.body.accept(this);
        irBuilder.emitGoto(startLabel);
        irBuilder.emitLabel(endLabel);
        return null;
    }

    @Override
    public Void visitIf(Stmt.IfStmt stmt) {
        var condition = stmt.condition.accept(this);
        var jump = irBuilder.newJump();
        irBuilder.emitIfFalse(condition, jump);

        stmt.thenBranch.accept(this);

        if (stmt.elseBranch != null){
            var jumpElse = irBuilder.newJump();
            irBuilder.emitGoto(jumpElse);
            irBuilder.emitLabel(jump);
            stmt.elseBranch.accept(this);
            irBuilder.emitLabel(jumpElse);
        }else{
            irBuilder.emitLabel(jump);
        }

        return null;
    }

    @Override
    public Void visitVar(Stmt.Var stmt) {
        if (stmt.initializer != null){
            var variable = stmt.name.lexeme;
            var value = stmt.initializer.accept(this);

            var destination = new IRValue(
                    variable,
                    ValueType.VARIABLE,
                    expressionTypes.get(stmt.initializer)
            );

            irBuilder.emitVariable(destination, value);
        }

        return null;
    }

    @Override
    public Void visitBlock(Stmt.Block stmt) {
        for (var statement : stmt.statements){
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visitPrint(Stmt.Print stmt) {
        var expresion = stmt.expr.accept(this);
        irBuilder.emitPrint(expresion);
        return null;
    }

    @Override
    public Void visitAssignment(Stmt.Assignment stmt) {
        var variable = stmt.variable.lexeme;
        var value = stmt.value.accept(this);

        var destination = new IRValue(
                variable,
                ValueType.VARIABLE,
                expressionTypes.get(stmt.value)
        );

        irBuilder.emitVariable(destination, value);

        return null;
    }

    @Override
    public Void visitVarDeclarations(Stmt.VarDeclarations stmt) {
        for (var statement : stmt.variables){
            statement.accept(this);
        }
        return null;
    }

}
