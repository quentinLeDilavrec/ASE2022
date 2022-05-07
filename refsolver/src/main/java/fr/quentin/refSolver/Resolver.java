package fr.quentin.refSolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.util.QualifiedNameBasedSortedSet;

/**
 * Resolver
 */
public class Resolver {

    public Resolver(final Collection<CtType<?>> allTypes) {
        initTypes(allTypes);
    }

    private static final String METADATA_KEY_REVERSE = "depsAna.reversed";// + UUID.randomUUID();
    private static final String METADATA_KEY_SUPER_CLASSES = "depsAna.superClass";// + UUID.randomUUID();
    private static final String METADATA_KEY_SUPER_INTERFACES = "depsAna.superInterface";// + UUID.randomUUID();
    private static final String METADATA_KEY_INVS_COUNT = "depsAna.invsCount";// + UUID.randomUUID();
    private static final String METADATA_KEY_REVERSE_COUNT = "depsAna.accessCount";// + UUID.randomUUID();
    private static final String METADATA_KEY_TYPED_COUNT = "depsAna.typedCount";// + UUID.randomUUID();
    private static final String METADATA_KEY_TYPED = "depsAna.typed";// + UUID.randomUUID();
    private static final String METADATA_KEY_OVERRIDES = "depsAna.overrides";// + UUID.randomUUID();
    private static final String METADATA_KEY_OVERRIDES_COUNT = "depsAna.overridesCount";// + UUID.randomUUID();

    private static <T> Uses<T> makeUses(Class<T> clazz) {
        return new Uses<T>(clazz);
    }

    // @SuppressWarnings("unchecked")
    // private <T> void insertMetaData(final CtElement element, final String key,
    // final Uses<T> defaultValue,
    // final T value) {
    // Object x = element.getMetadata(key);
    // if (x == null) {
    // x = defaultValue;
    // element.putMetadata(key, x);
    // }
    // if (x instanceof Uses) {
    // ((Uses<T>) x).add(value);
    // }
    // }

    @SuppressWarnings("unchecked")
    private <T> void insertMetaData(final CtElement element, final String key, final T value) {
        Object x = element.getMetadata(key);
        if (x == null) {
            x = makeUses(value.getClass());
            element.putMetadata(key, x);
        }
        if (x instanceof Uses) {
            ((Uses<T>) x).add(value);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void insertMetaData(final CtElement element, final String key, final Uses<T> defaultValue,
            final T value) {
        Object x = element.getMetadata(key);
        if (x == null) {
            x = defaultValue;
            element.putMetadata(key, x);
        }
        if (x instanceof Uses) {
            ((Uses<T>) x).add(value);
        }
    }
    
	public static Set<CtTypeReference<?>> getUsedTypes(CtType<?> type, boolean includeSamePackage) {
		Set<CtTypeReference<?>> typeRefs = new QualifiedNameBasedSortedSet<>();
		for (CtTypeReference<?> typeRef : Query.getReferences(type, new ReferenceTypeFilter<CtTypeReference<?>>(CtTypeReference.class))) {
			if (isValidTypeReference(type, typeRef) && shouldIncludeSamePackage(type, includeSamePackage, typeRef)) {
				typeRefs.add(typeRef);
			}
		}
		return typeRefs;
	}

	private static boolean shouldIncludeSamePackage(CtType<?> type, boolean includeSamePackage, CtTypeReference<?> typeRef) {
		return includeSamePackage || (type.getPackage() != null && !getPackageReference(typeRef).equals(type.getPackage().getReference()));
	}

	private static boolean isValidTypeReference(CtType<?> type, CtTypeReference<?> typeRef) {
        return !(isFromJavaLang(typeRef) || typeRef.isPrimitive() 
        || typeRef instanceof CtArrayTypeReference 
        || CtTypeReference.NULL_TYPE_NAME.equals(typeRef.getSimpleName()));
    }
    
	private static CtPackageReference getPackageReference(CtTypeReference<?> tref) {
		CtPackageReference pref = tref.getPackage();
		while (pref == null) {
			tref = tref.getDeclaringType();
			pref = tref.getPackage();
		}
		return pref;
    }
    
	private static boolean isFromJavaLang(CtTypeReference<?> typeRef) {
		return typeRef.getPackage() != null && "java.lang".equals(typeRef.getPackage().getQualifiedName());
	}

    private void initTypes(final Collection<CtType<?>> allTypes) {
        for (final CtType<?> type : allTypes) {
            Set<CtTypeReference<?>> usedTypes;
            try {
                usedTypes = getUsedTypes(type, true);
            } catch (Exception e) {
                continue;
            }
            // TODO !!!! refs are recreated each time !!!!
            for (final CtTypeReference<?> typeRef : usedTypes) {
                CtType<?> typeDeclaration = typeRef.getTypeDeclaration();
                if (typeDeclaration != null) {
                    insertMetaData(typeDeclaration, Resolver.METADATA_KEY_REVERSE, makeUses(CtType.class), type);
                }
            }
            final CtTypeReference<?> superClass = type.getSuperclass();
            if (superClass != null) {
                CtType<?> typeDeclaration = superClass.getTypeDeclaration();
                if (typeDeclaration != null) {
                    insertMetaData(typeDeclaration, Resolver.METADATA_KEY_SUPER_CLASSES, makeUses(CtType.class), type);
                }
            }
            final Set<CtTypeReference<?>> superInterfaces = type.getSuperInterfaces();
            for (final CtTypeReference<?> superInterface : superInterfaces) {
                CtType<?> typeDeclaration = superInterface.getTypeDeclaration();
                if (typeDeclaration != null) {
                    insertMetaData(typeDeclaration, Resolver.METADATA_KEY_SUPER_INTERFACES, makeUses(CtType.class), type);
                }
            }
        }
    }

    private <T> void initTypesForTyped(final CtType<?> declaringType) {
        final Object types = declaringType.getMetadata(Resolver.METADATA_KEY_REVERSE);
        if (types != null && types instanceof Uses) {
            assert ((Uses<?>) types).getType().equals(CtType.class);
            @SuppressWarnings("unchecked")
            Set<CtType<Object>> tops = ((Uses<CtType<?>>) types).getValues().stream().map(x -> x.getTopLevelType())
                    .collect(Collectors.toSet());
            for (final CtType<?> type : tops) {
                final Object exe_count = type.getMetadata(Resolver.METADATA_KEY_TYPED_COUNT);
                if (exe_count == null || !(exe_count instanceof Integer)) {
                    final List<CtTypedElement<?>> typeds = type.getElements(new TypeFilter<>(CtTypedElement.class));
                    type.putMetadata(Resolver.METADATA_KEY_TYPED_COUNT, typeds.size());
                    for (final CtTypedElement<?> typed : typeds) {
                            CtTypeReference<?> typeRef = typed.getType();
                        if (typeRef != null) {
                            CtType<?> typeDeclaration = typeRef.getTypeDeclaration();
                            if (typeDeclaration != null) {
                                insertMetaData(typeDeclaration, Resolver.METADATA_KEY_TYPED, typed);
                            }
                        }
                    }
                }
            }
        }

    }

    private void initExecutables(final CtType<?> declaringType) {
        final Object types = declaringType.getMetadata(Resolver.METADATA_KEY_REVERSE);
        if (types != null && types instanceof Uses) {
            assert ((Uses<?>) types).getType().equals(CtType.class);
            @SuppressWarnings("unchecked")
            Set<CtType<Object>> tops = ((Uses<CtType<?>>) types).getValues().stream().map(x -> x.getTopLevelType())
                    .collect(Collectors.toSet());
            for (final CtType<?> type : tops) {
                final Object exe_count = type.getMetadata(Resolver.METADATA_KEY_INVS_COUNT);
                if (exe_count == null || !(exe_count instanceof Integer)) {
                    final List<CtAbstractInvocation<?>> invs = type
                            .getElements(new TypeFilter<>(CtAbstractInvocation.class));
                    type.putMetadata(Resolver.METADATA_KEY_INVS_COUNT, invs.size());
                    for (final CtAbstractInvocation<?> inv : invs) {
                        CtExecutable<?> declaration = inv.getExecutable().getDeclaration();
                        if (declaration != null) {
                            insertMetaData(declaration, Resolver.METADATA_KEY_REVERSE,
                                    makeUses(CtAbstractInvocation.class), inv);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initOverrides(final CtType<?> declaringType) {
        final Object classes = declaringType.getMetadata(Resolver.METADATA_KEY_SUPER_CLASSES);
        if (classes != null && classes instanceof Uses) {
            initOverridesAux((Uses<CtType<?>>) classes);
        }
        final Object interfaces = declaringType.getMetadata(Resolver.METADATA_KEY_SUPER_INTERFACES);
        if (interfaces != null && interfaces instanceof Uses) {
            initOverridesAux((Uses<CtType<?>>) interfaces);
        }
    }

    private void initOverridesAux(final Uses<CtType<?>> types) {
        assert types.getType().equals(CtType.class);
        Set<CtType<Object>> tops = types.getValues().stream().map(x -> x.getTopLevelType()).collect(Collectors.toSet());
        for (final CtType<?> type : tops) {
            final Object override_count = type.getMetadata(Resolver.METADATA_KEY_OVERRIDES_COUNT);
            if (override_count == null || !(override_count instanceof Integer)) {
                final List<CtExecutable<?>> overriders = type.getElements(new Filter<CtExecutable<?>>() {

                    @Override
                    public boolean matches(CtExecutable<?> element) {
                        return element.getReference().getOverridingExecutable() != null;
                    }

                });
                type.putMetadata(Resolver.METADATA_KEY_OVERRIDES_COUNT, overriders.size());
                for (final CtExecutable<?> exe : overriders) {
                    CtExecutable<?> declaration = exe.getReference().getOverridingExecutable().getDeclaration();
                    if (declaration != null) {
                        insertMetaData(declaration, Resolver.METADATA_KEY_OVERRIDES, exe);
                    }
                }
            }
        }
    }

    private void initVariables(final CtVariable<?> declaringElement) {

        // if (declaringElement instanceof CtField && ((CtField)
        // declaringElement).isPrivate()) {
        if ((declaringElement instanceof CtField && !((CtField<?>) declaringElement).isPrivate())) {
            // everywhere the declaring type is used including own declaring types
            // should precompute it like executables

            final CtType<?> parentType = declaringElement.getParent(CtType.class);
            final Object types = parentType.getMetadata(Resolver.METADATA_KEY_REVERSE);
            if (types != null) {
                @SuppressWarnings("unchecked")
                Set<CtType<?>> tops = ((Uses<CtType<?>>) types).getValues().stream().map(x -> x.getTopLevelType())
                        .collect(Collectors.toSet());
                for (final CtType<?> type : tops) {
                    initVariables(type);
                }
            }
        // } else if (declaringElement instanceof CtLocalVariable || declaringElement
            // instanceof CtCatchVariable
            // || declaringElement instanceof CtParameter) {
            // final CtExecutable<?> parentExecutable =
            // declaringElement.getParent(CtExecutable.class);
            // Object access_count =
            // parentExecutable.getMetadata(METADATA_KEY_REVERSE_COUNT);
            // if (access_count == null || !(access_count instanceof Integer)) {
            // List<CtVariableAccess> access = parentExecutable.getElements(new
            // TypeFilter(CtVariableAccess.class));
            // access.forEach(read -> {
            // insertMetaData(read.getVariable(), METADATA_KEY_ACCES,
            // new Uses<CtVariableAccess>(CtVariableAccess.class), read);
            // });
            // insertMetaData(parentExecutable, METADATA_KEY_REVERSE_COUNT, new
            // Uses<Integer>(Integer.class),
            // access.size());
            // }
        } else {// CtTypeMember
            // own declaring types
            final CtType<?> parentType = declaringElement.getParent(CtType.class).getTopLevelType();
            if (parentType != null) {
                initVariables(parentType);
            }
        }
    }

    private void initVariables(final CtType<?> parentType) {
        final Object access_count = parentType.getMetadata(Resolver.METADATA_KEY_REVERSE_COUNT);
        if (access_count == null || !(access_count instanceof Integer)) {
            final List<CtVariableAccess<?>> access = parentType.getElements(new TypeFilter<>(CtVariableAccess.class));
            access.forEach(read -> {
                CtVariable<?> declaration = read.getVariable().getDeclaration();
                if (declaration != null) {
                    insertMetaData(declaration, Resolver.METADATA_KEY_REVERSE, makeUses(CtVariableAccess.class), read);
                }
            });
            insertMetaData(parentType, Resolver.METADATA_KEY_REVERSE_COUNT, access.size());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Set<CtVariableAccess<T>> references(final CtVariable<T> declaringElement) {
        final Object accs = declaringElement.getMetadata(Resolver.METADATA_KEY_REVERSE);
        if (accs == null) {
            declaringElement.putMetadata(METADATA_KEY_REVERSE, makeUses(CtTypedElement.class));
            initVariables(declaringElement);
            return ((Uses<CtVariableAccess<T>>) declaringElement.getMetadata(Resolver.METADATA_KEY_REVERSE))
                    .getValues();
        } else if (accs instanceof Uses) {
            return ((Uses<CtVariableAccess<T>>) accs).getValues();
        }
        return null;
    }

    public <T> CtVariable<T> reference(final CtVariableAccess<T> referencingElement) {
        final CtVariableReference<T> ref = referencingElement.getVariable();
        final CtVariable<T> r = ref.getDeclaration();
        return r;
    }

    @SuppressWarnings("unchecked")
    public <T> Set<CtAbstractInvocation<T>> references(final CtExecutable<T> declaringElement) {
        final Object invs = declaringElement.getMetadata(Resolver.METADATA_KEY_REVERSE);
        if (invs == null) {
            declaringElement.putMetadata(Resolver.METADATA_KEY_REVERSE, makeUses(CtTypedElement.class));
            initExecutables(declaringElement.getParent(CtType.class));
            return ((Uses<CtAbstractInvocation<T>>) declaringElement.getMetadata(Resolver.METADATA_KEY_REVERSE))
                    .getValues();
        } else if (invs instanceof Uses) {
            return ((Uses<CtAbstractInvocation<T>>) invs).getValues();
        }
        return null;
    }

    public <T> CtExecutable<?> override(final CtExecutable<T> declaringElement) {
        final CtExecutableReference<T> ref = declaringElement.getReference();
        final CtExecutableReference<?> override = ref.getOverridingExecutable();
        if (override != null) {
            return override.getDeclaration();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> Set<CtExecutable<?>> overrides(final CtExecutable<T> declaringElement) {
        final Object executables = declaringElement.getMetadata(Resolver.METADATA_KEY_OVERRIDES);
        if (executables == null) {
            declaringElement.putMetadata(METADATA_KEY_OVERRIDES, makeUses(CtTypedElement.class));
            initOverrides(declaringElement.getParent(CtType.class));
            return ((Uses<CtExecutable<?>>) declaringElement.getMetadata(Resolver.METADATA_KEY_OVERRIDES)).getValues();
        } else if (executables instanceof Uses) {
            return ((Uses<CtExecutable<?>>) executables).getValues();
        }
        return null;
    }

    public <T> CtExecutable<T> reference(final CtAbstractInvocation<T> referencingElement) {
        final CtExecutableReference<T> ref = referencingElement.getExecutable();
        final CtExecutable<T> r = ref.getDeclaration();
        return r;
    }

    @SuppressWarnings("unchecked")
    public <T> Set<CtType<?>> referencesAllUsagesFromTypes(final CtType<T> declaringElement) {
        final Object types = declaringElement.getMetadata(Resolver.METADATA_KEY_REVERSE);
        assert types != null;
        if (types instanceof Uses) {
            return ((Uses<CtType<?>>) types).getValues();
        }
        assert false : types;
        return new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Set<CtTypedElement<?>> references(final CtType<T> declaringElement) {
        final Object typed = declaringElement.getMetadata(Resolver.METADATA_KEY_TYPED);
        if (typed == null) {
            declaringElement.putMetadata(METADATA_KEY_TYPED, makeUses(CtTypedElement.class));
            initTypesForTyped(declaringElement);
            return ((Uses<CtTypedElement<?>>) declaringElement.getMetadata(Resolver.METADATA_KEY_TYPED)).getValues();
        } else if (typed instanceof Uses) {
            return ((Uses<CtTypedElement<?>>) typed).getValues();
        }
        assert false : typed;
        return new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Set<CtType<?>> referencesSuperInterface(final CtType<T> declaringElement) {
        final Object types = declaringElement.getReference().getMetadata(Resolver.METADATA_KEY_SUPER_INTERFACES);
        if (types == null) {
            return new HashSet<>();
        } else if (types instanceof Uses) {
            return ((Uses<CtType<?>>) types).getValues();
        }
        assert false : types;
        return new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Set<CtType<?>> referencesSuperClass(final CtType<T> declaringElement) {
        final Object types = declaringElement.getMetadata(Resolver.METADATA_KEY_SUPER_CLASSES);
        if (types == null) {
            return new HashSet<>();
        } else if (types instanceof Uses) {
            return ((Uses<CtType<?>>) types).getValues();
        }
        assert false : types;
        return new HashSet<>();
    }

    public <T> CtType<?> referenceSuperClass(final CtType<T> referencingElement) {
        final CtTypeReference<?> superClass = referencingElement.getSuperclass();
        return superClass.getTypeDeclaration();
    }

    public <T> Set<CtType<?>> referenceSuperInterfaces(final CtType<T> referencingElement) {
        final Set<CtTypeReference<?>> superInterfaces = referencingElement.getSuperInterfaces();

        return superInterfaces.stream().map(x -> x.getTypeDeclaration()).collect(Collectors.toSet());
    }

}