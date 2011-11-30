package org.fireflow.engine.persistence.springjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.impl.WorkItem;
import org.springframework.jdbc.core.RowMapper;

/**
 * 共8个字段
 * @author wmj2003
 */
public class WorkItemRowMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException	{
		WorkItem workItem = new WorkItem();

		workItem.setId(rs.getString("id"));
		workItem.setState(rs.getInt("state"));
		workItem.setCreatedTime(rs.getTimestamp("created_time"));
		workItem.setClaimedTime(rs.getTimestamp("claimed_time"));
		workItem.setEndTime(rs.getTimestamp("end_time"));

		workItem.setActorId(rs.getString("actor_id"));
		workItem.setTaskInstanceId(rs.getString("taskinstance_id"));
		workItem.setComments(rs.getString("comments"));
		return workItem;
	}
}
