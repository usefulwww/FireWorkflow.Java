/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.model.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.Loop;
import org.fireflow.model.net.Synchronizer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author app
 */
public class Dom4JFPDLParserTest {

    public Dom4JFPDLParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of parse method, of class Dom4JFPDLParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("-----------Test Dom4JFPDLParser---------------");
        final String PrepareGoodsActivity_ID = "Goods_Deliver_Process.PrepareGoodsActivity";
        final String PaymentActivity_ID = "Goods_Deliver_Process.PaymentActivity";
        InputStream in = Dom4JFPDLParserTest.class.getResourceAsStream("/org/fireflow/model/io/example_workflow.xml");
        Dom4JFPDLParser instance = new Dom4JFPDLParser();

        WorkflowProcess result = instance.parse(in);
        assertNotNull(result);

        assertNotNull(result.getStartNode());
        assertEquals(1, result.getTasks().size());
        assertEquals(4, result.getActivities().size());
        assertEquals(2, result.getSynchronizers().size());
        assertEquals(1, result.getEndNodes().size());
        assertEquals(8, result.getTransitions().size());
        
        Task task = result.getTasks().get(0);
        assertEquals("Goods_Deliver_Process.PrepareGoodsTask",task.getId());

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
    public void testParse2() throws Exception {
        System.out.println("-----------Test Dom4JFPDLParser 2 检验是否能够解析Loop元素---------------");
        String synchronizer1Id = "MyFirstProcess.Synchronizer1";
        String synchronizer2Id = "MyFirstProcess.Synchronizer2";

        InputStream in = Dom4JFPDLParserTest.class.getResourceAsStream("/org/fireflow/model/io/MyFirstProcess.xml");
        Dom4JFPDLParser instance = new Dom4JFPDLParser();

        WorkflowProcess result = instance.parse(in);

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