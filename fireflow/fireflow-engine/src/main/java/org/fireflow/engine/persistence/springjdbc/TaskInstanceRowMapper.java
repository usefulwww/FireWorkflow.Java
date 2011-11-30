package org.fireflow.engine.persistence.springjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.impl.TaskInstance;
import org.springframework.jdbc.core.RowMapper;

/**
 * taskinstance 映射(共21个字段)
 * @author wmj2003
 */
public class TaskInstanceRowMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException	{
		TaskInstance taskInstance = new TaskInstance();
		taskInstance.setId(rs.getString("id"));
		// 20090922 wmj2003 没有给biz_type赋值 是否需要给基于jdbc的数据增加 setBizType()方法？
		taskInstance.setTaskId(rs.getString("task_id"));
		taskInstance.setActivityId(rs.getString("activity_id"));
		taskInstance.setName(rs.getString("name"));

		taskInstance.setDisplayName(rs.getString("display_name"));
		taskInstance.setState(rs.getInt("state"));
		taskInstance.setSuspended(rs.getInt("suspended") == 1 ? true : false);
		taskInstance.setTaskType(rs.getString("task_type"));
		taskInstance.setCreatedTime(rs.getTimestamp("created_time"));

		taskInstance.setStartedTime(rs.getTimestamp("started_time"));
		taskInstance.setEndTime(rs.getTimestamp("end_time"));
		taskInstance.setAssignmentStrategy(rs.getString("assignment_strategy"));
		taskInstance.setProcessInstanceId(rs.getString("processinstance_id"));
		taskInstance.setProcessId(rs.getString("process_id"));

		taskInstance.setVersion(rs.getInt("version"));
		taskInstance.setTargetActivityId(rs.getString("target_activity_id"));
		taskInstance.setFromActivityId(rs.getString("from_activity_id"));
		taskInstance.setStepNumber(rs.getInt("step_number"));
		taskInstance.setCanBeWithdrawn(rs.getInt("can_be_withdrawn") == 1 ? true : false);

		return taskInstance;
	}
}