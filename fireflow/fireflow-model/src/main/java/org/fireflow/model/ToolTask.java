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

import org.fireflow.model.resource.Application;

/**
 * Tool类型的Task
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
@SuppressWarnings("serial")
public class ToolTask extends Task{

    /**
     * 任务所引用的应用程序对象。
     */
    protected Application application = null;

    public ToolTask(){
        this.setType(TOOL);
    }

    public ToolTask(IWFElement parent, String name) {
        super(parent, name);
        this.setType(TOOL);
    }

    /**
     * 返回任务自动执行的Application。只有TOOL类型的任务才有Application。
     * @return
     * @see org.fireflow.model.reference.Application
     */
    public Application getApplication() {
        return application;
    }

    /**
     * 设置任务自动执行的Application
     * @param application
     * @see org.fireflow.model.reference.Application
     */
    public void setApplication(Application application) {
        this.application = application;
    }
}
