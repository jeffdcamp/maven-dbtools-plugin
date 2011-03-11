package com.jdc.mvn.plugins.dbtools;

import org.apache.maven.plugin.AbstractMojo;

/**
 * Goal which creates JPA entities based on an DBTools schema.xml file
 *
 * @author <a href="mailto:jeff@soupbowl.net">Jeff Campbell</a>
 * @version $Id$
 *
 */
public abstract class AbstractDBToolsMojo extends AbstractMojo {

    /**
     * Name of the directory where the schema file is located.
     *
     * @parameter expression="${basedir}/src/main/db"
     */
    protected String schemaDir;
    /**
     * Name of the schema file to do the generation from.
     *
     * @parameter default-value="schema.xml"
     */
    protected String schemaXMLFilename;
    /**
     * Vendor to generate sql to.
     *
     * Example values: InterBase, Firebird, DB2, Oracle, Sybase, PostgreSQL, InstantDB,
     * HSQLDB, iAnywhere, Derby, PointBase, mySQL, MS SQLSERVER, MS SQLSERVER2000,
     * Cloudscape, InformixDB,
     *
     * @required
     * @parameter expression=""
     *
     */
    protected String dbVendor;
    /**
     * Name of alias of database connection that will be added in DBTools
     *
     * @parameter default-value=""
     */
    protected String dbAlias;
    /**
     * Classpath of JDBC Driver
     *
     * @parameter default-value=""
     */
    protected String dbDriverClasspath;
    /**
     * Database URL
     *
     * @parameter default-value=""
     */
    protected String dbUrl;
    /**
     * Database Username
     *
     * @parameter default-value=""
     */
    protected String dbUsername;
    /**
     * Database Password
     *
     * @parameter default-value=""
     */
    protected String dbPassword;

    protected String getSchemaFullFilename() {
        if (schemaDir.endsWith("\\") || schemaDir.endsWith("/")) {
            return schemaDir + schemaXMLFilename;
        } else {
            return schemaDir + "/" + schemaXMLFilename;
        }
    }
}