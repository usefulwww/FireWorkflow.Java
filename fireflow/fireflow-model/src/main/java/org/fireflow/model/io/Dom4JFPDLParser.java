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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.fireflow.model.DataField;
import org.fireflow.model.Duration;
import org.fireflow.model.EventListener;
import org.fireflow.model.FormTask;
import org.fireflow.model.IWFElement;
import org.fireflow.model.SubflowTask;
import org.fireflow.model.Task;
import org.fireflow.model.TaskRef;
import org.fireflow.model.ToolTask;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Loop;
import org.fireflow.model.net.Node;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.fireflow.model.resource.Application;
import org.fireflow.model.resource.Form;
import org.fireflow.model.resource.Participant;
import org.fireflow.model.resource.SubWorkflowProcess;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Anthony Eden
 * updated by nychen2000
 */
public class Dom4JFPDLParser implements IFPDLParser {


    /* (non-Javadoc)
     * @see org.fireflow.model.io.IFPDLParser#parse(java.io.InputStream)
     */
    public WorkflowProcess parse(InputStream in) throws IOException,
            FPDLParserException {
        try {
            SAXReader reader = new SAXReader(new DocumentFactory());
            reader.setEntityResolver(new EntityResolver() {

                String emptyDtd = "";
                ByteArrayInputStream bytels = new ByteArrayInputStream(emptyDtd.getBytes());

                public InputSource resolveEntity(String publicId,
                        String systemId) throws SAXException, IOException {
                    return new InputSource(bytels);
                }
            });
            Document document = reader.read(in);

            WorkflowProcess wp = parse(document);//解析
            return wp;
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new FPDLParserException("Error parsing document.", e);
        } finally {
        }
//        return parse(new InputStreamReader(in));
    }

    /**
     * 解析流程定义文件
     * @param document
     * @return
     * @throws FPDLParserException
     */
    @SuppressWarnings("static-access")
	protected WorkflowProcess parse(Document document) throws FPDLParserException {
        Element workflowProcessElement = document.getRootElement();
//流程ID
        WorkflowProcess wp = new WorkflowProcess(workflowProcessElement.attributeValue(NAME));
        wp.setSn(UUID.randomUUID().toString());//使用UUID作为流程实例ID
        //流程整体描述
        wp.setDescription(Util4Parser.elementAsString(workflowProcessElement,
                DESCRIPTION));
        //流程Task creator
        wp.setTaskInstanceCreator(workflowProcessElement.attributeValue(TASK_INSTANCE_CREATOR));

        wp.setFormTaskInstanceRunner(workflowProcessElement.attributeValue(FORM_TASK_INSTANCE_RUNNER));
        wp.setToolTaskInstanceRunner(workflowProcessElement.attributeValue(TOOL_TASK_INSTANCE_RUNNER));
        wp.setSubflowTaskInstanceRunner(workflowProcessElement.attributeValue(SUBFLOW_TASK_INSTANCE_RUNNER));

        wp.setFormTaskInstanceCompletionEvaluator(workflowProcessElement.attributeValue(FORM_TASK_INSTANCE_COMPLETION_EVALUATOR));
        wp.setToolTaskInstanceCompletionEvaluator(workflowProcessElement.attributeValue(TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR));
        wp.setSubflowTaskInstanceCompletionEvaluator(workflowProcessElement.attributeValue(SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR));
//流程显示名称
        wp.setDisplayName(workflowProcessElement.attributeValue(DISPLAY_NAME));
        //下面两个属性，暂时还未使用
        wp.setResourceFile(workflowProcessElement.attributeValue(RESOURCE_FILE));
        wp.setResourceManager(workflowProcessElement.attributeValue(RESOURCE_MANAGER));

        //解析datafields
        this.loadDataFields(wp, wp.getDataFields(), Util4Parser.child(
                workflowProcessElement, this.DATA_FIELDS));
        //开始节点
        loadStartNode(wp, Util4Parser.child(workflowProcessElement, START_NODE));
//整体流程对应的task ,这个属性好像暂时未启用
        loadTasks(wp, wp.getTasks(),Util4Parser.child(
                    workflowProcessElement, TASKS) );
//所有业务节点,同时将这个节点的所有的属性都解析出来保存到节点信息中。
        loadActivities(wp, wp.getActivities(), Util4Parser.child(
                workflowProcessElement, ACTIVITIES));
        //工作流同步器节点
        loadSynchronizers(wp, wp.getSynchronizers(), Util4Parser.child(
                workflowProcessElement, SYNCHRONIZERS));
        //结束节点
        loadEndNodes(wp, wp.getEndNodes(), Util4Parser.child(
                workflowProcessElement, END_NODES));
        //转移线
        loadTransitions(wp, Util4Parser.child(workflowProcessElement,
                TRANSITIONS));
//循环线
        loadLoops(wp,Util4Parser.child(workflowProcessElement, LOOPS));
//所有的监听器
        loadEventListeners(wp.getEventListeners(), Util4Parser.child(workflowProcessElement, EVENT_LISTENERS));
//加载扩展属性
        Map<String, String> extAttrs = wp.getExtendedAttributes();
        loadExtendedAttributes(extAttrs, Util4Parser.child(
                workflowProcessElement, EXTENDED_ATTRIBUTES));

        return wp;

    }

    /**
     * @param listeners
     * @param element
     */
    protected void loadEventListeners(List<EventListener> listeners, Element element) {
        listeners.clear();
        if (element == null) {
            return;
        }
        if (element == null) {
            return;
        }
        List<Element> listenerElms = Util4Parser.children(element, EVENT_LISTENER);
        Iterator<Element> iter = listenerElms.iterator();
        while (iter.hasNext()) {
            Element elm = iter.next();
            EventListener listener = new EventListener();
            listener.setClassName(elm.attributeValue(CLASS_NAME));

            listeners.add(listener);
        }
    }

    /**
     * @param wp
     * @param element
     * @throws FPDLParserException
     */
    protected void loadStartNode(WorkflowProcess wp, Element element)
            throws FPDLParserException {
        if (element == null) {
            return;
        }
        StartNode startNode = new StartNode(wp);
        startNode.setSn(UUID.randomUUID().toString());
        startNode.setDescription(Util4Parser.elementAsString(element,
                DESCRIPTION));
        startNode.setDisplayName(element.attributeValue(DISPLAY_NAME));
        loadExtendedAttributes(startNode.getExtendedAttributes(), Util4Parser.child(element, EXTENDED_ATTRIBUTES));
        wp.setStartNode(startNode);
    }

    /**
     * @param wp
     * @param endNodes
     * @param element
     * @throws FPDLParserException
     */
    protected void loadEndNodes(WorkflowProcess wp, List<EndNode> endNodes,
            Element element) throws FPDLParserException {
        endNodes.clear();
        if (element == null) {
            return;
        }
        List<Element> endNodesElms = Util4Parser.children(element, END_NODE);
        Iterator<Element> iter = endNodesElms.iterator();
        while (iter.hasNext()) {
            Element elm = iter.next();
            EndNode endNode = new EndNode(wp, elm.attributeValue(NAME));
            endNode.setSn(UUID.randomUUID().toString());
            endNode.setDescription(Util4Parser.elementAsString(element,
                    DESCRIPTION));
            endNode.setDisplayName(element.attributeValue(DISPLAY_NAME));
            loadExtendedAttributes(endNode.getExtendedAttributes(), Util4Parser.child(elm, EXTENDED_ATTRIBUTES));
            endNodes.add(endNode);
        }
    }

    /**
     * @param wp
     * @param synchronizers
     * @param element
     * @throws FPDLParserException
     */
    protected void loadSynchronizers(WorkflowProcess wp, List<Synchronizer> synchronizers,
            Element element) throws FPDLParserException {
        synchronizers.clear();
        if (element == null) {
            return;
        }
        List<Element> synchronizerElms = Util4Parser.children(element, SYNCHRONIZER);
        Iterator<Element> iter = synchronizerElms.iterator();
        while (iter.hasNext()) {
            Element elm = iter.next();
            Synchronizer synchronizer = new Synchronizer(wp, elm.attributeValue(NAME));
            synchronizer.setSn(UUID.randomUUID().toString());
            synchronizer.setDescription(Util4Parser.elementAsString(element,
                    DESCRIPTION));
            synchronizer.setDisplayName(element.attributeValue(DISPLAY_NAME));

            loadExtendedAttributes(synchronizer.getExtendedAttributes(),
                    Util4Parser.child(elm, EXTENDED_ATTRIBUTES));

            synchronizers.add(synchronizer);
        }
    }

    /**
     * @param wp
     * @param activities
     * @param element
     * @throws FPDLParserException
     */
    protected void loadActivities(WorkflowProcess wp, List<Activity> activities,
            Element element) throws FPDLParserException {

        if (element == null) {
            // log.debug("Activites element was null");
            return;
        }

        List<Element> activitElements = Util4Parser.children(element, ACTIVITY);
        activities.clear();
        Iterator<Element> iter = activitElements.iterator();
        while (iter.hasNext()) {
            Element activityElement =  iter.next();

            Activity activity = new Activity(wp, activityElement.attributeValue(NAME));
            activity.setSn(UUID.randomUUID().toString());
            activity.setDisplayName(activityElement.attributeValue(DISPLAY_NAME));
            activity.setDescription(Util4Parser.elementAsString(
                    activityElement, DESCRIPTION));
            activity.setCompletionStrategy(activityElement.attributeValue(COMPLETION_STRATEGY));
            loadEventListeners(activity.getEventListeners(), Util4Parser.child(activityElement, EVENT_LISTENERS));
            loadExtendedAttributes(activity.getExtendedAttributes(),
                    Util4Parser.child(activityElement, EXTENDED_ATTRIBUTES));

            loadTasks(activity, activity.getInlineTasks(), Util4Parser.child(
                    activityElement, TASKS));
            loadTaskRefs((WorkflowProcess)activity.getParent(),activity,activity.getTaskRefs(),Util4Parser.child(activityElement, TASKREFS));
            activities.add(activity);
        }
    }

    /**
     * @param workflowProcess
     * @param parent
     * @param taskRefs
     * @param taskRefsElement
     */
    protected void loadTaskRefs(WorkflowProcess workflowProcess,IWFElement parent,List<TaskRef> taskRefs,Element taskRefsElement){
        taskRefs.clear();
        if (taskRefsElement==null){
            return;
        }
        List<Element> taskRefElems = Util4Parser.children(taskRefsElement, TASKREF);
        Iterator<Element> iter = taskRefElems.iterator();
        while(iter.hasNext()){
            Element elm = iter.next();
            String taskId = elm.attributeValue(REFERENCE);
            Task task = (Task)workflowProcess.findWFElementById(taskId);
            if (task!=null){
                TaskRef taskRef = new TaskRef(parent,task);
                taskRef.setSn(UUID.randomUUID().toString());
                taskRefs.add(taskRef);
            }
        }
    }

    /**
     * @param parent
     * @param tasks
     * @param tasksElement
     * @throws FPDLParserException
     */
    protected void loadTasks(IWFElement parent, List<Task> tasks, Element tasksElement)
            throws FPDLParserException {
        tasks.clear();
        if (tasksElement == null) {
            return;
        }
        List<Element> tasksElms = Util4Parser.children(tasksElement, TASK);
        Iterator<Element> iter = tasksElms.iterator();
        while (iter.hasNext()) {
            Element elm = iter.next();
            Task task = createTask(parent, elm);
            if (task!=null)tasks.add(task);
        }
    }

    /**
     * @param parent
     * @param taskElement
     * @return
     * @throws FPDLParserException
     */
    protected Task createTask(IWFElement parent, Element taskElement)
            throws FPDLParserException {
        Task task = null;
        String type = taskElement.attributeValue(TYPE);
        if (Task.FORM.equals(type)) {
            task = new FormTask(parent, taskElement.attributeValue(NAME));
            ((FormTask) task).setAssignmentStrategy(taskElement.attributeValue(COMPLETION_STRATEGY));
            ((FormTask) task).setDefaultView(taskElement.attributeValue(DEFAULT_VIEW));

            ((FormTask) task).setPerformer(createPerformer(Util4Parser.child(taskElement,
                    PERFORMER)));
            ((FormTask) task).setEditForm(createForm(Util4Parser.child(taskElement, EDIT_FORM)));
            ((FormTask) task).setViewForm(createForm(Util4Parser.child(taskElement, VIEW_FORM)));
            ((FormTask) task).setListForm(createForm(Util4Parser.child(taskElement, LIST_FORM)));

        } else if (Task.TOOL.equals(type)) {
            task = new ToolTask(parent, taskElement.attributeValue(NAME));

            ((ToolTask) task).setApplication(createApplication(Util4Parser.child(taskElement,
                    APPLICATION)));
        } else if (Task.SUBFLOW.equals(type)) {
            task = new SubflowTask(parent, taskElement.attributeValue(NAME));
            ((SubflowTask) task).setSubWorkflowProcess(createSubWorkflowProcess(Util4Parser.child(taskElement, SUB_WORKFLOW_PROCESS)));

        } else {
            return null;
        }

        task.setSn(UUID.randomUUID().toString());
        task.setDisplayName(taskElement.attributeValue(DISPLAY_NAME));
        task.setDescription(Util4Parser.elementAsString(taskElement, DESCRIPTION));
        String sPriority = taskElement.attributeValue(PRIORITY);
        int priority = 0;
        if (sPriority != null) {
            try {
                priority = Integer.parseInt(sPriority);
            } catch (Exception e) {
            }
        }
        task.setPriority(priority);
        task.setDuration(createDuration(Util4Parser.child(taskElement, DURATION)));

        task.setTaskInstanceCreator(taskElement.attributeValue(TASK_INSTANCE_CREATOR));
        task.setTaskInstanceRunner(taskElement.attributeValue( TASK_INSTANCE_RUNNER));
        task.setTaskInstanceCompletionEvaluator(taskElement.attributeValue(TASK_INSTANCE_COMPLETION_EVALUATOR));
        task.setLoopStrategy(taskElement.attributeValue(LOOP_STRATEGY));

        loadEventListeners(task.getEventListeners(), Util4Parser.child(taskElement, EVENT_LISTENERS));
        loadExtendedAttributes(task.getExtendedAttributes(),
                Util4Parser.child(taskElement, EXTENDED_ATTRIBUTES));
        return task;

    }

    /**
     * @param performerElement
     * @return
     */
    @SuppressWarnings("static-access")
	protected Participant createPerformer(Element performerElement) {
        if (performerElement == null) {
            return null;
        }
        Participant part = new Participant(performerElement.attributeValue(NAME));
        part.setDisplayName(performerElement.attributeValue(DISPLAY_NAME));
        part.setDescription(Util4Parser.elementAsString(performerElement,
                DESCRIPTION));
        part.setAssignmentHandler(Util4Parser.elementAsString(performerElement,
                this.ASSIGNMENT_HANDLER));
        return part;
    }

    /**
     * @param subFlowElement
     * @return
     */
    @SuppressWarnings("static-access")
	protected SubWorkflowProcess createSubWorkflowProcess(Element subFlowElement) {
        if (subFlowElement == null) {
            return null;
        }

        SubWorkflowProcess subFlow = new SubWorkflowProcess(subFlowElement.attributeValue(NAME));
        subFlow.setDisplayName(subFlowElement.attributeValue(DISPLAY_NAME));
        subFlow.setDescription(Util4Parser.elementAsString(subFlowElement,
                DESCRIPTION));
        subFlow.setWorkflowProcessId(Util4Parser.elementAsString(subFlowElement,
                this.WORKFLOW_PROCESS_ID));

        return subFlow;
    }

    /**
     * @param applicationElement
     * @return
     */
    protected Application createApplication(Element applicationElement) {
        if (applicationElement == null) {
            return null;
        }
        Application app = new Application(applicationElement.attributeValue(APPLICATION));
        app.setDisplayName(applicationElement.attributeValue(DISPLAY_NAME));
        app.setDescription(Util4Parser.elementAsString(applicationElement,
                DESCRIPTION));
        app.setHandler(Util4Parser.elementAsString(applicationElement,
                HANDLER));
        return app;
    }

    /**
     * @param formElement
     * @return
     */
    protected Form createForm(Element formElement) {
        if (formElement == null) {
            return null;
        }
        Form form = new Form(formElement.attributeValue(NAME));
        form.setDisplayName(formElement.attributeValue(DISPLAY_NAME));
        form.setDescription(Util4Parser.elementAsString(formElement,
                DESCRIPTION));
        form.setUri(Util4Parser.elementAsString(formElement, URI));
        return form;
    }

    /**
     * @param durationElement
     * @return
     */
    @SuppressWarnings("static-access")
	protected Duration createDuration(Element durationElement) {
        if (durationElement == null) {
            return null;
        }
        String sValue = durationElement.attributeValue(VALUE);
        String sIsBusTime = durationElement.attributeValue(this.IS_BUSINESS_TIME);
        boolean isBusinessTime = true;
        int value = 1;
        if (sValue != null) {
            try {
                value = Integer.parseInt(sValue);
                isBusinessTime = Boolean.parseBoolean(sIsBusTime);
            } catch (Exception ex) {
                return null;
            }
        }
        Duration duration = new Duration(value, durationElement.attributeValue(UNIT));
        duration.setBusinessTime(isBusinessTime);
        return duration;
    }

    /**
     * @param wp
     * @param loopElement
     * @return
     * @throws FPDLParserException
     */
    protected Loop createLoop(WorkflowProcess wp, Element loopElement)
            throws FPDLParserException {
        String fromNodeId = loopElement.attributeValue(FROM);
        String toNodeId = loopElement.attributeValue(TO);
        Synchronizer fromNode = (Synchronizer) wp.findWFElementById(fromNodeId);
        Synchronizer toNode = (Synchronizer) wp.findWFElementById(toNodeId);

        Loop loop = new Loop(wp,
                loopElement.attributeValue(NAME), fromNode,
                toNode);
        loop.setSn(UUID.randomUUID().toString());

        loop.setDisplayName(loopElement.attributeValue(DISPLAY_NAME));
        loop.setDescription(Util4Parser.elementAsString(loopElement,
                DESCRIPTION));
        Element conditionElement = Util4Parser.child(loopElement, CONDITION);
        loop.setCondition(conditionElement == null ? ""
                : conditionElement.getStringValue());

        // load extended attributes
        Map<String, String> extAttrs = loop.getExtendedAttributes();
        loadExtendedAttributes(extAttrs, Util4Parser.child(loopElement,
                EXTENDED_ATTRIBUTES));

        return loop;
    }

     /**
     * @param wp
     * @param loopsElement
     * @throws FPDLParserException
     */
    protected void loadLoops(WorkflowProcess wp ,Element loopsElement)throws FPDLParserException{
         if (loopsElement==null)return;
         List<Element> loopElementList = Util4Parser.children(loopsElement, LOOP);

        List<Loop> loops = wp.getLoops();



        loops.clear();

        Iterator<Element> iter = loopElementList.iterator();
        while (iter.hasNext()) {
            Element loopElement = iter.next();
            Loop loop = createLoop(wp, loopElement);
            loops.add(loop);



            Synchronizer fromNode = (Synchronizer)loop.getFromNode();
            Synchronizer toNode = (Synchronizer)loop.getToNode();

            fromNode.getLeavingLoops().add(loop);
            toNode.getEnteringLoops().add(loop);
        }
     }


    /**
     * @param wp
     * @param transitionsElement
     * @throws FPDLParserException
     */
    protected void loadTransitions(WorkflowProcess wp, Element transitionsElement)
            throws FPDLParserException {

        if (transitionsElement == null) {
            return;
        }

        loadTransitions(wp, Util4Parser.children(transitionsElement, TRANSITION));
    }

    /**
     * @param wp
     * @param elements
     * @throws FPDLParserException
     */
    protected void loadTransitions(WorkflowProcess wp, List<Element> elements)
            throws FPDLParserException {
        List<Transition> transitions = wp.getTransitions();

        transitions.clear();

        Iterator<Element> iter = elements.iterator();
        while (iter.hasNext()) {
            Element transitionElement =  iter.next();
            Transition transition = createTransition(wp, transitionElement);
            transitions.add(transition);
            Node fromNode = transition.getFromNode();
            Node toNode = transition.getToNode();
            if (fromNode != null && (fromNode instanceof Activity)) {
                ((Activity) fromNode).setLeavingTransition(transition);
            } else if (fromNode != null && (fromNode instanceof Synchronizer)) {
                ((Synchronizer) fromNode).getLeavingTransitions().add(
                        transition);
            }
            if (toNode != null && (toNode instanceof Activity)) {
                ((Activity) toNode).setEnteringTransition(transition);
            } else if (toNode != null && (toNode instanceof Synchronizer)) {
                ((Synchronizer) toNode).getEnteringTransitions().add(transition);
            }
        }
    }

    /**
     * @param wp
     * @param element
     * @return
     * @throws FPDLParserException
     */
    protected Transition createTransition(WorkflowProcess wp, Element element)
            throws FPDLParserException {
        String fromNodeId = element.attributeValue(FROM);
        String toNodeId = element.attributeValue(TO);
        Node fromNode = (Node) wp.findWFElementById(fromNodeId);
        Node toNode = (Node) wp.findWFElementById(toNodeId);

        Transition transition = new Transition(wp,
                element.attributeValue(NAME), fromNode,
                toNode);
        transition.setSn(UUID.randomUUID().toString());

        transition.setDisplayName(element.attributeValue(DISPLAY_NAME));
        transition.setDescription(Util4Parser.elementAsString(element,
                DESCRIPTION));
        Element conditionElement = Util4Parser.child(element, CONDITION);
        transition.setCondition(conditionElement == null ? ""
                : conditionElement.getStringValue());

        // load extended attributes
        Map<String, String> extAttrs = transition.getExtendedAttributes();
        loadExtendedAttributes(extAttrs, Util4Parser.child(element,
                EXTENDED_ATTRIBUTES));

        return transition;
    }

    /**
     * @param wp
     * @param dataFields
     * @param element
     * @throws FPDLParserException
     */
    protected void loadDataFields(WorkflowProcess wp, List<DataField> dataFields,
            Element element) throws FPDLParserException {

        if (element == null) {
            return;
        }

        List<Element> datafieldsElement = Util4Parser.children(element, DATA_FIELD);
        dataFields.clear();
        Iterator<Element> iter = datafieldsElement.iterator();
        while (iter.hasNext()) {
            Element dataFieldElement = iter.next();
            dataFields.add(createDataField(wp, dataFieldElement));
        }
    }

    /**
     * @param wp
     * @param element
     * @return
     * @throws FPDLParserException
     */
    protected DataField createDataField(WorkflowProcess wp, Element element)
            throws FPDLParserException {
        if (element == null) {
            return null;
        }
        String dataType = element.attributeValue(DATA_TYPE);
        if (dataType == null) {
            dataType = DataField.STRING;
        }

        DataField dataField = new DataField(wp, element.attributeValue(NAME),
                dataType);
        dataField.setSn(UUID.randomUUID().toString());

        dataField.setDisplayName(element.attributeValue(DISPLAY_NAME));
        dataField.setInitialValue(element.attributeValue(INITIAL_VALUE));
        dataField.setDescription(Util4Parser.elementAsString(element,
                DESCRIPTION));

        loadExtendedAttributes(dataField.getExtendedAttributes(), Util4Parser.child(element, EXTENDED_ATTRIBUTES));

        return dataField;
    }

    /**
     * @param extendedAttributes
     * @param element
     * @throws FPDLParserException
     */
    protected void loadExtendedAttributes(Map<String, String> extendedAttributes,
            Element element) throws FPDLParserException {

        if (element == null) {
            return;
        }
        extendedAttributes.clear();
        List<Element> extendAttributeElementsList = Util4Parser.children(element,
                EXTENDED_ATTRIBUTE);
        Iterator<Element> iter = extendAttributeElementsList.iterator();
        while (iter.hasNext()) {
            Element extAttrElement = iter.next();
            String name = extAttrElement.attributeValue(NAME);
            String value = extAttrElement.attributeValue(VALUE);

            extendedAttributes.put(name, value);

        }
    }
}
