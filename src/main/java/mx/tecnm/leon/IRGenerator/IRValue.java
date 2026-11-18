package mx.tecnm.leon.IRGenerator;

import mx.tecnm.leon.Semantic.Type;

public class IRValue {
    public final String operand;
    public final ValueType type;
    public final Type dataType;

    public IRValue(String operand, ValueType type, Type dataType){
        this.operand = operand;
        this.type = type;
        this.dataType = dataType;
    }

    @Override
    public String toString(){
        return operand;
    }
}
