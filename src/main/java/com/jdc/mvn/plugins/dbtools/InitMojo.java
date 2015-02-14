package com.jdc.mvn.plugins.dbtools;

import org.apache.maven.plugin.MojoExecutionException;
import org.dbtools.gen.DBToolsInit;

/**
 * Goal which creates initial schema.xml and dbschema.xsd files
 *
 * @author Jeff Campbell
 * @version $Id$
 * @goal init
 * @phase generate-sources
 */
public class InitMojo extends AbstractDBToolsMojo {
    /**
     * Based directory where the source files will be generated.
     *
     * @parameter expression="${basedir}/src/main/database"
     */
    private String databaseSrcDir;

    /**
     * Skip code generation
     *
     * @parameter default-value="false"
     */
    private boolean skip = false;

    @Override
    public void execute() throws MojoExecutionException {
        if (!skip) {
            getLog().info("Initializing DBTools...");
            verifyParameters();
            initXmlFiles();
        } else {
            getLog().info("SKIPPING DBTools init.");
        }
    }

    private void verifyParameters() throws MojoExecutionException {
    }

    private void initXmlFiles() throws MojoExecutionException {
        if (skip) {
            return;
        }

        DBToolsInit dbToolsInit = new DBToolsInit();

        dbToolsInit.initDBTools(databaseSrcDir);

        getLog().info("DBTools init complete");
    }
}