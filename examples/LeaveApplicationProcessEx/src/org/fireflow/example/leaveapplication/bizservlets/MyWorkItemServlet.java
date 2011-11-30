package org.fireflow.example.leaveapplication.bizservlets;

import java.io.IOException;
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
import org.fireflow.example.leaveapplication.data.LeaveApprovalInfoDAO;
import org.fireflow.example.ou.User;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.FormTask;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MyWorkItemServlet extends HttpServlet {
	protected void doPost(final HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		// 获得spring bean factory
		final WebApplicationContext springContext = this.getSpringContext();

		// 获得事务模板
		final TransactionTemplate transactionTemplate = (TransactionTemplate) springContext
				.getBean("transactionTemplate");

		// 获得fire workflow的runtimeContext
		final RuntimeContext runtimeContext = (RuntimeContext) springContext
				.getBean("runtimeContext");

		// 获得当前用户
		final User currentUser = (User) req.getSession(true).getAttribute(
				"CURRENT_USER");

		String actionName = req.getParameter("actionName") + "";

		if (actionName.equals("doQueryMyToDoWorkItems")) {
			doQueryMyToDoWorkItems(req, resp, springContext,
					transactionTemplate, runtimeContext, currentUser);
			return;
		} else if (actionName.equals("claimWorkItem")) {
			claimWorkItem(req, resp, springContext, transactionTemplate,
					runtimeContext, currentUser);
			return;
		} else if (actionName.equals("openForm")){
			openForm(req, resp, springContext, transactionTemplate,
					runtimeContext, currentUser);
			return;
		}else if (actionName.equals("completeWorkItem")){
			completeWorkItem(req, resp, springContext, transactionTemplate,
					runtimeContext, currentUser);
			return;
		}

		forwardToMyWorkItemView(req, resp);
	}

	/**
	 * 查询待办任务
	 * 
	 * @param springContext
	 * @param transactionTemplate
	 * @param runtimeContext
	 * @param currentUser
	 */
	public void doQueryMyToDoWorkItems(HttpServletRequest req,
			HttpServletResponse resp, WebApplicationContext springContext,
			TransactionTemplate transactionTemplate,
			RuntimeContext runtimeContext, User currentUser)
			throws IOException, ServletException {

		IWorkflowSession wflsession = runtimeContext.getWorkflowSession();

		List myTodoWorkitems = wflsession.findMyTodoWorkItems(currentUser
				.getId());

		req.setAttribute("MY_TODO_WORKITEMS", myTodoWorkitems);

		forwardToMyWorkItemView(req, resp);
	}

	/**
	 * 签收工单
	 * 
	 * @param req
	 * @param resp
	 * @param springContext
	 * @param transactionTemplate
	 * @param runtimeContext
	 * @param currentUser
	 * @throws IOException
	 * @throws ServletException
	 */
	public void claimWorkItem(HttpServletRequest req, HttpServletResponse resp,
			WebApplicationContext springContext,
			TransactionTemplate transactionTemplate,
			final RuntimeContext runtimeContext, User currentUser)
			throws IOException, ServletException {

		final String workItemId = req.getParameter("selectedWorkItemId");

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(
					TransactionStatus transactionStatus) {
				IWorkflowSession wflsession = runtimeContext
						.getWorkflowSession();
				IWorkItem wi = wflsession.findWorkItemById(workItemId);
				try {
					if (wi != null && wi.getState() == IWorkItem.INITIALIZED) {
						wi.claim();
					}
				} catch (EngineException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KernelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		doQueryMyToDoWorkItems(req, resp, springContext, transactionTemplate,
				runtimeContext, currentUser);
	}

	/**
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

			} else {
				doQueryMyToDoWorkItems(req, resp, springContext,
						transactionTemplate, runtimeContext, currentUser);

			}
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			doQueryMyToDoWorkItems(req, resp, springContext,
					transactionTemplate, runtimeContext, currentUser);
		}
	}
	
	/**
	 * 结束当前的workitem
	 * 
	 * @return
	 */
	public void completeWorkItem(HttpServletRequest req, HttpServletResponse resp,
			WebApplicationContext springContext,
			TransactionTemplate transactionTemplate,
			final RuntimeContext runtimeContext, User currentUser)
			throws IOException, ServletException {
		final String workItemId = req.getParameter("selectedWorkItemId");
		transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							TransactionStatus arg0) {
						if (workItemId != null) {
							try {
								IWorkflowSession wflSession = runtimeContext
										.getWorkflowSession();
								IWorkItem wi = wflSession
										.findWorkItemById(workItemId);
								if (wi != null
										&& wi.getState() == IWorkItem.RUNNING) {
									wi.complete();
								}
							} catch (EngineException e) {
								e.printStackTrace();
								arg0.setRollbackOnly();
							} catch (KernelException e) {
								e.printStackTrace();
								arg0.setRollbackOnly();
							}
						}
					}
				});
		doQueryMyToDoWorkItems(req, resp, springContext,
				transactionTemplate, runtimeContext, currentUser);
	}
	

	private WebApplicationContext getSpringContext() {
		ServletContext application = getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(application);
		return ctx;
	}

	private void forwardToMyWorkItemView(HttpServletRequest req,
			HttpServletResponse resp) throws IOException, ServletException {
		RequestDispatcher dispatcher = req
				.getRequestDispatcher("/org/fireflow/example/leaveapplication/bizpages/MyWorkItem.jsp");
		dispatcher.forward(req, resp);
	}
}
