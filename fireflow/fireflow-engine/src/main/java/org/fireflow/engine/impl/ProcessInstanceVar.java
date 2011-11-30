/**
 * Copyright 2007-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author 非也,nychen2000@163.com
 */
public class ProcessInstanceVar {
	ProcessInstanceVarPk varPrimaryKey = null;
	String value = null;
	String valueType = null;

	public ProcessInstanceVarPk getVarPrimaryKey() {
		return varPrimaryKey;
	}

	public void setVarPrimaryKey(ProcessInstanceVarPk varPrimaryKey) {
		this.varPrimaryKey = varPrimaryKey;
	}

	public String getName() {
		return varPrimaryKey == null ? null : varPrimaryKey.getName();
	}

	public String getProcessInstanceId() {
		return varPrimaryKey == null ? null : varPrimaryKey
				.getProcessInstanceId();
	}

	public Object getValue() {
		if (Integer.class.getName().equals(this.valueType)
				|| int.class.getName().equals(this.valueType)) {
			return Integer.parseInt(this.value);
		} else if (Long.class.getName().equals(this.valueType)
				|| long.class.getName().equals(this.valueType)) {
			return Long.parseLong(this.value);
		} else if (String.class.getName().equals(this.valueType)) {
			return this.value;
		} else if (Float.class.getName().equals(this.valueType)
				|| float.class.getName().equals(this.valueType)) {
			return Float.parseFloat(this.value);
		} else if (Double.class.getName().equals(this.valueType)
				|| double.class.getName().equals(this.valueType)) {
			return Double.parseDouble(this.value);
		} else if (Boolean.class.getName().equals(this.valueType)
				|| boolean.class.getName().equals(this.valueType)) {
			return Boolean.parseBoolean(this.value);
		} else if (Date.class.getName().equals(this.valueType)) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
			try {
				return formatter.parse(this.value);
			} catch (ParseException e) {
				return null;
			}
		}
		return value;
	}

	public void setValue(Object v) {
		this.value = String.valueOf(v);
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ProcessInstanceVar))
			return false;
		ProcessInstanceVar var = (ProcessInstanceVar) obj;
		if (var.getVarPrimaryKey().equals(this.getVarPrimaryKey())) {
			return true;
		} else {
			return false;
		}
	}

	public int hashCode() {
		if (this.getVarPrimaryKey() == null) {
			return super.hashCode();
		} else {
			return this.getVarPrimaryKey().hashCode();
		}
	}
}
