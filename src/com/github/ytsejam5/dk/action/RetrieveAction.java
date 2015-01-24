package com.github.ytsejam5.dk.action;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.github.ytsejam5.dk.Action;
import com.github.ytsejam5.dk.ActionForward;
import com.github.ytsejam5.dk.data.DonburiBean;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;

public class RetrieveAction extends Action {

	public RetrieveAction(ServletConfig config) {
		super(config);
	}

	@Override
	public ActionForward doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String documentURI = request.getParameter("uri");

		DatabaseClient client = null;

		try {
			client = DatabaseClientFactory.newClient(
					config.getInitParameter("ML_HOST") /* localhost */,
					Integer.parseInt(config.getInitParameter("ML_PORT") /* 8080 */),
					config.getInitParameter("ML_USERNAME") /* admin */,
					config.getInitParameter("ML_PASSWORD") /* password */,
					Authentication.valueOf(config.getInitParameter("ML_AUTHTYPE") /* digest */));

			/*** 参照
			XMLDocumentManager documentManager = client.newXMLDocumentManager();
			JAXBContext context = JAXBContext.newInstance(DonburiBean.class);
			JAXBHandle<DonburiBean> documentHandle = new JAXBHandle<DonburiBean>(context);

			DonburiBean donburi = (DonburiBean) documentManager.read(documentURI, documentHandle).get();
			if (donburi == null) {
				throw new IllegalStateException("Document not found. [ " + documentURI + " ]");
			}

			donburi.setDocumentURI(documentURI);

			request.setAttribute("documentURI", documentURI);
			request.setAttribute("donburi", donburi);

		} catch (JAXBException e) {
			throw new IllegalStateException(e);
			/***/

		} catch (RuntimeException e) {
			request.setAttribute("documentURI", documentURI);
			request.setAttribute("error", e);
			return new ActionForward("/WEB-INF/jsp/form.jsp", false);

		} finally {
			if (client != null) {
				client.release();
			}
		}

		return new ActionForward("/WEB-INF/jsp/form.jsp", false);
	}

}
