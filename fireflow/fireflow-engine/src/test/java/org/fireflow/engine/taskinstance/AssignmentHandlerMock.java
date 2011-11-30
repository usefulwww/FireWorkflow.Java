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

package org.fireflow.engine.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.kernel.KernelException;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 12, 2009
 */
public class AssignmentHandlerMock implements IAssignmentHandler{
    public static final String ACTOR_ID = "Fireflow JUnit Tester";
    public void assign(IAssignable asignable, String performerName) throws EngineException, KernelException {
        asignable.assignToActor(ACTOR_ID);
    }

}
