package mx.tecnm.leon.IRGenerator.Instruction;

import mx.tecnm.leon.IRGenerator.IRValue;
import mx.tecnm.leon.Lexer.TokenType;

abstract public class Instruction {

    public abstract <R> R accept(InstructionVisitor<R> visitor);

    public static class BinaryInstruction extends Instruction {
        public final IRValue destination;
        public final IRValue left;
        public final TokenType operator;
        public final IRValue right;

        public BinaryInstruction(IRValue destination, IRValue left, TokenType operator, IRValue right) {
            this.destination = destination;
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public String toString() {
            return destination + " = " + left + " " + operator + " " + right;
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitBinary(this);
        }
    }

    public static class LogicInstruction extends Instruction {
        public final IRValue destination;
        public final IRValue left;
        public final TokenType operator;
        public final IRValue right;

        public LogicInstruction(IRValue destination, IRValue left, TokenType operator, IRValue right) {
            this.destination = destination;
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public String toString() {
            return destination + " = " + left + " " + operator + " " + right;
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitLogic(this);
        }
    }

    public static class AssignInstruction extends Instruction{
        public final IRValue destination;
        public final IRValue value;

        public AssignInstruction(IRValue destination, IRValue value) {
            this.destination = destination;
            this.value = value;
        }

        @Override
        public String toString() {
            return destination + " = " + value;
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitAssign(this);
        }
    }

    public static class PrintInstruction extends Instruction {
        public final IRValue value;

        public PrintInstruction(IRValue value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "print " + value;
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitPrint(this);
        }
    }

    public static class UnaryInstruction extends Instruction {
        public final IRValue destination;
        public final TokenType operator;
        public final IRValue operand;

        public UnaryInstruction(IRValue destination, TokenType operator, IRValue operand) {
            this.destination = destination;
            this.operator = operator;
            this.operand = operand;
        }

        @Override
        public String toString() {
            return destination + " = " + operator + operand;
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitUnary(this);
        }
    }

    public static class ReadInstruction extends Instruction {
        public final IRValue destination;

        public ReadInstruction(IRValue destination) {
            this.destination = destination;
        }

        @Override
        public String toString() {
            return "read " + destination;
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitRead(this);
        }
    }

    public static class LabelInstruction extends Instruction {
        public final IRValue label;

        public LabelInstruction(IRValue label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label + ":";
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitLabel(this);
        }
    }

    public static class JumpInstruction extends Instruction {
        public final IRValue destination;

        public JumpInstruction(IRValue destination) {
            this.destination = destination;
        }

        @Override
        public String toString() {
            return "goto " + destination;
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitJump(this);
        }
    }

    public static class ConditionalJumpInstruction extends Instruction {
        public final IRValue condition;
        public final IRValue destination;

        public ConditionalJumpInstruction(IRValue condition, IRValue destination) {
            this.condition = condition;
            this.destination = destination;
        }

        @Override
        public String toString() {
            return "ifFalse " + condition + " goto " + destination;
        }

        @Override
        public <R> R accept(InstructionVisitor<R> visitor) {
            return visitor.visitConditionalJump(this);
        }
    }

}
