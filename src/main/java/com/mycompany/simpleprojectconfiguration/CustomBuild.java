/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.simpleprojectconfiguration;

import hudson.model.Build;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 *
 * @author kshantaraman
 */
public class CustomBuild extends Build<CustomProject,CustomBuild>{
    
    public CustomBuild(CustomProject project) throws IOException {
        super(project);
    }

    public CustomBuild(CustomProject job, Calendar timestamp) {
        super(job, timestamp);
    }

    public CustomBuild(CustomProject project, File buildDir) throws IOException {
        super(project, buildDir);
    }
    
}
