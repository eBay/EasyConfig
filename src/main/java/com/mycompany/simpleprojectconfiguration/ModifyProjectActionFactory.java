package com.mycompany.simpleprojectconfiguration;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import hudson.model.AbstractProject;

import java.util.Collection;
import java.util.Collections;

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
