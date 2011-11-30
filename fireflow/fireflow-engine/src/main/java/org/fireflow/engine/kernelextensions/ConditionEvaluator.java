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

package org.fireflow.engine.kernelextensions;

import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.kernel.plugin.IKernelExtension;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 18, 2009
 */
public class ConditionEvaluator implements IKernelExtension, IRuntimeContextAware{

    public String getExtentionTargetName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getExtentionPointName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setRuntimeContext(RuntimeContext ctx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RuntimeContext getRuntimeContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
