/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.engine.persistence.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * wangmj 流程实例变量类型
 * 
 * @author chennieyun
 * 
 */
public class ProcessInstanceVariableType implements UserType {
	@SuppressWarnings("unchecked")
	private static final Class returnedClass = Object.class;
	private static final int[] sqlTypes = new int[] { Types.VARCHAR };

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable,
	 *      java.lang.Object)
	 */
	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
	 */
	public Object deepCopy(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return arg0;
		// ProcessInstanceVar source = (ProcessInstanceVar)arg0;
		// ProcessInstanceVar target = new
		// ProcessInstanceVar(source.getValueAsString(),source.getType());
		// return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
	 */
	public Serializable disassemble(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#equals(java.lang.Object,
	 *      java.lang.Object)
	 */
	public boolean equals(Object arg0, Object arg1) throws HibernateException {
		// TODO Auto-generated method stub
		if (arg0 == arg1)
			return true;
		if (arg0 == null || arg1 == null)
			return false;
		if (arg0.equals(arg1)) {
			return true;
		} else {
			return false;
		}

		// if (arg0==arg1)return true;
		// if (arg0==null || arg1==null)return false;
		// ProcessInstanceVar var0 = (ProcessInstanceVar)arg0;
		// ProcessInstanceVar var1 = (ProcessInstanceVar)arg1;
		// if (var0.getType()==null || var1.getType()==null)return false;
		// if (var0.getType().equals(var1.getType())){
		// if (var0.getValueAsString() == null && var1.getValueAsString()==null)
		// return true;
		// if (var0.getValueAsString()==null ||
		// var1.getValueAsString()==null)return false;
		// if (var0.getValueAsString().equals(var1.getValueAsString())){
		// return true;
		// }else{
		// return false;
		// }
		// }else{
		// return false;
		// }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
	 */
	public int hashCode(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return arg0.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet,
	 *      java.lang.String[], java.lang.Object)
	 */
	public Object nullSafeGet(ResultSet arg0, String[] arg1, Object arg2)
			throws HibernateException, SQLException {
		String value = arg0.getString(arg1[0]);
		if (value == null)
			return null;
		int index = value.indexOf("#");
		if (index == -1) {
			return null;
		}
		String type = value.substring(0, index);
		String strValue = value.substring(index + 1);
		if (type.equals(String.class.getName())) {
			return strValue;
		}
		if (strValue == null || strValue.trim().equals("")) {
			return null;
		}
		if (type.equals(Integer.class.getName())) {
			return new Integer(strValue);
		} else if (type.equals(Long.class.getName())) {
			return new Long(strValue);
		} else if (type.equals(Float.class.getName())) {
			return new Float(strValue);
		} else if (type.equals(Double.class.getName())) {
			return new Double(strValue);
		} else if (type.equals(Boolean.class.getName())) {
			return new Boolean(strValue);
		} else if (type.equals(java.util.Date.class.getName())) {
			return new java.util.Date(new Long(strValue));
		} else {
			throw new HibernateException("Fireflow不支持数据类型" + type);
		}
		// String stringValue = arg0.getString(arg1[0]);
		// String type = arg0.getString(arg1[1]);
		// if (type==null || stringValue==null
		// || type.trim().equals("")
		// || stringValue.trim().equals("")){
		// throw new SQLException("流程变量的类型不可以为空");
		// }
		// if (!type.equals(ProcessInstanceVar.BOOLEAN_TYPE) &&
		// !type.equals(ProcessInstanceVar.DATE_TYPE) &&
		// !type.equals(ProcessInstanceVar.FLOAT_TYPE) &&
		// !type.equals(ProcessInstanceVar.INTEGER_TYPE) &&
		// !type.equals(ProcessInstanceVar.STRING_TYPE)){
		// throw new
		// SQLException("流程变量的数据类型只能是S-String,I-Integer,F-Float,B-boolean,D-Date之一");
		// }
		// ProcessInstanceVar var = new ProcessInstanceVar(stringValue,type);
		// return var;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement,
	 *      java.lang.Object, int)
	 */
	public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2)
			throws HibernateException, SQLException {
		if (arg1 == null) {
			arg0.setString(arg2, null);
			return;
		}
		String type = arg1.getClass().getName();
		if (type.equals(String.class.getName())) {
			arg0.setString(arg2, type + "#" + arg1);
		} else if (type.equals(Integer.class.getName())
				|| type.equals(Long.class.getName())) {
			arg0.setString(arg2, type + "#" + arg1);
		} else if (type.equals(Float.class.getName())
				|| type.equals(Double.class.getName())) {
			arg0.setString(arg2, type + "#" + arg1);
		} else if (type.equals(Boolean.class.getName())) {
			arg0.setString(arg2, type + "#" + arg1);
		} else if (type.equals(java.util.Date.class.getName())) {
			arg0.setString(arg2, type + "#" + ((Date) arg1).getTime());
		} else {
			throw new HibernateException("Fireflow不支持数据类型" + type);
		}

		// ProcessInstanceVar var = (ProcessInstanceVar)arg1;
		// if (var==null || var.getType()==null ||
		// var.getType().trim().equals("")){
		// throw new SQLException("流程变量的类型不可以为空");
		// }
		// if (!var.getType().equals(ProcessInstanceVar.BOOLEAN_TYPE) &&
		// !var.getType().equals(ProcessInstanceVar.DATE_TYPE) &&
		// !var.getType().equals(ProcessInstanceVar.FLOAT_TYPE) &&
		// !var.getType().equals(ProcessInstanceVar.INTEGER_TYPE) &&
		// !var.getType().equals(ProcessInstanceVar.STRING_TYPE)){
		// throw new
		// SQLException("流程变量的数据类型只能是S-String,I-Integer,F-Float,B-boolean,D-Date之一");
		// }
		// arg0.setString(arg2, var.getValueAsString());
		// arg0.setString(arg2+1, var.getType());
		// // TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#replace(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	@SuppressWarnings("unchecked")
	public Class returnedClass() {
		// TODO Auto-generated method stub
		return returnedClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		// TODO Auto-generated method stub
		return sqlTypes;
	}

}
