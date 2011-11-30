package org.fireflow.jsf;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FireflowJsfResourceLoader extends HttpServlet {
	public static final String Resource_Servlet_Name = "/org-fireflow-jsf-resource";
	public static final String Resource_Path = "resourcePath";

	// public void doFilter(ServletRequest request, ServletResponse response,
	// FilterChain chain) throws IOException, ServletException {
	// if (!(response instanceof HttpServletResponse)) {
	// //If this is a portlet request, just continue the chaining
	// chain.doFilter(request, response);
	// return;
	// }
	//
	// HttpServletResponse httpResponse = (HttpServletResponse) response;
	// HttpServletRequest httpRequest = (HttpServletRequest) request;
	//        
	// String url = httpRequest.getRequestURI();
	// int idx = url.indexOf()
	// this.getClass().getClassLoader().getResourceAsStream(arg0)
	//
	// }
	public void service(javax.servlet.ServletRequest req,
			javax.servlet.ServletResponse res)
			throws javax.servlet.ServletException, java.io.IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) res;
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		String uri = httpRequest.getParameter(Resource_Path);
		System.out.println("uri is "+uri);
		if (uri == null)
			return;
		InputStream in = this.getClass().getResourceAsStream(uri);
		writeResource(httpRequest,httpResponse,in);
	}

	protected void writeResource(HttpServletRequest request,
			HttpServletResponse response, InputStream in) throws IOException {
		ServletOutputStream out = response.getOutputStream();
		try {
			byte[] buffer = new byte[1024];
			for (int size = in.read(buffer); size != -1; size = in.read(buffer)) {
				out.write(buffer, 0, size);
			}
			out.flush();
		} catch (IOException e) {

		}
	}
}
