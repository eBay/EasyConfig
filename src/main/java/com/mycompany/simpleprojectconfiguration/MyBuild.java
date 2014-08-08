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
public class MyBuild extends Build<MyProject,MyBuild>{
    
    public MyBuild(MyProject project) throws IOException {
        super(project);
    }

    public MyBuild(MyProject job, Calendar timestamp) {
        super(job, timestamp);
    }

    public MyBuild(MyProject project, File buildDir) throws IOException {
        super(project, buildDir);
    }
    
}
