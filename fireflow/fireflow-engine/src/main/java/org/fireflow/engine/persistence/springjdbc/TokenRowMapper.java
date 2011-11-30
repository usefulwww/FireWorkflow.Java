package org.fireflow.engine.persistence.springjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.kernel.impl.Token;
import org.springframework.jdbc.core.RowMapper;

/**
 * 共7个字段
 * @author wmj2003
 */
public class TokenRowMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException 	{
		Token token = new Token();
		token.setId(rs.getString("id"));
		token.setAlive(rs.getInt("alive") == 1 ? true : false);
		token.setValue(rs.getInt("value"));
		token.setNodeId(rs.getString("node_id"));
		token.setProcessInstanceId(rs.getString("processinstance_id"));

		token.setStepNumber(rs.getInt("step_number"));
		token.setFromActivityId(rs.getString("from_activity_id"));

		return token;
	}
}