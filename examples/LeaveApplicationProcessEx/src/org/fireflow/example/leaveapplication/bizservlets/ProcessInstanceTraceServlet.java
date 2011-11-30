package org.fireflow.example.leaveapplication.bizservlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fireflow.example.leaveapplication.data.LeaveApplicationInfoDAO;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ProcessInstanceTraceServlet extends HttpServlet {
	protected void doPost(final HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		// 获得spring bean factory
		final WebApplicationContext springContext = this.getSpringContext();

		// 获得事务模板
		final TransactionTemplate transactionTemplate = (TransactionTemplate) springContext
				.getBean("transactionTemplate");

		// 获得请假信息的DAO
		final LeaveApplicationInfoDAO leaveApplicationDAO = (LeaveApplicationInfoDAO) springContext
				.getBean("LeaveApplicationInfoDAO");

		String applicantName = req.getParameter("applicant");
		List taskInstanceList = leaveApplicationDAO
				.findTaskInstanceListByApplicantName(applicantName);
		req.setAttribute("TASKINSTANCE_LIST", taskInstanceList);

		RequestDispatcher dispatcher = req
				.getRequestDispatcher("/org/fireflow/example/leaveapplication/bizpages/ProcessInstanceTrace.jsp");
		dispatcher.forward(req, resp);
	}

	private WebApplicationContext getSpringContext() {
		ServletContext application = getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(application);
		return ctx;
	}
}
