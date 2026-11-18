package mx.tecnm.leon.IRGenerator;

import mx.tecnm.leon.IRGenerator.Instruction.Instruction;
import mx.tecnm.leon.Lexer.TokenType;
import mx.tecnm.leon.Semantic.Type;

import java.util.ArrayList;
import java.util.List;

public class IRBuilder {
    private final List<Instruction> instructions = new ArrayList<>();
    private int counter = 0;
    private int jumpCounter = 0;

    public IRValue newTemp(Type valueType){
        return new IRValue("t"+counter++, ValueType.TEMPORARY, valueType);
    }

    public IRValue newJump(){
        return new IRValue("l"+jumpCounter++, ValueType.LABEL, null);
    }

    public void emitBinary(IRValue temp, IRValue left, TokenType operator, IRValue right){

        var instruction = new Instruction.BinaryInstruction(
                temp,
                left,
                operator,
                right
        );

        instructions.add(instruction);
    }

    public void emitUnary(IRValue temp, TokenType operator, IRValue right){
        var instruction = new Instruction.UnaryInstruction(temp, operator, right);

        instructions.add(instruction);
    }

    public void emitRead(IRValue temp){
        var instruction = new Instruction.ReadInstruction(temp);

        instructions.add(instruction);
    }

    public void emitLogic(IRValue temp, IRValue left, TokenType operator, IRValue right){
        var instruction = new Instruction.LogicInstruction(temp, left, operator, right);
        instructions.add(instruction);
    }

    public void emitIfFalse(IRValue condition, IRValue jump){
        var instruction = new Instruction.ConditionalJumpInstruction(condition, jump);

        instructions.add(instruction);
    }

    public void emitLabel(IRValue label){
        var instruction = new Instruction.LabelInstruction(label);

        instructions.add(instruction);
    }

    public void emitGoto(IRValue label){
        var instruction = new Instruction.JumpInstruction(label);

        instructions.add(instruction);
    }

    public void emitVariable(IRValue destination, IRValue value){
        var instruction = new Instruction.AssignInstruction(destination, value);

        instructions.add(instruction);
    }

    public void emitPrint(IRValue value){
        var instruction = new Instruction.PrintInstruction(value);

        instructions.add(instruction);
    }

    public List<Instruction> getInstructions(){
        return this.instructions;
    }

}
