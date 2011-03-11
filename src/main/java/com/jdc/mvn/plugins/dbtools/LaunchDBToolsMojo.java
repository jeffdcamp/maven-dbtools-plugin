package com.jdc.mvn.plugins.dbtools;

import com.jdc.db.DBToolsFrame;
import org.apache.maven.plugin.MojoExecutionException;


/**
 * Goal which launches DBTools
 *
 * @goal launch
 * @author <a href="mailto:jeff@soupbowl.net">Jeff Campbell</a>
 * @version $Id$
 *
 */
public class LaunchDBToolsMojo extends AbstractDBToolsMojo {
    
    public void execute() throws MojoExecutionException {
        DBToolsFrame.main(null);
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