package com.github.ytsejam5.dk;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionForwardProcessor extends HttpServlet {

	private static final long serialVersionUID = 1056251146877648616L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doProccess(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doProccess(request, response);
	}

	private void doProccess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String forwardURL = (String) request.getAttribute("forwardURL");

		RequestDispatcher rd = request.getRequestDispatcher(forwardURL);
		rd.include(request, response);
	}
}
