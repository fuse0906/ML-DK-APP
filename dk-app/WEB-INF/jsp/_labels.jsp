<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%!
	public static Map<String, String> FACET_LABEL = new HashMap<String, String>();
	static {
		FACET_LABEL.put("ingredient", "主原料");
		FACET_LABEL.put("category", "カテゴリ");
	}
	
	public static List<String> CATEGORY = new LinkedList<String>();
	static {
		CATEGORY.add("王道");
		CATEGORY.add("さっぱり系");
		CATEGORY.add("こってり系");
		CATEGORY.add("キワモノ");
	}
	
	public static Map<String, String> AGGREGATE_NAME_LABEL = new HashMap<String, String>();
	static {
		AGGREGATE_NAME_LABEL.put("count", "集計対象件数");
		AGGREGATE_NAME_LABEL.put("min", "最安値 (円)");
		AGGREGATE_NAME_LABEL.put("max", "最高値 (円)");
		AGGREGATE_NAME_LABEL.put("avg", "平均値 (円)");
		AGGREGATE_NAME_LABEL.put("median", "中央値 (円)");
		AGGREGATE_NAME_LABEL.put("sum", "総合計値 (円)");
		AGGREGATE_NAME_LABEL.put("stddev", "標準偏差");
		AGGREGATE_NAME_LABEL.put("stddev-population", "母標準偏差");
		AGGREGATE_NAME_LABEL.put("variance", "分散");
		AGGREGATE_NAME_LABEL.put("variance-population", "母分散");
	}
	
	public static Map<String, String> SORT_LABEL = new LinkedHashMap<String, String>();
	static {
		SORT_LABEL.put("score", "スコア順");
		SORT_LABEL.put("name-asc", "名前順 (昇順)");
		SORT_LABEL.put("name-desc", "名前順 (降順)");
		SORT_LABEL.put("created-asc", "登録日順 (昇順)");
		SORT_LABEL.put("created-desc", "登録日順 (降順)");
		SORT_LABEL.put("updated-asc", "更新日順 (昇順)");
		SORT_LABEL.put("updated-desc", "更新日順 (降順)");
	}
%>