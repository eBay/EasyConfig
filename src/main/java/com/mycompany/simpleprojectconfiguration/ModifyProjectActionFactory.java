package com.mycompany.simpleprojectconfiguration;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import hudson.model.AbstractProject;

import java.util.Collection;
import java.util.Collections;

/**
*
* ModifyProjectActionFactory class extending from {@Link TransientProjectActionFactory} adds the "Basic Configuration" Link to the Jenkins Side Bar
* 
*/

@Extension
public class ModifyProjectActionFactory extends TransientProjectActionFactory{
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Collection<? extends Action> createFor(AbstractProject target){
            return Collections.singleton(new MyAction(target));
            
	}
}
