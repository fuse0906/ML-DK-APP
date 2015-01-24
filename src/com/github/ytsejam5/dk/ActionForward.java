package com.github.ytsejam5.dk;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionForward {

	private String url = null;
	private boolean isRedirect = false;

	public ActionForward(String url, boolean isRedirect) {
		this.url = url;
		this.isRedirect = isRedirect;
	}

	public void forward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (isRedirect) {
			response.sendRedirect(url);

		} else {
			request.setAttribute("forwardURL", url);

			RequestDispatcher rd = request
					.getRequestDispatcher("/WEB-INF/jsp/template.jsp");
			rd.forward(request, response);
		}
	}
}
