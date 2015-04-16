package com.jdc.mvn.plugins.dbtools;

import org.apache.maven.plugin.MojoExecutionException;
import org.dbtools.gen.DBObjectsBuilder;
import org.dbtools.gen.GenConfig;
import org.dbtools.gen.android.AndroidObjectsBuilder;
import org.dbtools.gen.jpa.JPAObjectsBuilder;

import java.io.File;

/**
 * Goal which creates database classes based on an DBTools schema.xml file
 *
 * @author <a href="mailto:jeff@soupbowl.net">Jeff Campbell</a>
 * @version $Id$
 * @goal genclasses
 * @phase generate-sources
 */
public class GenClassesMojo extends AbstractDBToolsMojo {
    /**
     * Based directory where the source files will be generated.
     *
     * @parameter expression="${basedir}/src/main/java"
     */
    private String outputSrcDir;

    /**
     * Based directory where the test source files will be generated.
     *
     * @parameter expression="${basedir}/src/test/java"
     */
    private String outputTestSrcDir;

    /**
     * Type of application: JPA, ANDROID
     *
     * @parameter default-value="JPA"
     */
    private String type = "JPA";

    /**
     * Add JEE/Spring Transactional annotations to CRUD methods in BaseManager
     *
     * @parameter default-value="false"
     */
    private boolean javaEESupport = false;

    /**
     * Use JSR 310 DateTime (using Joda)
     *
     * @parameter default-value="false"
     */
    private boolean dateTimeSupport = false;

    /**
     * Use CDI Dependency Injection
     *
     * @parameter default-value="false"
     */
    private boolean injectionSupport = false;

    /**
     * Use Otto Event Bus to subscribe to database changes
     *
     * @parameter default-value="false"
     */
    private boolean ottoSupport = false;

    /**
     * Use jsr 305 (@Nullable, @Notnull, etc)
     *
     * @parameter default-value="true"
     */
    private boolean jsr305Support = true;

    /**
     * Use SQLQueryBuilder for generated queries
     *
     * @parameter default-value="false"
     */
    private boolean sqlQueryBuilderSupport = false;

    /**
     * If using multiple databases, it may be better to organize domain objects by database name
     *
     * @parameter default-value="false"
     */
    private boolean includeDatabaseNameInPackage = false;

    /**
     * Name of the base package that should be used for generated files.  This
     * package name is a base to the packages that will be generated
     * (example: com.company.data will produce the following com.company.data.object1,
     * com.company.data.object2, etc) This package is also used to determine the
     * directories to create in both the
     * src/main/java AND src/test/java directories
     *
     * @parameter
     * @required
     */
    private String basePackageName;

    /**
     * Generate default unit tests to test generate classes.  This may be useful
     * to help increase code coverage tests.
     *
     * @parameter default-value="false"
     */
    private boolean genUnitTests = false;

    /**
     * Skip code generation
     *
     * @parameter default-value="false"
     */
    private boolean skip = false;

    /**
     * Classpath of the renderer to use to generate objects.  If not specified,
     * then the default renderer is used.  Include the dependency jar file for
     * the classpath item:
     * <p/>
     * <!--jar file that has the renderer -->
     * <dependencies>
     * <dependency>
     * <groupId>mygroup</groupId>
     * <artifactId>myartifact</artifactId>
     * <version>1.0</version>
     * </dependency>
     * </dependencies>
     *
     * @parameter
     */
    private String rendererClasspath = null;

    @Override
    public void execute() throws MojoExecutionException {
        if (!skip) {
            getLog().info("Generating classes...");
            verifyParameters();
            genClasses();
        } else {
            getLog().info("SKIPPING Database classes.");
        }
    }

    private void verifyParameters() throws MojoExecutionException {
        File schemaFile = new File(getSchemaFullFilename());
        if (!schemaFile.exists()) {
            throw new MojoExecutionException("Could not find file: " + getSchemaFullFilename());
        }
    }

    private String getPathFromPackage(String baseDir, String packageName) {
        String path = baseDir;

        if (!path.endsWith("\\") && !path.endsWith("/")) {
            path += "/";
        }

        for (char c : packageName.toCharArray()) {
            switch (c) {
                case '.':
                    path += '/';
                    break;
                default:
                    path += c;
            }

        }

        return path;
    }

    private void genClasses() throws MojoExecutionException {
        DBObjectsBuilder builder;

        switch (type) {
            default:
            case "JPA":
                builder = new JPAObjectsBuilder();
                break;
            case "ANDROID":
                builder = new AndroidObjectsBuilder();
        }

        GenConfig genConfig = new GenConfig();
        genConfig.setDateTimeSupport(dateTimeSupport);
        genConfig.setInjectionSupport(injectionSupport);
        genConfig.setJavaeeSupport(javaEESupport);
        genConfig.setIncludeDatabaseNameInPackage(includeDatabaseNameInPackage);
        genConfig.setOttoSupport(ottoSupport);
        genConfig.setJsr305Support(jsr305Support);
        genConfig.setSqlQueryBuilderSupport(sqlQueryBuilderSupport);
        builder.setGenConfig(genConfig);

        // schema file
        builder.setXmlFilename(getSchemaFullFilename());

        // tables to build objects for
        builder.setTables(null); // means all

        // output directory
        //File source = project.
        builder.setOutputBaseDir(getPathFromPackage(outputSrcDir, basePackageName));

        // object information
        builder.setPackageBase(basePackageName);

        // GENERATE
        builder.build();

        // show results
        int filesGenerated = builder.getNumberFilesGenerated();
        getLog().info("Generated [" + filesGenerated + "] files.");
    }
}