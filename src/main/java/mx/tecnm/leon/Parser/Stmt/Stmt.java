package mx.tecnm.leon.Parser.Stmt;

import mx.tecnm.leon.Lexer.Token;
import mx.tecnm.leon.Parser.Expr.Expr;
import mx.tecnm.leon.Parser.interfaces.StmtVisitor;

import java.util.List;

abstract public class Stmt {

    public abstract  <T> T accept(StmtVisitor<T> visitor);

   public static class WhileStmt extends Stmt{
       private final Expr condition;
       private final Stmt body;

       public WhileStmt(Expr condition, Stmt body){
           this.condition = condition;
           this.body = body;
       }

       public Expr getCondition() {
           return condition;
       }

       public Stmt getBody() {
           return body;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitWhile(this);
       }
   }

   public static class IfStmt extends Stmt{
       private final Expr condition;
       private final Stmt thenBranch;
       private final Stmt elseBranch;

       public IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) {
           this.condition = condition;
           this.thenBranch = thenBranch;
           this.elseBranch = elseBranch;
       }

       public Expr getCondition() {
           return condition;
       }

       public Stmt getThenBranch() {
           return thenBranch;
       }

       public Stmt getElseBranch() {
           return elseBranch;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitIf(this);
       }
   }

   public static class Var extends Stmt {
       private final Token type;
       private final Token name;
       private final Expr initializer;

       public Var(Token type, Token name, Expr initializer){
           this.type = type;
           this.name = name;
           this.initializer = initializer;
       }

       public Token getType() {
           return type;
       }

       public Token getName() {
           return name;
       }

       public Expr getInitializer() {
           return initializer;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitVar(this);
       }
   }

   public static class Block extends Stmt {
       private final List<Stmt> statements;

       public Block(List<Stmt> statements){
           this.statements = statements;
       }

       public List<Stmt> getStatements() {
           return statements;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitBlock(this);
       }
   }

   public static class Print extends Stmt{
       private final Expr expr;

       public Print(Expr expr){
           this.expr = expr;
       }

       public Expr getExpr() {
           return expr;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitPrint(this);
       }
   }

   public static class Assignment extends Stmt{
       private final Token variable;
        private final Expr value;

       public Assignment(Token variable, Expr value) {
           this.variable = variable;
           this.value = value;
       }

       public Token getVariable() {
           return variable;
       }

       public Expr getValue() {
           return value;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitAssignment(this);
       }
   }

   public static class VarDeclaration extends Stmt{
       private final List<Stmt.Var> variables;

       public VarDeclaration(List<Stmt.Var> variables){
           this.variables = variables;
       }

       public List<Stmt.Var> getVariables() {
           return variables;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitVarDeclaration(this);
       }
   }

}
