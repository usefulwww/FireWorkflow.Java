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
package org.fireflow.model;

/**
 * 流程变量
 * @author 非也,nychen2000@163.com
 */
@SuppressWarnings("serial")
public class DataField extends AbstractWFElement {
	/**
	 * 字符串类型
	 */
    public static final String STRING = "STRING";
    
    /**
     * 浮点类型
     * 
     */
    public static final String FLOAT = "FLOAT";
    
    /**
     * 双精度类型
     */
    public static final String DOUBLE = "DOUBLE";
    
    /**
     * 整数类型
     */
    public static final String INTEGER = "INTEGER";
    
    /**
     * 长整型
     */
    public static final String LONG = "LONG";
    
    /**
     * 日期时间类型
     */
    public static final String DATETIME = "DATETIME";
    
    /**
     * 布尔类型
     */
    public static final String BOOLEAN = "BOOLEAN";
    
    /**
     * 数据类型
     */
    private String dataType;
    
    /**
     * 初始值
     */
    private String initialValue;
    
    /**
     * 数据格式
     */
    private String dataPattern;
    

    public DataField() {
        this.setDataType(STRING);
    }

    public DataField(WorkflowProcess workflowProcess, String name, String dataType) {
        super(workflowProcess, name);
        setDataType(dataType);
    }

    /**
     * 返回流程变量的数据类型
     * @return 数据类型
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型，其取值只能是<br>
     * DataField.STRING, DataField.FLOAT, DataField.DOUBLE, DataField.INTEGER,
     * DataField.LONG,DataField.DATETIME, DataField.BOOLEAN
     * @param dataType
     */
    public void setDataType(String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }
        this.dataType = dataType;
    }

    /**
     * 返回初始值
     * @return 初始值
     */
    public String getInitialValue() {
        return initialValue;
    }

    /**
     * 设置初始值
     * @param initialValue 初始值
     */
    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    /**
     * 返回数据的pattern，目前主要用于日期类型。如 yyyyMMdd 等等。
     * @return
     */
    public String getDataPattern() {
        return dataPattern;
    }

    /**
     * 设置数据的pattern，目前主要用于日期类型。如 yyyyMMdd 等等。
     * @param dataPattern
     */
    public void setDataPattern(String dataPattern) {
        this.dataPattern = dataPattern;
    }
    

}
