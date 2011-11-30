package org.fireflow;

public interface IManagedBean {
	/**
	 * 执行业务操作查询
	 * @return
	 */
	public String doBizOperQuery();
	
	/**
	 * 
	 * @return
	 */
	public String doSelectBizData();
	
	/**
	 * 保存业务数据
	 * @return
	 */
	public String doSaveBizData();
}
