package mx.tecnm.leon.Backend;

import mx.tecnm.leon.IRGenerator.IRValue;
import mx.tecnm.leon.IRGenerator.Instruction.Instruction;
import mx.tecnm.leon.IRGenerator.Instruction.InstructionVisitor;
import mx.tecnm.leon.IRGenerator.ValueType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackAllocator implements InstructionVisitor<Void> {

    private final List<Instruction> instructions;
    private final Map<String, Integer> stackOffsets = new HashMap<>();
    private int stackSize = 0;

    public StackAllocator(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public void allocateStackSlots(){
        for (var instruction : instructions){
            instruction.accept(this);
        }
    }

    private void allocateValue(IRValue value){
        if (value.type == ValueType.VARIABLE || value.type == ValueType.TEMPORARY){
            if (!stackOffsets.containsKey(value.operand)){
                stackSize+=8;
                stackOffsets.put(value.operand, -stackSize);
            }
        }
    }

    public int alignedStackSize() {
        return ((stackSize + 15) / 16) * 16;
    }

    @Override
    public Void visitBinary(Instruction.BinaryInstruction instruction) {
        var destination = instruction.destination;
        var left = instruction.left;
        var right = instruction.right;

        allocateValue(destination);
        allocateValue(left);
        allocateValue(right);

        return null;
    }

    @Override
    public Void visitLogic(Instruction.LogicInstruction instruction) {
        var destination = instruction.destination;
        var left = instruction.left;
        var right = instruction.right;

        allocateValue(destination);
        allocateValue(left);
        allocateValue(right);

        return null;
    }

    @Override
    public Void visitAssign(Instruction.AssignInstruction instruction) {
        var variable = instruction.value;
        var destination = instruction.destination;

        allocateValue(variable);
        allocateValue(destination);
        return null;
    }

    @Override
    public Void visitUnary(Instruction.UnaryInstruction instruction) {
        var destination = instruction.destination;
        var operand = instruction.operand;

        allocateValue(destination);
        allocateValue(operand);

        return null;
    }

    @Override
    public Void visitRead(Instruction.ReadInstruction instruction) {
        var destination = instruction.destination;

        allocateValue(destination);
        return null;
    }

    @Override
    public Void visitPrint(Instruction.PrintInstruction instruction) {
        var value = instruction.value;

        allocateValue(value);
        return null;
    }

    @Override
    public Void visitLabel(Instruction.LabelInstruction instruction) {
        return null;
    }

    @Override
    public Void visitJump(Instruction.JumpInstruction instruction) {
        return null;
    }

    @Override
    public Void visitConditionalJump(Instruction.ConditionalJumpInstruction instruction) {
        var condition = instruction.condition;

        allocateValue(condition);
        return null;
    }

    public Map<String, Integer> getStackOffsets() {
        return stackOffsets;
    }
}
