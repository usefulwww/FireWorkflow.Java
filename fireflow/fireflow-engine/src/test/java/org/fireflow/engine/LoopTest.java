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

package org.fireflow.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.CurrentUserAssignmentHandlerMock;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 14, 2009
 */
public class LoopTest extends FireFlowAbstractTests {
  
    /**
     * 创建流程实例，并执行实例的run方法。
     */
    @Test
    public void testStartNewProcess() {
        System.out.println("--------------Start a new process ----------------");
        IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status)
			{
				 try {
			            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
			            //启动"/workflowdefinition/Loop.xml
			            IProcessInstance currentProcessInstance = workflowSession.createProcessInstance("Loop","fireflowTester");
			            currentProcessInstance.setProcessInstanceVariable("loopFlag", new Integer(1));
			            currentProcessInstance.run();
			            return currentProcessInstance;
			        } catch (EngineException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        } catch (KernelException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        }catch(Exception ex){
			        	ex.printStackTrace();
			        }
			        return null;
			}
        	
        });
       
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        //String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
    }

    @Test
    public void testCompleteWorkItem1(){
    	
    	final IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status)
			{
				 try {
			            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
			            //启动"/workflowdefinition/Loop.xml
			            IProcessInstance currentProcessInstance = workflowSession.createProcessInstance("Loop","fireflowTester");
			            currentProcessInstance.setProcessInstanceVariable("loopFlag", new Integer(1));
			            currentProcessInstance.run();
			            return currentProcessInstance;
			        } catch (EngineException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        } catch (KernelException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        }catch(Exception ex){
			        	ex.printStackTrace();
			        }
			        return null;
			}
        	
        });
    	
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
    	//-------------------------------------
        transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				// TODO Auto-generated method stub 
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem1.claim();
            workItem1.complete();
        } catch (EngineException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
				return null;
			}
		});
       

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(2,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        final String workItem11Id = ((IWorkItem) workItemList.get(0)).getId();
        transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem11Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 2);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));

        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(3,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());

        //String workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    }

    @Test
    public void testCompleteWorkItem2(){
    	final IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status)
			{
				 try {
			            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
			            //启动"/workflowdefinition/Loop.xml
			            IProcessInstance currentProcessInstance = workflowSession.createProcessInstance("Loop","fireflowTester");
			            currentProcessInstance.setProcessInstanceVariable("loopFlag", new Integer(1));
			            currentProcessInstance.run();
			            return currentProcessInstance;
			        } catch (EngineException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        } catch (KernelException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        }catch(Exception ex){
			        	ex.printStackTrace();
			        }
			        return null;
			}
        	
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
    	//-------------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem1.claim();
            workItem1.complete();
        } catch (EngineException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(2,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        final String workItem11Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem11Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 2);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));

        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(3,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());

        final String workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem.claim();
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(4,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(4,token.getStepNumber().intValue());

        final String workItem21Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem21Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());;
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 4);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity3");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(5,taskInst.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity4");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.COMPLETED),taskInst .getState());
        assertEquals(5,taskInst.getStepNumber().intValue());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(2, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(5,token.getStepNumber().intValue());
        token = (IToken)tokensList.get(1);
        assertEquals(5,token.getStepNumber().intValue());


        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        //String workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

    }

    @Test
    public void testCompleteWorkItem3(){
    	final IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status)
			{
				 try {
			            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
			            //启动"/workflowdefinition/Loop.xml
			            IProcessInstance currentProcessInstance = workflowSession.createProcessInstance("Loop","fireflowTester");
			            currentProcessInstance.setProcessInstanceVariable("loopFlag", new Integer(1));
			            currentProcessInstance.run();
			            return currentProcessInstance;
			        } catch (EngineException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        } catch (KernelException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        }catch(Exception ex){
			        	ex.printStackTrace();
			        }
			        return null;
			}
        	
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
    	//-------------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem1.claim();
            workItem1.complete();
        } catch (EngineException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(2,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        final String workItem11Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem11Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 2);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));

        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(3,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());

        final String workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem.claim();
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(4,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(4,token.getStepNumber().intValue());

        final String workItem21Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem21Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());;
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 4);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity3");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(5,taskInst.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity4");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.COMPLETED),taskInst .getState());
        assertEquals(5,taskInst.getStepNumber().intValue());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(2, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(5,token.getStepNumber().intValue());
        token = (IToken)tokensList.get(1);
        assertEquals(5,token.getStepNumber().intValue());


        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        final String workItem3Id = ((IWorkItem) workItemList.get(0)).getId();
    	//----------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem.claim();
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity3");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(6,taskInst.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity4");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.COMPLETED),taskInst .getState());
        assertEquals(5,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(2, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(6,token.getStepNumber().intValue());

        final String workItem31Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem31Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 5);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        
        


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(7,taskInst.getStepNumber().intValue());


        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(7,token.getStepNumber().intValue());

       // String workItem5Id = ((IWorkItem) workItemList.get(0)).getId();
        
    }

    @Test
    public void testCompleteWorkItem5(){
    	final IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status)
			{
				 try {
			            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
			            //启动"/workflowdefinition/Loop.xml
			            IProcessInstance currentProcessInstance = workflowSession.createProcessInstance("Loop","fireflowTester");
			            currentProcessInstance.setProcessInstanceVariable("loopFlag", new Integer(1));
			            currentProcessInstance.run();
			            return currentProcessInstance;
			        } catch (EngineException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        } catch (KernelException ex) {
			        	ex.printStackTrace();
			            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
			        }catch(Exception ex){
			        	ex.printStackTrace();
			        }
			        return null;
			}
        	
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
    	//-------------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem1.claim();
            workItem1.complete();
        } catch (EngineException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
        	ex.printStackTrace();
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
				return null;
			}
		});
        
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(2,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        final String workItem11Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem11Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 2);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));

        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(3,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());

        final String workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem.claim();
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(4,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(4,token.getStepNumber().intValue());

        final String workItem21Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem21Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());;
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 4);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity3");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(5,taskInst.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity4");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.COMPLETED),taskInst .getState());
        assertEquals(5,taskInst.getStepNumber().intValue());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(2, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(5,token.getStepNumber().intValue());
        token = (IToken)tokensList.get(1);
        assertEquals(5,token.getStepNumber().intValue());


        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        final String workItem3Id = ((IWorkItem) workItemList.get(0)).getId();
    	//----------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem.claim();
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity3");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(6,taskInst.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity4");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.COMPLETED),taskInst .getState());
        assertEquals(5,taskInst.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(2, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(6,token.getStepNumber().intValue());

        final String workItem31Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem31Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 5);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(7,taskInst.getStepNumber().intValue());


        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(7,token.getStepNumber().intValue());

        final String workItem5Id = ((IWorkItem) workItemList.get(0)).getId();
    	//-------------------------------
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem.claim();
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Loop.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getState()==ITaskInstance.COMPLETED){
            taskInst =  ((ITaskInstance) taskInstanceList.get(1));
        }
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(8,taskInst.getStepNumber().intValue());


        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
//        IToken token2 = (IToken)tokensList.get(1);
        assertEquals(8,token.getStepNumber().intValue());

        final String workItem51Id = ((IWorkItem) workItemList.get(0)).getId();
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				 try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem51Id);
//            IProcessInstance processInstance = runtimeContext.getPersistenceService().findProcessInstanceById(currentProcessInstance.getId());
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
       

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Loop", "Loop.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        final String workItem511Id = ((IWorkItem) workItemList.get(0)).getId();
        
        this.transactionTemplate.execute(new TransactionCallback()
		{
			
			public Object doInTransaction(TransactionStatus status)
			{
				try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Loop.xml
            IWorkItem workItem = workflowSession.findWorkItemById(workItem511Id);
            IProcessInstance currentProcessInstance1 = workflowSession.findProcessInstanceById(currentProcessInstance.getId());
            currentProcessInstance1.setProcessInstanceVariable("loopFlag", 6);
            workItem.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
				return null;
			}
		});
        
        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(0, tokensList.size());

        IProcessInstance processInstance = persistenceService.findProcessInstanceById(currentProcessInstance.getId());
        assertNotNull(processInstance);
        assertEquals(IProcessInstance.COMPLETED,processInstance.getState().intValue());
    }

}
