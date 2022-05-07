package fr.quentin.refSolver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import spoon.reflect.declaration.CtType;

final class Uses<T> {
    private final Set<T> values = new HashSet<>();
    private final Class<T> type;

    Uses(final Class<T> class1) {
        this.type = class1;
    }

    public Class<T> getType() {
        return type;
    }

    public void add(final T value) {
        assert type.isInstance(value);
        values.add(value);
    }

    public boolean contains(final T value) {
        assert type.isInstance(value);
        return values.contains(value);
    }

    public Set<T> getValues() {
        return Collections.unmodifiableSet(values);
    }

	public boolean addAll(Set<T> value) {
        return values.addAll(value);
	}
}