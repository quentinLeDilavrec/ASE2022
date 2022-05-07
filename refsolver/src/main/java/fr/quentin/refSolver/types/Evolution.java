package fr.quentin.refSolver.types;

import java.util.List;

// @uniq // (also put relations as attributs)
public interface Evolution {
    // @relation
    public static interface Before {
        String getDescription();

        // @source
        Evolution getSource();

        // @target
        Range getTarget();
    }

    // @relation
    public static interface After {
        String getDescription();

        // @source
        Evolution getSource();

        // @target
        Range getTarget();
    }

    // @computed
    String getUrl();

    // @relation
    List<Evolution.Before> getBefore();

    // @relation
    List<Evolution.After> getAfter();
}