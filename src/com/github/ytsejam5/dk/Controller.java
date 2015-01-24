package com.github.ytsejam5.dk;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ytsejam5.dk.action.CreateAction;
import com.github.ytsejam5.dk.action.CreateFormAction;
import com.github.ytsejam5.dk.action.DeleteAction;
import com.github.ytsejam5.dk.action.ListAction;
import com.github.ytsejam5.dk.action.RetrieveAction;
import com.github.ytsejam5.dk.action.UpdateAction;

public class Controller extends HttpServlet {

	private static final long serialVersionUID = 6784183103362883686L;

	public static Map<String, Class<? extends Action>> ROUTE = new HashMap<String, Class<? extends Action>>();
	static {
		ROUTE.put("create", CreateAction.class);
		ROUTE.put("create-form", CreateFormAction.class);
		ROUTE.put("retrieve", RetrieveAction.class);
		ROUTE.put("update", UpdateAction.class);
		ROUTE.put("delete", DeleteAction.class);
		ROUTE.put("list", ListAction.class);
	}

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
		ServletConfig config = getServletConfig();

		String command = request.getParameter("command");
		if (Utils.isBlank(command)) {
			command = "list";
		}

		Action action = null;

		try {
			Class<? extends Action> actionClass = (Class<? extends Action>) ROUTE.get(command);
			Constructor<? extends Action> constractor = actionClass.getConstructor(new Class[] { ServletConfig.class, });
			action = constractor.newInstance(new Object[] { config });

		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}

		if (action == null) {
			action = new ListAction(config);
		}

		ActionForward forward = action.doProcess(request, response);
		forward.forward(request, response);
		
		response.getOutputStream().flush();
	}
}
