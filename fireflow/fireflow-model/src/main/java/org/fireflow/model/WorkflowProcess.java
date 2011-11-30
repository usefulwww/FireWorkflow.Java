/**
 * Copyright 2003-2008 非也
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
package org.fireflow.model;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Loop;
import org.fireflow.model.net.Node;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;

/**
 * 业务流程。<br/>
 * 这是Fire workflow工作流模型的最顶层元素。
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
@SuppressWarnings("serial")
public class WorkflowProcess extends AbstractWFElement {

	/**
	 * 流程数据项，运行时转换为流程变量进行存储。
	 */
	private List<DataField> dataFields = new ArrayList<DataField>();

	/**
	 * 全局Task
	 */
	private List<Task> tasks = new ArrayList<Task>();

	/**
	 * 流程环节
	 */
	private List<Activity> activities = new ArrayList<Activity>();

	/**
	 * 转移
	 */
	private List<Transition> transitions = new ArrayList<Transition>();

	/**
	 * 循环
	 */
	private List<Loop> loops = new ArrayList<Loop>();

	/**
	 * 同步器
	 */
	private List<Synchronizer> synchronizers = new ArrayList<Synchronizer>();

	/**
	 * 开始节点
	 */
	private StartNode startNode = null;

	/**
	 * 结束节点
	 */
	private List<EndNode> endNodes = new ArrayList<EndNode>();

	// 其他属性
	/**
	 * 资源文件（在1.0中暂时未使用）
	 */
	private String resourceFile = null;

	/**
	 * 资源管理器（在1.0中暂时未使用）
	 */
	private String resourceManager = null;

	/**
	 * 本流程全局的任务实例创建器。 如果没有设置，引擎将使用DefaultTaskInstanceCreator来创建TaskInstance。
	 */
	protected String taskInstanceCreator = null;

	/**
	 * 本流程全局的FormTask
	 * Instance运行器。如果没有设置，引擎将使用DefaultFormTaskInstanceRunner来运行TaskInstance。
	 */
	protected String formTaskInstanceRunner = null;

	/**
	 * 本流程全局的ToolTask
	 * Instance运行器。如果没有设置，引擎将使用DefaultToolTaskInstanceRunner来运行TaskInstance。
	 */
	protected String toolTaskInstanceRunner = null;

	/**
	 * 本流程全局的SubflowTask
	 * Instance运行器。如果没有设置，引擎将使用DefaultSubflowTaskInstanceRunner来运行TaskInstance。
	 */
	protected String subflowTaskInstanceRunner = null;

	/**
	 * 本流程全局的FormTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
	 * 如果没有设置，引擎使用缺省实现DefaultFormTaskInstanceCompletionEvaluator。
	 */
	protected String formTaskInstanceCompletionEvaluator = null;

	/**
	 * 本流程全局的ToolTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
	 * 如果没有设置，引擎使用缺省实现DefaultToolTaskInstanceCompletionEvaluator。
	 */
	protected String toolTaskInstanceCompletionEvaluator = null;

	/**
	 * 本流程全局的SubflowTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
	 * 如果没有设置，引擎使用缺省实现DefaultSubflowTaskInstanceCompletionEvaluator。
	 */
	protected String subflowTaskInstanceCompletionEvaluator = null;

	/**
	 * 构造函数
	 * 
	 * @param id
	 * @param name
	 * @param pkg
	 */
	public WorkflowProcess(String name) {
		super(null, name);
	}

	/**
	 * 返回所有的流程数据项
	 * 
	 * @return
	 */
	public List<DataField> getDataFields() {
		return dataFields;
	}

	/**
	 * 返回所有的环节
	 * 
	 * @return
	 */
	public List<Activity> getActivities() {
		return activities;
	}

	/**
	 * 返回所有的循环
	 * 
	 * @return
	 */
	public List<Loop> getLoops() {
		return loops;
	}

	/**
	 * 返回所有的转移
	 * 
	 * @return
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * 返回开始节点
	 * 
	 * @return
	 */
	public StartNode getStartNode() {
		return startNode;
	}

	public void setStartNode(StartNode startNode) {
		this.startNode = startNode;
	}

	/**
	 * 返回所有的结束节点
	 * 
	 * @return
	 */
	public List<EndNode> getEndNodes() {
		return endNodes;
	}

	/**
	 * 返回所有的同步器
	 * 
	 * @return
	 */
	public List<Synchronizer> getSynchronizers() {
		return synchronizers;
	}

	/**
	 * 返回所有的全局Task
	 * 
	 * @return
	 */
	public List<Task> getTasks() {
		return this.tasks;
	}

	/**
	 * 保留
	 * 
	 * @return
	 */
	public String getResourceFile() {
		return resourceFile;
	}

	/**
	 * 保留
	 * 
	 * @return
	 */
	public void setResourceFile(String resourceFile) {
		this.resourceFile = resourceFile;
	}

	/**
	 * 保留
	 * 
	 * @return
	 */
	public String getResourceManager() {
		return resourceManager;
	}

	/**
	 * 保留
	 * 
	 * @return
	 */
	public void setResourceManager(String resourceMgr) {
		this.resourceManager = resourceMgr;
	}

	/**
	 * 通过ID查找该流程中的任意元素
	 * 
	 * @param id
	 *            元素的Id
	 * @return 流程元素，如：Activity,Task,Synchronizer等等
	 */
	public IWFElement findWFElementById(String id) {
		if (this.getId().equals(id)) {
			return this;
		}

		List<Task>  tasksList = this.getTasks();
		for (int i = 0; i < tasksList.size(); i++) {
			Task task =  tasksList.get(i);
			if (task.getId().equals(id)) {
				return task;
			}
		}

		List<Activity> activityList = this.getActivities();
		for (int i = 0; i < activityList.size(); i++) {
			Activity activity = activityList.get(i);
			if (activity.getId().equals(id)) {
				return activity;
			}
			List<Task> taskList = activity.getTasks();
			for (int j = 0; j < taskList.size(); j++) {
				Task task =  taskList.get(j);
				if (task.getId().equals(id)) {
					return task;
				}
			}
		}
		if (this.getStartNode().getId().equals(id)) {
			return this.getStartNode();
		}
		
		List<Synchronizer> synchronizerList = this.getSynchronizers();
		for (int i = 0; i < synchronizerList.size(); i++) {
			Synchronizer synchronizer = synchronizerList.get(i);
			if (synchronizer.getId().equals(id)) {
				return synchronizer;
			}
		}

		List<EndNode> endNodeList = this.getEndNodes();
		for (int i = 0; i < endNodeList.size(); i++) {
			EndNode endNode =  endNodeList.get(i);
			if (endNode.getId().equals(id)) {
				return endNode;
			}
		}

		List<Transition> transitionList = this.getTransitions();
		for (int i = 0; i < transitionList.size(); i++) {
			Transition transition = transitionList.get(i);
			if (transition.getId().equals(id)) {
				return transition;
			}
		}

		List<DataField> dataFieldList = this.getDataFields();
		for (int i = 0; i < dataFieldList.size(); i++) {
			DataField dataField =  dataFieldList.get(i);
			if (dataField.getId().equals(id)) {
				return dataField;
			}
		}

		List<Loop> loopList = this.getLoops();
		for (int i = 0; i < loopList.size(); i++) {
			Loop loop = loopList.get(i);
			if (loop.getId().equals(id)) {
				return loop;
			}
		}
		return null;
	}

	/**
	 * 通过Id查找任意元素的序列号
	 * 
	 * @param id
	 *            流程元素的id
	 * @return 流程元素的序列号
	 */
	public String findSnById(String id) {
		IWFElement elem = this.findWFElementById(id);
		if (elem != null) {
			return elem.getSn();
		}
		return null;
	}

	/**
	 * 验证workflow process是否完整正确。
	 * 
	 * @return null表示流程正确；否则表示流程错误，返回值是错误原因
	 */
	public String validate() {
		String errHead = "Workflow process is invalid：";
		if (this.getStartNode() == null) {
			return errHead + "must have one start node";
		}
		if (this.getStartNode().getLeavingTransitions().size() == 0) {
			return errHead + "start node must have leaving transitions.";
		}

		List<Activity> activities = this.getActivities();
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);
			String theName = (activity.getDisplayName() == null || activity
					.getDisplayName().equals("")) ? activity.getName()
					: activity.getDisplayName();
			if (activity.getEnteringTransition() == null) {
				return errHead + "activity[" + theName
						+ "] must have entering transition.";
			}
			if (activity.getLeavingTransition() == null) {
				return errHead + "activity[" + theName
						+ "] must have leaving transition.";
			}

			// check tasks
			List<Task> taskList = activity.getTasks();
			for (int j = 0; j < taskList.size(); j++) {
				Task task =  taskList.get(j);
				if (task.getType() == null) {
					return errHead + "task[" + task.getId()
							+ "]'s taskType can Not be null.";
				} else if (task.getType().equals(Task.FORM)) {
					FormTask formTask = (FormTask) task;
					if (formTask.getPerformer() == null) {
						return errHead + "FORM-task[id=" + task.getId()
								+ "] must has a performer.";
					}
				} else if (task.getType().equals(Task.TOOL)) {
					ToolTask toolTask = (ToolTask) task;
					if (toolTask.getApplication() == null) {
						return errHead + "TOOL-task[id=" + task.getId()
								+ "] must has a application.";
					}
				} else if (task.getType().equals(Task.SUBFLOW)) {
					SubflowTask subflowTask = (SubflowTask) task;
					if (subflowTask.getSubWorkflowProcess() == null) {
						return errHead + "SUBFLOW-task[id=" + task.getId()
								+ "] must has a subflow.";
					}
				} else {
					return errHead + " unknown task type of task["
							+ task.getId() + "]";
				}
			}
		}

		List<Synchronizer> synchronizers = this.getSynchronizers();
		for (int i = 0; i < synchronizers.size(); i++) {
			Synchronizer synchronizer = synchronizers.get(i);
			String theName = (synchronizer.getDisplayName() == null || synchronizer
					.getDisplayName().equals("")) ? synchronizer.getName()
					: synchronizer.getDisplayName();
			if (synchronizer.getEnteringTransitions().size() == 0) {
				return errHead + "synchronizer[" + theName
						+ "] must have entering transition.";
			}
			if (synchronizer.getLeavingTransitions().size() == 0) {
				return errHead + "synchronizer[" + theName
						+ "] must have leaving transition.";
			}
		}

		List<EndNode> endnodes = this.getEndNodes();
		for (int i = 0; i < endnodes.size(); i++) {
			EndNode endnode = endnodes.get(i);
			String theName = (endnode.getDisplayName() == null || endnode
					.getDisplayName().equals("")) ? endnode.getName() : endnode
					.getDisplayName();
			if (endnode.getEnteringTransitions().size() == 0) {
				return errHead + "end node[" + theName
						+ "] must have entering transition.";
			}
		}

		List<Transition> transitions = this.getTransitions();
		for (int i = 0; i < transitions.size(); i++) {
			Transition transition = transitions.get(i);
			String theName = (transition.getDisplayName() == null || transition
					.getDisplayName().equals("")) ? transition.getName()
					: transition.getDisplayName();
			if (transition.getFromNode() == null) {
				return errHead + "transition[" + theName
						+ "] must have from node.";

			}
			if (transition.getToNode() == null) {
				return errHead + "transition[" + theName
						+ "] must have to node.";
			}
		}

		// check datafield
		List<DataField> dataFieldList = this.getDataFields();
		for (int i = 0; i < dataFieldList.size(); i++) {
			DataField df =  dataFieldList.get(i);
			if (df.getDataType() == null) {
				return errHead + "unknown data type of datafield[" + df.getId()
						+ "]";
			}
		}

		return null;
	}

	/**
	 * 判断是否可以从from节点到达to节点
	 * 
	 * @param fromNodeId
	 *            from节点的id
	 * @param toNodeId
	 *            to节点的id
	 * @return
	 */
	public boolean isReachable(String fromNodeId, String toNodeId) {
		if (fromNodeId == null || toNodeId == null) {
			return false;
		}
		if (fromNodeId.equals(toNodeId)) {
			return true;
		}
		List<Node> reachableList = this.getReachableNodes(fromNodeId);

		for (int j = 0; reachableList != null && j < reachableList.size(); j++) {
			Node node =  reachableList.get(j);
			if (node.getId().equals(toNodeId)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断两个Activity是否在同一个执行线上
	 * 
	 * @param activityId1
	 * @param activityId2
	 * @return true表示在同一个执行线上，false表示不在同一个执行线上
	 */
	public boolean isInSameLine(String activityId1, String activityId2) {
		Node node1 = (Node) this.findWFElementById(activityId1);
		Node node2 = (Node) this.findWFElementById(activityId2);
		if (node1 == null || node2 == null)
			return false;
		List<Node> connectableNodes4Activity1 = new ArrayList<Node>();
		connectableNodes4Activity1.add(node1);
		connectableNodes4Activity1.addAll(getReachableNodes(activityId1));
		connectableNodes4Activity1.addAll(getEnterableNodes(activityId1));

		List<Node> connectableNodes4Activity2 = new ArrayList<Node>();
		connectableNodes4Activity2.add(node2);
		connectableNodes4Activity2.addAll(getReachableNodes(activityId2));
		connectableNodes4Activity2.addAll(getEnterableNodes(activityId2));
		/*
		 * System.out.println("===Inside WorkflowProcess.isInSameLine()::connectableNodes4Activity1.size()="
		 * +connectableNodes4Activity1.size());System.out.println(
		 * "===Inside WorkflowProcess.isInSameLine()::connectableNodes4Activity2.size()="
		 * +connectableNodes4Activity2.size());
		 * System.out.println("-----------------------activity1--------------");
		 * for (int i=0;i<connectableNodes4Activity1.size();i++){ Node node =
		 * (Node)connectableNodes4Activity1.get(i);
		 * System.out.println("node.id of act1 is "+node.getId()); }
		 * 
		 * 
		 * System.out.println("---------------------activity2--------------------"
		 * ); for (int i=0;i<connectableNodes4Activity2.size();i++){ Node node =
		 * (Node)connectableNodes4Activity2.get(i);
		 * System.out.println("node.id of act2 is "+node.getId()); }
		 */

		if (connectableNodes4Activity1.size() != connectableNodes4Activity2.size()) {
			return false;
		}

		for (int i = 0; i < connectableNodes4Activity1.size(); i++) {
			Node node =  connectableNodes4Activity1.get(i);
			boolean find = false;
			for (int j = 0; j < connectableNodes4Activity2.size(); j++) {
				Node tmpNode = connectableNodes4Activity2.get(j);
				if (node.getId().equals(tmpNode.getId())) {
					find = true;
					break;
				}
			}
			if (!find)
				return false;
		}
		return true;
	}
    /**
     * 获取可以到达的节点
     * @param nodeId
     * @return
     */
	public List<Node> getReachableNodes(String nodeId) {
		List<Node> reachableNodesList = new ArrayList<Node>();
		Node node = (Node) this.findWFElementById(nodeId);
		if (node instanceof Activity) {
			Activity activity = (Activity) node;
			Transition leavingTransition = activity.getLeavingTransition();
			if (leavingTransition != null) {
				Node toNode = leavingTransition.getToNode();
				if (toNode != null) {
					reachableNodesList.add(toNode);
					reachableNodesList.addAll(getReachableNodes(toNode.getId()));
				}
			}
		} else if (node instanceof Synchronizer) {
			Synchronizer synchronizer = (Synchronizer) node;
			List<Transition> leavingTransitions = synchronizer.getLeavingTransitions();
			for (int i = 0; leavingTransitions != null
					&& i < leavingTransitions.size(); i++) {
				Transition leavingTransition =  leavingTransitions.get(i);
				if (leavingTransition != null) {
					Node toNode = (Node) leavingTransition.getToNode();
					if (toNode != null) {
						reachableNodesList.add(toNode);
						reachableNodesList.addAll(getReachableNodes(toNode.getId()));
					}

				}
			}
		}
        //剔除重复节点
        List<Node> tmp = new ArrayList<Node>();
		boolean alreadyInTheList = false;
		for (int i = 0; i < reachableNodesList.size(); i++) {
			Node nodeTmp = reachableNodesList.get(i);
			alreadyInTheList = false;
			for (int j = 0; j < tmp.size(); j++) {
				Node nodeTmp2 = tmp.get(j);
				if (nodeTmp2.getId().equals(nodeTmp.getId())) {
					alreadyInTheList = true;
					break;
				}
			}
			if (!alreadyInTheList) {
				tmp.add(nodeTmp);
			}
		}
		reachableNodesList = tmp;
		return reachableNodesList;
	}
    /**
     * 获取进入的节点(activity 或者synchronizer)
     * @param nodeId
     * @return
     */
	public List<Node> getEnterableNodes(String nodeId) {
		List<Node> enterableNodesList = new ArrayList<Node>();
		Node node = (Node) this.findWFElementById(nodeId);
		if (node instanceof Activity) {
			Activity activity = (Activity) node;
			Transition enteringTransition = activity.getEnteringTransition();
			if (enteringTransition != null) {
				Node fromNode = enteringTransition.getFromNode();
				if (fromNode != null) {
					enterableNodesList.add(fromNode);
					enterableNodesList.addAll(getEnterableNodes(fromNode.getId()));
				}
			}
		} else if (node instanceof Synchronizer) {
			Synchronizer synchronizer = (Synchronizer) node;
			List<Transition> enteringTransitions = synchronizer.getEnteringTransitions();
			for (int i = 0; enteringTransitions != null
					&& i < enteringTransitions.size(); i++) {
				Transition enteringTransition =  enteringTransitions.get(i);
				if (enteringTransition != null) {
					Node fromNode = enteringTransition.getFromNode();
					if (fromNode != null) {
						enterableNodesList.add(fromNode);
						enterableNodesList.addAll(getEnterableNodes(fromNode.getId()));
					}

				}
			}
		}

	    //剔除重复节点 
		//TODO mingjie.mj 20091018 改为使用集合是否更好?
        List<Node> tmp = new ArrayList<Node>();
		boolean alreadyInTheList = false;
		for (int i = 0; i < enterableNodesList.size(); i++) {
			Node nodeTmp = enterableNodesList.get(i);
			alreadyInTheList = false;
			for (int j = 0; j < tmp.size(); j++) {
				Node nodeTmp2 =  tmp.get(j);
				if (nodeTmp2.getId().equals(nodeTmp.getId())) {
					alreadyInTheList = true;
					break;
				}
			}
			if (!alreadyInTheList) {
				tmp.add(nodeTmp);
			}
		}
		enterableNodesList = tmp;
		return enterableNodesList;
	}

	public String getTaskInstanceCreator() {
		return taskInstanceCreator;
	}

	public void setTaskInstanceCreator(String taskInstanceCreator) {
		this.taskInstanceCreator = taskInstanceCreator;
	}

	public String getFormTaskInstanceCompletionEvaluator() {
		return formTaskInstanceCompletionEvaluator;
	}

	public void setFormTaskInstanceCompletionEvaluator(
			String formTaskInstanceCompletionEvaluator) {
		this.formTaskInstanceCompletionEvaluator = formTaskInstanceCompletionEvaluator;
	}

	public String getFormTaskInstanceRunner() {
		return formTaskInstanceRunner;
	}

	public void setFormTaskInstanceRunner(String formTaskInstanceRunner) {
		this.formTaskInstanceRunner = formTaskInstanceRunner;
	}

	public String getSubflowTaskInstanceCompletionEvaluator() {
		return subflowTaskInstanceCompletionEvaluator;
	}

	public void setSubflowTaskInstanceCompletionEvaluator(
			String subflowTaskInstanceCompletionEvaluator) {
		this.subflowTaskInstanceCompletionEvaluator = subflowTaskInstanceCompletionEvaluator;
	}

	public String getSubflowTaskInstanceRunner() {
		return subflowTaskInstanceRunner;
	}

	public void setSubflowTaskInstanceRunner(String subflowTaskInstanceRunner) {
		this.subflowTaskInstanceRunner = subflowTaskInstanceRunner;
	}

	public String getToolTaskInstanceRunner() {
		return toolTaskInstanceRunner;
	}

	public void setToolTaskInstanceRunner(String toolTaskInstanceRunner) {
		this.toolTaskInstanceRunner = toolTaskInstanceRunner;
	}

	public String getToolTaskInstanceCompletionEvaluator() {
		return toolTaskInstanceCompletionEvaluator;
	}

	public void setToolTaskInstanceCompletionEvaluator(
			String toolTaskIntanceCompletionEvaluator) {
		this.toolTaskInstanceCompletionEvaluator = toolTaskIntanceCompletionEvaluator;
	}

}
