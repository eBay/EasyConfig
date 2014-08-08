/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simpleprojectconfiguration;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 *
 * @author kshantaraman
 */
@Extension
public class MyActionListener extends ItemListener {

    @Override
    public void onUpdated(Item item) {
        for (Action action : Jenkins.getInstance().getActions()) {
            if (action.getClass() == MyAction.class) {
                AbstractProject proj = (AbstractProject) item;

                XmlFile projConfig = proj.getConfigFile();
                String config = projConfig.toString();
                String gitRepo = config.substring(config.indexOf("<url>") + 5, config.indexOf("</url>"));
                String branch = config.substring(config.indexOf("<name>") + 6, config.indexOf("</name>"));
                String mavengoals = config.substring(config.indexOf("<target>") + 8, config.indexOf("<target>"));
                String mavenPom = config.substring(config.indexOf("<pom>") + 5, config.indexOf("</pom>"));
                String eMailReceipient = config.substring(config.indexOf("<recipientList>") + 15, config.indexOf("</recipientList>"));
                //Action newAction=new MyAction((AbstractItem) proj,gitRepo,branch,mavengoals,mavenPom,eMailReceipient);
                MyAction act=(MyAction) action;
                act.setGitBranch(branch);
                act.setGitRepo(gitRepo);
                act.setMavenGoals(mavengoals);
                act.setMavenPOM(mavenPom);
                act.seteMailRecipient(eMailReceipient);
                proj.addAction(act);
                proj.addAction(action);
                try {
                    proj.save();
                } catch (IOException ex) {
                    Logger.getLogger(MyActionListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
