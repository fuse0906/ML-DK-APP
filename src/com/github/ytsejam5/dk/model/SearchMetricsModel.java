package com.github.ytsejam5.dk.model;

public class SearchMetricsModel {

	private long totalTime = -1;
	private long queryResolutionTime = -1;
	private long facetResolutionTime = -1;
	private long snippetResolutionTime = -1;

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public long getQueryResolutionTime() {
		return queryResolutionTime;
	}

	public void setQueryResolutionTime(long queryResolutionTime) {
		this.queryResolutionTime = queryResolutionTime;
	}

	public long getFacetResolutionTime() {
		return facetResolutionTime;
	}

	public void setFacetResolutionTime(long facetResolutionTime) {
		this.facetResolutionTime = facetResolutionTime;
	}

	public long getSnippetResolutionTime() {
		return snippetResolutionTime;
	}

	public void setSnippetResolutionTime(long snippetResolutionTime) {
		this.snippetResolutionTime = snippetResolutionTime;
	}
}
