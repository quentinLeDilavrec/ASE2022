package fr.quentin.refSolver;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.junit.Test;

import fr.quentin.refSolver.AugmentedAST;
import fr.quentin.refSolver.ImpactAnalysis;
import fr.quentin.refSolver.ImpactChain;
import fr.quentin.refSolver.Position;
import fr.quentin.refSolver.ImpactAnalysis.ImpactAnalysisException;
import spoon.MavenLauncher;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void simpleSysTest() throws ImpactAnalysisException {
        MavenLauncher launcherAll = new MavenLauncher(
                "/home/quentin/resources/Versions/graphhopper/graphhopper/7f80425b6a0af9bdfef12c8a873676e39e0a04a6/",
                MavenLauncher.SOURCE_TYPE.ALL_SOURCE);
        launcherAll.getEnvironment().setLevel("INFO");
        launcherAll.getFactory().getEnvironment().setLevel("INFO");

        try {
            launcherAll.buildModel();
        } catch (Exception e) {
            for (CategorizedProblem pb : ((JDTBasedSpoonCompiler) launcherAll.getModelBuilder()).getProblems()) {
                System.out.println(pb);
            }
        }

        AugmentedAST<MavenLauncher> aug = new AugmentedAST<>(launcherAll);
        ImpactAnalysis l = new ImpactAnalysis(aug, 10);

        fr.quentin.refSolver.Explorer imptst1;
        try {
            Set<ImmutablePair<Object, Position>> tmp = new HashSet<>();
            tmp.add(new ImmutablePair<>(null,
                    new Position("core/src/test/java/com/graphhopper/GraphHopperTest.java", 2000, 3000)));
                    tmp.add(new ImmutablePair<>(null,
                            new Position("core/src/test/java/com/graphhopper/GraphHopperTest.java", 3000, 4000)));
                            tmp.add(new ImmutablePair<>(null,
                                    new Position("core/src/main/java/com/graphhopper/GraphHopper.java", 4000, 5000)));
            imptst1 = l.getImpactedTests2(tmp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fr.quentin.refSolver.Impacts rawImpacts = new fr.quentin.refSolver.Impacts(imptst1.getFinishedChains());
    }
}
