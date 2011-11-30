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
package org.fireflow.engine.kernelextensions;

import org.fireflow.engine.EngineException;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.NodeInstanceEvent;
import org.fireflow.kernel.impl.StartNodeInstance;

;

/**
 * @author 非也
 *
 */
public class StartNodeInstanceExtension extends SynchronizerInstanceExtension {

    public String getExtentionPointName() {
        return StartNodeInstance.Extension_Point_NodeInstanceEventListener;
    }

    /* (non-Javadoc)
     * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionTargetName()
     */
    public String getExtentionTargetName() {
        return StartNodeInstance.Extension_Target_Name;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.kernelextensions.SynchronizerInstanceExtension#onNodeInstanceEventFired(org.fireflow.kernel.event.NodeInstanceEvent)
     */
    public void onNodeInstanceEventFired(NodeInstanceEvent e)
            throws KernelException, EngineException {
    	//开始节点，不需要做任何处理！
//        System.out.println("==Inside StartNode Extension....");
    }
}
