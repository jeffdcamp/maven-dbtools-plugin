package com.jdc.mvn.plugins.dbtools;

import com.jdc.db.schema.SchemaRenderer;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Goal which creates SQL based on an DBTools schema.xml file
 *
 * @goal gensql
 * @phase generate-sources
 * @author <a href="mailto:jeff@soupbowl.net">Jeff Campbell</a>
 * @version $Id$
 *
 */
public class GenSQLMojo extends AbstractDBToolsMojo {

    /**
     * SQL file to be generated.
     *
     * @parameter expression="schema.sql"
     */
    private String outputFilename;
    /**
     * Based directory where the sql file will be generated.
     *
     * @parameter expression="${basedir}/target"
     */
    private String outputDir;
    /**
     * Include Post SQL Script(s) as specified in the schema.xml
     *
     * @parameter default-value="true"
     */
    private boolean includePostSQLScript = true;
    /**
     * Include drop schema in script
     *
     * @parameter default-value="false"
     */
    private boolean includeDrop = false;
    /**
     * Include Post SQL Script(s) as specified in the schema.xml
     *
     * @parameter default-value=""
     */
    private String mappingFilename = "";

    public void execute() throws MojoExecutionException {
        getLog().info("Generating SQL...");
        verifyParameters();
        genSQL();
    }

    private void verifyParameters() throws MojoExecutionException {
        File schemaFile = new File(getSchemaFullFilename());
        if (!schemaFile.exists()) {
            throw new MojoExecutionException("Could not find file: " + getSchemaFullFilename());
        }
    }

    private void genSQL() throws MojoExecutionException {
        getLog().info("DB Vendor: " + dbVendor);

        SchemaRenderer sr = SchemaRenderer.getRenderer(dbVendor);

        // initialize renderer
        //sr.setParentComponent(parentComponent);
        sr.setShowConsoleProgress(false);
        sr.setDbVendorName(dbVendor);
        sr.setSchemaXMLFilename(getSchemaFullFilename(), false);
        sr.setOutputFile(outputDir + File.separator + outputFilename);
        sr.setExecuteSQLScriptFiles(!includePostSQLScript);
        sr.setCreateSchema(true);
        sr.setCreatePostSchema(true);
        sr.setTablesToGenerate(null); // if null... all tables
        sr.setViewsToGenerate(null); // if null... all views
        sr.setDropTables(includeDrop);
        if (!mappingFilename.equals("")) {
            sr.setMappingFilename(mappingFilename);
        }

        boolean success = sr.executeRenderer();
        if (!success) {
            throw new MojoExecutionException("Failed to create sql script");
        }


        getLog().info("Generated [" + dbVendor + "] SQL.");
    }
}