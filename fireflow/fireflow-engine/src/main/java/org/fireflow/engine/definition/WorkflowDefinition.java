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
package org.fireflow.engine.definition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.Dom4JFPDLParser;
import org.fireflow.model.io.Dom4JFPDLSerializer;
import org.fireflow.model.io.FPDLParserException;
import org.fireflow.model.io.FPDLSerializerException;

/**
 * 流程定义对象，
 * 映射到表T_FF_DF_WORKFLOWDEF
 * @author 非也,nychen2000@163.com
 */
public class WorkflowDefinition extends WorkflowDefinitionInfo{

    protected transient WorkflowProcess workflowProcess;

    protected String processContent; //流程定义文件的内容


    public String getProcessContent() {
        return processContent;
    }

    public void setProcessContent(String processContent) {
        this.processContent = processContent;
    }

    /**
     * 获取业务流程对象
     * @return
     * @throws RuntimeException
     */
    public WorkflowProcess getWorkflowProcess() throws RuntimeException{
        if (workflowProcess == null) {
            if (this.processContent != null && !this.processContent.trim().equals("")) {

                ByteArrayInputStream in = null;
                try {
                    Dom4JFPDLParser parser = new Dom4JFPDLParser();//采用dom4j来解析xml
                    in = new ByteArrayInputStream(this.processContent.getBytes("utf-8"));
                    this.workflowProcess = parser.parse(in);

                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } 
                catch(FPDLParserException ex){
                    Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } finally {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }
        return workflowProcess;
    }

    /**
     * @param process
     * @throws RuntimeException
     */
    public void setWorkflowProcess(WorkflowProcess process) throws  RuntimeException {
        try {
            this.workflowProcess = process;
            this.processId = workflowProcess.getId();
            this.name = workflowProcess.getName();
            this.displayName = workflowProcess.getDisplayName();
            this.description = workflowProcess.getDescription();

            Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ser.serialize(workflowProcess, out);

            this.processContent = out.toString("utf-8");
        } catch (FPDLSerializerException ex) {
            Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.toString());
        }
    }

}
