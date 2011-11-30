package org.fireflow.example.leaveapplication.bizservlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.example.leaveapplication.data.LeaveApplicationInfo;
import org.fireflow.example.leaveapplication.data.LeaveApplicationInfoDAO;
import org.fireflow.example.ou.User;
import org.fireflow.kernel.KernelException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SubmitApplicationServlet extends HttpServlet {
	protected void doPost(final HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		// 构建请假信息pojo
		final LeaveApplicationInfo leaveApplicationInfo = buildLeaveApplicationInfo(req);

		// 获得spring bean factory
		final WebApplicationContext springContext = this.getSpringContext();

		// 获得事务模板
		final TransactionTemplate transactionTemplate = (TransactionTemplate) springContext
				.getBean("transactionTemplate");

		// 获得请假信息的DAO
		final LeaveApplicationInfoDAO leaveApplicationDAO = (LeaveApplicationInfoDAO) springContext
				.getBean("LeaveApplicationInfoDAO");

		// 获得fire workflow的runtimeContext
		final RuntimeContext runtimeContext = (RuntimeContext) springContext
				.getBean("runtimeContext");

		// 获得当前用户
		final User currentUser = (User) req.getSession(true).getAttribute(
				"CURRENT_USER");

		// 保存请假信息，并启动流程实例
		transactionTemplate.execute(new TransactionCallback() {

			@Override
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					// 1、保存请假信息
					leaveApplicationDAO.save(leaveApplicationInfo);

					// 2、创建流程实例
					IWorkflowSession workflowSession = runtimeContext
							.getWorkflowSession();

					IProcessInstance processInstance = workflowSession
							.createProcessInstance("LeaveApplicationProcess",
									currentUser == null ? "--" : currentUser
											.getId());

					// 3、设置流程变量
					processInstance.setProcessInstanceVariable("sn",
							leaveApplicationInfo.getSn());
					processInstance.setProcessInstanceVariable("applicantId",
							leaveApplicationInfo.getApplicantId());
					processInstance.setProcessInstanceVariable("leaveDays",
							leaveApplicationInfo.getLeaveDays());

					// 4、启动流程实例
					processInstance.run();
				} catch (EngineException e) {
					e.printStackTrace();
					req.setAttribute("ERR", "错误: "+e.getMessage());
				} catch (KernelException e) {
					e.printStackTrace();
					req.setAttribute("ERR", "错误: "+e.getMessage());
				}
				return null;
			}

		});
		req.setAttribute("INFO", "保存成功");
		RequestDispatcher dispatcher = req
				.getRequestDispatcher("/org/fireflow/example/leaveapplication/bizpages/SubmitApplication.jsp");
		dispatcher.forward(req, resp);
	}

	/**
	 * 构建请假信息POJO
	 * @param req
	 * @return
	 */
	private LeaveApplicationInfo buildLeaveApplicationInfo(
			HttpServletRequest req) {
		LeaveApplicationInfo leaveApplicationInfo = new LeaveApplicationInfo();
		User currentUser = (User) req.getSession(true).getAttribute(
				"CURRENT_USER");
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dformat2 = new SimpleDateFormat("yyyyMMdd-HHmmss");
		leaveApplicationInfo.setSn(dformat2.format(new Date()));
		
		leaveApplicationInfo.setApplicantId(currentUser == null ? "--"
				: currentUser.getId());		
		leaveApplicationInfo.setApplicantName(currentUser == null ? "--"
				: currentUser.getName());
		leaveApplicationInfo.setLeaveReason(req.getParameter("leaveReason"));
		leaveApplicationInfo.setFromDate(req.getParameter("fromDate"));
		leaveApplicationInfo.setToDate(req.getParameter("toDate"));
		leaveApplicationInfo.setLeaveDays(new Integer(req
				.getParameter("leaveDays")));
		leaveApplicationInfo.setSubmitTime(dformat.format(new Date()));

		return leaveApplicationInfo;
	}

	private WebApplicationContext getSpringContext() {
		ServletContext application = getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(application);
		return ctx;
	}
}
