/*
The MIT License
Copyright (c) 2004-2013, Sun Microsystems, Inc., Kohsuke Kawaguchi, id:cactusman, Vincent Latombe
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package com.mycompany.simpleprojectconfiguration;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import hudson.model.AbstractProject;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;

/**
*
* ModifyProjectActionFactory class extending from {@link TransientProjectActionFactory} adds the "Basic Configuration" Link to the Jenkins Side Bar
* 
*/

@Extension
public class ModifyProjectActionFactory extends TransientProjectActionFactory{
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Collection<? extends Action> createFor(AbstractProject target){
            return Collections.singleton(new BasicConfigurationAction(target));
        }
}
