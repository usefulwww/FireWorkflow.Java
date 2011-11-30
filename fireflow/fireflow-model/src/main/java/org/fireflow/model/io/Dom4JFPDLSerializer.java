/*--

Copyright (C) 2002-2003 Anthony Eden.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions, and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions, and the disclaimer that follows
these conditions in the documentation and/or other materials
provided with the distribution.

3. The names "OBE" and "Open Business Engine" must not be used to
endorse or promote products derived from this software without prior
written permission.  For written permission, please contact
me@anthonyeden.com.

4. Products derived from this software may not be called "OBE" or
"Open Business Engine", nor may "OBE" or "Open Business Engine"
appear in their name, without prior written permission from
Anthony Eden (me@anthonyeden.com).

In addition, I request (but do not require) that you include in the
end-user documentation provided with the redistribution and/or in the
software itself an acknowledgement equivalent to the following:
"This product includes software developed by
Anthony Eden (http://www.anthonyeden.com/)."

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

For more information on OBE, please see <http://www.openbusinessengine.org/>.

 */
package org.fireflow.model.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
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

/**
 * @author Anthony Eden
 * updated by nychen2000
 */
public class Dom4JFPDLSerializer implements IFPDLSerializer {

    public static final String DEFAULT_XPDL_VERSION = "1.0";
    public static final String DEFAULT_VENDOR = "\u975E\u4E5F";


    /* (non-Javadoc)
     * @see org.fireflow.model.io.IFPDLSerializer#serialize(org.fireflow.model.WorkflowProcess, java.io.OutputStream)
     */
    public void serialize(WorkflowProcess workflowProcess, OutputStream out)
            throws IOException, FPDLSerializerException {

        Document document = workflowProcessToDom(workflowProcess);

        // write the document to the output stream
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding("UTF-8");

        XMLWriter writer = new XMLWriter(out, format);

        writer.write(document);
        out.flush();
    }


    /**
     * @param workflowProcess
     * @return
     * @throws FPDLSerializerException
     */
    public Document workflowProcessToDom(WorkflowProcess workflowProcess)
            throws FPDLSerializerException {
        DocumentFactory df = new DocumentFactory();

        // serialize the Package
        Element workflowProcessElement = df.createElement(new QName(
                WORKFLOW_PROCESS, FPDL_NS));
        workflowProcessElement.addNamespace(FPDL_NS_PREFIX, FPDL_URI);
//      workflowProcessElement.addNamespace(XSD_NS_PREFIX, XSD_URI);
//      workflowProcessElement.addNamespace(XSI_NS_PREFIX, XSI_URI);
        workflowProcessElement.addAttribute(ID, workflowProcess.getId());
        workflowProcessElement.addAttribute(NAME, workflowProcess.getName());
        workflowProcessElement.addAttribute(DISPLAY_NAME, workflowProcess.getDisplayName());
        workflowProcessElement.addAttribute(RESOURCE_FILE, workflowProcess.getResourceFile());
        workflowProcessElement.addAttribute(RESOURCE_MANAGER, workflowProcess.getResourceManager());

        Util4Serializer.addElement(workflowProcessElement, DESCRIPTION,
                workflowProcess.getDescription());
        if (workflowProcess.getTaskInstanceCreator() != null && !workflowProcess.getTaskInstanceCreator().trim().equals("")) {
            workflowProcessElement.addAttribute(TASK_INSTANCE_CREATOR, workflowProcess.getTaskInstanceCreator());
        }
        if (workflowProcess.getFormTaskInstanceRunner()!=null && !workflowProcess.getFormTaskInstanceRunner().trim().equals("")){
            workflowProcessElement.addAttribute(FORM_TASK_INSTANCE_RUNNER, workflowProcess.getFormTaskInstanceRunner());
        }
        if (workflowProcess.getToolTaskInstanceRunner()!=null && !workflowProcess.getToolTaskInstanceRunner().trim().equals("")){
            workflowProcessElement.addAttribute(TOOL_TASK_INSTANCE_RUNNER, workflowProcess.getToolTaskInstanceRunner());
        }
        if (workflowProcess.getSubflowTaskInstanceRunner()!=null && !workflowProcess.getSubflowTaskInstanceRunner().trim().equals("")){
            workflowProcessElement.addAttribute(SUBFLOW_TASK_INSTANCE_RUNNER, workflowProcess.getSubflowTaskInstanceRunner());
        }
        if (workflowProcess.getFormTaskInstanceCompletionEvaluator()!=null && !workflowProcess.getFormTaskInstanceCompletionEvaluator().trim().equals("")){
            workflowProcessElement.addAttribute(FORM_TASK_INSTANCE_COMPLETION_EVALUATOR, workflowProcess.getFormTaskInstanceCompletionEvaluator());
        }
        if (workflowProcess.getToolTaskInstanceCompletionEvaluator()!=null && !workflowProcess.getToolTaskInstanceCompletionEvaluator().trim().equals("")){
            workflowProcessElement.addAttribute(TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR, workflowProcess.getToolTaskInstanceCompletionEvaluator());
        }
        if (workflowProcess.getSubflowTaskInstanceCompletionEvaluator()!=null && !workflowProcess.getSubflowTaskInstanceCompletionEvaluator().trim().equals("")){
            workflowProcessElement.addAttribute(SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR, workflowProcess.getSubflowTaskInstanceCompletionEvaluator());
        }          
        writeDataFields(workflowProcess.getDataFields(), workflowProcessElement);
        writeStartNode(workflowProcess.getStartNode(), workflowProcessElement);

        writeTasks(workflowProcess.getTasks(), workflowProcessElement);

        writeActivities(workflowProcess.getActivities(), workflowProcessElement);
        writeSynchronizers(workflowProcess.getSynchronizers(),
                workflowProcessElement);
        writeEndNodes(workflowProcess.getEndNodes(), workflowProcessElement);
        writeTransitions(workflowProcess.getTransitions(),
                workflowProcessElement);

        writeLoops(workflowProcess.getLoops(), workflowProcessElement);

        writeEventListeners(workflowProcess.getEventListeners(), workflowProcessElement);

        writeExtendedAttributes(workflowProcess.getExtendedAttributes(),
                workflowProcessElement);

        Document document = df.createDocument(workflowProcessElement);
        document.addDocType(FPDL_NS_PREFIX + ":" + WORKFLOW_PROCESS, PUBLIC_ID, SYSTEM_ID);
        return document;

    }

    /**
     * @param workflowProcess
     * @return
     * @throws IOException
     * @throws FPDLSerializerException
     */
    public String workflowProcessToXMLString(WorkflowProcess workflowProcess)
            throws IOException, FPDLSerializerException {
        Document dom = workflowProcessToDom(workflowProcess);
        OutputFormat format = new OutputFormat("    ", true);
        format.setEncoding("utf-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        XMLWriter writer = new XMLWriter(out, format);

        writer.write(dom);
        return out.toString();

    }

    /**
     * @param eventListeners
     * @param parentElement
     */
    protected void writeEventListeners(List<EventListener> eventListeners, Element parentElement) {
        if (eventListeners == null || eventListeners.size() == 0) {
            return;
        }

        Element eventListenersElm =
                Util4Serializer.addElement(parentElement,
                EVENT_LISTENERS);
        for (int i = 0; i < eventListeners.size(); i++) {
            EventListener listener = (EventListener) eventListeners.get(i);
            Element eventListenerElm = Util4Serializer.addElement(
                    eventListenersElm, EVENT_LISTENER);
            eventListenerElm.addAttribute(CLASS_NAME, listener.getClassName());
        }
    }

    /**
     * @param dataFields
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeDataFields(List<DataField> dataFields, Element parent)
            throws FPDLSerializerException {

        if (dataFields == null || dataFields.size() == 0) {
            return;
        }

        Element dataFieldsElement = Util4Serializer.addElement(parent,
                DATA_FIELDS);
        Iterator<DataField> iter = dataFields.iterator();
        while (iter.hasNext()) {
            DataField dataField = iter.next();
            Element dataFieldElement = Util4Serializer.addElement(
                    dataFieldsElement, DATA_FIELD);

            dataFieldElement.addAttribute(ID, dataField.getId());
            dataFieldElement.addAttribute(NAME, dataField.getName());
            dataFieldElement.addAttribute(DISPLAY_NAME, dataField.getDisplayName());
            dataFieldElement.addAttribute(DATA_TYPE, dataField.getDataType());

            dataFieldElement.addAttribute(INITIAL_VALUE,
                    dataField.getInitialValue());

            Util4Serializer.addElement(dataFieldElement, DESCRIPTION, dataField.getDescription());

            writeExtendedAttributes(dataField.getExtendedAttributes(),
                    dataFieldElement);
        }
    }

    /**
     * @param endNodes
     * @param parent
     */
    protected void writeEndNodes(List<EndNode> endNodes, Element parent) {
        Element endNodesElement = Util4Serializer.addElement(parent, END_NODES);
        Iterator<EndNode> iter = endNodes.iterator();

        while (iter.hasNext()) {
            writeEndNode( iter.next(), endNodesElement);
        }
    }

    /**
     * @param endNode
     * @param parent
     */
    protected void writeEndNode(EndNode endNode, Element parent) {
        Element endNodeElement = Util4Serializer.addElement(parent, END_NODE);
        endNodeElement.addAttribute(ID, endNode.getId());
        endNodeElement.addAttribute(NAME, endNode.getName());
        endNodeElement.addAttribute(DISPLAY_NAME, endNode.getDisplayName());

        Util4Serializer.addElement(endNodeElement, DESCRIPTION, endNode.getDescription());

        writeExtendedAttributes(endNode.getExtendedAttributes(), endNodeElement);

    }

    /**
     * @param startNode
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeStartNode(StartNode startNode, Element parent)
            throws FPDLSerializerException {
        if (startNode == null) {
            return;
        }
        Element startElement = Util4Serializer.addElement(parent, START_NODE);
        startElement.addAttribute(ID, startNode.getId());
        startElement.addAttribute(NAME, startNode.getName());

        startElement.addAttribute(DISPLAY_NAME, startNode.getDisplayName());

        Util4Serializer.addElement(startElement, DESCRIPTION, startNode.getDescription());

        writeExtendedAttributes(startNode.getExtendedAttributes(), startElement);
    }

    /**
     * @param synchronizers
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeSynchronizers(List<Synchronizer> synchronizers, Element parent)
            throws FPDLSerializerException {
        if (synchronizers == null || synchronizers.size() == 0) {
            return;
        }
        Element synchronizersElement = Util4Serializer.addElement(parent,
                SYNCHRONIZERS);

        Iterator<Synchronizer> iter = synchronizers.iterator();

        while (iter.hasNext()) {
            writeSynchronizer( iter.next(), synchronizersElement);
        }
    }

    /**
     * @param synchronizer
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeSynchronizer(Synchronizer synchronizer, Element parent)
            throws FPDLSerializerException {
        Element synchronizerElement = Util4Serializer.addElement(parent,
                SYNCHRONIZER);
        synchronizerElement.addAttribute(ID, synchronizer.getId());
        synchronizerElement.addAttribute(NAME, synchronizer.getName());
        synchronizerElement.addAttribute(DISPLAY_NAME, synchronizer.getDisplayName());

        Util4Serializer.addElement(synchronizerElement, DESCRIPTION,
                synchronizer.getDescription());
        writeExtendedAttributes(synchronizer.getExtendedAttributes(),
                synchronizerElement);
    }

    /**
     * @param activities
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeActivities(List<Activity> activities, Element parent)
            throws FPDLSerializerException {

        if (activities == null || activities.size() == 0) {
            return;
        }

        Element activitiesElement = Util4Serializer.addElement(parent,
                ACTIVITIES);

        Iterator<Activity> iter = activities.iterator();
        while (iter.hasNext()) {
            writeActivity(  iter.next(), activitiesElement);
        }
    }

    /**
     * @param activity
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeActivity(Activity activity, Element parent)
            throws FPDLSerializerException {

        Element activityElement = Util4Serializer.addElement(parent, ACTIVITY);

        activityElement.addAttribute(ID, activity.getId());
        activityElement.addAttribute(NAME, activity.getName());
        activityElement.addAttribute(DISPLAY_NAME, activity.getDisplayName());
        activityElement.addAttribute(COMPLETION_STRATEGY, activity.getCompletionStrategy());

        Util4Serializer.addElement(activityElement, DESCRIPTION, activity.getDescription());
        writeEventListeners(activity.getEventListeners(), activityElement);
        writeExtendedAttributes(activity.getExtendedAttributes(),
                activityElement);

        writeTasks(activity.getInlineTasks(), activityElement);
        writeTaskRefs(activity.getTaskRefs(), activityElement);
    }

    /**
     * @param taskRefs
     * @param parent
     */
    protected void writeTaskRefs(List<TaskRef> taskRefs, Element parent) {
        Element taskRefsElement = Util4Serializer.addElement(parent, TASKREFS);
        Iterator<TaskRef>  iter = taskRefs.iterator();
        while (iter.hasNext()) {
            TaskRef taskRef = iter.next();
            Element taskRefElement = Util4Serializer.addElement(taskRefsElement, TASKREF);
            taskRefElement.addAttribute(REFERENCE, taskRef.getReferencedTask().getId());
        }
    }

    /**
     * @param tasks
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeTasks(List<Task> tasks, Element parent)
            throws FPDLSerializerException {
        Element tasksElement = Util4Serializer.addElement(parent, TASKS);
        Iterator<Task> iter = tasks.iterator();

        while (iter.hasNext()) {
            writeTask((Task) iter.next(), tasksElement);
        }
    }

    /**
     * @param task
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeTask(Task task, Element parent)
            throws FPDLSerializerException {
        Element taskElement = Util4Serializer.addElement(parent, TASK);

        taskElement.addAttribute(ID, task.getId());
        taskElement.addAttribute(NAME, task.getName());
        taskElement.addAttribute(DISPLAY_NAME, task.getDisplayName());
        taskElement.addAttribute(TYPE, task.getType());

        if (task instanceof FormTask) {
            this.writePerformer(((FormTask) task).getPerformer(), taskElement);

            taskElement.addAttribute(COMPLETION_STRATEGY, ((FormTask) task).getAssignmentStrategy());
            taskElement.addAttribute(DEFAULT_VIEW, ((FormTask) task).getDefaultView());
            writeForm(EDIT_FORM, ((FormTask) task).getEditForm(), taskElement);
            writeForm(VIEW_FORM, ((FormTask) task).getViewForm(), taskElement);
            writeForm(LIST_FORM, ((FormTask) task).getListForm(), taskElement);
        } else if (task instanceof ToolTask) {

            this.writeApplication(((ToolTask) task).getApplication(), taskElement);
//            taskElement.addAttribute(EXECUTION, ((ToolTask) task).getExecution());
        } else if (task instanceof SubflowTask) {
            this.writeSubWorkflowProcess(((SubflowTask) task).getSubWorkflowProcess(), taskElement);
        }

        taskElement.addAttribute(PRIORITY, Integer.toString(task.getPriority()));

        writeDuration(task.getDuration(), taskElement);
        Util4Serializer.addElement(taskElement, DESCRIPTION, task.getDescription());

        if (task.getTaskInstanceCreator() != null && !task.getTaskInstanceCreator().trim().equals("")) {
            taskElement.addAttribute(TASK_INSTANCE_CREATOR, task.getTaskInstanceCreator());
        }
        if (task.getTaskInstanceRunner()!=null && !task.getTaskInstanceRunner().trim().equals("")){
            taskElement.addAttribute( TASK_INSTANCE_RUNNER, task.getTaskInstanceRunner());
        }
        if (task.getTaskInstanceCompletionEvaluator()!=null && !task.getTaskInstanceCompletionEvaluator().trim().equals("")){
            taskElement.addAttribute( TASK_INSTANCE_COMPLETION_EVALUATOR, task.getTaskInstanceCompletionEvaluator());
        }

        if (task.getLoopStrategy()!=null && !task.getLoopStrategy().trim().equals("")){
            taskElement.addAttribute(LOOP_STRATEGY, task.getLoopStrategy());
        }

        writeEventListeners(task.getEventListeners(), taskElement);
        writeExtendedAttributes(task.getExtendedAttributes(), taskElement);
    }

    /**
     * @param duration
     * @param parent
     */
    protected void writeDuration(Duration duration, Element parent) {
        if (duration == null) {
            return;
        }
        Element durationElement = Util4Serializer.addElement(parent, DURATION);
        durationElement.addAttribute(VALUE, Integer.toString(duration.getValue()));
        durationElement.addAttribute(UNIT, duration.getUnit());
        durationElement.addAttribute(IS_BUSINESS_TIME, Boolean.toString(duration.isBusinessTime()));
    }

    /**
     * @param formName
     * @param form
     * @param parent
     */
    protected void writeForm(String formName, Form form, Element parent) {
        if (form == null) {
            return;
        }
        Element editFormElement = Util4Serializer.addElement(parent, formName);
        editFormElement.addAttribute(NAME, form.getName());
        editFormElement.addAttribute(DISPLAY_NAME, form.getDisplayName());

        Util4Serializer.addElement(editFormElement, DESCRIPTION, form.getDescription());
        Util4Serializer.addElement(editFormElement, URI, form.getUri());
    }

    /**
     * @param loops
     * @param parent
     */
    protected void writeLoops(List<Loop> loops, Element parent) {
        if (loops == null || loops.size() == 0) {
            return;
        }
        Element transitionsElement = Util4Serializer.addElement(parent,
                LOOPS);

        Iterator<Loop> iter = loops.iterator();
        while (iter.hasNext()) {
            Loop loop = iter.next();

            Element loopElement = Util4Serializer.addElement(transitionsElement,
                    LOOP);

            loopElement.addAttribute(ID, loop.getId());
            loopElement.addAttribute(FROM, loop.getFromNode().getId());
            loopElement.addAttribute(TO, loop.getToNode().getId());

            loopElement.addAttribute(NAME, loop.getName());
            loopElement.addAttribute(DISPLAY_NAME, loop.getDisplayName());

            Util4Serializer.addElement(loopElement, CONDITION, loop.getCondition());

            writeExtendedAttributes(loop.getExtendedAttributes(),
                    loopElement);
        }
    }

    /**
     * @param transitions
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeTransitions(List<Transition> transitions, Element parent)
            throws FPDLSerializerException {

        if (transitions == null || transitions.size() == 0) {
            return;
        }

        Element transitionsElement = Util4Serializer.addElement(parent,
                TRANSITIONS);

        Iterator<Transition>  iter = transitions.iterator();
        while (iter.hasNext()) {
            writeTransition( iter.next(), transitionsElement);
        }
    }

    /**
     * @param transition
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeTransition(Transition transition, Element parent)
            throws FPDLSerializerException {

        Element transitionElement = Util4Serializer.addElement(parent,
                TRANSITION);

        transitionElement.addAttribute(ID, transition.getId());
        transitionElement.addAttribute(FROM, transition.getFromNode().getId());
        transitionElement.addAttribute(TO, transition.getToNode().getId());

        transitionElement.addAttribute(NAME, transition.getName());
        transitionElement.addAttribute(DISPLAY_NAME, transition.getDisplayName());

        Util4Serializer.addElement(transitionElement, CONDITION, transition.getCondition());

        writeExtendedAttributes(transition.getExtendedAttributes(),
                transitionElement);
    }

    /**
     * @param extendedAttributes
     * @param parent
     * @return
     */
    protected Element writeExtendedAttributes(Map<String,String> extendedAttributes,
            Element parent) {

        if (extendedAttributes == null || extendedAttributes.size() == 0) {
            return null;
        }

        Element extendedAttributesElement =
                Util4Serializer.addElement(parent,
                EXTENDED_ATTRIBUTES);

        Iterator<String> keys = extendedAttributes.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = extendedAttributes.get(key);

            Element extendedAttributeElement = Util4Serializer.addElement(
                    extendedAttributesElement, EXTENDED_ATTRIBUTE);
            extendedAttributeElement.addAttribute(NAME, key.toString());
            if (value != null) {
                extendedAttributeElement.addAttribute(VALUE, value.toString());
            }

        }

        return extendedAttributesElement;

    }

    /**
     * @param participant
     * @param parent
     */
    protected void writePerformer(Participant participant, Element parent) {
        if (participant == null) {
            return;
        }

        Element participantElement = Util4Serializer.addElement(parent,
                PERFORMER);

        participantElement.addAttribute(NAME, participant.getName());
        participantElement.addAttribute(DISPLAY_NAME, participant.getDisplayName());

        Util4Serializer.addElement(participantElement, DESCRIPTION, participant.getDescription());

        Util4Serializer.addElement(participantElement, ASSIGNMENT_HANDLER,
                participant.getAssignmentHandler());

    }

    /**
     * @param subWorkflowProcess
     * @param parent
     */
    protected void writeSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess, Element parent) {
        if (subWorkflowProcess == null) {
            return;
        }
        Element subflowElement = Util4Serializer.addElement(parent,
                SUB_WORKFLOW_PROCESS);

        subflowElement.addAttribute(NAME, subWorkflowProcess.getName());
        subflowElement.addAttribute(DISPLAY_NAME, subWorkflowProcess.getDisplayName());

        Util4Serializer.addElement(subflowElement, DESCRIPTION, subWorkflowProcess.getDescription());

        Util4Serializer.addElement(subflowElement, WORKFLOW_PROCESS_ID,
                subWorkflowProcess.getWorkflowProcessId());

    }

    /**
     * @param application
     * @param parent
     * @throws FPDLSerializerException
     */
    protected void writeApplication(Application application, Element parent)
            throws FPDLSerializerException {

        if (application == null) {
            return;
        }

        Element applicationElement = Util4Serializer.addElement(parent,
                APPLICATION);

        applicationElement.addAttribute(NAME, application.getName());
        applicationElement.addAttribute(DISPLAY_NAME, application.getDisplayName());

        Util4Serializer.addElement(applicationElement, DESCRIPTION, application.getDescription());

        Util4Serializer.addElement(applicationElement, HANDLER, application.getHandler());

    }
}
