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
package org.fireflow.kernel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.kernel.impl.NetInstance;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.model.WorkflowProcess;

/**
 * @author 非也
 *
 */
public class KernelManager implements IRuntimeContextAware {

    /**
     * 工作流网实例
     */
    private HashMap<String, INetInstance> netInstanceMap = new HashMap<String, INetInstance>();
    /**
     * wangmj spring 初始化的时候将扩展属性注入到这个map中
     * 内核扩展对象
     */
    private Map<String, List<IKernelExtension>> kernelExtensions = new HashMap<String, List<IKernelExtension>>();
    /**
     * 工作流总线
     */
    protected RuntimeContext rtCtx = null;

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
        if (this.kernelExtensions != null && this.kernelExtensions.size()>0) {
            Iterator<List<IKernelExtension>> values = this.kernelExtensions.values().iterator();
            while (values != null && values.hasNext()) {
                List<IKernelExtension> extensionList = values.next();
                for (int i = 0; extensionList != null && i < extensionList.size(); i++) {
                    IKernelExtension extension =  extensionList.get(i);
                    ((IRuntimeContextAware) extension).setRuntimeContext(rtCtx);
                    //wangmj? 如何就强制转换成IRuntimeContextAware了？
                    //答：因为IKernelExtension 的实现类，同时也实现了接口IRuntimeContextAware
                }
            }
        }
    }
    
    public RuntimeContext getRuntimeContext() {
        return this.rtCtx;
    }
    
    /**
     * 在获取工作流网实例的时候，调用createNetInstance方法，创建实例
     * @param processId  流程定义ID
     * @param version    流程版本号
     * @return
     * @throws KernelException
     */
    public INetInstance getNetInstance(String processId, Integer version) throws KernelException {
        INetInstance netInstance = this.netInstanceMap.get(processId + "_V_" + version);
        if (netInstance == null) {
        	//数据流定义在runtimeContext初始化的时候，就被加载了，将流程定义的xml读入到内存中
            WorkflowDefinition def = rtCtx.getDefinitionService().getWorkflowDefinitionByProcessIdAndVersionNumber(processId, version);
            netInstance = this.createNetInstance(def);
        }
        return netInstance;
    }

    /**
     * 清空所有工作流网的实例
     */
    public void clearAllNetInstance() {
        netInstanceMap.clear();
    }

    /**
     * 创建一个工作流网实例
     * @param workflowDef
     * @return
     * @throws KernelException
     */
    public INetInstance createNetInstance(WorkflowDefinition workflowDef) throws KernelException {
        if (workflowDef==null)return null;
        WorkflowProcess workflowProcess = null;
        workflowProcess = workflowDef.getWorkflowProcess();//解析fpdl

        if (workflowProcess == null ){
        	throw new KernelException(null,null,"The WorkflowProcess property of WorkflowDefinition[processId="+workflowDef.getProcessId()+"] is null. ");
        }
        String validateMsg =  workflowProcess.validate();//校验工作流定义是否有效
        if (validateMsg != null){
        	throw new KernelException(null,null,validateMsg);
        }
        NetInstance netInstance = new NetInstance(workflowProcess, kernelExtensions);

        netInstance.setVersion(workflowDef.getVersion());//设置版本号
        //map的key的组成规则：流程定义ID_V_版本号
        netInstanceMap.put(workflowDef.getProcessId() + "_V_" + workflowDef.getVersion(), netInstance);

        return netInstance;
    }

    public Map<String, List<IKernelExtension>> getKernelExtensions() {
        return kernelExtensions;
    }

    public void setKernelExtensions(Map<String, List<IKernelExtension>> arg0) {
        this.kernelExtensions = arg0;
    }
}
