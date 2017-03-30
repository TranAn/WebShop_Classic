package com.tranan.webstorage.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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