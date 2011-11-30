/*
 * Copyright 2007-2009 非也
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

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.fireflow.kernel.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.kernel.IEdgeInstance;
import org.fireflow.kernel.INodeInstance;
import org.fireflow.kernel.event.IEdgeInstanceEventListener;

/**
 * wangmj  实现edge的公共方法 ，边有两种，一是transition，二是loop。
 * @author 非也
 * @version 1.0
 * Created on Mar 18, 2009
 */
public abstract class EdgeInstance implements IEdgeInstance{
    protected INodeInstance leavingNodeInstance = null;
    protected INodeInstance enteringNodeInstance = null;
    protected int weight = 0;
    protected List<IEdgeInstanceEventListener> eventListeners = new ArrayList<IEdgeInstanceEventListener>();

    public INodeInstance getLeavingNodeInstance() {
        return leavingNodeInstance;
    }

    public void setLeavingNodeInstance(INodeInstance nodeInst) {
        this.leavingNodeInstance = nodeInst;
    }

    public INodeInstance getEnteringNodeInstance() {
        return enteringNodeInstance;
    }

    public void setEnteringNodeInstance(INodeInstance nodeInst) {
        this.enteringNodeInstance = nodeInst;
    }

}
