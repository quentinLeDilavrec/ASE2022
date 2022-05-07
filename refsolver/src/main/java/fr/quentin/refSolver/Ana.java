package fr.quentin.refSolver;

import java.beans.Expression;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

import fr.quentin.refSolver.ImpactAnalysis.ImpactAnalysisException;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryImpl;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

public class Ana {
    static int totalrefs = 0;

    public static <T> void main(String[] args) throws IOException, ImpactAnalysisException {
        String root = "/home/quentin/spoon2/";
        String repo = "INRIA/spoon";
        String module = "";
        if (args.length == 0) {
        } else if (args.length == 3) {
            System.err.println(args);
            root = args[0];
            repo = args[1];
            module = args[2];
        } else {
            System.err.println("missing arguments" + args);
        }
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.err.println("Used memory is megabytes: "
                + (memory / (1024L * 1024L)));

        MavenLauncher launcherAll = new MavenLauncher(
                // "/home/quentin/resources/Versions/graphhopper/graphhopper/7f80425b6a0af9bdfef12c8a873676e39e0a04a6/",
                root + module,
                MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
        launcherAll.getEnvironment().setLevel("INFO");
        launcherAll.getFactory().getEnvironment().setLevel("INFO");

        try {
            launcherAll.buildModel();
        } catch (Exception e) {
            System.err.println(e);
            for (CategorizedProblem pb : ((JDTBasedSpoonCompiler) launcherAll.getModelBuilder()).getProblems()) {
                System.err.println(pb);
            }
        }
        JsonObject result = new JsonObject();

        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        memory = runtime.totalMemory() - runtime.freeMemory();
        System.err.println("Used memory is megabytes: "
                + (memory / (1024L * 1024L)));

        // System.err.println("aaa");
        // HashMap<CtElement, ArrayList<CtElement>> res = new HashMap<>();
        // find_ref_rel(launcherAll, res);

        CtRefHolderVisitor visitor = new CtRefHolderVisitor();
        // visitor.scan(launcherAll.getModel()
        // .getUnnamedModule());
        // RefRelVisitor visitor = new RefRelVisitor();
        visitor.scan(launcherAll.getModel().getUnnamedModule());
        // CtType<?> a = launcherAll.getModel().getAllTypes().stream()
        // .filter(x -> x.getSimpleName().equals("ContractVerifier")).findFirst().get();
        // visitor.scan(a);
        // visitor.res.entrySet().stream().forEach((entr) -> {
        // CtElement k = entr.getKey();
        // if (k instanceof CtParameter) {
        // CtParameter<?> aaa = (CtParameter<?>) k;
        // if (aaa.getSimpleName().equals("msg"))
        // System.err.println("after scan" + aaa.getParent().toStringDebug());
        // }
        // });
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        memory = runtime.totalMemory() - runtime.freeMemory();
        System.err.println("Used memory is megabytes: "
                + (memory / (1024L * 1024L)));
        Aux2 f = new Aux2();
        f.root_path = Path.of(root);
        System.err.println(visitor.res.size());
        visitor.res.forEach(f);
        f.toString();
        JsonElement refs_at_commit = f.toJson();
        // // Run the garbage collector
        // runtime.gc();
        // // Calculate the used memory
        // memory = runtime.totalMemory() - runtime.freeMemory();
        // System.err.println("Used memory is megabytes: "
        // + (memory / (1024L * 1024L)));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(refs_at_commit));
    }

    public static <T> void main_old(String[] args) throws IOException, ImpactAnalysisException {
        String root = "/home/quentin/spoon2/";
        String repo = "INRIA/spoon";
        String module = "";
        // acc per repo
        JsonObject refs_per_commit = new JsonObject();
        JsonObject evolutions = new JsonObject(); // "commitid1": {"commitid2": {"ins":[],"del":[],"upd":[]}}
        // String repo = "/home/quentin/spoon2/";
        // String root = "/home/quentin/hadoop/";
        // String module = "hadoop-hdfs-project/hadoop-hdfs/";
        // String repo = "apache/hadoop";
        ArrayList<String> commitids = new ArrayList<>();
        commitids.add("f89b6939ab6eb1b59db09b21c93c040f4bdd1541");
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.err.println("Used memory is megabytes: "
                + (memory / (1024L * 1024L)));

        // per commit
        for (String commitid : commitids) {
            MavenLauncher launcherAll = new MavenLauncher(
                    // "/home/quentin/resources/Versions/graphhopper/graphhopper/7f80425b6a0af9bdfef12c8a873676e39e0a04a6/",
                    root + module,
                    MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
            launcherAll.getEnvironment().setLevel("INFO");
            launcherAll.getFactory().getEnvironment().setLevel("INFO");

            try {
                launcherAll.buildModel();
            } catch (Exception e) {
                System.err.println(e);
                for (CategorizedProblem pb : ((JDTBasedSpoonCompiler) launcherAll.getModelBuilder()).getProblems()) {
                    System.err.println(pb);
                }
            }
            // MavenLauncher launcherAll2 = new MavenLauncher(
            // //
            // "/home/quentin/resources/Versions/graphhopper/graphhopper/7f80425b6a0af9bdfef12c8a873676e39e0a04a6/",
            // root + module,
            // MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
            // launcherAll2.getEnvironment().setLevel("INFO");
            // launcherAll2.getFactory().getEnvironment().setLevel("INFO");

            // try {
            // launcherAll2.buildModel();
            // } catch (Exception e) {
            // System.err.println(e);
            // for (CategorizedProblem pb : ((JDTBasedSpoonCompiler)
            // launcherAll2.getModelBuilder()).getProblems()) {
            // System.err.println(pb);
            // }
            // }

            // Run the garbage collector
            runtime.gc();
            // Calculate the used memory
            memory = runtime.totalMemory() - runtime.freeMemory();
            System.err.println("Used memory is megabytes: "
                    + (memory / (1024L * 1024L)));

            // System.err.println("aaa");
            // HashMap<CtElement, ArrayList<CtElement>> res = new HashMap<>();
            // find_ref_rel(launcherAll, res);

            CtRefHolderVisitor visitor = new CtRefHolderVisitor();
            // visitor.scan(launcherAll.getModel()
            // .getUnnamedModule());
            // RefRelVisitor visitor = new RefRelVisitor();
            visitor.scan(launcherAll.getModel().getUnnamedModule());
            // CtType<?> a = launcherAll.getModel().getAllTypes().stream()
            // .filter(x -> x.getSimpleName().equals("ContractVerifier")).findFirst().get();
            // visitor.scan(a);
            // visitor.res.entrySet().stream().forEach((entr) -> {
            // CtElement k = entr.getKey();
            // if (k instanceof CtParameter) {
            // CtParameter<?> aaa = (CtParameter<?>) k;
            // if (aaa.getSimpleName().equals("msg"))
            // System.err.println("after scan" + aaa.getParent().toStringDebug());
            // }
            // });
            // Run the garbage collector
            runtime.gc();
            // Calculate the used memory
            memory = runtime.totalMemory() - runtime.freeMemory();
            System.err.println("Used memory is megabytes: "
                    + (memory / (1024L * 1024L)));
            Aux2 f = new Aux2();
            f.root_path = Path.of(root);
            System.err.println(visitor.res.size());
            visitor.res.forEach(f);
            f.toString();
            JsonElement refs_at_commit = f.toJson();
            refs_per_commit.add(commitid, refs_at_commit);
        }
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        memory = runtime.totalMemory() - runtime.freeMemory();
        System.err.println("Used memory is megabytes: "
                + (memory / (1024L * 1024L)));

        // per pairs of commits
        for (int i = 0; i < commitids.size(); i++) {
            JsonObject cBefore = new JsonObject();
            String commitBefore = commitids.get(i);
            for (int j = i + 1; j < commitids.size(); j++) {
                JsonObject cAfter = new JsonObject();
                String commitAfter = commitids.get(j);
                JsonObject evos = new JsonObject();
                JsonArray deletes = new JsonArray();
                JsonArray inserts = new JsonArray();
                JsonArray updates = new JsonArray();
                // compute diffs
                evos.add("del", deletes);
                evos.add("ins", inserts);
                evos.add("upd", updates);
                cBefore.add(commitAfter, cAfter);
            }
            evolutions.add(commitBefore, cBefore);
        }

        // per repo
        // JsonObject r = new JsonObject();
        // r.add("refs", refs_per_commit);
        // r.add("evolutions", evolutions);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(refs_per_commit.get("f89b6939ab6eb1b59db09b21c93c040f4bdd1541")));
    }

    private static void find_ref_rel(MavenLauncher launcherAll, HashMap<CtElement, ArrayList<CtElement>> res) {
        launcherAll.getModel()
                .getRootPackage().getElements(new AbstractFilter<CtElement>() {
                    @Override
                    public boolean matches(CtElement element) {
                        return true;
                        // // System.err.println(element);
                        // return element instanceof CtAbstractInvocation
                        // || element instanceof CtExecutable
                        // || element instanceof CtVariableAccess
                        // || element instanceof CtVariable
                        // // || element instanceof CtArrayAccess
                        // || element instanceof CtAnnotation
                        // || element instanceof CtThisAccess
                        // || element instanceof CtTypeAccess
                        // // || element instanceof CtExecutableReferenceExpression
                        // || element instanceof CtType
                        // || (element instanceof CtExpression
                        // && ((CtExpression<?>) element).getTypeCasts().size() != 0);
                    }
                })
                .<CtElement>forEach((element) -> {
                    // System.err.println(element);
                    if (element instanceof CtAbstractInvocation) {
                        CtAbstractInvocation<?> e = (CtAbstractInvocation<?>) element;
                        CtExecutableReference<?> r = e.getExecutable();
                        CtExecutable<?> d = r.getDeclaration();
                        if (d != null) {
                            totalrefs++;
                            res.putIfAbsent(d, new ArrayList<>());
                            res.get(d).add(e);
                        }
                        if (element instanceof CtConstructorCall) {
                            CtConstructorCall<?> ee = (CtConstructorCall<?>) element;
                            CtTypeReference<?> rr = ee.getType();
                            CtType<?> dd = rr.getDeclaration();
                            if (dd != null) {
                                totalrefs++;
                                res.putIfAbsent(dd, new ArrayList<>());
                                res.get(dd).add(ee);
                            }
                        }
                    }
                    if (element instanceof CtTypeAccess) {
                        CtTypeAccess<?> e = (CtTypeAccess<?>) element;
                        CtTypeReference<?> r = e.getAccessedType();
                        if (r != null) {
                            CtType<?> d = r.getDeclaration();
                            if (d != null) {
                                totalrefs++;
                                res.putIfAbsent(d, new ArrayList<>());
                                res.get(d).add(e);
                            }
                        }
                    }
                    if (element instanceof CtThisAccess) {
                        CtThisAccess<?> e = (CtThisAccess<?>) element;
                        CtTypeReference<?> r = e.getType();
                        if (r != null) {
                            CtType<?> d = r.getDeclaration();
                            if (d != null) {
                                totalrefs++;
                                res.putIfAbsent(d, new ArrayList<>());
                                res.get(d).add(e);
                            }
                        }
                    }
                    if (element instanceof CtExecutable) {
                        CtExecutable<?> e = (CtExecutable<?>) element;
                        CtTypeReference<?> r = e.getType();
                        if (r != null) {
                            CtType<?> d = r.getDeclaration();
                            if (d != null) {
                                totalrefs++;
                                res.putIfAbsent(d, new ArrayList<>());
                                res.get(d).add(e);
                            }
                        }
                        // } else if (element instanceof CtExpression) {
                        // CtExpression<?> e = (CtExpression<?>) element;
                        // CtExpression<?> r = e.getAssigned();
                        // CtTypeReference<?> r2 = r.getType();
                        // Ct<?> d = r2.getDeclaration();
                        // if (d != null) {
                        // res.putIfAbsent(d, new ArrayList<>());
                        // res.get(d).add(e);
                        // }
                    }
                    if (element instanceof CtVariableAccess) {
                        CtVariableAccess<?> e = (CtVariableAccess<?>) element;
                        CtVariableReference<?> r = e.getVariable();
                        CtVariable<?> d = r.getDeclaration();
                        if (d != null) {
                            res.putIfAbsent(d, new ArrayList<>());
                            res.get(d).add(e);
                        }
                        // if (element instanceof CtTargetedExpression) { // TODO should fall back to
                        // CtTypeAccess ?
                        // CtTargetedExpression<?,?> ee = (CtTargetedExpression<?,?>) element;
                        // CtExpression<?> rr = ee.getTarget();
                        // CtType<?> dd = rr.getDeclaration();
                        // if (dd != null) {
                        // totalrefs++;
                        // res.putIfAbsent(dd, new ArrayList<>());
                        // res.get(dd).add(ee);
                        // }
                        // }
                    }
                    if (element instanceof CtVariable) {
                        res.putIfAbsent(element, new ArrayList<>());
                        CtVariable<?> e = (CtVariable<?>) element;
                        CtTypeReference<?> r = e.getType();
                        if (r != null) {
                            CtType<?> d = r.getDeclaration();
                            if (d != null) {
                                res.putIfAbsent(d, new ArrayList<>());
                                res.get(d).add(e);
                            }
                        }
                    }
                    if (element instanceof CtAnnotation) {
                        CtAnnotation<?> e = (CtAnnotation<?>) element;
                        CtTypeReference<?> r = e.getType();
                        if (r != null) {
                            CtType<?> d = r.getDeclaration();
                            if (d != null) {
                                res.putIfAbsent(d, new ArrayList<>());
                                res.get(d).add(e);
                            }
                        }
                    }
                    if (element instanceof CtType) {
                        res.putIfAbsent(element, new ArrayList<>());
                        CtTypeInformation e = (CtTypeInformation) element;
                        CtTypeReference<?> r = e.getSuperclass();
                        if (r != null) {
                            CtType<?> d = r.getDeclaration();
                            if (d != null) {
                                res.putIfAbsent(d, new ArrayList<>());
                                res.get(d).add(element);
                            }
                        }
                        Set<CtTypeReference<?>> i = e.getSuperInterfaces();
                        for (CtTypeReference<?> rr : i) {
                            CtType<?> dd = rr.getDeclaration();
                            if (dd != null) {
                                res.putIfAbsent(dd, new ArrayList<>());
                                res.get(dd).add(element);
                            }
                        }
                    }
                    if (element instanceof CtExpression
                            && ((CtExpression<?>) element).getTypeCasts().size() != 0) {
                        CtExpression<?> e = (CtExpression<?>) element;
                        List<CtTypeReference<?>> cs = e.getTypeCasts();
                        for (CtTypeReference<?> rr : cs) {
                            CtType<?> dd = rr.getDeclaration();
                            if (dd != null) {
                                res.putIfAbsent(dd, new ArrayList<>());
                                res.get(dd).add(element);
                            }
                        }
                    }
                });
    }

    private static class Aux implements BiConsumer<CtElement, Set<CtElement>> {
        int did = 0;
        int refs = 0;
        int decls = 0;
        JsonArray references = new JsonArray();
        JsonObject declarations = new JsonObject();
        Path root_path;

        @Override
        public void accept(CtElement k, Set<CtElement> v) {
            // System.err.println(k.getClass().getName());
            JsonObject d = new JsonObject();
            String tn = k.getClass().getSimpleName();
            d.addProperty("type", tn.substring(2, tn.length() - 4));
            try {
                JsonElement p = position(k).toJson();
                d.add("position", p);
                declarations.add("" + did, d);
                decls++;
                System.err.print(k.getClass());
                System.err.print(": ");
                System.err.println(v.size());
                v.forEach((x) -> {
                    try {
                        JsonElement pp = position(x).toJson();
                        JsonObject r = new JsonObject();
                        String tn2 = x.getClass().getSimpleName();
                        r.addProperty("type", tn2.substring(2, tn2.length() - 4));
                        r.add("position", pp);
                        r.addProperty("declaration", did);
                        references.add(r);
                        refs++;
                    } catch (ShadowElementException e) {
                    }
                });
                did++;
            } catch (ShadowElementException e) {
            }
        }

        int fnw(String s, int init) {
            for (int i = init; i < s.length(); i++)
                if (!Character.isWhitespace(s.charAt(i)))
                    return i;
            return 0;
        }

        int fnwr(String s, int init) {
            for (int i = init; i < s.length(); i++)
                if (!Character.isWhitespace(s.charAt(s.length() - 1 - i)))
                    return i;
            return 0;
        }

        Position position(CtElement e) throws ShadowElementException {
            SourcePosition p = e.getPosition();
            if (p.getFile() == null)
                throw new ShadowElementException();
            if (e.getRoleInParent() == CtRole.FOR_INIT) {
                int start = p.getSourceStart();
                int end = p.getSourceEnd() + 1;
                end = end + p.getCompilationUnit().getOriginalSourceCode().substring(end).indexOf(";");
                end = end + 1;
                start = p.getCompilationUnit().getOriginalSourceCode().substring(0, start).getBytes().length;
                end = p.getCompilationUnit().getOriginalSourceCode().substring(0, end).getBytes().length;
                end = end - start;
                assert end != 0;
                return new Position(root_path.relativize(p.getFile().toPath()).toString(), start, end);
            } else if (e.getRoleInParent() == CtRole.TRY_RESOURCE) {
                int start = p.getSourceStart();
                int end = p.getSourceEnd() + 1;
                if (p.getCompilationUnit().getOriginalSourceCode().substring(start, end).endsWith(";")) {
                    end = end - 1;
                }
                start = p.getCompilationUnit().getOriginalSourceCode().substring(0, start).getBytes().length;
                end = p.getCompilationUnit().getOriginalSourceCode().substring(0, end).getBytes().length;
                end = end - start;
                assert end != 0;
                return new Position(root_path.relativize(p.getFile().toPath()).toString(), start, end);
            }

            // if
            // (root_path.relativize(p.getFile().toPath()).toString().equals("src/test/java/spoon/test/comment/testclasses/TestClassWithComments.java"))
            // System.err.println("a");
            if (!e.getComments().isEmpty()) {
                // if
                // (root_path.relativize(p.getFile().toPath()).toString().equals("src/test/java/spoon/test/comment/testclasses/TestClassWithComments.java"))
                // System.err.println(e.toStringDebug());
                List<CtComment> c = e.getComments();
                // List<Integer> c_starts =
                // c.stream().map(x->x.getPosition().getSourceStart()).collect(Collectors.toList());
                int start = p.getSourceStart();
                int end = p.getSourceEnd() + 1;
                int i = 0;
                while (i < c.size()) {
                    CtComment curr = c.get(i);
                    if (start == curr.getPosition().getSourceStart()) {
                        start = curr.getPosition().getSourceEnd();
                        start = start + fnw(
                                p.getCompilationUnit().getOriginalSourceCode().substring(start, p.getSourceEnd()), 1);
                    } else {
                        break;
                    }
                    i++;
                }
                i = c.size() - 1;
                while (i >= 0) {
                    CtComment curr = c.get(i);
                    if (end == curr.getPosition().getSourceEnd() + 1) {
                        // if
                        // (root_path.relativize(p.getFile().toPath()).toString().equals("src/test/java/spoon/test/comment/testclasses/TestClassWithComments.java"))
                        // System.err.println("++++" + c.size());
                        end = curr.getPosition().getSourceStart();
                        // if
                        // (root_path.relativize(p.getFile().toPath()).toString().equals("src/test/java/spoon/test/comment/testclasses/TestClassWithComments.java"))
                        // System.err.println("((()))" +
                        // fnwr(p.getCompilationUnit().getOriginalSourceCode().substring(start, end),
                        // 0));
                        end = end - fnwr(
                                p.getCompilationUnit().getOriginalSourceCode().substring(start, end), 0);
                    } else {
                        break;
                    }
                    i--;
                }
                // if
                // (root_path.relativize(p.getFile().toPath()).toString().equals("src/test/java/spoon/test/comment/testclasses/TestClassWithComments.java"))
                // System.err.println(p.getCompilationUnit().getOriginalSourceCode().substring(start,
                // end));
                start = p.getCompilationUnit().getOriginalSourceCode().substring(0, start).getBytes().length;
                end = p.getCompilationUnit().getOriginalSourceCode().substring(0, end).getBytes().length;
                end = end - start;
                assert end != 0;
                // c.get(0).getPosition().getSourceEnd();
                // int start = c_end
                // + fnw(p.getCompilationUnit().getOriginalSourceCode().substring(c_end,
                // p.getSourceEnd()),1);
                return new Position(root_path.relativize(p.getFile().toPath()).toString(), start, end);
            } else if (p instanceof DeclarationSourcePosition) {
                DeclarationSourcePosition pp = (DeclarationSourcePosition) p;
                int s = pp.getModifierSourceStart();
                int start = s + fnw(p.getCompilationUnit().getOriginalSourceCode().substring(s, p.getSourceEnd()), 0);
                int end = p.getSourceEnd() + 1;
                start = p.getCompilationUnit().getOriginalSourceCode().substring(0, start).getBytes().length;
                end = p.getCompilationUnit().getOriginalSourceCode().substring(0, end).getBytes().length;
                end = end - start;
                assert end != 0;
                return new Position(root_path.relativize(p.getFile().toPath()).toString(), start, end);
            } else {
                int start = p.getSourceStart();
                int end = p.getSourceEnd() + 1;
                start = p.getCompilationUnit().getOriginalSourceCode().substring(0, start).getBytes().length;
                end = p.getCompilationUnit().getOriginalSourceCode().substring(0, end).getBytes().length;
                end = end - start;
                assert end != 0;
                return new Position(root_path.relativize(p.getFile().toPath()).toString(), start, end);
            }
        }

        @Override
        public String toString() {
            System.err.println(decls);
            System.err.println(refs);
            return null;
        }

        public JsonElement toJson() {
            JsonObject r = new JsonObject();
            r.add("declarations", declarations);
            r.add("references", references);
            return r;
        }
    }

    private static final class Aux2 extends Aux {
        public JsonElement toJson() {
            return references;
        }

        @Override
        public void accept(CtElement k, Set<CtElement> v) {
            // if (k instanceof CtExecutable && !(k instanceof CtConstructor)) {
            // return; // TODO remove if wanted
            // }
            // // System.err.println(k.getClass().getName());
            // // if (!(k instanceof CtType)) {
            // // return; // TODO remove if wanted
            // // }
            // if (!(k instanceof CtType) && !(k instanceof CtLocalVariable)) {
            // return; // TODO remove if wanted
            // }
            if (k instanceof CtExecutable || k instanceof CtField) {
                return; // TODO remove if wanted
            }
            // if (k instanceof CtEnum) {
            // return; // TODO remove if wanted
            // }
            // if (k instanceof CtAnnotationType) {
            // return; // TODO remove if wanted
            // }
            // if (k instanceof CtTypeParameter) {
            // return; // TODO remove if wanted
            // }

            // if (k.getParent() instanceof CtNewClass) {
            // return; // TODO remove if wanted
            // }
            // // if (k.getParent() instanceof CtBlock) {
            // // return; // TODO remove if wanted
            // // }
            try {
                JsonObject entry = new JsonObject();
                JsonElement p = position(k).toJson();
                String tn = k.getClass().getSimpleName();
                ((JsonObject) p).addProperty("type", tn.substring(2, tn.length() - 4));
                ((JsonObject) p).addProperty("pos_type", k.getPosition().getClass().getSimpleName());

                String ptn = k.getParent().getClass().getSimpleName();
                ((JsonObject) p).addProperty("parent_type", ptn.substring(2, ptn.length() - 4));
                ((JsonObject) p).addProperty("main_start", k.getPosition().getSourceStart());
                entry.add("decl", p);
                decls++;
                JsonArray vr = new JsonArray();
                v.forEach((x) -> {
                    // if (x instanceof CtConstructor) {
                    // return; // TODO remove if wanted
                    // }
                    // if (x instanceof CtThisAccess) {
                    // return; // TODO remove if wanted
                    // }
                    if (x instanceof CtLambda) {
                        return; // TODO remove if wanted
                    }
                    if (x instanceof CtParameter) {
                        SourcePosition pp = x.getPosition();
                        if (!(pp instanceof NoSourcePosition)) {
                            String aaa = pp.getCompilationUnit().getOriginalSourceCode().substring(pp.getSourceStart(),
                                    pp.getSourceEnd() + 1);
                            // System.err.println(aaa);
                            if (!aaa.contains(" "))
                                return;
                        }
                    }
                    // // if (x instanceof CtClass && (((CtClass<?>) x).isAnonymous())) {
                    // // return; // TODO remove if wanted
                    // // }
                    try {
                        JsonElement pp = position(x).toJson();
                        String tn2 = x.getClass().getSimpleName();
                        ((JsonObject) pp).addProperty("type", tn2.substring(2, tn2.length() - 4));
                        String ptn2 = k.getParent().getClass().getSimpleName();
                        ((JsonObject) pp).addProperty("parent_type", ptn.substring(2, ptn2.length() - 4));
                        ((JsonObject) p).addProperty("pos_type", x.getPosition().getClass().getSimpleName());
                        vr.add(pp);
                        refs++;
                    } catch (ShadowElementException e) {
                    }
                });
                ;
                entry.add("refs", vr);
                references.add(entry);
            } catch (ShadowElementException e) {
            }
        }
    }

    static class PlaceHolderEvolution implements Evolution<Object> {
        Set<Position> impacts = Collections.newSetFromMap(new IdentityHashMap<>());

        PlaceHolderEvolution(SourcePositionHolder holder) throws IOException {
            SourcePosition p = holder.getPosition();
            this.impacts.add(new Position(p.getFile().getCanonicalPath(), p.getSourceStart(), p.getSourceEnd()));
        }

        @Override
        public Set<Position> getPreEvolutionPositions() {
            return impacts;
        }

        @Override
        public Set<Position> getPostEvolutionPositions() {
            return Collections.newSetFromMap(new IdentityHashMap<>());
        }

        @Override
        public Object getOriginal() {
            return null;
        }

        @Override
        public String getCommitIdAfter() {
            return "";
        }

        @Override
        public String getCommitIdBefore() {
            return "";
        }

    }

    static class ShadowElementException extends Exception {

    }
    
    static class RefRelVisitor extends CtScanner {
        IdentityHashMap<CtElement, Set<CtElement>> res = new IdentityHashMap<>();
    
        int totalrefs = 0;
    
        @Override
        protected void enter(CtElement element) {
            // System.err.println(element);
            if (element instanceof CtAbstractInvocation) {
                CtAbstractInvocation<?> e = (CtAbstractInvocation<?>) element;
                CtExecutableReference<?> r = e.getExecutable();
                CtExecutable<?> d = r.getDeclaration();
                if (d != null) {
                    totalrefs++;
                    res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                    res.get(d).add(e);
                }
                if (element instanceof CtConstructorCall) {
                    CtConstructorCall<?> ee = (CtConstructorCall<?>) element;
                    CtTypeReference<?> rr = ee.getType();
                    CtType<?> dd = rr.getDeclaration();
                    if (dd != null) {
                        totalrefs++;
                        res.putIfAbsent(dd, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(dd).add(ee);
                    }
                }
            }
            if (element instanceof CtTypeAccess) {
                CtTypeAccess<?> e = (CtTypeAccess<?>) element;
                CtTypeReference<?> r = e.getAccessedType();
                if (r != null) {
                    CtType<?> d = r.getDeclaration();
                    if (d != null) {
                        totalrefs++;
                        res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(d).add(e);
                    }
                }
            }
            if (element instanceof CtThisAccess) {
                CtThisAccess<?> e = (CtThisAccess<?>) element;
                CtTypeReference<?> r = e.getType();
                if (r != null) {
                    CtType<?> d = r.getDeclaration();
                    if (d != null) {
                        totalrefs++;
                        res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(d).add(e);
                    }
                }
            }
            if (element instanceof CtConstructor) {
                res.putIfAbsent(element, Collections.newSetFromMap(new IdentityHashMap<>()));
                CtConstructor<?> e = (CtConstructor<?>) element;
                List<CtParameter<?>> ps = e.getParameters();
                for (CtParameter<?> p : ps) {
                    res.putIfAbsent(p, Collections.newSetFromMap(new IdentityHashMap<>()));
                }
            }
            if (element instanceof CtExecutable) {
                res.putIfAbsent(element, Collections.newSetFromMap(new IdentityHashMap<>()));
                CtExecutable<?> e = (CtExecutable<?>) element;
                CtTypeReference<?> r = e.getType();
                if (r != null) {
                    CtType<?> d = r.getDeclaration();
                    if (d != null) {
                        totalrefs++;
                        res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(d).add(e);
                    }
                }
                List<CtParameter<?>> ps = e.getParameters();
                for (CtParameter<?> p : ps) {
                    res.putIfAbsent(p, Collections.newSetFromMap(new IdentityHashMap<>()));
                }
                // } else if (element instanceof CtExpression) {
                // CtExpression<?> e = (CtExpression<?>) element;
                // CtExpression<?> r = e.getAssigned();
                // CtTypeReference<?> r2 = r.getType();
                // Ct<?> d = r2.getDeclaration();
                // if (d != null) {
                // res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                // res.get(d).add(e);
                // }
            }
            if (element instanceof CtVariableAccess) {
                CtVariableAccess<?> e = (CtVariableAccess<?>) element;
                CtVariableReference<?> r = e.getVariable();
                CtVariable<?> d = r.getDeclaration();
                if (d != null) {
                    res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                    res.get(d).add(e);
                }
                // if (element instanceof CtTargetedExpression) { // TODO should fall back to
                // CtTypeAccess ?
                // CtTargetedExpression<?,?> ee = (CtTargetedExpression<?,?>) element;
                // CtExpression<?> rr = ee.getTarget();
                // CtType<?> dd = rr.getDeclaration();
                // if (dd != null) {
                // totalrefs++;
                // res.putIfAbsent(dd, Collections.newSetFromMap(new IdentityHashMap<>()));
                // res.get(dd).add(ee);
                // }
                // }
            }
            if (element instanceof CtVariable) {
                res.putIfAbsent(element, Collections.newSetFromMap(new IdentityHashMap<>()));
                CtVariable<?> e = (CtVariable<?>) element;
                CtTypeReference<?> r = e.getType();
                if (r != null) {
                    CtType<?> d = r.getDeclaration();
                    if (d != null) {
                        res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(d).add(e);
                    }
                }
            }
            if (element instanceof CtAnnotation) {
                CtAnnotation<?> e = (CtAnnotation<?>) element;
                CtTypeReference<?> r = e.getType();
                if (r != null) {
                    CtType<?> d = r.getDeclaration();
                    if (d != null) {
                        res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(d).add(e);
                    }
                }
            }
            if (element instanceof CtCatchVariable) {
                res.putIfAbsent(element, Collections.newSetFromMap(new IdentityHashMap<>()));
            }
            if (element instanceof CtParameter) {
                res.putIfAbsent(element, Collections.newSetFromMap(new IdentityHashMap<>()));
            }
            if (element instanceof CtType) {
                res.putIfAbsent(element, Collections.newSetFromMap(new IdentityHashMap<>()));
                CtTypeInformation e = (CtTypeInformation) element;
                CtTypeReference<?> r = e.getSuperclass();
                if (r != null) {
                    CtType<?> d = r.getDeclaration();
                    if (d != null) {
                        res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(d).add(element);
                    }
                }
                Set<CtTypeReference<?>> i = e.getSuperInterfaces();
                for (CtTypeReference<?> rr : i) {
                    CtType<?> dd = rr.getDeclaration();
                    if (dd != null) {
                        res.putIfAbsent(dd, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(dd).add(element);
                    }
                }
            }
            if (element instanceof CtExpression
                    && ((CtExpression<?>) element).getTypeCasts().size() != 0) {
                CtExpression<?> e = (CtExpression<?>) element;
                List<CtTypeReference<?>> cs = e.getTypeCasts();
                for (CtTypeReference<?> rr : cs) {
                    CtType<?> dd = rr.getDeclaration();
                    if (dd != null) {
                        res.putIfAbsent(dd, Collections.newSetFromMap(new IdentityHashMap<>()));
                        res.get(dd).add(element);
                    }
                }
            }
        }
    
    }
}
// Set<SourcePosition> impinvo = l.getImpactedInvocations("",5,10);
// Set<SourcePosition> impdecl = l.getImpactingDeclarations("",5,10);

// System.out.println("0000000000000000000000");

// System.out.println(imptst);
// System.out.println();
// System.out.println(impinvo);
// System.out.println();
// System.out.println(impdecl);
// if (False) {
// launcher.getModel().getRootPackage().accept(new CtScanner() {
// @Override
// public <T> void visitCtInvocation(CtInvocation<T> invocation) {
// if (invocation.getExecutable().isConstructor()) {
// return;
// }
// for (CtExecutableReference<?> executable : allExecutableReference) {
// // if (computedMethods.contains(executable)) {
// // continue;
// // } else {
// // computedMethods.add(executable);
// // }
// if
// (invocation.getExecutable().getSimpleName().equals(executable.getSimpleName())
// && invocation.getExecutable().isOverriding(executable)) {

// // allMethods.add(executable);

// System.out.println("" +
// // invocation.getType().getDeclaration() +
// invocation.getPosition().getCompilationUnit().getDeclaredPackage() + "."
// + invocation.getPosition().getCompilationUnit().getFile().getName() + ":"
// + invocation.getPosition().getSourceStart() + ":"
// + invocation.getPosition().getSourceEnd() + "-->" +
// executable.getDeclaringType()
// + "." + executable
// // + ":" + executable.getType()
// );
// }
// }
// }
// });
// }

