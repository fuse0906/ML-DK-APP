<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="com.github.ytsejam5.dk.Utils" %>
<%@ page import="com.github.ytsejam5.dk.data.DonburiBean" %>
<%@ page import="com.github.ytsejam5.dk.data.KanjoBean" %>
<%@ include file="_labels.jsp" %>
<%	String documentURI = (String)request.getAttribute("documentURI");
	DonburiBean donburi = (DonburiBean)request.getAttribute("donburi"); %>
	<header>
		<h1>どんぶり勘定システム<small>(MarkLogic Java API利用版)</small></h1>
		<nav class="ink-navigation">
			<ul class="menu horizontal black">
				<li><a href="app">どんぶり検索</a></li>
				<li class="active"><a href="app?command=create-form">どんぶり登録</a></li>
			</ul>
		</nav>
	</header>

	<div class="column-group">
		<h2>どんぶり<%= Utils.isBlank(documentURI) ? "登録" : "詳細"%></h2>
		<hr/>
		<div class="all-25" id="left-container">
			&nbsp;
		</div>
		<div class="all-75" id="right-container">
<%	Exception e = (Exception)request.getAttribute("error");
	if (e != null){ %>
            <div class="ink-alert basic error" role="alert">
                <p><b>Error:</b> <%= Utils.xmlEncode(e.getMessage()) %></p>
            </div>
            <br/>
<%	} %>
			<form action="app" method="post" class="ink-form all-100">
				<input type="hidden" name="command" value="<%= Utils.isBlank(documentURI) ? "create" : "update"%>"/>
				<input type="hidden" name="uri" value="<%= Utils.xmlEncode(documentURI) %>"/>
                <fieldset>
					<div class="control-group column-group gutters">
					    <label for="name" class="all-20 align-right">どんぶり名称</label>
					    <div class="control all-80">
					        <input type="text" id="name" name="name" value="<%= donburi != null ? Utils.xmlEncode(donburi.getName()) : "" %>"/>
					    </div>
					    <label for="ingredient" class="all-20 align-right">主原料</label>
					    <div class="control all-80">
					        <input type="text" id="ingredient" name="ingredient" value="<%=  donburi != null ? Utils.xmlEncode(donburi.getIngredient()) : "" %>"/>
					    </div>
					    <label for="category" class="all-20 align-right">分類</label>
					    <div class="control all-80">
			           		<select id="category" name="category">
								<option value=""></option>
<%	for (Iterator categoryIterator = CATEGORY.iterator(); categoryIterator.hasNext(); ){
		String category = (String)categoryIterator.next(); %>
								<option value="<%= Utils.xmlEncode(category) %>"<%=  donburi != null && category.equals(donburi.getCategory()) ? " selected" : "" %>><%= Utils.xmlEncode(category) %></option>
<%	} %>
							</select>
					    </div>
					    <label for="description" class="all-20 align-right">説明</label>
					    <div class="control all-80">
					        <textarea id="description" name="description" rows="8"><%=  donburi != null ? Utils.xmlEncode(donburi.getDescription()) : "" %></textarea>
					    </div>
					    <hr class="all-100"/>
					    <div class="all-20 align-right">勘定情報</div>
						<div class="control-group all-80">
							<div class="control-group all-75">
		                        <div class="align-center">店舗名</div>
<%	List kanjoList = donburi != null ? donburi.getKanjo() : null;
	for (int i = 0; i < 3; i++){ %>
		                        <div class="control">
		                            <input type="text" name="shop<%= i %>" value="<%= kanjoList != null && i < kanjoList.size() ? ((KanjoBean)kanjoList.get(i)).getShop() : "" %>"/>
		                        </div>
<%	} %>
		                    </div>
							<div class="control-group all-25">
							    <div class="align-center">販売価格</div>
<%	for (int i = 0; i < 3; i++){ %>
							    <div class="control">
							        <input type="text" name="price<%= i %>" value="<%= kanjoList != null && i < kanjoList.size() ? ((KanjoBean)kanjoList.get(i)).getPrice() : "" %>"/>
							    </div>
<%	} %>
							</div>
						</div>

					</div>
				</fieldset>
<%	if (Utils.isBlank(documentURI)){ %>
				<button class="ink-button all-100">登録</button>
<% 	} else { %>
				<button class="ink-button all-100">修正</button>
			</form>
			<form action="app" method="post" class="ink-form all-100">
				<input type="hidden" name="command" value="delete"/>
				<input type="hidden" name="uri" value="<%= Utils.xmlEncode(documentURI) %>"/>
				<button class="ink-button all-100">削除</button>
<% 	} %>
			</form>
		</div>
	</div>