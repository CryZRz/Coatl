package mx.tecnm.leon.Parser.Expr;

import mx.tecnm.leon.Lexer.Token;
import mx.tecnm.leon.Parser.interfaces.ExprVisitor;

abstract public class Expr {

    public abstract <T> T accept(ExprVisitor<T> visitor);

    public static class Binary extends Expr{
        private final Expr left;
        private final Expr right;
        private final Token token;

        public Binary(Expr left, Token operator, Expr right ){
            this.left = left;
            this.right = right;
            this.token = operator;
        }

        public Expr getLeft() {
            return left;
        }

        public Expr getRight() {
            return right;
        }

        public Token getToken() {
            return token;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitBinary(this);
        }
    }

    public static class Unary extends Expr {
        private final Token op;
        private final Expr right;

        public Unary(Token op, Expr right){
            this.op = op;
            this.right = right;
        }

        public Token getOp() {
            return op;
        }

        public Expr getRight() {
            return right;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitUnary(this);
        }
    }

    public static class Literal extends Expr {
        private final Object valueOb;
        private final Token value;

        public Literal(Token value, Object valueOb){
            this.value = value;
            this.valueOb = valueOb;
        }

        public Object getValueOb() {
            return valueOb;
        }

        public Token getValue() {
            return value;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    public static class Grouping extends Expr {
        private final Expr grouping;

        public Grouping(Expr grouping){
            this.grouping = grouping;
        }

        public Expr getGrouping() {
            return grouping;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitGrouping(this);
        }
    }

    public static class Variable extends Expr{
        private final Token name;

        public Variable(Token name){
            this.name = name;
        }

        public Token getName() {
            return name;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitVariable(this);
        }
    }

    public static class Read extends Expr {

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitRead(this);
        }
    }

    public static class Logical extends Expr {
        private final Expr left;
        private final Token operator;
        private final Expr right;

        public Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public Expr getLeft() {
            return left;
        }

        public Token getOperator() {
            return operator;
        }

        public Expr getRight() {
            return right;
        }

        @Override
        public <T> T accept(ExprVisitor<T> visitor) {
            return visitor.visitLogical(this);
        }
    }

}
