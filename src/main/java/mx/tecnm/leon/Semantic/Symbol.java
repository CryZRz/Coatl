package mx.tecnm.leon.Semantic;

public class Symbol {
    public Type type;
    public boolean initialized;

    public Symbol(Type type, boolean initialized){
        this.type = type;
        this.initialized = initialized;
    }
}
