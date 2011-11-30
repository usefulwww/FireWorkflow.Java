package org.fireflow.engine.persistence.springjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.impl.ProcessInstanceVar;
import org.fireflow.engine.impl.ProcessInstanceVarPk;
import org.springframework.jdbc.core.RowMapper;


public class ProcessInstanceVarRowMapper implements RowMapper
{
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException
	{
		ProcessInstanceVar processInstanceVar = new ProcessInstanceVar();
		ProcessInstanceVarPk pk = new ProcessInstanceVarPk();
		pk.setProcessInstanceId(rs.getString("processinstance_id"));
		pk.setName(rs.getString("name"));
		processInstanceVar.setVarPrimaryKey(pk);

		String valueStr = rs.getString("value");
		Object valueObj = getObject(valueStr);
		processInstanceVar.setValue(valueObj);

		return processInstanceVar;

	}

	public Object getObject(String value)
	{
		if (value == null)
			return null;
		int index = value.indexOf("#");
		if (index == -1)
		{
			return null;
		}
		String type = value.substring(0, index);
		String strValue = value.substring(index + 1);
		if (type.equals(String.class.getName()))
		{
			return strValue;
		}
		if (strValue == null || strValue.trim().equals(""))
		{
			return null;
		}
		if (type.equals(Integer.class.getName()))
		{
			return new Integer(strValue);
		}
		else if (type.equals(Long.class.getName()))
		{
			return new Long(strValue);
		}
		else if (type.equals(Float.class.getName()))
		{
			return new Float(strValue);
		}
		else if (type.equals(Double.class.getName()))
		{
			return new Double(strValue);
		}
		else if (type.equals(Boolean.class.getName()))
		{
			return new Boolean(strValue);
		}
		else if (type.equals(java.util.Date.class.getName()))
		{
			return new java.util.Date(new Long(strValue));
		}
		else
		{
			throw new RuntimeException("Fireflow不支持数据类型" + type);
		}
	}
}
