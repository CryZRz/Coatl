package mx.tecnm.leon.IRGenerator.Instruction;

public interface InstructionVisitor<R> {
    R visitBinary(Instruction.BinaryInstruction instruction);
    R visitLogic(Instruction.LogicInstruction instruction);
    R visitAssign(Instruction.AssignInstruction instruction);
    R visitUnary(Instruction.UnaryInstruction instruction);
    R visitRead(Instruction.ReadInstruction instruction);
    R visitPrint(Instruction.PrintInstruction instruction);
    R visitLabel(Instruction.LabelInstruction instruction);
    R visitJump(Instruction.JumpInstruction instruction);
    R visitConditionalJump(Instruction.ConditionalJumpInstruction instruction);
}
