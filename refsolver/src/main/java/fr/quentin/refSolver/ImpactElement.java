package fr.quentin.refSolver;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;

/**
 * 
 */
public class ImpactElement {
    private Position position;
    private final CtElement content;
    private final Map<String, Object> more = new HashMap<>();
    private final Map<Object, Position> evolutions = new HashMap<>();
    public static final String BEST_FLOW = "best flow graph";
    public static final String BEST_TYPE = "best type graph";
    public static final String BEST_CG = "best call graph";
    public static final String BEST_STRUC = "best struc graph";
    public static final String BEST_OTHER = "best other graph";

    public static ImpactElement build(CtElement e) {
        return build(e.getPosition(), e);
    }

    public static ImpactElement build(Position position) {
        return build(position, null);
    }

    public static ImpactElement build(String file, int start, int end) {
        return build(file, start, end, null);
    }

    public static ImpactElement build(String file, int start, int end, CtElement content) {
        return build(new Position(file, start, end), content);
    }

    public static ImpactElement build(SourcePosition position) {
        return build(position, null);
    }

    public static ImpactElement build(SourcePosition position, CtElement content) {
        if (position == null) {
            throw new UnsupportedOperationException("the position is mandatory to make an ImpactElement");
        }
        if (position instanceof NoSourcePosition) {
            throw new UnsupportedOperationException("NoSourcePosition cannot be wrapped as an ImpactElement");
        }
        return build(position.getFile().getAbsolutePath(), position.getSourceStart(), position.getSourceEnd(), content);
    }

    public static ImpactElement build(Position position, CtElement content) {
        return new ImpactElement(position, content);
    }

    /**
     * @return the getEvolutionWithNonCorrectedPosition
     */
    public Map<Object, Position> getEvolutionWithNonCorrectedPosition() {
        return evolutions;
    }

    public <T> T getMD(String key) {
        return (T) more.get(key);
    }

    public <T> T getMD(String key, T deflt) {
        return (T) more.getOrDefault(key, deflt);
    }

    public <T> T putMD(String key, T value) {
        return (T) more.put(key, value);
    }

    protected ImpactElement(Position position, CtElement content) {
        this.position = position;
        this.content = content;
    }

    /**
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return the evolutions
     */
    public Set<Object> getEvolutions() {
        return evolutions.keySet();
    }

    public void addEvolution(Object evolution, Position nonCorrectedPosition) {
        evolutions.put(evolution, nonCorrectedPosition);
    }

    public void addEvolution(Object evolution) {
        evolutions.put(evolution, position);
    }

    /**
     * 
     * @return nullable
     */
    public CtElement getContent() {
        return content;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImpactElement other = (ImpactElement) obj;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        return true;
    }

}
