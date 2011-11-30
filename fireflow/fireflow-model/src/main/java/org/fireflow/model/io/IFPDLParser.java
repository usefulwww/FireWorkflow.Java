package org.fireflow.model.io;

import java.io.IOException;
import java.io.InputStream;

import org.fireflow.model.WorkflowProcess;


/**
 * FPDL解析器，将一个xml格式的fpdl流程定义文件解析成WorkflowProcess对象。
 * @author Chennieyun
 */
public interface IFPDLParser extends FPDLNames{

    /** 
     * Parse the given InputStream into a WorkflowProcess object.<br/>
     * 将输入流解析成为一个WorkflowProcess对象。
     * @param in The InputStream
     * @throws IOException Any I/O Exception
     * @throws FPDLParserException Any parser exception
     */
    public WorkflowProcess parse(InputStream in) throws IOException, FPDLParserException;

}
