package mx.tecnm.leon.TAC;

public class TACInstruction {
    public String op, arg1, arg2, result;

    public TACInstruction(String op, String arg1, String arg2, String result){
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    @Override
    public String toString() {
        return switch (op) {
            case "" -> result + " = " + arg1;
            case "label" -> result + ":";
            case "goto" -> "goto " + result;
            case "ifFalse" -> "ifFalse " + arg1 + " goto " + result;
            case "print" -> "print " + arg1;
            case "read" -> result + " = read";
            default -> arg2 == null
                    ? result + " = " + op + " " + arg1
                    : result + " = " + arg1 + " " + op + " " + arg2;
        };
    }
}
