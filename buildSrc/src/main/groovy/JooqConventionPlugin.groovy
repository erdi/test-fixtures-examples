import nu.studer.gradle.jooq.JooqExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.JavaExec
import org.gradle.plugins.ide.idea.model.IdeaModel

class JooqConventionPlugin implements Plugin<Project> {

    private final static String JOOQ_DB_NAME = 'jooq'

    @Override
    void apply(Project project) {
        project.pluginManager.apply('nu.studer.jooq')

        project.dependencies.add('jooqRuntime', 'org.hsqldb:hsqldb:2.4.0')

        def schemaBootstrapConfiguration = project.configurations.create('dbSchemaBootstrap')
        def jooqGenerationDbDir = project.file('build/db/jooq')
        def generatedSourceDir = project.file('src/jooq/java')

        configureJooqPlugin(project, jooqGenerationDbDir, generatedSourceDir)

        def bootstrapDbTask = addBootstrapDbTask(project, jooqGenerationDbDir, schemaBootstrapConfiguration)

        def schemaSourceGenerationTask = project.tasks.getByName('generateMainJooqSchemaSource')
        schemaSourceGenerationTask.inputs.files(bootstrapDbTask.outputs)

        configureIdea(project, schemaSourceGenerationTask, generatedSourceDir)
    }

    private void configureIdea(Project project, Task schemaSourceGenerationTask, File generatedSourceDir) {
        project.tasks.getByName('ideaModule').dependsOn(schemaSourceGenerationTask)
        project.extensions.getByType(IdeaModel).module.sourceDirs += generatedSourceDir
    }

    private void configureJooqPlugin(Project project, File jooqGenerationDbDir, File generatedSourceDir) {
        def extension = project.extensions.getByType(JooqExtension)
        extension.version = '3.10.7'
        extension.main(project.sourceSets.main) {
            jdbc {
                driver = 'org.hsqldb.jdbc.JDBCDriver'
                url = "jdbc:hsqldb:file:${jooqGenerationDbDir.absolutePath}/$JOOQ_DB_NAME;shutdown=true;readonly=true;files_readonly=true"
                user = 'sa'
                password = ''
            }
            generator {
                database {
                    name = 'org.jooq.util.hsqldb.HSQLDBDatabase'
                    inputSchema = 'DEFAULT_SCHEMA'
                }
                target {
                    packageName = 'db'
                    directory = generatedSourceDir.absolutePath
                }
            }
        }
    }

    private Task addBootstrapDbTask(Project project, File jooqGenerationDbDir,
                                    Configuration schemaBootstrapConfiguration) {
        def dbBootstrapProjectAndResources = schemaBootstrapConfiguration + project.files('src/main/resources')

        project.task('bootstrapDbForJooqCodeGeneration', type: JavaExec) { JavaExec it ->
            it.inputs.files(dbBootstrapProjectAndResources)
            it.outputs.dir(jooqGenerationDbDir)

            it.doFirst {
                if (jooqGenerationDbDir.exists()) {
                    jooqGenerationDbDir.deleteDir()
                }
            }

            it.classpath = dbBootstrapProjectAndResources
            it.main = "example.dbbootstrap.FileHsqlDbBootstrapper"
            it.args = [jooqGenerationDbDir.absolutePath, JOOQ_DB_NAME]
        }
    }
}
