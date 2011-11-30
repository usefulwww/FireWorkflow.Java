/**
 * Copyright 2003-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.fireflow.model.DataField;
import org.fireflow.model.Duration;
import org.fireflow.model.EventListener;
import org.fireflow.model.FormTask;
import org.fireflow.model.SubflowTask;
import org.fireflow.model.Task;
import org.fireflow.model.TaskRef;
import org.fireflow.model.ToolTask;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Loop;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.fireflow.model.resource.Application;
import org.fireflow.model.resource.Form;
import org.fireflow.model.resource.Participant;
import org.fireflow.model.resource.SubWorkflowProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author chennieyun
 */
public class JAXP_FPDL_Serializer implements IFPDLSerializer {

    String encoding = "UTF-8";

    protected void serialize(WorkflowProcess workflowProcess, Writer out)
            throws IOException, FPDLSerializerException {
        try {
            Document document = workflowProcessToDom(workflowProcess);

            DOMSource doms = new DOMSource(document);

            StreamResult result = new StreamResult(new BufferedWriter(out));

            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", new Integer(2));

            Transformer transformer = tf.newTransformer();

            Properties properties = transformer.getOutputProperties();
            properties.setProperty(OutputKeys.ENCODING, encoding);
            properties.setProperty(OutputKeys.METHOD, "xml");
            properties.setProperty(OutputKeys.INDENT, "yes");
            properties.setProperty(OutputKeys.DOCTYPE_PUBLIC, PUBLIC_ID);
            properties.setProperty(OutputKeys.DOCTYPE_SYSTEM, SYSTEM_ID);

            transformer.setOutputProperties(properties);

            transformer.transform(doms, result);
        } catch (TransformerException ex) {
            Logger.getLogger(JAXP_FPDL_Serializer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void serialize(WorkflowProcess workflowProcess, OutputStream out)
            throws IOException, FPDLSerializerException {
        serialize(workflowProcess, new OutputStreamWriter(out, encoding));
    /*
    try {
    Document document = workflowProcessToDom(workflowProcess);

    DOMSource doms = new DOMSource(document);

    StreamResult result = new StreamResult(new BufferedWriter(new OutputStreamWriter(out)));
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setAttribute("indent-number", new Integer(2));

    Transformer transformer = tf.newTransformer();

    Properties properties = transformer.getOutputProperties();
    properties.setProperty(OutputKeys.ENCODING, encoding);
    properties.setProperty(OutputKeys.METHOD, "xml");
    properties.setProperty(OutputKeys.INDENT, "yes");
    properties.setProperty(OutputKeys.DOCTYPE_PUBLIC, PUBLIC_ID);
    properties.setProperty(OutputKeys.DOCTYPE_SYSTEM, SYSTEM_ID);

    transformer.setOutputProperties(properties);

    transformer.transform(doms, result);
    } catch (TransformerException ex) {
    Logger.getLogger(JAXP_FPDL_Serializer.class.getName()).log(Level.SEVERE, null, ex);
    }
     */
    }

    public Document workflowProcessToDom(WorkflowProcess workflowProcess)
            throws FPDLSerializerException {
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(JAXP_FPDL_Serializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new FPDLSerializerException(ex.getMessage());
        }
        Document document = docBuilder.newDocument();
        Element workflowProcessElement = document.createElementNS(FPDL_URI, WORKFLOW_PROCESS);
        workflowProcessElement.setPrefix(FPDL_NS_PREFIX);
//                workflowProcessElement.setAttributeNS(XSD_URI, NAME, XSD_URI)
//        workflowProcessElement.addNamespace(FPDL_NS_PREFIX, FPDL_URI);
//        workflowProcessElement.addNamespace(XSD_NS_PREFIX, XSD_URI);
//        workflowProcessElement.addNamespace(XSI_NS_PREFIX, XSI_URI);
        workflowProcessElement.setAttribute(ID, workflowProcess.getId());
        workflowProcessElement.setAttribute(NAME, workflowProcess.getName());
        workflowProcessElement.setAttribute(DISPLAY_NAME, workflowProcess.getDisplayName());
        workflowProcessElement.setAttribute(RESOURCE_FILE, workflowProcess.getResourceFile());
        workflowProcessElement.setAttribute(RESOURCE_MANAGER, workflowProcess.getResourceManager());

        document.appendChild(workflowProcessElement);


        Util4JAXPSerializer.addElement(document, workflowProcessElement, DESCRIPTION,
                workflowProcess.getDescription());

        if (workflowProcess.getTaskInstanceCreator() != null && !workflowProcess.getTaskInstanceCreator().trim().equals("")) {
            workflowProcessElement.setAttribute(TASK_INSTANCE_CREATOR, workflowProcess.getTaskInstanceCreator());
        }
        if (workflowProcess.getFormTaskInstanceRunner()!=null && !workflowProcess.getFormTaskInstanceRunner().trim().equals("")){
            workflowProcessElement.setAttribute( FORM_TASK_INSTANCE_RUNNER, workflowProcess.getFormTaskInstanceRunner());
        }
        if (workflowProcess.getToolTaskInstanceRunner()!=null && !workflowProcess.getToolTaskInstanceRunner().trim().equals("")){
            workflowProcessElement.setAttribute( TOOL_TASK_INSTANCE_RUNNER, workflowProcess.getToolTaskInstanceRunner());
        }
        if (workflowProcess.getSubflowTaskInstanceRunner()!=null && !workflowProcess.getSubflowTaskInstanceRunner().trim().equals("")){
            workflowProcessElement.setAttribute(SUBFLOW_TASK_INSTANCE_RUNNER, workflowProcess.getSubflowTaskInstanceRunner());
        }
        if (workflowProcess.getFormTaskInstanceCompletionEvaluator()!=null && !workflowProcess.getFormTaskInstanceCompletionEvaluator().trim().equals("")){
            workflowProcessElement.setAttribute(FORM_TASK_INSTANCE_COMPLETION_EVALUATOR, workflowProcess.getFormTaskInstanceCompletionEvaluator());
        }
        if (workflowProcess.getToolTaskInstanceCompletionEvaluator()!=null && !workflowProcess.getToolTaskInstanceCompletionEvaluator().trim().equals("")){
            workflowProcessElement.setAttribute( TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR, workflowProcess.getToolTaskInstanceCompletionEvaluator());
        }
        if (workflowProcess.getSubflowTaskInstanceCompletionEvaluator()!=null && !workflowProcess.getSubflowTaskInstanceCompletionEvaluator().trim().equals("")){
            workflowProcessElement.setAttribute(SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR, workflowProcess.getSubflowTaskInstanceCompletionEvaluator());
        }


        writeDataFields(workflowProcess.getDataFields(), workflowProcessElement, document);
        writeStartNode(workflowProcess.getStartNode(), workflowProcessElement, document);

        writeTasks(workflowProcess.getTasks(), workflowProcessElement, document);


        writeActivities(workflowProcess.getActivities(), workflowProcessElement, document);
        writeSynchronizers(workflowProcess.getSynchronizers(),
                workflowProcessElement, document);
        writeEndNodes(workflowProcess.getEndNodes(), workflowProcessElement, document);
        writeTransitions(workflowProcess.getTransitions(),
                workflowProcessElement, document);
        writeLoops(workflowProcess.getLoops(), workflowProcessElement, document);

        writeEventListeners(workflowProcess.getEventListeners(), workflowProcessElement, document);

        writeExtendedAttributes(workflowProcess.getExtendedAttributes(),
                workflowProcessElement, document);

//        Document document = df.createDocument(workflowProcessElement);
//        document.addDocType(WORKFLOW_PROCESS, this.PUBLIC_ID, this.SYSTEM_ID);
        return document;

    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected void writeEventListeners(List<EventListener> eventListeners, Element parentElement, Document document) {
        if (eventListeners == null || eventListeners.size() == 0) {
            return;
        }

        Element eventListenersElm =
                Util4JAXPSerializer.addElement(document, parentElement,
                EVENT_LISTENERS);
        for (int i = 0; i < eventListeners.size(); i++) {
            EventListener listener =  eventListeners.get(i);
            Element eventListenerElm = Util4JAXPSerializer.addElement(document,
                    eventListenersElm, EVENT_LISTENER);
            eventListenerElm.setAttribute(CLASS_NAME, listener.getClassName());
        }
    }

    protected void writeDataFields(List<DataField> dataFields, Element parent, Document document)
            throws FPDLSerializerException {

        if (dataFields == null || dataFields.size() == 0) {
            return;
        }

        Element dataFieldsElement = Util4JAXPSerializer.addElement(document, parent,
                DATA_FIELDS);
        Iterator<DataField> iter = dataFields.iterator();
        while (iter.hasNext()) {
            DataField dataField = iter.next();
            Element dataFieldElement = Util4JAXPSerializer.addElement(document,
                    dataFieldsElement, DATA_FIELD);

            dataFieldElement.setAttribute(ID, dataField.getId());
            dataFieldElement.setAttribute(NAME, dataField.getName());
            dataFieldElement.setAttribute(DISPLAY_NAME, dataField.getDisplayName());
            dataFieldElement.setAttribute(DATA_TYPE, dataField.getDataType());

            dataFieldElement.setAttribute(INITIAL_VALUE,
                    dataField.getInitialValue());

            Util4JAXPSerializer.addElement(document, dataFieldElement, DESCRIPTION, dataField.getDescription());

            writeExtendedAttributes(dataField.getExtendedAttributes(),
                    dataFieldElement, document);
        }
    }

    protected void writeEndNodes(List<EndNode> endNodes, Element parent, Document document) {
        Element endNodesElement = Util4JAXPSerializer.addElement(document, parent, END_NODES);
        Iterator<EndNode> iter = endNodes.iterator();

        while (iter.hasNext()) {
            writeEndNode( iter.next(), endNodesElement, document);
        }
    }

    protected void writeEndNode(EndNode endNode, Element parent, Document document) {
        Element endNodeElement = Util4JAXPSerializer.addElement(document, parent, END_NODE);
        endNodeElement.setAttribute(ID, endNode.getId());
        endNodeElement.setAttribute(NAME, endNode.getName());
        endNodeElement.setAttribute(DISPLAY_NAME, endNode.getDisplayName());

        Util4JAXPSerializer.addElement(document, endNodeElement, DESCRIPTION, endNode.getDescription());

        writeExtendedAttributes(endNode.getExtendedAttributes(), endNodeElement, document);

    }

    protected void writeStartNode(StartNode startNode, Element parent, Document document)
            throws FPDLSerializerException {
        if (startNode == null) {
            return;
        }
        Element startElement = Util4JAXPSerializer.addElement(document, parent, START_NODE);
        startElement.setAttribute(ID, startNode.getId());
        startElement.setAttribute(NAME, startNode.getName());

        startElement.setAttribute(DISPLAY_NAME, startNode.getDisplayName());

        Util4JAXPSerializer.addElement(document, startElement, DESCRIPTION, startNode.getDescription());

        writeExtendedAttributes(startNode.getExtendedAttributes(), startElement, document);
    }

    protected void writeSynchronizers(List<Synchronizer> synchronizers, Element parent, Document document)
            throws FPDLSerializerException {
        if (synchronizers == null || synchronizers.size() == 0) {
            return;
        }
        Element synchronizersElement = Util4JAXPSerializer.addElement(document, parent,
                SYNCHRONIZERS);

        Iterator<Synchronizer> iter = synchronizers.iterator();

        while (iter.hasNext()) {
            writeSynchronizer( iter.next(), synchronizersElement, document);
        }
    }

    protected void writeSynchronizer(Synchronizer synchronizer, Element parent, Document document)
            throws FPDLSerializerException {
        Element synchronizerElement = Util4JAXPSerializer.addElement(document, parent,
                SYNCHRONIZER);
        synchronizerElement.setAttribute(ID, synchronizer.getId());
        synchronizerElement.setAttribute(NAME, synchronizer.getName());
        synchronizerElement.setAttribute(DISPLAY_NAME, synchronizer.getDisplayName());

        Util4JAXPSerializer.addElement(document, synchronizerElement, DESCRIPTION,
                synchronizer.getDescription());
        writeExtendedAttributes(synchronizer.getExtendedAttributes(),
                synchronizerElement, document);
    }

    protected void writeActivities(List<Activity> activities, Element parent, Document document)
            throws FPDLSerializerException {

        if (activities == null || activities.size() == 0) {
            return;
        }

        Element activitiesElement = Util4JAXPSerializer.addElement(document, parent,
                ACTIVITIES);

        Iterator<Activity> iter = activities.iterator();
        while (iter.hasNext()) {
            writeActivity( iter.next(), activitiesElement, document);
        }
    }

    protected void writeActivity(Activity activity, Element parent, Document document)
            throws FPDLSerializerException {

        Element activityElement = Util4JAXPSerializer.addElement(document, parent, ACTIVITY);

        activityElement.setAttribute(ID, activity.getId());
        activityElement.setAttribute(NAME, activity.getName());
        activityElement.setAttribute(DISPLAY_NAME, activity.getDisplayName());
        activityElement.setAttribute(COMPLETION_STRATEGY, activity.getCompletionStrategy());

        Util4JAXPSerializer.addElement(document, activityElement, DESCRIPTION, activity.getDescription());
        writeEventListeners(activity.getEventListeners(), activityElement, document);
        writeExtendedAttributes(activity.getExtendedAttributes(),
                activityElement, document);

        writeTasks(activity.getInlineTasks(), activityElement, document);
        writeTaskRefs(activity.getTaskRefs(), activityElement,document);
    }

    protected void writeTaskRefs(List<TaskRef> taskRefs, Element parent,Document document) {
        Element taskRefsElement = Util4JAXPSerializer.addElement(document,parent, TASKREFS);
        Iterator<TaskRef> iter = taskRefs.iterator();
        while (iter.hasNext()) {
            TaskRef taskRef = iter.next();
            Element taskRefElement = Util4JAXPSerializer.addElement(document,taskRefsElement, TASKREF);
            taskRefElement.setAttribute(REFERENCE, taskRef.getReferencedTask().getId());
        }
    }

    protected void writeTasks(List<Task> tasks, Element parent, Document document)
            throws FPDLSerializerException {
        Element tasksElement = Util4JAXPSerializer.addElement(document, parent, TASKS);
        Iterator<Task> iter = tasks.iterator();

        while (iter.hasNext()) {
            writeTask( iter.next(), tasksElement, document);
        }
    }

    protected void writeTask(Task task, Element parent, Document document)
            throws FPDLSerializerException {
        Element taskElement = Util4JAXPSerializer.addElement(document, parent, TASK);

        taskElement.setAttribute(ID, task.getId());
        taskElement.setAttribute(NAME, task.getName());
        taskElement.setAttribute(DISPLAY_NAME, task.getDisplayName());
        taskElement.setAttribute(TYPE, task.getType());
        Util4JAXPSerializer.addElement(document, taskElement, DESCRIPTION, task.getDescription());

        if (task instanceof FormTask) {
            this.writePerformer(((FormTask) task).getPerformer(), taskElement, document);

            taskElement.setAttribute(COMPLETION_STRATEGY, ((FormTask) task).getAssignmentStrategy());
            taskElement.setAttribute(DEFAULT_VIEW, ((FormTask) task).getDefaultView());
            writeForm(EDIT_FORM, ((FormTask) task).getEditForm(), taskElement, document);
            writeForm(VIEW_FORM, ((FormTask) task).getViewForm(), taskElement, document);
            writeForm(LIST_FORM, ((FormTask) task).getListForm(), taskElement, document);
        } else if (task instanceof ToolTask) {

            this.writeApplication(((ToolTask) task).getApplication(), taskElement, document);
//            taskElement.setAttribute(EXECUTION, ((ToolTask) task).getExecution());
        } else if (task instanceof SubflowTask) {
            this.writeSubWorkflowProcess(((SubflowTask) task).getSubWorkflowProcess(), taskElement, document);
        }

        taskElement.setAttribute(PRIORITY, Integer.toString(task.getPriority()));

        writeDuration(task.getDuration(), taskElement, document);
        if (task.getTaskInstanceCreator() != null && !task.getTaskInstanceCreator().trim().equals("")) {
            taskElement.setAttribute(TASK_INSTANCE_CREATOR, task.getTaskInstanceCreator());
        }
        if (task.getTaskInstanceRunner()!=null && !task.getTaskInstanceRunner().trim().equals("")){
            taskElement.setAttribute(TASK_INSTANCE_RUNNER, task.getTaskInstanceRunner());
        }
        if (task.getTaskInstanceCompletionEvaluator()!=null && !task.getTaskInstanceCompletionEvaluator().trim().equals("")){
            taskElement.setAttribute( TASK_INSTANCE_COMPLETION_EVALUATOR, task.getTaskInstanceCompletionEvaluator());
        }

        if (task.getLoopStrategy()!=null && !task.getLoopStrategy().trim().equals("")){
            taskElement.setAttribute(LOOP_STRATEGY, task.getLoopStrategy());
        }
        writeEventListeners(task.getEventListeners(), taskElement, document);
        writeExtendedAttributes(task.getExtendedAttributes(), taskElement, document);

//        Element taskElement = Util4JAXPSerializer.addElement(document, parent, TASK);
//
//        taskElement.setAttribute(ID, task.getId());
//        taskElement.setAttribute(NAME, task.getName());
//        taskElement.setAttribute(DISPLAY_NAME, task.getDisplayName());
//        taskElement.setAttribute(TYPE, task.getType());
////        taskElement.setAttribute(START_MODE, task.getStartMode());
//        taskElement.setAttribute(COMPLETION_STRATEGY, task.getAssignmentStrategy());
//        taskElement.setAttribute(DEFAULT_VIEW, task.getDefaultView());
//        taskElement.setAttribute(PRIORITY, Integer.toString(task.getPriority()));
//        taskElement.setAttribute(EXECUTION, task.getExecution());
//
//        Util4JAXPSerializer.addElement(document, taskElement, DESCRIPTION, task.getDescription());
//
//        writeForm(EDIT_FORM, task.getEditForm(), taskElement, document);
//        writeForm(VIEW_FORM, task.getViewForm(), taskElement, document);
//        writeForm(LIST_FORM, task.getListForm(), taskElement, document);
//        this.writeApplication(task.getApplication(), taskElement, document);
//        this.writeSubWorkflowProcess(task.getSubWorkflowProcess(), taskElement, document);
//        this.writePerformer(task.getPerformer(), taskElement, document);
//
//        writeDuration(task.getDuration(), taskElement, document);
//        writeEventListeners(task.getEventListeners(), taskElement, document);
//        writeExtendedAttributes(task.getExtendedAttributes(), taskElement, document);
    }

    protected void writeDuration(Duration duration, Element parent, Document document) {
        if (duration == null) {
            return;
        }
        Element durationElement = Util4JAXPSerializer.addElement(document, parent, DURATION);
        durationElement.setAttribute(VALUE, Integer.toString(duration.getValue()));
        durationElement.setAttribute(UNIT, duration.getUnit());
        durationElement.setAttribute(IS_BUSINESS_TIME, Boolean.toString(duration.isBusinessTime()));
    }

    protected void writeForm(String formName, Form form, Element parent, Document document) {
        if (form == null) {
            return;
        }
        Element editFormElement = Util4JAXPSerializer.addElement(document, parent, formName);
        editFormElement.setAttribute(NAME, form.getName());
        editFormElement.setAttribute(DISPLAY_NAME, form.getDisplayName());

        Util4JAXPSerializer.addElement(document, editFormElement, DESCRIPTION, form.getDescription());
        Util4JAXPSerializer.addElement(document, editFormElement, URI, form.getUri());
    }

    protected void writeLoops(List<Loop> loops, Element parent, Document document) {
        if (loops == null || loops.size() == 0) {
            return;
        }
        Element transitionsElement = Util4JAXPSerializer.addElement(document, parent,
                LOOPS);

        Iterator<Loop> iter = loops.iterator();
        while (iter.hasNext()) {
            Loop loop = iter.next();

            Element loopElement = Util4JAXPSerializer.addElement(document, transitionsElement,
                    LOOP);

            loopElement.setAttribute(ID, loop.getId());
            loopElement.setAttribute(FROM, loop.getFromNode().getId());
            loopElement.setAttribute(TO, loop.getToNode().getId());

            loopElement.setAttribute(NAME, loop.getName());
            loopElement.setAttribute(DISPLAY_NAME, loop.getDisplayName());

            Util4JAXPSerializer.addElement(document, loopElement, CONDITION, loop.getCondition());

            writeExtendedAttributes(loop.getExtendedAttributes(),
                    loopElement, document);
        }
    }

    protected void writeTransitions(List<Transition> transitions, Element parent, Document document)
            throws FPDLSerializerException {

        if (transitions == null || transitions.size() == 0) {
            return;
        }

        Element transitionsElement = Util4JAXPSerializer.addElement(document, parent,
                TRANSITIONS);

        Iterator<Transition> iter = transitions.iterator();
        while (iter.hasNext()) {
            writeTransition( iter.next(), transitionsElement, document);
        }
    }

    protected void writeTransition(Transition transition, Element parent, Document document)
            throws FPDLSerializerException {

        Element transitionElement = Util4JAXPSerializer.addElement(document, parent,
                TRANSITION);

        transitionElement.setAttribute(ID, transition.getId());
        transitionElement.setAttribute(FROM, transition.getFromNode().getId());
        transitionElement.setAttribute(TO, transition.getToNode().getId());

        transitionElement.setAttribute(NAME, transition.getName());
        transitionElement.setAttribute(DISPLAY_NAME, transition.getDisplayName());

        Util4JAXPSerializer.addElement(document, transitionElement, CONDITION, transition.getCondition());

        writeExtendedAttributes(transition.getExtendedAttributes(),
                transitionElement, document);
    }

    protected Element writeExtendedAttributes(Map<String,String> extendedAttributes,
            Element parent, Document document) {

        if (extendedAttributes == null || extendedAttributes.size() == 0) {
            return null;
        }

        Element extendedAttributesElement =
                Util4JAXPSerializer.addElement(document, parent,
                EXTENDED_ATTRIBUTES);
//                        parent
//				.addElement(EXTENDED_ATTRIBUTES);

        Iterator<String> keys = extendedAttributes.keySet().iterator();
        while (keys.hasNext()) {
        	String key = keys.next();
        	String value = extendedAttributes.get(key);

            Element extendedAttributeElement = Util4JAXPSerializer.addElement(document,
                    extendedAttributesElement, EXTENDED_ATTRIBUTE);
            extendedAttributeElement.setAttribute(NAME, key.toString());
            if (value != null) {
                extendedAttributeElement.setAttribute(VALUE, value.toString());
            }

        }

        return extendedAttributesElement;

    }

    protected void writePerformer(Participant participant, Element parent, Document document) {
        if (participant == null) {
            return;
        }

        Element participantElement = Util4JAXPSerializer.addElement(document, parent,
                PERFORMER);

        participantElement.setAttribute(NAME, participant.getName());
        participantElement.setAttribute(DISPLAY_NAME, participant.getDisplayName());

        Util4JAXPSerializer.addElement(document, participantElement, DESCRIPTION, participant.getDescription());

        Util4JAXPSerializer.addElement(document, participantElement, ASSIGNMENT_HANDLER,
                participant.getAssignmentHandler());

    }

    protected void writeSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess, Element parent, Document document) {
        if (subWorkflowProcess == null) {
            return;
        }
        Element subflowElement = Util4JAXPSerializer.addElement(document, parent,
                SUB_WORKFLOW_PROCESS);

        subflowElement.setAttribute(NAME, subWorkflowProcess.getName());
        subflowElement.setAttribute(DISPLAY_NAME, subWorkflowProcess.getDisplayName());

        Util4JAXPSerializer.addElement(document, subflowElement, DESCRIPTION, subWorkflowProcess.getDescription());

        Util4JAXPSerializer.addElement(document, subflowElement, WORKFLOW_PROCESS_ID,
                subWorkflowProcess.getWorkflowProcessId());

    }

    protected void writeApplication(Application application, Element parent, Document document)
            throws FPDLSerializerException {

        if (application == null) {
            return;
        }

        Element applicationElement = Util4JAXPSerializer.addElement(document, parent,
                APPLICATION);

        applicationElement.setAttribute(NAME, application.getName());
        applicationElement.setAttribute(DISPLAY_NAME, application.getDisplayName());

        Util4JAXPSerializer.addElement(document, applicationElement, DESCRIPTION, application.getDescription());

        Util4JAXPSerializer.addElement(document, applicationElement, HANDLER, application.getHandler());

    }
}
