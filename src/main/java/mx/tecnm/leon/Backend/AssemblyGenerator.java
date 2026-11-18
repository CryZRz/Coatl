package mx.tecnm.leon.Backend;

import mx.tecnm.leon.Backend.Exceptions.AssemblyException;
import mx.tecnm.leon.IRGenerator.IRValue;
import mx.tecnm.leon.IRGenerator.Instruction.Instruction;
import mx.tecnm.leon.IRGenerator.Instruction.InstructionVisitor;
import mx.tecnm.leon.IRGenerator.ValueType;
import mx.tecnm.leon.Lexer.TokenType;

import java.util.List;

public class AssemblyGenerator implements InstructionVisitor<String> {

    private final List<Instruction> instructions;
    private final StackAllocator stackAllocator;

    public AssemblyGenerator(List<Instruction> instructions) {
        this.instructions = instructions;
        stackAllocator = new StackAllocator(instructions);
    }

    public String generate(){
        stackAllocator.allocateStackSlots();

        var sourceCode = new StringBuilder();
        sourceCode.append(generateGlobalSection());
        sourceCode.append(generatePrologue());

        for (var instruction : instructions){
            sourceCode.append(instruction.accept(this));
        }

        sourceCode.append(generateEpilogue());
        return sourceCode.toString();
    }

    private String generatePrologue(){
        return String.format(
                "main:\n push rbp \n mov rbp, rsp \n sub rsp, %d \n",
                stackAllocator.alignedStackSize()
                );
    }

    private String generateEpilogue(){
        return "mov rsp, rbp \n pop rbp \n mov eax, 0 \n ret \n";
    }

    private String resolveImports(){
        var needsPrint = true;
        var needsScan = true;
        var stringBuilder = new StringBuilder();

        for (var instruction : instructions){
            if (instruction instanceof Instruction.PrintInstruction){
                if (needsPrint) {
                    stringBuilder.append("extern printf \n");
                    needsPrint = false;
                }
            }
            if (instruction instanceof Instruction.ReadInstruction){
                if (needsScan) {
                    stringBuilder.append("extern scanf \n");
                    needsScan = false;
                };
            }
        }

        return stringBuilder.toString();
    }

    private String generateGlobalSection(){
        return resolveImports() +
                generateDataSection() +
                "section .text \n global main\n\n";
    }

    private String generateDataSection(){
        var needsPrint = true;
        var needsScan = true;
        var stringBuilder = new StringBuilder();

        stringBuilder.append("section .data \n");

        for (var instruction : instructions){
            if (instruction instanceof Instruction.PrintInstruction){
                if (needsPrint) {
                    stringBuilder.append("fmt_int db '%ld', 10, 0 \n");
                    needsPrint = false;
                }
            }
            if (instruction instanceof Instruction.ReadInstruction){
                if (needsScan) {
                    stringBuilder.append("fmt_read_int db '%ld', 0 \n");
                    needsScan = false;
                };
            }
        }

        return  stringBuilder.toString();
    }

    private String getMemoryOperand(IRValue value){
        return  String.format(
                "[rbp %d]",
                stackAllocator.getStackOffsets().get(value.operand)
        );
    }

    private String getOperand(IRValue value){
        if (value.type == ValueType.CONSTANT){
            return value.operand;
        }

        return getMemoryOperand(value);
    }

    @Override
    public String visitBinary(Instruction.BinaryInstruction instruction) {
        var destination = instruction.destination;
        var left = instruction.left;
        var right = instruction.right;

        if (instruction.operator == TokenType.SLASH){
            return String.format(
                    "mov rax, %s \n cqo \n mov rcx, %s \n idiv rcx \n mov qword %s, rax \n",
                    getOperand(left),
                    getOperand(right),
                    getMemoryOperand(destination)
            );
        }

        var operation = switch (instruction.operator){
            case TokenType.PLUS -> "add";
            case TokenType.MINUS -> "sub";
            case TokenType.STAR -> "imul";
            default -> throw new AssemblyException("Error desconocido");
        };

        return String.format(
                "mov rax, %s \n %s rax, %s \n mov qword %s, rax\n",
                getOperand(left),
                operation,
                getOperand(right),
                getMemoryOperand(destination)
        );
    }

    @Override
    public String visitLogic(Instruction.LogicInstruction instruction) {
        var destination = instruction.destination;
        var left = instruction.left;
        var operator = instruction.operator;
        var right = instruction.right;

        if (operator == TokenType.AND || operator == TokenType.OR) {
            var bitwiseOp = operator == TokenType.AND ? "and" : "or";
            return String.format(
                    "mov rax, %s \n %s rax, %s \n mov qword %s, rax \n",
                    getOperand(left),
                    bitwiseOp,
                    getOperand(right),
                    getMemoryOperand(destination)
            );
        }

        var setInstruction = switch (operator) {
            case TokenType.EQUAL_EQUAL -> "sete";
            case TokenType.BANG_EQUAL -> "setne";
            case TokenType.GREATER -> "setg";
            case TokenType.GREATER_EQUAL -> "setge";
            case TokenType.LESS -> "setl";
            case TokenType.LESS_EQUAL -> "setle";
            default -> throw new AssemblyException("Unexpected operator");
        };

        return String.format(
                "mov rax, %s \n cmp rax, %s \n %s al \n movzx rax, al \n mov qword %s, rax \n",
                getOperand(left),
                getOperand(right),
                setInstruction,
                getMemoryOperand(destination)
        );
    }

    @Override
    public String visitAssign(Instruction.AssignInstruction instruction) {
        var value = instruction.value;
        var destination = instruction.destination;

        if (value.type == ValueType.CONSTANT){
            return String.format(
                    "mov qword %s, %s \n",
                    getMemoryOperand(destination),
                    value.operand
            );
        }

        return String.format(
                "mov rax, %s \n mov qword %s, rax \n",
                getMemoryOperand(value),
                getMemoryOperand(destination)
        );
    }

    @Override
    public String visitUnary(Instruction.UnaryInstruction instruction) {
        var destination = instruction.destination;
        var operator = instruction.operator;
        var operand = instruction.operand;

        switch (operator){
            case TokenType.MINUS: {
                return String.format(
                        "mov rax, %s \n %s rax \n mov qword %s, rax\n",
                        getOperand(operand),
                        "neg",
                        getMemoryOperand(destination)
                );
            }
            case TokenType.NOT: {
                return String.format(
                        "mov rax, %s \n cmp rax, 0 \n sete al \n movzx rax, al \n mov qword %s, rax \n",
                        getOperand(operand),
                        getMemoryOperand(destination)
                );
            }
            default: throw new AssemblyException("Unexpected error");
        }
    }

    @Override
    public String visitRead(Instruction.ReadInstruction instruction) {
        var destination = instruction.destination;

        return String.format(
                "lea rdi, [rel fmt_read_int] \n"+
                        "lea rsi, %s \n"+
                        "xor eax, eax \n"+
                        "call scanf \n",
                getMemoryOperand(destination)

        );
    }

    @Override
    public String visitPrint(Instruction.PrintInstruction instruction) {
        var value = instruction.value;

        return String.format(
                "lea rdi, [rel fmt_int] \n mov rsi, %s \n xor eax, eax \n call printf \n",
                getOperand(value)
        );
    }

    @Override
    public String visitLabel(Instruction.LabelInstruction instruction) {
        var label = instruction.label;

        return String.format("%s: \n", label);
    }

    @Override
    public String visitJump(Instruction.JumpInstruction instruction) {
        var jump = instruction.destination;
        return String.format("jmp %s\n", jump);
    }

    @Override
    public String visitConditionalJump(Instruction.ConditionalJumpInstruction instruction) {
        var condition = instruction.condition;
        var destination = instruction.destination;

        return String.format(
                "mov rax, %s \n cmp rax, 0 \n je %s \n",
                getMemoryOperand(condition),
                destination.operand
        );
    }
}
