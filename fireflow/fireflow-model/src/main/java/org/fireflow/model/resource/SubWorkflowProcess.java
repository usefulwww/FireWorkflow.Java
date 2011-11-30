/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.model.resource;

/**
 * 子流程。保存对另一个流程引用信息
 * @author chennieyun
 */
public class SubWorkflowProcess extends AbstractResource {

	/**
	 * 所引用的流程的id
	 */
    private String workflowProcessId = null;

    /**
     * @param name
     */
    public SubWorkflowProcess(String name) {
        this.setName(name);
    }

    public String getWorkflowProcessId() {
        return workflowProcessId;
    }

    public void setWorkflowProcessId(String workflowProcessId) {
        this.workflowProcessId = workflowProcessId;
    }
}
