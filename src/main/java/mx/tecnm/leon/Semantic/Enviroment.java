package mx.tecnm.leon.Semantic;

import mx.tecnm.leon.Semantic.Exceptions.SemanticException;

import java.util.HashMap;
import java.util.Map;

public class Enviroment {

    private final Map<String, Symbol> variables = new HashMap<>();
    private final Enviroment enclosing;

    public Enviroment(){
        this.enclosing = null;
    }

    public Enviroment(Enviroment enclosing){
        this.enclosing = enclosing;
    }

    private Enviroment ancestor(int distance){
        var enviroment = this;
        for (int i = 0; i < distance; i++) {
            assert enviroment != null;
            enviroment = enviroment.enclosing;
        }

        return enviroment;
    }

    public Symbol getAt(String variable, int distance){
        return ancestor(distance).get(variable);
    }

    public void assignAt(String variable, int distance){
        ancestor(distance).assign(variable);
    }

    public void define(String variable, Symbol symbol){
        if (!variables.containsKey(variable)){
            variables.put(variable, symbol);
            return;
        }

        throw new SemanticException("Variable already has been defined");
    }

    public Symbol get(String variable){
        if (variables.containsKey(variable)){
            return variables.get(variable);
        }

        if (enclosing != null){
            return enclosing.get(variable);
        }

        throw new SemanticException("Undefined variable: " + variable);
    }

    public void assign(String variable){
        if (variables.containsKey(variable)){
            variables.get(variable).initialized = true;
            return;
        }

        if (enclosing != null){
            enclosing.assign(variable);
            return;
        }

        throw new SemanticException("Undefined variable: " + variable);
    }

}
