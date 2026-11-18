package mx.tecnm.leon.Semantic;

import mx.tecnm.leon.Lexer.TokenType;
import mx.tecnm.leon.Parser.Expr.Expr;
import mx.tecnm.leon.Parser.Stmt.Stmt;
import mx.tecnm.leon.Parser.interfaces.ExprVisitor;
import mx.tecnm.leon.Parser.interfaces.StmtVisitor;
import mx.tecnm.leon.Semantic.Exceptions.SemanticException;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SemanticAnalyzer implements ExprVisitor<Type>, StmtVisitor<Void> {

    private final List<Stmt> statements;
    private Enviroment enviroment = new Enviroment();
    private final Map<Expr, Type> expressionTypes = new IdentityHashMap<>();

    public SemanticAnalyzer(List<Stmt> statements){
        this.statements = statements;
    }

    public void analyze(){
        for (var stmt : statements){
            stmt.accept(this);
        }
    }

    @Override
    public Void visitVar(Stmt.Var stmt) {
        var name = stmt.name;
        var type = resolveType(stmt.type.type);
        Type typeRight;

        if (stmt.initializer != null){
            typeRight = stmt.initializer.accept(this);
            //rral a = 20.1 + 10.1;
            //Aqui validar para int -> real
            if (typeRight != type){
                throw new SemanticException("Try to assign " + type + " to a type " + typeRight);
            }

            enviroment.define(name.lexeme, new Symbol(type, true));
            return null;
        }

        enviroment.define(name.lexeme, new Symbol(type, false));
        return null;
    }

    @Override
    public Type visitBinary(Expr.Binary expr) {
        var leftType = expr.left.accept(this);
        var rightType = expr.right.accept(this);
        var operator = expr.operator;

        switch (operator.type) {
            case TokenType.PLUS:
            case TokenType.MINUS:
            case TokenType.STAR:
            case TokenType.SLASH:
            case TokenType.GREATER:
            case TokenType.GREATER_EQUAL:
            case TokenType.LESS:
            case TokenType.LESS_EQUAL: {
                if (
                        (leftType != Type.INT && leftType != Type.REAL) ||
                        (rightType != Type.INT && rightType != Type.REAL)
                ) {
                    throw new SemanticException("Operands must be numeric");
                }
                break;
            }
            case TokenType.EQUAL_EQUAL:
            case TokenType.BANG_EQUAL: {
                var bothNumeric = (leftType == Type.INT || leftType == Type.REAL) &&
                                      (rightType == Type.INT || rightType == Type.REAL);
                var bothBool = (leftType == Type.BOOL && rightType == Type.BOOL);

                if (!bothNumeric && !bothBool) {
                    throw new SemanticException("Operands must be of compatible types for comparison");
                }
                break;
            }
            default:
                throw new SemanticException("Unsupported binary operator: " + operator.lexeme);
        }

        switch (operator.type) {
            case TokenType.PLUS:
            case TokenType.MINUS:
            case TokenType.STAR: {
                if (rightType == Type.REAL || leftType == Type.REAL) {
                    return saveType(expr, Type.REAL);
                }
                return saveType(expr, Type.INT);
            }
            case TokenType.SLASH:
                return saveType(expr, Type.REAL);
            case TokenType.EQUAL_EQUAL:
            case TokenType.BANG_EQUAL:
            case TokenType.LESS:
            case TokenType.LESS_EQUAL:
            case TokenType.GREATER_EQUAL:
            case TokenType.GREATER:
                return saveType(expr, Type.BOOL);
            default:
                throw new SemanticException("Unsupported binary operator: " + operator.lexeme);
        }
    }

    //not 5
    //-1
    @Override
    public Type visitUnary(Expr.Unary expr) {
        var operator = expr.op;
        var rightType = expr.right.accept(this);

        switch (operator.type){
            case TokenType.NOT: {
                if (rightType != Type.BOOL){
                    throw new SemanticException("Apply not to a invalid type");
                }
                return saveType(expr, Type.BOOL);
            }
            case TokenType.MINUS: {
                if (rightType != Type.INT && rightType != Type.REAL) {
                    throw new SemanticException("Unary minus requires a numeric operand");
                }
                return saveType(expr, rightType);
            }
            default: throw new SemanticException("Invalid aperand");

        }
    }

    @Override
    public Type visitLiteral(Expr.Literal expr) {
        return saveType(expr, resolveType(expr.value.type));
    }

    @Override
    public Type visitGrouping(Expr.Grouping expr) {
        var type = expr.grouping.accept(this);
        return saveType(expr, type);
    }

    @Override
    public Type visitVariable(Expr.Variable expr) {
        var variable = enviroment.get(expr.name.lexeme);

        if (!variable.initialized){
            throw new SemanticException("Try use a variable not initialized");
        }

        return saveType(expr, variable.type);
    }

    @Override
    public Type visitRead(Expr.Read expr) {
        return saveType(expr, Type.INT);
    }

    @Override
    public Type visitLogical(Expr.Logical expr) {
        var leftType = expr.left.accept(this);
        var rightType = expr.right.accept(this);

        if (leftType != Type.BOOL || rightType != Type.BOOL){
            throw new SemanticException("Apply logical operator a invalid types");
        }

        return saveType(expr, Type.BOOL);
    }
    //while(7)
    @Override
    public Void visitWhile(Stmt.WhileStmt stmt) {
        var condition = stmt.condition.accept(this);

        if (condition != Type.BOOL){
            throw new SemanticException("Expected a bool condition");
        }

        stmt.body.accept(this);

        return null;
    }

    @Override
    public Void visitIf(Stmt.IfStmt stmt) {
        var condition = stmt.condition.accept(this);

        if (condition != Type.BOOL){
            throw new SemanticException("Expected a bool condition");
        }

        stmt.thenBranch.accept(this);

        if (stmt.elseBranch != null){
            stmt.elseBranch.accept(this);
        }

        return null;
    }

    @Override
    public Void visitBlock(Stmt.Block stmt) {
        var environmentTemp = enviroment;
        enviroment = new Enviroment(environmentTemp);

        try {
            for (var statement : stmt.statements){
                statement.accept(this);
            }
        }finally {
            enviroment = environmentTemp;
        }

        return null;
    }

    @Override
    public Void visitPrint(Stmt.Print stmt) {
        stmt.expr.accept(this);
        return null;
    }

    @Override
    public Void visitAssignment(Stmt.Assignment stmt) {
        var variableType = enviroment.get(stmt.variable.lexeme);

        var typeValue = stmt.value.accept(this);

        if (variableType.type != typeValue){
            throw new SemanticException("Try to assign incompatible types " + variableType.type + " to " + typeValue);
        }

        enviroment.assign(stmt.variable.lexeme);

        return null;
    }

    @Override
    public Void visitVarDeclarations(Stmt.VarDeclarations stmt) {

        for (var varible : stmt.variables){
            varible.accept(this);
        }

        return null;
    }

    public Map<Expr, Type> getExpressionTypes() {
        return expressionTypes;
    }

    private Type resolveType(TokenType tokenType) {
        return switch (tokenType) {
            case TokenType.INT, TokenType.INTEGER_LITERAL -> Type.INT;
            case TokenType.REAL, TokenType.REAL_LITERAL -> Type.REAL;
            case TokenType.BOOL, TokenType.TRUE, TokenType.FALSE -> Type.BOOL;
            default -> throw new SemanticException("Invalid type");
        };
    }

    private Type saveType(Expr expr, Type type){
        expressionTypes.put(expr, type);
        return type;
    }
}
