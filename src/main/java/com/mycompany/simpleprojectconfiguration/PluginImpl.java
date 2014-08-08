/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.simpleprojectconfiguration;

import hudson.Plugin;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.AbstractItem;
import hudson.model.Hudson;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author kshantaraman
 */
public class PluginImpl extends Plugin{
    
    private List<MyAction> links = new ArrayList<MyAction>();
    @Override
    public void start() throws Exception{
        load();
        Hudson.getInstance().getActions().addAll(links);
    }
    
    @Initializer(after=InitMilestone.PLUGINS_STARTED)
    public static void init(){
    
    }
}