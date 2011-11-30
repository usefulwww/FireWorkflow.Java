package org.fireflow.engine.persistence.springjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.springframework.jdbc.core.RowMapper;

/**
 * 共14个字段
 * @author wmj2003
 */
public class ProcessInstanceTraceRowMapper implements RowMapper {
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException	{
		ProcessInstanceTrace processInstanceTrace = new ProcessInstanceTrace();

		processInstanceTrace.setId(rs.getString("id"));
		processInstanceTrace.setProcessInstanceId(rs.getString("processinstance_id"));
		processInstanceTrace.setStepNumber(rs.getInt("step_number"));
		processInstanceTrace.setMinorNumber(rs.getInt("minor_number"));
		processInstanceTrace.setType(rs.getString("type"));

		processInstanceTrace.setEdgeId(rs.getString("edge_id"));
		processInstanceTrace.setFromNodeId(rs.getString("from_node_id"));
		processInstanceTrace.setToNodeId(rs.getString("to_node_id"));

		return processInstanceTrace;

	}
}
