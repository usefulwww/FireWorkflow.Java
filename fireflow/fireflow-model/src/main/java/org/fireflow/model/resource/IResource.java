package org.fireflow.model.resource;

/**
 * 工作流引用的资源。
 * @author 非也
 *
 */
public interface IResource {
	/**
	 * 返回资源的名称
	 * @return 资源的名称
	 */
	public String getName() ;
	public void setName(String name);
	
	/**
	 * 返回资源的显示名称
	 * @return 资源的显示名称
	 */
	public String getDisplayName();
	public void setDisplayName(String displayName);
	
	/**
	 * 返回资源的描述
	 * @return 资源的描述
	 */
	public String getDescription();
	public void setDescription(String description);
}
