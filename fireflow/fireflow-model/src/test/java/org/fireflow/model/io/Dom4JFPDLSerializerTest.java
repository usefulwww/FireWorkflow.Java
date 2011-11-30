/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.Loop;
import org.fireflow.model.net.Synchronizer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author app
 */
public class Dom4JFPDLSerializerTest {

    public Dom4JFPDLSerializerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of serialize method, of class Dom4JFPDLSerializer.
     */
    @Test
    public void testSerialize() throws Exception {
        System.out.println("serialize");
        final String PrepareGoodsActivity_ID = "Goods_Deliver_Process.PrepareGoodsActivity";
        final String PaymentActivity_ID = "Goods_Deliver_Process.PaymentActivity";
        InputStream in = Dom4JFPDLParserTest.class.getResourceAsStream("/org/fireflow/model/io/example_workflow.xml");
        Dom4JFPDLParser instance = new Dom4JFPDLParser();

        WorkflowProcess workflowProcess1 = instance.parse(in);
        
        String tmpDir = System.getProperty("java.io.tmpdir");
        
        File f = new File(tmpDir+"example_workflow_tmp.xml");
        FileOutputStream out = new FileOutputStream(f);
        Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
        ser.serialize(workflowProcess1, out);
        out.flush();
        out.close();

        FileInputStream fIn= new FileInputStream(f);
        WorkflowProcess result = instance.parse(fIn);
        f.delete();
        assertNotNull(result);

        assertNotNull(result.getStartNode());
        assertEquals(1, result.getTasks().size());
        assertEquals(4, result.getActivities().size());
        assertEquals(2, result.getSynchronizers().size());
        assertEquals(1, result.getEndNodes().size());
        assertEquals(8, result.getTransitions().size());

        Activity paymentActivity = (Activity) result.findWFElementById(PaymentActivity_ID);
        assertNotNull(paymentActivity);
        assertEquals(1, paymentActivity.getTasks().size());
        assertEquals(1, paymentActivity.getInlineTasks().size());

        Activity prepareGoodsActivity = (Activity) result.findWFElementById(PrepareGoodsActivity_ID);
        assertNotNull(prepareGoodsActivity);
        assertEquals(1, prepareGoodsActivity.getTasks().size());
        assertEquals(1, prepareGoodsActivity.getTaskRefs().size());

    }

    @Test
    public void testSerialize2()throws Exception{
        String synchronizer1Id = "MyFirstProcess.Synchronizer1";
        String synchronizer2Id = "MyFirstProcess.Synchronizer2";
        InputStream in = Dom4JFPDLParserTest.class.getResourceAsStream("/org/fireflow/model/io/MyFirstProcess.xml");
        Dom4JFPDLParser instance = new Dom4JFPDLParser();

        WorkflowProcess workflowProcess1 = instance.parse(in);
        
        String tmpDir = System.getProperty("java.io.tmpdir");
        
        File f = new File(tmpDir+"MyFirstProcess_tmp.xml");
        FileOutputStream out = new FileOutputStream(f);
        Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
        ser.serialize(workflowProcess1, out);
        out.flush();
        out.close();

        FileInputStream fIn= new FileInputStream(f);
        WorkflowProcess result = instance.parse(fIn);
        f.delete();

        assertNotNull(result);

        assertEquals(1, result.getLoops().size());

        Loop loop = (Loop)result.getLoops().get(0);
        assertNotNull(loop.getCondition());

        Synchronizer sync1 = (Synchronizer)result.findWFElementById(synchronizer1Id);
        assertEquals(1,sync1.getEnteringLoops().size());
        assertEquals(0,sync1.getLeavingLoops().size());

        Synchronizer sync2 = (Synchronizer)result.findWFElementById(synchronizer2Id);
        assertEquals(0,sync2.getEnteringLoops().size());
        assertEquals(1,sync2.getLeavingLoops().size());
    }
}