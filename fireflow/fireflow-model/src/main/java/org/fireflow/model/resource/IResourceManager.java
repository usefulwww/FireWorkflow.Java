package org.fireflow.model.resource;

import java.util.List;

/**
 * 资源管理器（1.0未用到）
 * @author 非也
 *
 */
public interface IResourceManager {
	
	public List<Application> getApplications();
	
	public List<Participant> getParticipants();
	
	public List<Form> getForms();
}
