package org.fireflow.example;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fireflow.example.ou.OUManagementMock;
import org.fireflow.example.ou.User;

public class LoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doLogin(req,resp);
	}

	/*
	 * 登陆系统
	 */
	public void doLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userId = null;
		String userPwd = null;

		userId = request.getParameter("userId");
		userPwd = request.getParameter("userPwd");

		OUManagementMock ouMgr = OUManagementMock.getInstance();
		User currentUser = ouMgr.checkUser(userId, userPwd);

		HttpSession httpSession = request.getSession(true);

		if (currentUser != null) {
			httpSession.setAttribute("CURRENT_USER", currentUser);

			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/main.jsp");
			dispatcher.forward(request, response);
		} else {
			request.setAttribute("LOGIN_ERROR", "用户名或者密码错误。");
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/login.jsp");
			dispatcher.forward(request, response);
		}
	}
}
