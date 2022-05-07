package fr.quentin.refSolver;

public enum ImpactType {
    TEMPO("temporal"), //
    HIERA("hierarchical", Level.TYPE_GRAPH), //
    STRUC("structural"), //
    REF("reference"), //
    MODIFY("modify", Level.CALL_GRAPH), //
    VALUE("value", Level.FLOW_GRAPH), //
    ACCESS("access", Level.FLOW_GRAPH), //
    CALL("call", Level.CALL_GRAPH, REF), //
    TYPE("type", Level.TYPE_GRAPH, REF), //
    ANNOTATE("annotate", Level.CALL_GRAPH, STRUC), //
    EXPAND("expand", Level.CALL_GRAPH, STRUC), //
    DIRECT_EXPAND("direct expand", EXPAND), // TODO ignore this precision ?
    READ("read", Level.FLOW_GRAPH, REF, ACCESS), //
    WRITE("write", Level.FLOW_GRAPH, REF, ACCESS), //
    OVERRIDING("overriding", Level.TYPE_GRAPH, HIERA), //
    OVERRIDED("overrided", Level.TYPE_GRAPH, HIERA), //
    ARGUMENT("argument", Level.TYPE_GRAPH, ACCESS, STRUC), // arg is for invocations
    PARAMETER("parameter", Level.CALL_GRAPH, STRUC), // param is for declarations
    BRANCH("branch", Level.STRUCT_GRAPH, STRUC, TEMPO), //
    RETURN("return", Level.FLOW_GRAPH, VALUE, STRUC), //
    DECL_RETURN("decl return", Level.CALL_GRAPH, TYPE, STRUC), //
    THROW("throw", Level.STRUCT_GRAPH, BRANCH), //
    CONTINUE("continue", Level.STRUCT_GRAPH, BRANCH), //
    BREAK("break", Level.STRUCT_GRAPH, BRANCH), //
    CONDITION("condition", Level.STRUCT_GRAPH, VALUE, BRANCH), //
    THEN("then", Level.STRUCT_GRAPH, BRANCH), //
    ELSE("else", Level.STRUCT_GRAPH, BRANCH), //
    CONDITION_LAZY("condition lazy", Level.STRUCT_GRAPH, CONDITION, ELSE), //
    EXTEND("extend", Level.TYPE_GRAPH, HIERA), //
    IMPLEMENT("implement", Level.TYPE_GRAPH, HIERA), //
    ;

    /**
     * UNDEFINED ⊃ STRUCT_GRAPH ⊃ FLOW_GRAPH ⊃ TYPE_GRAPH ⊃ CALL_GRAPH
     */
    enum Level {
        OTHER, // Ω
        STRUCT_GRAPH, //
        FLOW_GRAPH, //
        TYPE_GRAPH, //
        CALL_GRAPH, //
        ;
    }

    ImpactType[] parents;
    final String displayName;
    final Level level;

    ImpactType(String displayName, ImpactType... parents) {
        this(displayName, Level.OTHER, parents);
    }

    ImpactType(String displayName, Level level, ImpactType... parents) {
        this.displayName = displayName;
        this.level = level;
        this.parents = parents;
    }

    @Override
    public String toString() {
        return displayName;
    }

    boolean is(ImpactType type) {
        if (type.equals(this)) {
            return true;
        }
        for (ImpactType parent : parents) {
            if (parent.is(type))
                return true;
        }
        return false;
    }
}