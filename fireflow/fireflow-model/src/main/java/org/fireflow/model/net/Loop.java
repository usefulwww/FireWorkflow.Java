/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.model.net;


import org.fireflow.model.WorkflowProcess;


/**
 * 循环。
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
@SuppressWarnings("serial")
public class Loop extends Edge{

    public Loop() {
    }

    public Loop(WorkflowProcess workflowProcess, String name, Synchronizer fromNode, Synchronizer toNode) {
        super(workflowProcess, name);
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

}
