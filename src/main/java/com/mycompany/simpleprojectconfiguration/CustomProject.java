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
