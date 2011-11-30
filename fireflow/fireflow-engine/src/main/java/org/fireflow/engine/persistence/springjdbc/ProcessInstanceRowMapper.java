package org.fireflow.engine.persistence.springjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.impl.ProcessInstance;
import org.springframework.jdbc.core.RowMapper;

/**
 * 共14个字段
 * @author wmj2003
 */
public class ProcessInstanceRowMapper implements RowMapper {
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException	{
		ProcessInstance processInstance = new ProcessInstance();

		processInstance.setId(rs.getString("id"));
		processInstance.setProcessId(rs.getString("process_id"));
		processInstance.setVersion(rs.getInt("version"));
		processInstance.setName(rs.getString("name"));
		processInstance.setDisplayName(rs.getString("display_name"));

		processInstance.setState(rs.getInt("state"));
		processInstance.setSuspended(rs.getInt("suspended") == 1 ? true : false);
		processInstance.setCreatorId(rs.getString("creator_id"));
		processInstance.setCreatedTime(rs.getTimestamp("created_time"));
		processInstance.setStartedTime(rs.getTimestamp("started_time"));

		processInstance.setExpiredTime(rs.getTimestamp("expired_time"));
		processInstance.setEndTime(rs.getTimestamp("end_time"));
		processInstance.setParentProcessInstanceId(rs.getString("parent_processinstance_id"));
		processInstance.setParentTaskInstanceId(rs.getString("parent_taskinstance_id"));

		return processInstance;

	}
}