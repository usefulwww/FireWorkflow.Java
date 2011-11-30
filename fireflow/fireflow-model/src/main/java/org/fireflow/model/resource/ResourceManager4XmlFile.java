/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.model.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.fireflow.model.io.Util4Parser;

/**
 *
 * @author chennieyun
 */
public class ResourceManager4XmlFile implements IResourceManager {
    List<Form> forms = null;
    List<Participant> participants = null;
    List<Application> applications = null;

    /**
     * 解析xml格式的资源文件，
     * @param xmlResourceFile
     * @throws java.lang.Exception
     */
    public void parseResource(String xmlResourceFile) throws Exception {
        File f = new File(xmlResourceFile);
        FileInputStream  fin = new FileInputStream(f);
        this.parseResource(fin);
    }

    /**
     * 解析xml格式的资源文件，
     * @param xmlResourceFile
     * @throws java.lang.Exception
     */
    public void parseResource(InputStream in) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(in);
        Element rootElement = document.getRootElement();
        forms = loadForms(rootElement);
        applications = this.loadApplications(rootElement);
        participants = this.loadParticipants(rootElement);
    }

    /**
     * @param element
     * @return
     */
    protected List<Form> loadForms( Element element)  {
        Element formsElement = Util4Parser.child(element, "Forms");
        if (formsElement==null)return null;
        List<Element> formElementList = Util4Parser.children(formsElement, "Form");
        List<Form> forms = new Vector<Form>();
        for (int i=0; formElementList!=null && i<formElementList.size();i++){
            Element formElm = formElementList.get(i);
            String name = formElm.attributeValue("Name");
            Form form = new Form(name);
            form.setDisplayName(formElm.attributeValue("DisplayName"));
            form.setDescription(Util4Parser.elementAsString(formElm, "Description"));
            form.setUri(Util4Parser.elementAsString(formElm, "Uri"));
            forms.add(form);
        }
        return forms;
    }
    
    /**
     * @param element
     * @return
     */
    protected List<Application> loadApplications( Element element)  {
        Element applicationsElement = Util4Parser.child(element, "Applications");
        if (applicationsElement==null)return null;
        List<Element> appElementList = Util4Parser.children(applicationsElement, "Application");
        List<Application> apps = new Vector<Application>();
        for (int i=0; appElementList!=null && i<appElementList.size();i++){
            Element appElm = appElementList.get(i);
            String name = appElm.attributeValue("Name");
            Application app = new Application(name);
            app.setDisplayName(appElm.attributeValue("DisplayName"));
            app.setDescription(Util4Parser.elementAsString(appElm, "Description"));
            app.setHandler(Util4Parser.elementAsString(appElm, "Handler"));
            apps.add(app);
        }
        return apps;
    }    
    
    /**
     * @param element
     * @return
     */
    protected List<Participant> loadParticipants( Element element)  {
        Element participantsElement = Util4Parser.child(element, "Participants");
        if (participantsElement==null)return null;
        List<Element> partElementList = Util4Parser.children(participantsElement, "Participant");
        List<Participant> parts = new Vector<Participant>();
        for (int i=0; partElementList!=null && i<partElementList.size();i++){
            Element partElm = partElementList.get(i);
            String name = partElm.attributeValue("Name");
            Participant participant = new Participant(name);
            participant.setDisplayName(partElm.attributeValue("DisplayName"));
            participant.setDescription(Util4Parser.elementAsString(partElm, "Description"));
            participant.setAssignmentHandler(Util4Parser.elementAsString(partElm, "AssignmentHandler"));
            parts.add(participant);
        }
        return parts;
    }        
    /* (non-Javadoc)
     * @see org.fireflow.model.resource.IResourceManager#getApplications()
     */
    public List<Application> getApplications() {
        return this.applications;
    }

    /* (non-Javadoc)
     * @see org.fireflow.model.resource.IResourceManager#getParticipants()
     */
    public List<Participant> getParticipants() {
        return this.participants;
    }

    /* (non-Javadoc)
     * @see org.fireflow.model.resource.IResourceManager#getForms()
     */
    public List<Form> getForms() {
        return this.forms;
    }
}
