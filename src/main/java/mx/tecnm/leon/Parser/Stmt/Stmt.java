package mx.tecnm.leon.Parser.Stmt;

import mx.tecnm.leon.Lexer.Token;
import mx.tecnm.leon.Parser.Expr.Expr;
import mx.tecnm.leon.Parser.interfaces.StmtVisitor;

import java.util.List;

abstract public class Stmt {

    public abstract  <T> T accept(StmtVisitor<T> visitor);

   public static class WhileStmt extends Stmt{
       public final Expr condition;
       public final Stmt body;

       public WhileStmt(Expr condition, Stmt body){
           this.condition = condition;
           this.body = body;
       }


       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitWhile(this);
       }
   }

   public static class IfStmt extends Stmt{
       public final Expr condition;
       public final Stmt thenBranch;
       public final Stmt elseBranch;

       public IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) {
           this.condition = condition;
           this.thenBranch = thenBranch;
           this.elseBranch = elseBranch;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitIf(this);
       }
   }

   public static class Var extends Stmt {
       public final Token type;
       public final Token name;
       public final Expr initializer;

       public Var(Token type, Token name, Expr initializer){
           this.type = type;
           this.name = name;
           this.initializer = initializer;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitVar(this);
       }
   }

   public static class Block extends Stmt {
       public final List<Stmt> statements;

       public Block(List<Stmt> statements){
           this.statements = statements;
       }


       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitBlock(this);
       }
   }

   public static class Print extends Stmt{
       public final Expr expr;

       public Print(Expr expr){
           this.expr = expr;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitPrint(this);
       }
   }

   public static class Assignment extends Stmt{
       public final Token variable;
       public final Expr value;

       public Assignment(Token variable, Expr value) {
           this.variable = variable;
           this.value = value;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitAssignment(this);
       }
   }

   public static class VarDeclarations extends Stmt{
       public final List<Stmt.Var> variables;

       public VarDeclarations(List<Stmt.Var> variables){
           this.variables = variables;
       }

       @Override
       public <T> T accept(StmtVisitor<T> visitor) {
           return visitor.visitVarDeclarations(this);
       }
   }

}
