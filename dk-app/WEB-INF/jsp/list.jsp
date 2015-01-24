<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.github.ytsejam5.dk.Utils" %>
<%@ page import="com.github.ytsejam5.dk.data.DonburiBean" %>
<%@ page import="com.github.ytsejam5.dk.model.AggregateResultModel" %>
<%@ page import="com.github.ytsejam5.dk.model.FacetModel" %>
<%@ page import="com.github.ytsejam5.dk.model.FacetModel.FacetValueModel" %>
<%@ page import="com.github.ytsejam5.dk.model.PaginationModel" %>
<%@ page import="com.github.ytsejam5.dk.model.SearchMetricsModel" %>
<%@ page import="com.github.ytsejam5.dk.model.SearchResultRowModel" %>
<%@ page import="com.github.ytsejam5.dk.model.SearchResultRowModel.ScoreModel" %>
<%@ page import="com.github.ytsejam5.dk.model.TupleModel" %>
<%@ include file="_labels.jsp" %>
<%
	String query = (String)request.getAttribute("query");
	String sort = (String)request.getAttribute("sort");
%>
	<header>
		<h1>どんぶり勘定システム<small>(MarkLogic Java API利用版)</small></h1>
		<nav class="ink-navigation">
			<ul class="menu horizontal black">
				<li class="active"><a href="app">どんぶり検索</a></li>
				<li><a href="app?command=create-form">どんぶり登録</a></li>
			</ul>
		</nav>
	</header>

	<div class="column-group">
		<h2>どんぶり検索</h2>
		<hr/>
		
		<div class="all-25" id="left-container">
<%	List<FacetModel> facetModelList = (List<FacetModel>)request.getAttribute("facetModelList");
	if (facetModelList != null){
		for (Iterator facetIterator = facetModelList.iterator(); facetIterator.hasNext(); ){
			FacetModel facet = (FacetModel)facetIterator.next();
			
			String facetName = facet.getName();
			String facetLabel = (String)FACET_LABEL.get(facetName); %>
			<dl>
				<dt><%= Utils.xmlEncode(facetLabel) %></dt>
<%			List<FacetValueModel> facetValueList = (List<FacetValueModel>)facet.getFacetValueList();
			for (Iterator facetValueIterator = facetValueList.iterator(); facetValueIterator.hasNext(); ){
				FacetValueModel facetValue = (FacetValueModel)facetValueIterator.next();
				String facetValueLabel = facetValue.getLabel();
				long facetValueCount = facetValue.getCount();
				
				String constraint = facetName + ":\"" + facetValueLabel + "\"";
				String facetQuery = query.contains(constraint) ? query : (Utils.isBlank(query) ? "" : query + " ") + constraint; %>
				<dd><a href="<%= "app?q=" + Utils.urlEncode(facetQuery) %>"><%= Utils.xmlEncode(facetValueLabel) %></a> (<%= facetValueCount %>)</dd>
<%			} %>
			</dl>
<%		}
	}
	
	List<TupleModel> ｃooccurrenceList = (List<TupleModel>)request.getAttribute("categoryIngredientCooccurrenceModelList");
	if (ｃooccurrenceList != null){ %>
			<hr/>
			<dl>
				<dt>カテゴリ傾向 (共起)</dt>
<%		for (Iterator ｃooccurrenceIterator = ｃooccurrenceList.iterator(); ｃooccurrenceIterator.hasNext(); ){
			TupleModel ｃooccurrence = (TupleModel)ｃooccurrenceIterator.next();
	
			List<String> valueList = ｃooccurrence.getValueList();
			String label = Utils.join(valueList, " - ");
			long count = ｃooccurrence.getCount(); %>
				<dd><%= Utils.xmlEncode(label) %> (<%= count %>)</dd>
<%		} %>
			</dl>
<%	} 

	List<AggregateResultModel> aggregatePriceList = (List<AggregateResultModel>)request.getAttribute("aggregatePriceModelList");
	if (aggregatePriceList != null){ %>
			<hr/>
			<dl>
				<dt>価格情報</dt>
<%		for (Iterator aggregatePriceIterator = aggregatePriceList.iterator(); aggregatePriceIterator.hasNext(); ){
			AggregateResultModel aggregatePrice = (AggregateResultModel)aggregatePriceIterator.next();
			
			String name = aggregatePrice.getName();
			String label = (String)AGGREGATE_NAME_LABEL.get(name);
			String value = aggregatePrice.getValue(); %>
				<dd><%= Utils.xmlEncode(label) %>: <%= Utils.xmlEncode(value) %></dd>
<%		} %>
			</dl>
<%	} %>
			&nbsp;
		</div>
	
		<div class="all-75" id="right-container">
<%	Exception e = (Exception)request.getAttribute("error");
	if (e != null){ %>
            <div class="ink-alert basic error" role="alert">
                <p><b>Error:</b> <%= Utils.xmlEncode(e.getMessage()) %></p>
            </div>
<%	} %>
			<form action="app" method="get" class="ink-form">
				<fieldset>
				    <div class="column-group horizontal-gutters">
				        <div class="control-group all-100 push-center">
				            <div class="control append-button all-80" role="search">
				                <span><input type="text" name="q" id="q" value="<%= Utils.xmlEncode(query) %>"></span>
				                <button class="ink-button">Search</button>
				            </div>
				            <div class="align-right all-20" role="search">
<%	if ((Boolean)request.getAttribute("displaySortKeys") != null && ((Boolean)request.getAttribute("displaySortKeys")).booleanValue()){ %>
				           		<select id="sort" name="sort">
									<option value="">-- 表示順 --</option>
<%		for (Iterator sortKeyIterator = SORT_LABEL.keySet().iterator(); sortKeyIterator.hasNext(); ){
			String sortKey = (String)sortKeyIterator.next();
			String sortLabel = (String)SORT_LABEL.get(sortKey); %>
									<option value="<%= Utils.xmlEncode(sortKey) %>"<%= sortKey.equals(sort) ? " selected" : "" %>><%= Utils.xmlEncode(sortLabel) %></option>
<%		} %>
								</select>
<%	} %>
			       	 		</div>
			        	</div>
			    	</div>
			    </fieldset>
			</form>
<%	PaginationModel pagenation = (PaginationModel)request.getAttribute("paginationModel");
	if (pagenation != null){ %>
			<div class="all-100 align-center">該当件数 <%= pagenation.getTotalResult() %> 件中 <%= pagenation.getStart() %> 件目から <%= pagenation.getEnd() %> 件目を表示</div>
			
			<nav class="ink-navigation">
			    <ul class="pagination grey">
			    	<% if (pagenation.hasPrev()){ %>
			        <li class="all-25"><a href="<%= "app?q=" + Utils.urlEncode(query) + "&start=" + pagenation.getPrevStart() %>" class="align-center all-100">Previous</a></li>
			        <% } else { %>
			        <li class="all-25 disabled"><a href="#" class="align-center all-100">Previous</a></li>
			        <% } %>
			        <li class="all-50 disabled"><a href="#">&nbsp;</a></li>
			        <% if (pagenation.hasNext()){ %>
			        <li class="all-25"><a href="<%= "app?q=" + Utils.urlEncode(query) + "&start=" + pagenation.getNextStart() %>" class="align-center all-100">Next</a></li>
			        <% } else { %>
			        <li class="all-25 disabled"><a href="#" class="align-center all-100">Next</a></li>
			        <% } %>
			    </ul>
			</nav>
			
			<br/>
<%	}
	
	List<SearchResultRowModel> searchResultList = (List<SearchResultRowModel>)request.getAttribute("searchResultRowModelList");
	String snippetType = (String)request.getAttribute("snippetType");
	if (searchResultList != null){ %>
			<table class="ink-table alternating">
			    <thead>
<%		if (snippetType.equals("raw")){ %> 
			        <tr>
			            <th class="align-center" nowrap>名称</th>
			            <th class="align-center" nowrap>主原料</th>
			            <th class="align-center" nowrap>分類</th>
			            <th class="align-center" nowrap>説明</th>
			            <th class="align-center" nowrap>登録店舗数</th>
			            <th class="align-center" nowrap>登録日</th>
			            <th class="align-center" nowrap>更新日</th>
			        </tr>
<%		} else { %>
			        <tr>
			            <th class="align-center" nowrap>名称</th>
			            <th class="align-center" nowrap>該当結果</th>
			        </tr>
<%		}  %>

			    </thead>
			    <tbody>
<%		for (Iterator iterator = searchResultList.iterator(); iterator.hasNext(); ){
			SearchResultRowModel searchResultRow = (SearchResultRowModel)iterator.next();
			DonburiBean donburi = (DonburiBean)searchResultRow.getData();
			if (snippetType.equals("raw")){
				%>
			    	<tr class="search-result-row">
			    		<td nowrap><a href="app?command=retrieve&uri=<%= Utils.urlEncode(donburi.getDocumentURI()) %>"><%= Utils.xmlEncode(donburi.getName()) %></a></td>
			    		<td nowrap><%= Utils.xmlEncode(donburi.getIngredient()) %></td>
			    		<td nowrap><%= Utils.xmlEncode(donburi.getCategory()) %></td>
			    		<td><%= Utils.trim(Utils.xmlEncode(donburi.getDescription()), 24) %></td>		    	
			    		<td class="align-right"><span class="ink-badge grey"><%= donburi.getKanjo() != null ? donburi.getKanjo().size() : 0 %></span></td>
			    		<td class="align-center"><%= Utils.formatDate(donburi.getCreated()) %></td>
			    		<td class="align-center"><%= Utils.formatDate(donburi.getUpdated()) %></td>
			    	</tr>
<%			} else { %>
			    	<tr class="search-result-row">
			    		<td nowrap><a href="app?command=retrieve&uri=<%= Utils.urlEncode(donburi.getDocumentURI()) %>"><%= Utils.xmlEncode(donburi.getName()) %></a></td>
			    		<td>
<%				List<String> snippetTextList = searchResultRow.getSnippetTextList();
				if (snippetTextList != null){
					String snippet = Utils.join(snippetTextList, "...");
					out.print(Utils.encodeHTMLAndHighlight(snippet, "strong")); %>
						<br/>
			    		<br/>
<%				}

				ScoreModel score = searchResultRow.getScoreModel();
				if (score != null){ %>
			    		スコア:<%= score.getScore() %> - 適合度:<%= score.getFitness() %> - 信頼度:<%= score.getConfidence() %><br/>
<%				}

				List<DonburiBean> similarDonburiList = searchResultRow.getSimilarDonburiList();
				if (similarDonburiList != null){ %>
			    		類似ドンブリ: 
<%					for (Iterator<DonburiBean> similarDonburiIterator = similarDonburiList.iterator(); similarDonburiIterator.hasNext(); ){
						DonburiBean similarDonburi = (DonburiBean)similarDonburiIterator.next();
						String similarDonburiURI = similarDonburi.getDocumentURI();
						String similarDonburiName = similarDonburi.getName(); %>
						<a href="app?command=retrieve&uri=<%= Utils.urlEncode(similarDonburiURI) %>"><%= Utils.xmlEncode(similarDonburiName) %></a>					 
						<%= similarDonburiIterator.hasNext() ? " | " : "" %>
<%					} %>
							<br/>
<%				} %>

			    		</td>
			    	</tr>
<%			}
		} %>
			    </tbody>
			</table>
<%	}

	if (pagenation != null){ %>
			<nav class="ink-navigation">
			    <ul class="pagination grey">
			    	<% if (pagenation.hasPrev()){ %>
			        <li class="all-25"><a href="<%= "app?q=" + Utils.urlEncode(query) + "&start=" + pagenation.getPrevStart() %>" class="align-center all-100">Previous</a></li>
			        <% } else { %>
			        <li class="all-25 disabled"><a href="#" class="align-center all-100">Previous</a></li>
			        <% } %>
			        <li class="all-50 disabled"><a href="#">&nbsp;</a></li>
			        <% if (pagenation.hasNext()){ %>
			        <li class="all-25"><a href="<%= "app?q=" + Utils.urlEncode(query) + "&start=" + pagenation.getNextStart() %>" class="align-center all-100">Next</a></li>
			        <% } else { %>
			        <li class="all-25 disabled"><a href="#" class="align-center all-100">Next</a></li>
			        <% } %>
			    </ul>
			</nav>
<%	}
	
	SearchMetricsModel metrics = (SearchMetricsModel)request.getAttribute("searchMetricsModel");
	if (metrics != null){ %>
			<div class="all-100 align-right">
				クエリ解析時間:<%= metrics.getQueryResolutionTime() %>(ms) -
				ファセット解析時間:<%= metrics.getFacetResolutionTime() %>(ms) -
				スニペット解析時間:<%= metrics.getSnippetResolutionTime() %>(ms) -
				総処理時間:<%= metrics.getTotalTime() %>(ms)
			</div>
<%	} %>
		</div>
	</div>