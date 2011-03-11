package com.jdc.mvn.plugins.dbtools;

import com.jdc.db.DBProfile;
import com.jdc.db.DBToolsFrame;
import com.jdc.db.Database;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which launches DBTools
 *
 * @goal showdb
 * @author <a href="mailto:jeff@soupbowl.net">Jeff Campbell</a>
 * @version $Id$
 *
 */
public class ShowDBDBToolsMojo extends AbstractDBToolsMojo {

    private DBToolsFrame dbToolsFrame;

    public void execute() throws MojoExecutionException {

        Database db = new Database();
        db.setAlias(dbAlias);
        db.setVendorCode(dbVendor);
        db.setDriver(dbDriverClasspath);
        db.setUrl(dbUrl);
        db.setUsername(dbUsername);
        db.setPassword(dbPassword);
        db.setSchema(schemaDir);

        DBProfile dbProfile = new DBProfile(db);


        dbToolsFrame = new DBToolsFrame();
        dbToolsFrame.addDBProfile(dbProfile);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                dbToolsFrame.setVisible(true);
            }
        });


        waitIndefinitely();
    }

    /**
     * Causes the current thread to wait indefinitely. This method does not return.
     */
    private void waitIndefinitely() {
        Object lock = new Object();

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException exception) {
                getLog().warn("RunMojo.interrupted", exception);
            }
        }
    }
}