/**
 * Copyright 2004-2008 非也
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
package org.fireflow.model.net;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.WorkflowProcess;

/**
 * 同步器
 * @author 非也,nychen2000@163.com
 *
 */
@SuppressWarnings("serial")
public class Synchronizer extends Node {
	/**
	 * 输入转移的列表
	 */
    List<Transition> enteringTransitions = new ArrayList<Transition>();
    
    /**
     * 输出转移的列表
     */
    List<Transition> leavingTransitions = new ArrayList<Transition>();

    /**
     * 输入循环的列表
     */
    List<Loop> enteringLoops = new ArrayList<Loop>();
    
    /**
     * 输出循环的列表
     */
    List<Loop> leavingLoops = new ArrayList<Loop>();

    public Synchronizer() {
    }

    public Synchronizer(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    }

    /**
     * 返回输入Transition集合
     * @return
     */
    public List<Transition> getEnteringTransitions() {
        return enteringTransitions;
    }

    /**
     * 返回输出transition集合
     * @return
     */
    public List<Transition> getLeavingTransitions() {
        return leavingTransitions;
    }

    /**
     * 返回输入Loop集合
     * @return 
     */
    public List<Loop> getEnteringLoops(){
        return this.enteringLoops;
    }

    /**
     * 返回输出Loop集合
     * @return
     */
    public List<Loop> getLeavingLoops(){
        return this.leavingLoops;
    }
}
