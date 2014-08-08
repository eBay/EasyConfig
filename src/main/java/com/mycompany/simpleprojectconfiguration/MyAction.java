package com.mycompany.simpleprojectconfiguration;

import antlr.ANTLRException;

import hudson.model.Action;

import hudson.model.ItemGroup;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Project;

import hudson.security.AccessControlled;
import hudson.util.FormApply;
import hudson.util.FormValidation;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import hudson.plugins.git.GitSCM;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.cobertura.CoberturaPublisher;
import hudson.plugins.cobertura.targets.CoverageTarget;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.findbugs.FindBugsPublisher;
import hudson.tasks.LogRotator;
import hudson.plugins.pmd.PmdPublisher;
import hudson.plugins.pmd.PmdProjectAction;
import hudson.plugins.pmd.PmdResultAction;
import hudson.plugins.cobertura.targets.CoverageMetric;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.UserRemoteConfig;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.timestamper.*;
import hudson.XmlFile;
import hudson.plugins.disk_usage.DiskUsageProperty;

import hudson.plugins.ws_cleanup.PreBuildCleanup;
import hudson.tasks.JavadocArchiver;
import hudson.tasks.junit.JUnitResultArchiver;
//import hudson.triggers.

import hudson.tasks.ArtifactArchiver;

import hudson.triggers.SCMTrigger;
import hudson.triggers.Trigger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.transform.stream.StreamSource;
import jenkins.mvn.GlobalSettingsProvider;
import jenkins.mvn.SettingsProvider;
import org.jenkinsci.plugins.envinject.EnvInjectBuildWrapper;

import org.jenkinsci.plugins.envinject.EnvInjectJobPropertyInfo;

public class MyAction implements Action {

    private final AbstractProject project;
    public String gitRepo;
    public String gitBranch;
    public String mavenGoals;
    public String mavenPOM;
    public String eMailRecipient;
    public String currNode;
    Project p;
    public boolean findBugsCheck;
    public boolean coberturaCheck;
    public boolean PMDCheck;
    public boolean JUnitCheck;
    public boolean JavadocCheck;
    public boolean ArtifactArchiverCheck;
    public boolean eMailCheck;

    public boolean geteMailCheck() {
        for (Object x : p.getPublishersList()) {
            if (x instanceof ExtendedEmailPublisher) {
                return true;
            }
        }
        return false;
    }

    public boolean getFindBugsCheck() {
        for (Object x : p.getPublishersList()) {
            if (x instanceof FindBugsPublisher) {
                return true;
            }
        }
        return false;
    }

    public boolean getCoberturaCheck() {
        for (Object x : p.getPublishersList()) {
            if (x instanceof CoberturaPublisher) {
                return true;
            }
        }
        return false;
    }

    public boolean getPMDCheck() {
        for (Object x : p.getPublishersList()) {
            if (x instanceof PmdPublisher) {
                return true;
            }
        }
        return false;
    }

    public boolean getJUnitCheck() {
        for (Object x : p.getPublishersList()) {
            if (x instanceof JUnitResultArchiver) {
                return true;
            }
        }
        return false;
    }

    public boolean getJavadocCheck() {
        for (Object x : p.getPublishersList()) {
            if (x instanceof JavadocArchiver) {
                return true;
            }
        }
        return false;
    }

    public boolean getArtifactArchiverCheck() {
        for (Object x : p.getPublishersList()) {
            if (x instanceof ArtifactArchiver) {
                return true;
            }
        }
        return false;
    }

    public MyAction(AbstractProject project) {
        this.project = project;
        p = (Project) project;
    }

    public String getGitRepo() {
        if (p.getScm() instanceof GitSCM) {
            GitSCM gSCM = (GitSCM) p.getScm();
            //List<UserRemoteConfig> lUsr=gSCM.getUserRemoteConfigs();
            for (UserRemoteConfig usr : gSCM.getUserRemoteConfigs()) {
                return usr.getUrl();
            }
        }
        return "git@github.scm.corp.mycompany.com:{Enter your git repo here}";
    }

    public String getGitBranch() {
        if (p.getScm() instanceof GitSCM) {
            GitSCM gSCM = (GitSCM) p.getScm();
            for (BranchSpec br : gSCM.getBranches()) {
                return br.getName();
            }
        }
        return "master";
    }

    public String getMavenGoals() {
        if (!p.getBuildersList().isEmpty()) {
            for (Object builder : p.getBuilders()) {
                if (builder instanceof hudson.tasks.Maven) {
                    hudson.tasks.Maven mvn = (hudson.tasks.Maven) builder;
                    return mvn.getTargets();
                }
            }
        }
        return "-U clean install";
    }

    public String getMavenPOM() {
        if (!p.getBuildersList().isEmpty()) {
            for (Object builder : p.getBuilders()) {
                if (builder instanceof hudson.tasks.Maven) {
                    hudson.tasks.Maven mvn = (hudson.tasks.Maven) builder;
                    return mvn.pom;
                }
            }
        }
        return "pom.xml";
    }

    public String geteMailRecipient() {
        if (!p.getPublishersList().isEmpty()) {
            for (Object pub : p.getPublishersList()) {
                if (pub instanceof ExtendedEmailPublisher) {
                    ExtendedEmailPublisher eMailPub = (ExtendedEmailPublisher) pub;
                    return eMailPub.recipientList;
                }
            }
        }
        return "DL-myCompany-PlatformFrameworks-QE-SJ@corp.mycompany.com";
    }

    public String getProjectName() {
        return project.getDisplayName();
    }

    public String getIconFileName() {
        if (hasPermission()) {
            return "next.png";
        }
        return null;
    }

    public String getDisplayName() {
        if (hasPermission()) {
            return "Basic Configuration";
        }
        return null;
    }

    public String getUrlName() {
        if (hasPermission()) {
            return "modify";
        }
        return null;
    }

    public FormValidation doCheckGitRepo(@QueryParameter String value)
            throws IOException, ServletException {
        if (value.contains("{Enter your git")) {
            return FormValidation.error("Please change default value");
        }
        if (!value.endsWith(".git")) {
            return FormValidation.error("Invalid Git Repo");
        }
        if (value.isEmpty()) {
            return FormValidation.error("Please enter Git Repository");
        }

        return FormValidation.ok("Save form and check main configuration page for other errors(if any)");
    }

    public FormValidation doCheckMavenGoals(@QueryParameter String value)
            throws IOException, ServletException {
        if (value.isEmpty()) {
            return FormValidation.error("Please specify Maven Goals");
        }
        return FormValidation.ok("Save form and check main configuration page for other errors(if any)");
    }

    public FormValidation doCheckMavenPOM(@QueryParameter String value)
            throws IOException, ServletException {
        if (value.isEmpty()) {
            return FormValidation.error("Please Specify a Maven POM");
        }
        if (!value.endsWith(".xml")) {
            return FormValidation.error("Specify valid XML File");
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckeMailRecipient(@QueryParameter String value)
            throws IOException, ServletException {
        if (value.isEmpty()) {
            return FormValidation.error("Please specify eMail ID/DL to notify to");
        }
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN
                = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(value);
        if (!matcher.matches()) {
            return FormValidation.error("Invalid Format");
        }

        return FormValidation.ok();
    }

    public void setGitRepo(String gitRepo) {
        this.gitRepo = gitRepo;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public void setMavenGoals(String mavenGoals) {
        this.mavenGoals = mavenGoals;
    }

    public void setMavenPOM(String mavenPOM) {
        this.mavenPOM = mavenPOM;
    }

    public void seteMailRecipient(String eMailRecipient) {
        this.eMailRecipient = eMailRecipient;
    }

    private boolean hasPermission() {
        ItemGroup parent = project.getParent();
        if (parent instanceof AccessControlled) {
            AccessControlled accessControlled = (AccessControlled) parent;
            return accessControlled.hasPermission(AbstractProject.CONFIGURE);
        }
        return Hudson.getInstance().hasPermission(AbstractProject.CONFIGURE);
    }

    public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException, Descriptor.FormException {
        JSONObject formdata = req.getSubmittedForm();

        //AbstractItem item = (AbstractItem) Jenkins.getInstance().getItemByFullName(formdata.getString("name"));
        //String config = generateConfigFile(formdata);
        generateConfigFile(formdata);
        p.save();
        project.save();
        XmlFile config = project.getConfigFile();
        String conf = config.toString();
        StringBuilder configXML = openConfigFile(conf);
        project.updateByXml(new StreamSource(new StringReader(configXML.toString())));
        getJobProperty(conf);
        p.save();
        //project.addAction(hudson.plugins.);
        FormApply.success(req.getContextPath() + '/' + "job/" + getProjectName()).generateResponse(req, rsp, null);
    }

    private void generateConfigFile(JSONObject formdata) {

        boolean lrFlag = false;
        LogRotator lr = project.getLogRotator();
        if (lr != null) {
            lrFlag = true;
        }
        if (!lrFlag) {
            getLogRotator();
        }
        //if(p.getScm()!=null && p.getScm() instanceof GitSCM)
        //gSCM=(GitSCM)p.getScm();
        getSCM(formdata.getString("repo"), formdata.getString("branch"));
        try {
            //str.append(getSCM(formdata.getString("repo"), formdata.getString("branch")));
            Boolean b = p.blockBuildWhenDownstreamBuilding();
            if (b == false) {
                p.setBlockBuildWhenDownstreamBuilding(false);
            } else {
                p.setBlockBuildWhenDownstreamBuilding(true);
            }
            b = p.blockBuildWhenUpstreamBuilding();
            if (b == false) {
                p.setBlockBuildWhenUpstreamBuilding(false);
            } else {
                p.setBlockBuildWhenUpstreamBuilding(true);
            }
            if (!p.isConcurrentBuild()) {
                p.setConcurrentBuild(false);
            } else {
                p.setConcurrentBuild(true);
            }

            if (p.getJDK() == null) {
                if (Hudson.getInstance().getJDK("IBMJDK7") != null) {
                    p.setJDK(Hudson.getInstance().getJDK("IBMJDK7"));
                }
            } else {
                p.setJDK(p.getJDK());
            }
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        getTriggers();
        getBuilders(formdata.getString("goals"), formdata.getString("pom"));
        findBugsCheck = formdata.getBoolean("findbugscheck");
        coberturaCheck = formdata.getBoolean("coberturacheck");
        PMDCheck = formdata.getBoolean("PMDcheck");
        JavadocCheck = formdata.getBoolean("Javadoccheck");
        JUnitCheck = formdata.getBoolean("JUnitcheck");
        ArtifactArchiverCheck = formdata.getBoolean("Artifactcheck");
        eMailCheck = formdata.getBoolean("eMailcheck");
        getPublishers(formdata.getString("emailrecipient"), findBugsCheck, coberturaCheck, PMDCheck,
                JavadocCheck, JUnitCheck, ArtifactArchiverCheck, eMailCheck);
        getBuildWrappers();
    }

    private void getJobProperty(String conf) {
        DiskUsageProperty dp = new DiskUsageProperty();
        dp.setDiskUsageWithoutBuilds(new Long(144208));
        ConcurrentHashMap<String, Long> slaveWorkspaceMap = new ConcurrentHashMap<String, Long>();
        String curNode = conf.substring(0, conf.lastIndexOf("/"));
        slaveWorkspaceMap.put(curNode, new Long(326983836));
        //dp.putSlaveWorkspace(Hudson.getInstance().getNode(currNode), curNode);
        dp.getSlaveWorkspaceUsage().put(currNode, slaveWorkspaceMap);
        try {

            if (!p.getAllProperties().isEmpty()) {
                for (Object x : p.getAllProperties()) {
                    if (x instanceof DiskUsageProperty) {
                        p.removeProperty((DiskUsageProperty) x);
                    }
                }
            }
            p.addProperty(dp);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getLogRotator() {
        LogRotator lr = new LogRotator("-1", "10", "-1", "-1");
        try {
            project.setLogRotator(lr);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }

    private void getSCM(String repo, String branch) {
        UserRemoteConfig urc = new UserRemoteConfig(repo, null, null, null);
        List<UserRemoteConfig> urcList = new ArrayList<UserRemoteConfig>();
        urcList.add(urc);
        hudson.plugins.git.BranchSpec brspec = new BranchSpec(branch);
        List<BranchSpec> brSpecList = new ArrayList<BranchSpec>();
        brSpecList.add(brspec);
        GitSCMExtension gitExt = new hudson.plugins.git.extensions.impl.WipeWorkspace();
        List<GitSCMExtension> gsExtList = new ArrayList<GitSCMExtension>();
        gsExtList.add(gitExt);
        GitSCM gSCM = new GitSCM(urcList, brSpecList, false, null, null, null, gsExtList);
        try {
            p.setScm(gSCM);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }

    private void getTriggers() {
        boolean scmtrigFlag = false;
        try {
            Trigger trig = project.getTrigger(SCMTrigger.class);

            if (trig != null) //if(p.getTriggers().containsValue(SCMTrigger.class))
            {
                scmtrigFlag = true;
            }
            if (!scmtrigFlag) {
                SCMTrigger scmtrig = new SCMTrigger("H * * * *", false);
                p.addTrigger(scmtrig);
            }
        } catch (ANTLRException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getBuilders(String goals, String pom) {
        SettingsProvider svp = new jenkins.mvn.DefaultSettingsProvider();
        GlobalSettingsProvider gvp = new jenkins.mvn.DefaultGlobalSettingsProvider();
        try {
            for (Object x : p.getBuildersList()) {
                if (x instanceof hudson.tasks.Maven) {
                    //mavenFlag = true;
                    p.getBuildersList().remove(x);
                }
            }
            hudson.tasks.Maven mvn = new hudson.tasks.Maven(goals, "Maven 3.0.5", pom, null,
                    null, true, svp, gvp);

            //if(!mavenFlag)
            p.getBuildersList().add(mvn);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getFindBugsPublisher() {
        FindBugsPublisher FBP = new FindBugsPublisher("0", "30", "low", null, false, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, false, false, false, "**/target/findbugsXml.xml",
                false, false, null, null);
        try {
            p.getPublishersList().add(FBP);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getPMDPublisher() {
        PmdPublisher pmd = new PmdPublisher(null, null, "low", null, false, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, false, false, false, false, "**/pmd.xml");
        try {
            p.getPublishersList().add(pmd);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getArtifactArchiver() {
        ArtifactArchiver arc = new ArtifactArchiver("**/target/cobertura/cobertura.ser,pom.xml", "false", false);
        try {
            p.getPublishersList().add(arc);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getCoberturaPublisher() {
        Map<CoverageMetric, Integer> covMap = new HashMap<CoverageMetric, Integer>();
        covMap.put(CoverageMetric.CONDITIONAL, 7000000);
        CoverageTarget cTarg = new CoverageTarget(covMap);
        hudson.plugins.cobertura.renderers.SourceEncoding srcEnc = null;

        CoberturaPublisher cPub = new CoberturaPublisher("**/target/site/cobertura/coverage.xml", false,
                false, false, false, false,
                true, false, srcEnc.ASCII, 0);
        cPub.setHealthyTarget(cTarg);
        covMap.clear();
        covMap.put(CoverageMetric.CONDITIONAL, 0);
        cTarg = new CoverageTarget(covMap);
        cPub.setUnhealthyTarget(cTarg);
        covMap.clear();
        covMap.put(CoverageMetric.CONDITIONAL, 0);
        cTarg = new CoverageTarget(covMap);
        cPub.setFailingTarget(cTarg);
        try {
            project.getPublishersList().add(cPub);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getJUnitResultPublisher() {
        JUnitResultArchiver jArc = new JUnitResultArchiver("**/target/surefire-reports/*.xml", false, null);
        try {
            p.getPublishersList().add(jArc);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getJavaDocPublisher() {
        JavadocArchiver jArc = new JavadocArchiver("target/site/apidocs", false);
        try {
            p.getPublishersList().add(jArc);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getExtendedEmailPublisher(String recipientList) {
        List<EmailTrigger> emailTrigList = new ArrayList<EmailTrigger>();
        emailTrigList.add(new hudson.plugins.emailext.plugins.trigger.AlwaysTrigger(true,
                true, true,
                true, recipientList,
                "$PROJECT_DEFAULT_REPLYTO", "$PROJECT_DEFAULT_SUBJECT", "$PROJECT_DEFAULT_CONTENT",
                null, 0, "project"));
        ExtendedEmailPublisher eMailPub = new ExtendedEmailPublisher(recipientList, "project", "$DEFAULT_SUBJECT",
                "$DEFAULT_CONTENT", null, "$DEFAULT_PRESEND_SCRIPT",
                0, "$DEFAULT_REPLYTO", false,
                emailTrigList, null);
        try {
            p.getPublishersList().add(eMailPub);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getBuildWrappers() {
        Project p = (Project) project;
        boolean envInjBuildWrapFlag = false;
        boolean TimestamperBuildWrapFlag = false;
        boolean PBCBuildWrapFlag = false;
        for (Object x : p.getBuildWrappersList()) {
            if (x instanceof EnvInjectBuildWrapper) {
                envInjBuildWrapFlag = true;
                continue;
            }
            if (x instanceof TimestamperBuildWrapper) {
                TimestamperBuildWrapFlag = true;
                continue;
            }
            if (x instanceof PreBuildCleanup) {
                PBCBuildWrapFlag = true;
                continue;
            }
        }

        PreBuildCleanup pbc = new PreBuildCleanup(null, false, null, null);

        EnvInjectJobPropertyInfo eijpo = new EnvInjectJobPropertyInfo(null, "MAVEN_OPTS=-Xms256m -Xmx1024m", null, null, null, false);
        EnvInjectBuildWrapper envInj = new EnvInjectBuildWrapper();
        TimestamperBuildWrapper tmbw = new TimestamperBuildWrapper();
        envInj.setInfo(eijpo);
        try {
            if (!PBCBuildWrapFlag) {
                p.getBuildWrappersList().add(pbc);
            }
            if (!envInjBuildWrapFlag) {
                p.getBuildWrappersList().add(envInj);
            }
            if (!TimestamperBuildWrapFlag) {
                p.getBuildWrappersList().add(tmbw);
            }
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private StringBuilder openConfigFile(String conf) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            String currentLine;
            br = new BufferedReader(new FileReader(conf));
            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        String xml = sb.toString();
        //if(xml.contains("H * * * *"))
          //  xml=xml.replace("H * * * *", "H/5 * * * *");
        xml = xml.replace("<canRoam>true</canRoam>", "<canRoam>false</canRoam>");
        if (xml.contains("vectors")) {
            xml = xml.replace("<triggers class=\"vectors\">", "<triggers>");
        }
        if (!xml.contains("<assignedNode>")) {
            currNode = "CurrentDevelopment";
            xml = xml.replace("</scm>", "</scm><assignedNode>" + currNode + "</assignedNode>");
        } else {
            Pattern pattern = Pattern.compile("<(.*?)assignedNode>");
            String[] result = pattern.split(xml);
            currNode = result[1];
        }
        sb = new StringBuilder(xml);
        return sb;
    }

    private void getPublishers(String recipientList, boolean findBugsCheck,
            boolean coberturaCheck, boolean PMDCheck,
            boolean JavadocCheck, boolean JUnitCheck, boolean ArtifactArchiverCheck, boolean eMailCheck) {

        boolean FindBugsFlag = false;
        boolean PMDPublisherFlag = false;
        boolean CoberturaPubFlag = false;
        boolean JUnitPubFlag = false;
        boolean JavaDocPubFlag = false;
        boolean extendedEMailPubFlag = false;
        boolean ArtifactArchiverFlag = false;

        for (Object x : p.getPublishersList()) {
            if (x instanceof PmdPublisher) {
                PMDPublisherFlag = true;
                continue;
            }
            if (x instanceof CoberturaPublisher) {
                CoberturaPubFlag = true;
                continue;
            }
            if (x instanceof FindBugsPublisher) {
                FindBugsFlag = true;
                continue;
            }
            if (x instanceof ExtendedEmailPublisher) {

                extendedEMailPubFlag = true;
                    //p.getPublishersList().remove(ExtendedEmailPublisher.class);

                continue;
            }
            if (x instanceof JUnitResultArchiver) {
                JUnitPubFlag = true;
                continue;
            }
            if (x instanceof ArtifactArchiver) {
                ArtifactArchiverFlag = true;
                continue;
            }
            if (x instanceof JavadocArchiver) {
                JavaDocPubFlag = true;
                continue;
            }
        }
        try {
            if (FindBugsFlag && !findBugsCheck) {
                project.getPublishersList().remove(FindBugsPublisher.class);
            } else {
                if (findBugsCheck && !FindBugsFlag) {
                    getFindBugsPublisher();
                }
            }
            //if(!p.getPublishersList().contains(PmdPublisher.PUBLISHERS))
            if (PMDPublisherFlag && !PMDCheck) {

                project.getPublishersList().remove(PmdPublisher.class);
            } else {
                if (PMDCheck && !PMDPublisherFlag) {
                    getPMDPublisher();
                }
            }
            if (ArtifactArchiverFlag && !ArtifactArchiverCheck) {

                project.getPublishersList().remove(ArtifactArchiver.class);
            } else {
                if (ArtifactArchiverCheck && !ArtifactArchiverFlag) {
                    getArtifactArchiver();
                }
            }
            if (CoberturaPubFlag && !coberturaCheck) {

                project.getPublishersList().remove(CoberturaPublisher.class);
            } else {
                if (coberturaCheck && !CoberturaPubFlag) {
                    getCoberturaPublisher();
                }
            }
            if (JUnitPubFlag && !JUnitCheck) {

                project.getPublishersList().remove(JUnitResultArchiver.class);
            } else {
                if (JUnitCheck && !JUnitPubFlag) {
                    getJUnitResultPublisher();
                }
            }
            if (JavaDocPubFlag && !JavadocCheck) {

                project.getPublishersList().remove(JavadocArchiver.class);
            } else {
                if (JavadocCheck && !JavaDocPubFlag) {
                    getJavaDocPublisher();
                }
            }
            if (extendedEMailPubFlag) {
                if (!eMailCheck) {
                    project.getPublishersList().remove(ExtendedEmailPublisher.class);
                }
                if (eMailCheck) {
                    project.getPublishersList().remove(ExtendedEmailPublisher.class);
                    getExtendedEmailPublisher(recipientList);
                }
            } else {
                if (!extendedEMailPubFlag && eMailCheck) {
                    getExtendedEmailPublisher(recipientList);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
