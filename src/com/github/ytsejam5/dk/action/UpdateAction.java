package com.github.ytsejam5.dk.action;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.github.ytsejam5.dk.Action;
import com.github.ytsejam5.dk.ActionForward;
import com.github.ytsejam5.dk.Utils;
import com.github.ytsejam5.dk.data.DonburiBean;
import com.github.ytsejam5.dk.data.KanjoBean;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;

public class UpdateAction extends Action {

	public UpdateAction(ServletConfig config) {
		super(config);
	}

	@Override
	public ActionForward doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String documentURI = request.getParameter("uri");
		String name = request.getParameter("name");
		String ingredient = request.getParameter("ingredient");
		String category = request.getParameter("category");
		String description = request.getParameter("description");

		List<KanjoBean> kanjoList = new LinkedList<KanjoBean>();
		for (int i = 0; i < 3; i++) {
			if (!Utils.isBlank(request.getParameter("price" + i)) && request.getParameter("price" + i).matches("\\d+")) {
				KanjoBean bean = new KanjoBean();
				bean.setShop(request.getParameter("shop" + i));
				bean.setPrice(Integer.parseInt(request.getParameter("price" + i)));
				kanjoList.add(bean);
			}
		}

		DonburiBean donburi = null;

		DatabaseClient client = null;
		Transaction transaction = null;

		try {
			client = DatabaseClientFactory.newClient(
					config.getInitParameter("ML_HOST") /* localhost */,
					Integer.parseInt(config.getInitParameter("ML_PORT") /* 8080 */),
					config.getInitParameter("ML_USERNAME") /* admin */,
					config.getInitParameter("ML_PASSWORD") /* password */,
					Authentication.valueOf(config.getInitParameter("ML_AUTHTYPE") /* digest */));

			/*** 更新
			transaction = client.openTransaction();

			XMLDocumentManager documentManager = client.newXMLDocumentManager();
			JAXBContext context = JAXBContext.newInstance(DonburiBean.class);
			JAXBHandle<DonburiBean> documentHandle = new JAXBHandle<DonburiBean>(context);

			donburi = (DonburiBean) documentManager.read(documentURI, documentHandle, transaction).get();
			if (donburi == null) {
				throw new IllegalStateException("Document not found. [ " + documentURI + " ]");
			}

			donburi.setName(name);
			donburi.setIngredient(ingredient);
			donburi.setCategory(category);
			donburi.setDescription(description);
			donburi.setUpdated(new Date());
			donburi.setKanjo(kanjoList);
			documentHandle.set(donburi);

			documentManager.write(documentURI, documentHandle, transaction);

			transaction.commit();

		} catch (JAXBException e) {
			transaction.rollback();

			throw new IllegalStateException(e);
			/***/
			
		} catch (RuntimeException e) {
			transaction.rollback();

			request.setAttribute("documentURI", documentURI);
			request.setAttribute("donburi", donburi);
			request.setAttribute("error", e);

			return new ActionForward("/WEB-INF/jsp/form.jsp", false);

		} finally {
			if (client != null) {
				client.release();
			}
		}

		return new ActionForward("app", true);
	}
}
