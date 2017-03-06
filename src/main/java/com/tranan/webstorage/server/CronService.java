package com.tranan.webstorage.server;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class CronService extends HttpServlet {
	
	DataServiceImpl data_service = new DataServiceImpl();

	public void init() throws ServletException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		data_service.checkSalePlan();
	}

	public void destroy() {
	}
}