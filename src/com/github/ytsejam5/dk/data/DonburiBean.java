package com.github.ytsejam5.dk.data;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DonburiBean {

	private String documentURI = null;
	private String name = null;
	private String ingredient = null;
	private String category = null;
	private String description = null;
	private Date created = null;
	private Date updated = null;

	private List<KanjoBean> kanjo = null;

	public String getDocumentURI() {
		return documentURI;
	}

	public void setDocumentURI(String documentURI) {
		this.documentURI = documentURI;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public List<KanjoBean> getKanjo() {
		return kanjo;
	}

	public void setKanjo(List<KanjoBean> kanjo) {
		this.kanjo = kanjo;
	}

}
