package com.jdc.mvn.plugins.dbtools;

import com.jdc.db.objects.DBObjectBuilder;
import com.jdc.db.objects.GroupObjectBuilder;
import com.jdc.db.objects.jpa.JPADBObjectBuilder;
import com.jdc.db.objects.jpa.JPADBObjectBuilder.JPAManagerType;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Goal which creates JPA entities based on an DBTools schema.xml file
 *
 * @goal genclasses
 * @phase generate-sources
 * @author <a href="mailto:jeff@soupbowl.net">Jeff Campbell</a>
 * @version $Id$
 *
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
     * \     */
    private String outputTestSrcDir;
    
    /**
     * Build JEE entities for use in JEE Application Server (such as JBoss).
     * Default is to build JSE JPA entities for use in JSE Applications (such
     * as Tomcat, Java Desktop, etc)
     *
     * @parameter default-value="false"
     */
    private boolean useJavaEE = false;
    
    /**
     * If useJavaEE == true, create a local interface to StatlessSessionBean Manager
     *
     * @parameter default-value="true"
     */
    private boolean genLocalInterface = true;
    
    /**
     * If useJavaEE == true, create a remote interface to StatlessSessionBean Manager
     *
     * @parameter default-value="false"
     */
    private boolean genRemoteInterface = true;

    /**
     * If includeXMLSupport == true, all entities will include a dom4j toXML() xml constructor
     *
     * @parameter default-value="false"
     */
    private boolean includeXMLSupport = false;
    
    
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
     *
     *           <!--jar file that has the renderer -->
     *           <dependencies>
     *               <dependency>
     *                   <groupId>mygroup</groupId>
     *                   <artifactId>myartifact</artifactId>
     *                   <version>1.0</version>
     *               </dependency>
     *           </dependencies>
     *
     *  @parameter
     */
    private String rendererClasspath = null;
    
    @Override
    public void execute() throws MojoExecutionException {
        if (!skip) {
            getLog().info("Generating JPA Entities...");
            verifyParameters();
            genClasses();
        } else {
            getLog().info("SKIPPING Generating JPA Entities.");
        }
    }
    
    private void verifyParameters() throws MojoExecutionException {
        File schemaFile = new File(getSchemaFullFilename());
        if (!schemaFile.exists()) {
            throw new MojoExecutionException("Could not find file: "+ getSchemaFullFilename());
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
        GroupObjectBuilder gObjBuilder = new GroupObjectBuilder();

        if (rendererClasspath != null) {
            getLog().info("Using Custom Renderer: "+ rendererClasspath);
        } else {
            getLog().info("Using DBTools Renderer");
        }
        
        // load custom renderer (if requested) otherwise use the default JPA Renderer
        if (rendererClasspath != null && rendererClasspath.length() != 0) {
            try {
                Class c = this.getClass().getClassLoader().loadClass(rendererClasspath);
                Object o = c.newInstance();
                
                getLog().info("Renderer Object: "+ o);
                
                if (o instanceof DBObjectBuilder) {
                    DBObjectBuilder oBuilder = (DBObjectBuilder) o;
                    
                    oBuilder.setProperty("useJavaEE", useJavaEE);
                    oBuilder.setProperty("genLocalInterface", genLocalInterface);
                    oBuilder.setProperty("genRemoteInterface", genRemoteInterface);
                    oBuilder.setIncludeXMLSupport(includeXMLSupport);
                    
                    gObjBuilder.setObjectBuilder(oBuilder);
                    getLog().info("Using custom renderer ["+ rendererClasspath +"]");
                    
                    
                } else {
                    throw new MojoExecutionException("Could not cast renderer ["+ rendererClasspath +"] to DBObjectBuilder");
                }
                
            } catch (IllegalAccessException iaex) {
                getLog().error("Error accessing renderer class ["+ rendererClasspath +"].", iaex);
                throw new MojoExecutionException("Could create renderer ["+ rendererClasspath +"]", iaex);
            } catch (InstantiationException inex) {
                getLog().error("Error creating renderer class ["+ rendererClasspath +"].", inex);
                throw new MojoExecutionException("Could create renderer ["+ rendererClasspath +"]", inex);
            } catch (ClassNotFoundException ex) {
                getLog().error("Error finding specified renderer class ["+ rendererClasspath +"]\n be sure to include the dependency in the plugin (see plug-in doc)", ex);
                throw new MojoExecutionException("Could create renderer ["+ rendererClasspath +"]", ex);
            }
        } else {
            // Default JPA Renderer
            JPADBObjectBuilder jpaOB= new JPADBObjectBuilder();

            if (!useJavaEE) {
                jpaOB.setManagerType(JPAManagerType.JavaSE);
            } else {
                jpaOB.setManagerType(JPAManagerType.JavaEE);
                jpaOB.setJeeLocalInterface(genLocalInterface);
                jpaOB.setJeeRemoteInterface(genRemoteInterface);
            }

            jpaOB.setIncludeXMLSupport(includeXMLSupport);
            gObjBuilder.setObjectBuilder(jpaOB);
            
        }
        
        // schema file
        gObjBuilder.setXmlFilename(getSchemaFullFilename());
        
        // tables to build objects for
        gObjBuilder.setTables(null); // means all
        
        // output directory
        //File source = project.
        gObjBuilder.setOutputBaseDir(getPathFromPackage(outputSrcDir, basePackageName));
        if (genUnitTests) {
            gObjBuilder.setTestOutputBaseDir(getPathFromPackage(outputTestSrcDir, basePackageName));
        }
        
        // object information
        gObjBuilder.setPackageBase(basePackageName);
        
        // GENERATE
        gObjBuilder.build();
        
        // show results
        int filesGenerated = gObjBuilder.getObjectBuilder().getNumberFilesGenerated();
        getLog().info("Generated ["+ filesGenerated +"] JPA Entities.");
    }
}