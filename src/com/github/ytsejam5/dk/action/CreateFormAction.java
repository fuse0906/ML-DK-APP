package com.github.ytsejam5.dk.action;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ytsejam5.dk.Action;
import com.github.ytsejam5.dk.ActionForward;

public class CreateFormAction extends Action {

	public CreateFormAction(ServletConfig config) {
		super(config);
	}

	@Override
	public ActionForward doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		return new ActionForward("/WEB-INF/jsp/form.jsp", false);
	}

}
