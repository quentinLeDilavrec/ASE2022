package fr.quentin.refSolver;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.ImmutablePair;

import fr.quentin.refSolver.ImpactType.Level;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.LexicalScope;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.TypeFilter;

public class ImpactAnalysis {
    // preEval -> deps -> validation
    static Logger logger = Logger.getLogger(ImpactAnalysis.class.getName());

    private final Integer maxChainLength;

    final Resolver resolver;

    public final AugmentedAST<MavenLauncher> augmented;

    public ImpactAnalysis(final AugmentedAST<MavenLauncher> _ast) throws ImpactAnalysisException {
        this(_ast, 10);
    }

    public class ImpactAnalysisException extends Exception {
        private static final long serialVersionUID = 4914245185480342853L;

        ImpactAnalysisException(String message) {
            super(message);
        }
    }

    public ImpactAnalysis(final AugmentedAST<MavenLauncher> augmentedAst, final int maxChainLength)
            throws ImpactAnalysisException {
        this.augmented = augmentedAst;
        this.maxChainLength = maxChainLength;
        if (augmented == null) {
            throw new ImpactAnalysisException("augmentedAst is null");
        }
        MavenLauncher launcher = augmented.launcher;
        if (launcher == null) {
            throw new ImpactAnalysisException("launcher is null");
        }
        CtModel model = launcher.getModel();
        if (model == null) {
            throw new ImpactAnalysisException("model is null");
        }
        Collection<CtType<?>> allTypes = model.getAllTypes();
        logger.info("initializing resolver");
        this.resolver = new Resolver(allTypes);
        logger.info("done initializing resolver");
    }

    public static Boolean isTest(final CtExecutable<?> y) {
        for (final CtAnnotation<?> x : y.getAnnotations()) {
            // x.getAnnotationType().;
            if (Objects.equals(x.getAnnotationType().getQualifiedName(), "org.junit.Test")) {
                // System.out.println("\t\t" +
                // x.getAnnotationType().getQualifiedName()+"\n\t\t"+y.getSignature());
                return true;
            }
        }
        return false;
    }

    public Set<SourcePosition> getImpactedInvocations(final String string, final int i, final int j) {
        return null;
    }

    public Set<SourcePosition> getImpactingDeclarations(final String string, final int i, final int j) {
        return null;
    }

    public <T> Explorer getImpactedTests2(final Collection<ImmutablePair<Object, Position>> col) throws IOException {
        return getImpactedTests2(col, true);
    }

    public <T> Explorer getImpactedTests3(final Collection<ImmutablePair<Object, CtElement>> col, final boolean onTests)
            throws IOException {
        final Set<ImpactChain> chains = new HashSet<>();
        final Map<ImpactElement, ImpactElement> elements = new HashMap<>();
        for (final ImmutablePair<Object, CtElement> x : col) {
            final CtElement ele = x.right;
            final Object impactingThing = x.left;
            // List<CtElement> tmp = this.launcher.getModel().getElements(filter);
            final SourcePosition pos = ele.getPosition();
            assert pos.isValidPosition() : pos;
            ImpactElement tmp2 = ImpactElement.build(ele);
            elements.putIfAbsent(tmp2, tmp2);
            tmp2 = elements.get(tmp2);
            tmp2.addEvolution(impactingThing,
                    new Position(pos.getFile().getAbsolutePath(), pos.getSourceStart(), pos.getSourceEnd()));
            chains.add(new ImpactChain(tmp2));
        }
        Logger.getLogger("getImpactedTests").info(Integer.toString(chains.size()));
        return exploreAST2(elements, chains, onTests);
    }
    
    public <T> Explorer getImpactedTests4(final Collection<ImmutablePair<Object, Object>> col, final boolean onTests)
            throws IOException {
        final Set<ImpactChain> chains = new HashSet<>();
        final Map<ImpactElement, ImpactElement> elements = new HashMap<>();
        for (final ImmutablePair<Object, Object> x : col) {
            final Object impactingThing = x.left;
            CtElement element = null;
            Position position = null;
            if (x.right instanceof CtElement) {
                element = (CtElement) x.right;
                // List<CtElement> tmp = this.launcher.getModel().getElements(filter);
                final SourcePosition pos = element.getPosition();
                assert pos.isValidPosition() : pos;
                position = new Position(pos.getFile().getAbsolutePath(), pos.getSourceStart(), pos.getSourceEnd());
            } else if (x.right instanceof Position) {
                position = (Position) x.right;
                CtCompilationUnit cu = this.augmented.getCu(position.getFilePath());
                for (CtType<?> type : cu.getDeclaredTypes()) {
                    if (fr.quentin.refSolver.Utils.isContainingType(type, position.getStart(), position.getLength() - 1)) {
                        element = fr.quentin.refSolver.Utils.matchExact(type, position.getStart(), position.getLength() - 1);
                        break;
                    }
                }
            }
            assert element != null : position;
            assert position != null : element;
            ImpactElement tmp2 = ImpactElement.build(element);
            elements.putIfAbsent(tmp2, tmp2);
            tmp2 = elements.get(tmp2);
            tmp2.addEvolution(impactingThing, position);
            chains.add(new ImpactChain(tmp2));
        }
        Logger.getLogger("getImpactedTests").info(Integer.toString(chains.size()));
        return exploreAST2(elements, chains, onTests);
    }

    public <T> Explorer getImpactedTests2(final Collection<ImmutablePair<Object, Position>> col, final boolean onTests)
            throws IOException {
        final Set<ImpactChain> chains = new HashSet<>();
        final Map<ImpactElement, ImpactElement> elements = new HashMap<>();
        for (final ImmutablePair<Object, Position> x : col) {
            final Object impactingThing = x.left;
            final Position pos = x.right;
            CtElement element = null;
            CtCompilationUnit cu = this.augmented.getCu(pos.getFilePath());
            for (CtType<?> type : cu.getDeclaredTypes()) {
                if (fr.quentin.refSolver.Utils.isContainingType(type, pos.getStart(), pos.getLength() - 1)) {
                    element = fr.quentin.refSolver.Utils.matchExact(type, pos.getStart(), pos.getLength() - 1);
                    break;
                }
            }
            assert element != null : element;
            ImpactElement tmp2 = ImpactElement.build(element);
            elements.putIfAbsent(tmp2, tmp2);
            tmp2 = elements.get(tmp2);
            tmp2.addEvolution(impactingThing, pos);
            chains.add(new ImpactChain(tmp2));
        }
        Logger.getLogger("getImpactedTests").info(Integer.toString(chains.size()));
        return exploreAST2(elements, chains, onTests);
    }

    public <T> Explorer getImpactedTests(final Collection<Evolution<T>> x) throws IOException {
        final Set<ImpactChain> chains = new HashSet<>();
        final Map<ImpactElement, ImpactElement> elements = new HashMap<>();
        for (final Evolution<T> impactingThing : x) {
            for (final Position pos : impactingThing.getPreEvolutionPositions()) {
                final List<CtElement> tmp = this.augmented.launcher.getModel().getElements(new FilterEvolvedElements(
                        Paths.get(this.augmented.rootFolder.toAbsolutePath().toString(), pos.getFilePath()).toString(),
                        pos.getStart(), pos.getLength()));
                for (final CtElement element : tmp) {
                    ImpactElement tmp2 = ImpactElement.build(element);
                    elements.putIfAbsent(tmp2, tmp2);
                    tmp2 = elements.get(tmp2);
                    tmp2.addEvolution((Evolution<Object>) impactingThing, pos);
                    chains.add(new ImpactChain(tmp2));
                }
            }
        }
        Logger.getLogger("getImpactedTests").info(Integer.toString(chains.size()));
        return exploreAST2(elements, chains, true);
    }

    public <T> Explorer getImpactedTestsPostEvolution(final Collection<Evolution<T>> x) throws IOException {
        final Set<ImpactChain> chains = new HashSet<>();
        final Map<ImpactElement, ImpactElement> elements = new HashMap<>();
        for (final Evolution<T> impactingThing : x) {
            for (final Position pos : impactingThing.getPostEvolutionPositions()) {
                final List<CtElement> tmp = this.augmented.launcher.getModel().getElements(new FilterEvolvedElements(
                        Paths.get(this.augmented.rootFolder.toAbsolutePath().toString(), pos.getFilePath()).toString(),
                        pos.getStart(), pos.getLength()));
                for (final CtElement element : tmp) {
                    ImpactElement tmp2 = ImpactElement.build(element);
                    elements.putIfAbsent(tmp2, tmp2);
                    tmp2 = elements.get(tmp2);
                    tmp2.addEvolution((Evolution<Object>) impactingThing, pos);
                    chains.add(new ImpactChain(tmp2));
                }
            }
        }
        Logger.getLogger("getImpactedTestsPostEvolution").info(Integer.toString(chains.size()));
        return exploreAST2(elements, chains, true);
    }

    private class FilterEvolvedElements implements Filter<CtElement> {

        private final String file;
        private final int start;
        private final int end;

        public FilterEvolvedElements(final Position position) {
            this.file = position.getFilePath();
            this.start = position.getStart();
            this.end = position.getLength();
        }

        public FilterEvolvedElements(final String file, final int start, final int end) {
            this.file = file;
            this.start = start;
            this.end = end;
        }

        // public FilterEvolvedElements(ImpactElement p) {
        // this(p.getPosition());
        // }

        @Override
        public boolean matches(final CtElement element) {
            if (element instanceof CtExecutable<?>) {
                final SourcePosition p = element.getPosition();
                if (file == null || p.getFile() == null || !p.isValidPosition()) {
                    return false;
                }
                String c;
                try {
                    c = p.getFile().getCanonicalPath();
                } catch (final Exception e) {
                    e.printStackTrace();
                    return false;

                }
                if (!Objects.equals(c, file)) {
                    return false;
                } else if (p.getSourceEnd() < start || end < p.getSourceStart()) {
                    return false;
                } else {
                    // included or overlaping
                    if (element instanceof CtMethod) {
                        return true;
                    } else if (element instanceof CtConstructor) {
                        return true;
                    } else if (element instanceof CtLambda) {
                        return true;
                    } else if (element instanceof CtAnonymousExecutable) {
                        return false; // TODO see if we can handle it
                    } else {
                        Logger.getLogger("FilterEvolvedElements.matches")
                                .warning(element.getClass() + " is not handled by the filter.");
                        return false;
                    }
                }
            } else {
                return false;
            }

        }
    }

    private Explorer exploreAST2(final Map<ImpactElement, ImpactElement> impactElements,final Set<ImpactChain> impactChains,
            final boolean getOnTests) {
        long start = System.nanoTime();
        final Explorer explorer = new Explorer(this, impactElements, impactChains, maxChainLength, getOnTests);
        logger.info("starting to explore impacts");
        Level current = ImpactType.Level.CALL_GRAPH;
        while (current != null && ImpactType.Level.CALL_GRAPH.compareTo(current) >= 0) {
            current = explorer.process(current);
        }
        logger.info("Call graph mainly processed in " + (System.nanoTime() - start) / 1000000 + " s");
        while (current != null) {
            current = explorer.process(current);
        }
        // TODO if using multi-threading think about synchonizing prior to returning the
        // result
        logger.info("All graphs processed in " + (System.nanoTime() - start) / 1000000 + " s");
        return explorer;
    }

    // private Explorer exploreAST2Old(final Set<ImpactChain> impactChains, final
    // boolean getOnTests) {
    // final Explorer explorer = new Explorer(this, impactChains, maxChainLength,
    // getOnTests);

    // while (!explorer.processedChains.isEmpty()) {

    // final ImpactChain current = explorer.processedChains.poll();
    // final CtElement current_elem = current.getLast().getContent();
    // final Integer weight = explorer.alreadyMarchedChains.getOrDefault(current,
    // maxChainLength * 1);

    // if (weight <= 0) {
    // explorer.finishChain(current);
    // } else if (current_elem instanceof CtExecutable) {
    // if (isTest((CtExecutable<?>) current_elem)) {
    // explorer.finishedChains.add(current);
    // } else {
    // explorer.followUsage(current, (CtExecutable<?>) current_elem, weight);
    // }
    // } else if (current_elem instanceof CtExpression) {
    // explorer.followValue(current, (CtExpression<?>) current_elem, weight);
    // if (current_elem instanceof CtAbstractInvocation) {
    // // argument possible writes
    // explorer.followValueArguments(current, (CtAbstractInvocation<?>)
    // current_elem, weight);
    // // current type
    // }
    // // explorer.followTypes(current, (CtExpression<?>) current_elem, weight); //
    // // current type
    // // } else if (current_elem instanceof CtStatement) {
    // } else if (current_elem instanceof CtVariable) {
    // explorer.followVariableValueAndUses(current, (CtVariable<?>) current_elem,
    // weight);
    // // explorer.followTypes(current, (CtLocalVariable) current_elem, weight); //
    // // current type
    // // } else if (current_elem instanceof CtAssignment) { // is an expression
    // // explorer.followReads(current, (CtAssignment) current_elem, weight);
    // // // explorer.followTypes(current, (CtAssignment) current_elem, weight); //
    // // // current type
    // // } else if (current_elem instanceof CtReturn) {
    // // // explorer.followTypes(current, ((CtReturn)
    // // // current_elem).getReturnedExpression(), weight); // current
    // // // type
    // // explorer.followReads(current, (CtExpression<?>) ((CtReturn)
    // // current_elem).getReturnedExpression(),
    // // weight);
    // // explorer.expand3(current, current_elem, weight); // returns
    // } else if (current_elem instanceof CtType) {
    // explorer.followUses(current, (CtType<?>) current_elem, weight);
    // } else {
    // explorer.expandToScopeOtherwiseExecutableOtherwiseType(current, current_elem,
    // weight);
    // }
    // }
    // return explorer;
    // }

    // private List<ImpactChain<? extends CtElement>> exploreASTDecl(final
    // Collection<CtExecutable<?>> x) {
    // final ConcurrentLinkedQueue<ImpactChain<CtExecutable<?>>> s = new
    // ConcurrentLinkedQueue<>();
    // for (CtExecutable<?> y : x) {
    // s.add(new ImpactChain<CtExecutable<?>>(y));
    // }
    // // loop are not considered
    // final HashMap<ImpactChain<? extends CtElement>, Integer> alreadyMarched = new
    // HashMap<ImpactChain<? extends CtElement>, Integer>();
    // final List<ImpactChain<? extends CtElement>> r = new ArrayList<ImpactChain<?
    // extends CtElement>>();
    // // System.out.println("&&&&& " + x.size() + " " + s.size() + " " +
    // // alreadyMarched.size());

    // while (!s.isEmpty()) {
    // // System.out.println(s.size());
    // final ImpactChain<CtExecutable<?>> current = s.poll();
    // Integer fromAlreadyMarched = alreadyMarched.get(current);
    // if (fromAlreadyMarched != null) {
    // if (current.size() < fromAlreadyMarched) {
    // continue;
    // // System.out.println("22222222222222" + fromAlreadyMarched + " " +
    // // current.size());
    // } else {
    // if (current.size() > 4) {
    // continue;
    // }
    // }
    // }
    // alreadyMarched.put(current, current.size());
    // final Object z = current.getLast().getMetadata("call");
    // // System.out.println("@@@@@ " + y.getLast().getSignature() + " " +
    // s.size());
    // if (z instanceof Collection) {
    // final Collection<?> a = (Collection<?>) z;
    // for (final Object b : a) {
    // // System.out.println("aaa");
    // if (b instanceof CtInvocation) {
    // // System.out.println("bbb");
    // final CtInvocation<?> c = (CtInvocation<?>) b;
    // // System.out.println(c);
    // CtExecutable<?> p = c.getParent(CtExecutable.class);
    // if (p != null) {
    // // System.out.println("ccc");
    // ImpactChain<CtExecutable<?>> p2 = current.extend(p);
    // Integer fromAlreadyMarched2 = alreadyMarched.get(p);
    // if (isTest(p)) {
    // // System.out.println(p2.size());
    // // if (fromAlreadyMarched2 == null || current.size() < fromAlreadyMarched2) {
    // r.add(p2);
    // // }
    // } else {
    // if (fromAlreadyMarched2 == null || current.size() > fromAlreadyMarched2) {
    // s.add(p2);
    // } else {
    // Logger.getLogger("exploreASTDecl").warn("redundant node");
    // }
    // }
    // }
    // } else if (b instanceof CtConstructorCall) {
    // final CtConstructorCall<?> c = (CtConstructorCall<?>) b;
    // final CtConstructor<?> p = c.getParent(CtConstructor.class);
    // Integer fromAlreadyMarched2 = alreadyMarched.get(p);
    // if (p != null) {
    // if (fromAlreadyMarched2 == null || current.size() > fromAlreadyMarched2) {
    // s.add(current.extend(p));
    // }
    // }
    // } else {
    // }
    // }

    // }

    // }
    // return r;
    // }

}
