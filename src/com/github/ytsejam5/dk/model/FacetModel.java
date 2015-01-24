package com.github.ytsejam5.dk.model;

import java.util.List;

public class FacetModel {

	private String name = null;
	private List<FacetValueModel> facetValueList = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FacetValueModel> getFacetValueList() {
		return facetValueList;
	}

	public void setFacetValueList(List<FacetValueModel> facetValueList) {
		this.facetValueList = facetValueList;
	}

	public static class FacetValueModel {
		
		private String name = null;
		private String label = null;
		private long count = -1;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}
	}
}
