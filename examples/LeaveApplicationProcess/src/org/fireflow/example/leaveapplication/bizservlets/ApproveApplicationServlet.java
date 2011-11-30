package org.fireflow.example.leaveapplication.bizservlets;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.example.leaveapplication.data.LeaveApplicationInfo;
import org.fireflow.example.leaveapplication.data.LeaveApplicationInfoDAO;
import org.fireflow.example.leaveapplication.data.LeaveApprovalInfo;
import org.fireflow.example.leaveapplication.data.LeaveApprovalInfoDAO;
import org.fireflow.example.ou.User;
import org.fireflow.model.FormTask;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ApproveApplicationServlet extends HttpServlet {
	protected void doPost(final HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	

		// 获得spring bean factory
		final WebApplicationContext springContext = this.getSpringContext();

		// 获得事务模板
		final TransactionTemplate transactionTemplate = (TransactionTemplate) springContext
				.getBean("transactionTemplate");

		//获得审批信息DAO
		final LeaveApprovalInfoDAO leaveApprovalDAO = (LeaveApprovalInfoDAO) springContext
				.getBean("LeaveApprovalInfoDAO");
		
		// 获得请假信息的DAO
		final LeaveApplicationInfoDAO leaveApplicationInfoDAO = (LeaveApplicationInfoDAO)springContext
		.getBean("LeaveApplicationInfoDAO");

		// 获得fire workflow的runtimeContext
		final RuntimeContext runtimeContext = (RuntimeContext) springContext
				.getBean("runtimeContext");

		// 获得当前用户
		final User currentUser = (User) req.getSession(true).getAttribute(
				"CURRENT_USER");		
		
		// 构建请假审批信息pojo
		final LeaveApprovalInfo leaveApprovalInfo = buildLeaveApprovalInfo(req,currentUser);
		
		//保存请假审批信息
		transactionTemplate.execute(new TransactionCallback() {

			@Override
			public Object doInTransaction(TransactionStatus arg0) {
				//保存审批信息
				leaveApprovalDAO.save(leaveApprovalInfo);
				
				//同时将最后一次的审批信息保存到主表中
				LeaveApplicationInfo leaveApplicationInfo = leaveApplicationInfoDAO.findBySn(leaveApprovalInfo.getSn());
				leaveApplicationInfo.setApprovalFlag(leaveApprovalInfo.getApprovalFlag());
				leaveApplicationInfo.setApprover(leaveApprovalInfo.getApprover());
				leaveApplicationInfo.setApprovalTime(leaveApprovalInfo.getApprovalTime());
				leaveApplicationInfo.setApprovalDetail(leaveApprovalInfo.getDetail());
				leaveApplicationInfoDAO.update(leaveApplicationInfo);
				
				//将审批意见更新到流程变量中去
				String currentProcessInstanceId = req.getParameter("currentProcessInstanceId");
				IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
				IProcessInstance processInstance = workflowSession.findProcessInstanceById(currentProcessInstanceId);
				if(processInstance!=null){
					processInstance.setProcessInstanceVariable("approvalFlag", leaveApprovalInfo.getApprovalFlag());
				}
				return null;
			}
		});
		
		openForm(req, resp, springContext, transactionTemplate,
				runtimeContext, currentUser);
	}
	
	private LeaveApprovalInfo buildLeaveApprovalInfo(
			HttpServletRequest req,User currentUser) {
		LeaveApprovalInfo leaveApprovalInfo = new LeaveApprovalInfo();
		leaveApprovalInfo.setSn(req.getParameter("sn"));
		leaveApprovalInfo.setApprover(currentUser.getId());
		
		String decisionStr = req.getParameter("decision");
		if (decisionStr!=null && decisionStr.equals("1")){
			leaveApprovalInfo.setApprovalFlag(Boolean.TRUE);
		}else{
			leaveApprovalInfo.setApprovalFlag(Boolean.FALSE);
		}
		
		leaveApprovalInfo.setApprovalTime(new Date());
		return leaveApprovalInfo;
	}
	
	private WebApplicationContext getSpringContext() {
		ServletContext application = getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(application);
		return ctx;
	}	
	
	/**
	 * 审批后跳转到原界面
	 * 
	 * 打开工单对应的业务操作界面
	 * 
	 * @return
	 */
	public void openForm(HttpServletRequest req, HttpServletResponse resp,
			WebApplicationContext springContext,
			TransactionTemplate transactionTemplate,
			final RuntimeContext runtimeContext, User currentUser)
			throws IOException, ServletException {
		final String workItemId = req.getParameter("selectedWorkItemId");
		try {

			IWorkflowSession wflsession = runtimeContext.getWorkflowSession();
			IWorkItem wi = wflsession.findWorkItemById(workItemId);

			if (wi != null && wi.getState() == IWorkItem.RUNNING) {
				//用request传递当前WorkItem
				req.setAttribute("CURRENT_WORKITEM", wi);
				IProcessInstance processInstance = ((TaskInstance) wi
						.getTaskInstance()).getAliveProcessInstance();
				
				//用request传递当前sn
				String sn = (String) processInstance
						.getProcessInstanceVariable("sn");
				req.setAttribute("SN", sn);
				LeaveApplicationInfoDAO leaveApplicationInfoDAO = (LeaveApplicationInfoDAO)springContext.getBean("LeaveApplicationInfoDAO");
				LeaveApplicationInfo leaveApplicationInfo = leaveApplicationInfoDAO.findBySn(sn);
				req.setAttribute("LEAVE_APPLICATION", leaveApplicationInfo);
				
				//读取审批历史信息
				LeaveApprovalInfoDAO leaveApprovalDAO = (LeaveApprovalInfoDAO)springContext.getBean("LeaveApprovalInfoDAO");
				List leaveApprovalInfoList = leaveApprovalDAO.findApprovalInfoBySn(sn);
				req.setAttribute("APPROVAL_INFO_LIST", leaveApprovalInfoList);
				
				//从流程定义中获取操作界面url信息
				String formUri = ((FormTask) wi.getTaskInstance().getTask())
						.getEditForm().getUri();
				
				//跳转到对应的操作界面
				RequestDispatcher dispatcher = req
						.getRequestDispatcher(formUri);
				dispatcher.forward(req, resp);

			} 
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
