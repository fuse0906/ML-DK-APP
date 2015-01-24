package com.github.ytsejam5.dk;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Action {

	protected ServletConfig config = null;

	public Action(ServletConfig config) {
		this.config = config;
	}

	public abstract ActionForward doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
}
