package org.fireflow.example.leaveapplication.workflowextension;

public interface ITaskInstanceExtension {
	/**
	 * 返回这个TaskInstance的业务信息，用于列表显示
	 * @return
	 */
	public String getBizInfo();
}
