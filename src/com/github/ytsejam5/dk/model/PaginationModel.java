package com.github.ytsejam5.dk.model;

public class PaginationModel {

	private long start = -1;
	private long pageLength = -1;
	private long totalResult = -1;

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getPageLength() {
		return pageLength;
	}

	public void setPageLength(long pageLength) {
		this.pageLength = pageLength;
	}

	public long getEnd() {
		return Math.min(totalResult, start + pageLength - 1);
	}

	public long getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(long totalResult) {
		this.totalResult = totalResult;
	}

	public long getPrevStart() {
		return Math.max(1, start - pageLength);
	}

	public long getNextStart() {
		return Math.min(totalResult, start + pageLength);
	}

	public boolean hasPrev() {
		return start > 1;
	}

	public boolean hasNext() {
		return start + pageLength - 1 < totalResult;
	}
}
