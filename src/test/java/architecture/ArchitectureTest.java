package architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * This test checks architectural consistency.
 */
@AnalyzeClasses(packages = {"de.example.donutqueue"})
public class ArchitectureTest {

    @ArchTest
    public static ArchRule checkJerseyClassesContainment = classes().that()
            .resideOutsideOfPackages("de.example.donutqueue.server..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("javax.ws.rs")
            .as("Jersey classes should not be used outside of server package.");

    @ArchTest
    public static ArchRule checkMainAsEntryPointOnly = classes().that()
            .haveFullyQualifiedName("de.example.donutqueue.Main")
            .should().onlyBeAccessed().byClassesThat()
            .haveFullyQualifiedName("de.example.donutqueue.Main");

    @ArchTest
    public static ArchRule checkLayers = layeredArchitecture()
            .layer("server").definedBy("..donutqueue.server..")
            .layer("logic").definedBy("..donutqueue.core..", "..donutqueue.jobs..")
            .layer("db").definedBy("..donutqueue.db.dao..", "..donutqueue.db.pojo..")
            .layer("db_factory").definedBy("..donutqueue.db")
            .layer("main").definedBy("..donutqueue")
            .whereLayer("server").mayOnlyBeAccessedByLayers("main")
            .whereLayer("db").mayOnlyBeAccessedByLayers("logic", "main", "db_factory", "server");

}