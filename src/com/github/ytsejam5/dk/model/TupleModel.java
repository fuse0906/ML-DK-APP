package com.github.ytsejam5.dk.model;

import java.util.LinkedList;
import java.util.List;

public class TupleModel {

	private List<String> valueList = new LinkedList<String>();
	private long count = -1;

	public List<String> getValueList() {
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
