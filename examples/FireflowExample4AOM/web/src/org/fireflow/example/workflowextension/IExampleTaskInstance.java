package org.fireflow.example.workflowextension;

import java.util.List;

import org.fireflow.engine.IWorkItem;

/**
 * 为了便于页面展示，所有的业务TaskInstance均须实现该接口。
 * @author app
 *
 */
public interface IExampleTaskInstance {
	/**
	 * 获得当前的业务信息，将扩展taskInstance中的业务字段组成一个string返回，
	 * 便于在待办工单和已办工单界面进行统一的显示
	 * @return
	 */
	public String getBizInfo();
	
	/**
	 * 获得当前TaskInstance的所有workItem列表
	 * @return
	 */
	public List<IWorkItem> getWorkItems();
	
	/**
	 * 设置当前TaskInstance的所有的WorkItem列表
	 * @param workItems
	 */
	public void setWorkItems(List<IWorkItem> workItems);
}
