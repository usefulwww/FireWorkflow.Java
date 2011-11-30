/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.model.net;

import org.fireflow.model.AbstractWFElement;
import org.fireflow.model.WorkflowProcess;

/**
 * 工作流网的边。
 * @author 非也
 * @version 1.0
 * Created on Mar 18, 2009
 */
@SuppressWarnings("serial")
public class Edge extends AbstractWFElement {
    /**
     * 转移(或者循环)的源节点。<br>
     * 转移的源节点可以是StartNode、 Activity或者Synchronizer。<br>
     * 循环的源节点必须是Synchronizer或者EndNode，同时循环的目标节点必须是循环源节点的前驱。
     */
    protected Node fromNode = null;

    /**
     * 转移(或者循环)的目标节点。<br>
     * 转移的终止目标可以是EndNode、 Activity或者Synchronizer。<br>
     * 循环的目标节点必须是Synchronizer或者StartNode。
     */
    protected Node toNode = null;

    /**
     * 转移（或者循环）的启动条件
     */
    protected String condition = null;

    public Edge(){
        
    }

    public Edge(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    }

    /**
     * 返回转移(或者循环)的启动条件，转移（循环）启动条件是一个EL表达式
     * @return
     */
    public String getCondition() {
        return condition;
    }

    /**
     * 设置转移(或者循环)条件
     * @param condition
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * 返回转移(或者循环)的源节点
     * @return
     */
    public Node getFromNode() {
        return fromNode;
    }

    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }

    /**
     * 返回转移(或者循环)的目标节点
     * @return
     */
    public Node getToNode() {
        return toNode;
    }

    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }
}
