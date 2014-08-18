/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.simpleprojectconfiguration;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.Project;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;

/**
 *
 * @author kshantaraman
 */
public class CustomProject extends Project<CustomProject,CustomBuild> implements TopLevelItem{

    private CustomProject(ItemGroup parent, String name) {
        super(parent,name);
    }

    @Override
    protected Class<CustomBuild> getBuildClass() {
        return CustomBuild.class;
    }

    public TopLevelItemDescriptor getDescriptor() {
        return DESCRIPTOR;
    }
     @Extension(ordinal = 1000)
    public static final MyJobDescriptor DESCRIPTOR = new MyJobDescriptor();
     
     public static final class MyJobDescriptor extends AbstractProjectDescriptor{

        @Override
        public String getDisplayName() {
            return Messages.MyProject_DisplayName();
        }

        @Override
        public TopLevelItem newInstance(ItemGroup parent, String string) {
            return new CustomProject(parent,string);
        }
     }
}
