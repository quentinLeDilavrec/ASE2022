package fr.quentin.refSolver.types;

import java.util.List;

// @uniq // (also put relations as attributs)
public interface Impact {

    String getType();

    List<Impact.Cause> getCauses();

    List<Impact.Effect> getEffects();
    
    // @relation
    public static interface Cause {
        String getType();

        // @source
        Impact getSource();

        // @target
        Range getTarget();
    }

    // @relation
    public static interface Effect {
        String getType();

        // @source
        Impact getSource();

        // @target
        Range getTarget();
    }
}