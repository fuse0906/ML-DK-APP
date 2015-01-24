package com.github.ytsejam5.dk.action;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import com.github.ytsejam5.dk.Action;
import com.github.ytsejam5.dk.ActionForward;
import com.github.ytsejam5.dk.Utils;
import com.github.ytsejam5.dk.data.DonburiBean;
import com.github.ytsejam5.dk.model.AggregateResultModel;
import com.github.ytsejam5.dk.model.FacetModel;
import com.github.ytsejam5.dk.model.PaginationModel;
import com.github.ytsejam5.dk.model.SearchMetricsModel;
import com.github.ytsejam5.dk.model.SearchResultRowModel;
import com.github.ytsejam5.dk.model.TupleModel;
import com.github.ytsejam5.dk.model.FacetModel.FacetValueModel;
import com.github.ytsejam5.dk.model.SearchResultRowModel.ScoreModel;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.FacetResult;
import com.marklogic.client.query.FacetValue;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.SearchMetrics;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.TypedDistinctValue;
import com.marklogic.client.query.ValuesDefinition;

public class ListAction extends Action {

	private static String SNIPPET_TYPE = "snippet"; /* スニペットの返却タイプ  "snippet" or "raw" */
	
	
	private static int PAGE_LENGTH = 10;
	private static boolean SIMILAR_QUERY_ENABLED = !SNIPPET_TYPE.equals("raw"); /* 類似ドキュメント検索のON or OFF。スニペットの返却タイプ "raw" と同居できません。 */

	public ListAction(ServletConfig config) {
		super(config);
	}

	@Override
	public ActionForward doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String query = request.getParameter("q");
		String sort = request.getParameter("sort");

		query = Utils.isBlank(query) ? "" : query.replaceAll("　", " "); /* full-width space -> half-wide space */
		query = !Utils.isBlank(sort) ? query.replaceAll("\\s*sort:\\S+", "") + " sort:" + sort : query;
		request.setAttribute("query", query);

		sort = Utils.isBlank(sort) ? "" : sort;
		request.setAttribute("sort", sort);

		long start = !Utils.isBlank(request.getParameter("start")) && request.getParameter("start").matches("\\d+") ? Long .parseLong(request.getParameter("start")) : 1;
				
		request.setAttribute("snippetType", SNIPPET_TYPE);

		DatabaseClient client = null;

		try {
			client = DatabaseClientFactory.newClient(
					config.getInitParameter("ML_HOST") /* localhost */,
					Integer.parseInt(config.getInitParameter("ML_PORT") /* 8080 */),
					config.getInitParameter("ML_USERNAME") /* admin */,
					config.getInitParameter("ML_PASSWORD") /* password */,
					Authentication.valueOf(config.getInitParameter("ML_AUTHTYPE") /* digest */));
			
			XMLDocumentManager documentManager = client.newXMLDocumentManager();
			JAXBContext context = JAXBContext.newInstance(DonburiBean.class);
			JAXBHandle<DonburiBean> recordHandle = new JAXBHandle<DonburiBean>(context);
			
			QueryDefinition queryDef = null;

			/*** 1. 検索 ***/
			QueryManager queryManager = client.newQueryManager();
			queryManager.setPageLength(PAGE_LENGTH);

			
			{
				/** 1.1 string query */
				StringQueryDefinition stringQueryDef = queryManager.newStringDefinition();
				stringQueryDef.setCriteria(query);
				
				queryDef = stringQueryDef;
				/**/
			}

			{
				/** 1.2 key-value query (string queryのブロックをコメントアウトし、当該ブロックのコメントを外します。)
				KeyValueQueryDefinition keyValueQueryDef = queryManager.newKeyValueDefinition();
				keyValueQueryDef.put(queryManager.newElementLocator(new QName("shop"), null), query);
				
				queryDef = keyValueQueryDef;
				/**/
			}
			
			{
				/** 1.3 structured query (key-value queryのブロックをコメントアウトし、当該ブロックのコメントを外します。) 
				StringHandle queryHandle = new StringHandle();
				queryHandle.setFormat(Format.XML);
	
				String structuredQueryAndOptions = Utils.resourceAsString(
						"/query-options.xml",
						new Object[] { Utils.xmlEncode(query), SNIPPET_TYPE, SIMILAR_QUERY_ENABLED });
				queryHandle.set(structuredQueryAndOptions);
	
				RawCombinedQueryDefinition rowCombinedQueryDef = queryManager.newRawCombinedQueryDefinition(queryHandle);
	
				request.setAttribute("displaySortKeys", Boolean.TRUE);
				
				queryDef = rowCombinedQueryDef;
				/**/
			}
			

			SearchHandle searchHandle = queryManager.search(queryDef, new SearchHandle(), start);

			MatchDocumentSummary[] results = searchHandle.getMatchResults();

			/*** 2. ページネーション 
			PaginationModel paginationModel = new PaginationModel();
			long totalResult = searchHandle.getTotalResults();

			paginationModel.setStart(Math.min(totalResult, start));
			paginationModel.setPageLength(PAGE_LENGTH);
			paginationModel.setTotalResult(totalResult);

			request.setAttribute("paginationModel", paginationModel);
			/***/
			
			List<SearchResultRowModel> searchResultRowModelList = new LinkedList<SearchResultRowModel>();

			for (int i = 0; i < results.length; i++) {
				SearchResultRowModel searchResultRow = new SearchResultRowModel();
				searchResultRowModelList.add(searchResultRow);

				String documentURI = results[i].getUri();
				
				/*** 3. POJOのリスト表示 (定数：SNIPPET_TYPEを"raw"に変更します。) ***/
				if (SNIPPET_TYPE.equals("raw")){
					DonburiBean donburi = results[i].getFirstSnippet(recordHandle).get();
					donburi.setDocumentURI(documentURI);
					searchResultRow.setData(donburi);
				
				} else {
					DonburiBean donburi = (DonburiBean) documentManager.read(documentURI, recordHandle).get();
					donburi.setDocumentURI(documentURI);
					searchResultRow.setData(donburi);
				}
				

				List<String> snippetTexts = new LinkedList<String>();
				searchResultRow.setSnippetTextList(snippetTexts);
				Document[] snippets = results[i].getSnippets();
				for (int j = 0; j < snippets.length; j++) {
					String snippetText = Utils.dom2string(snippets[j]);
					snippetTexts.add(snippetText);
				}

				/*** 4. スコアリング  (定数：SNIPPET_TYPEを"snippet"に戻します。)
				ScoreModel scoreModel = new ScoreModel();
				scoreModel.setScore(results[i].getScore());
				scoreModel.setFitness(results[i].getFitness());
				scoreModel.setConfidence(results[i].getConfidence());
				searchResultRow.setScoreModel(scoreModel);
				/***/

				/*** 5. 類似データ  (定数：SNIPPET_TYPEが"snippet"になっている必要があります。)
				List<DonburiBean> similarDonburis = new LinkedList<DonburiBean>();
				searchResultRow.setSimilarDonburiList(similarDonburis);
				String[] similarDocumentURIs = results[i].getSimilarDocumentUris();
				if (similarDocumentURIs != null) {
					for (int j = 0; j < similarDocumentURIs.length; j++) {
						if (documentURI.equals(similarDocumentURIs[j])) {
							continue; // skipping for same document
						}
						DonburiBean similarDonburi = (DonburiBean) documentManager.read(similarDocumentURIs[j], recordHandle).get();
						similarDonburi.setDocumentURI(similarDocumentURIs[j]);
						similarDonburis.add(similarDonburi);
					}
				}
				/***/
			}

			request.setAttribute("searchResultRowModelList", searchResultRowModelList);
			

			/*** 6. ファセット  
			List<FacetModel> facetModelList = new LinkedList<FacetModel>();

			FacetResult[] facetResults = searchHandle.getFacetResults();
			if (facetResults != null) {
				for (int i = 0; i < facetResults.length; i++) {
					FacetModel facet = new FacetModel();
					facetModelList.add(facet);

					String facetName = facetResults[i].getName();
					facet.setName(facetName);

					List<FacetValueModel> facetValues = new LinkedList<FacetValueModel>();
					facet.setFacetValueList(facetValues);

					FacetValue[] facetValuesResults = facetResults[i].getFacetValues();
					for (int j = 0; j < facetValuesResults.length; j++) {
						String facetValueName = facetValuesResults[j].getName();
						String facetValueLabel = facetValuesResults[j].getLabel();
						long facetValueCount = facetValuesResults[j].getCount();

						FacetModel.FacetValueModel facetValue = new FacetModel.FacetValueModel();
						facetValue.setName(facetValueName);
						facetValue.setLabel(facetValueLabel);
						facetValue.setCount(facetValueCount);

						facetValues.add(facetValue);
					}
				}
			}

			request.setAttribute("facetModelList", facetModelList);
			/***/

			/*** 7. 共起抽出 
			if (totalResult > 0) {
				ValuesDefinition vdef = queryManager.newValuesDefinition("category-ingredient");
				vdef.setFrequency(ValuesDefinition.Frequency.ITEM);
				vdef.setDirection(ValuesDefinition.Direction.DESCENDING);
				vdef.setQueryDefinition((RawCombinedQueryDefinition)queryDef);

				TuplesHandle tupleResults = queryManager.tuples(vdef, new TuplesHandle());

				List<TupleModel> categoryIngredientCooccurrenceModelList = new LinkedList<TupleModel>();

				Tuple[] tuples = tupleResults.getTuples();
				for (int i = 0; i < tuples.length; i++) {
					TupleModel cooccurrenceModel = new TupleModel();
					categoryIngredientCooccurrenceModelList.add(cooccurrenceModel);

					TypedDistinctValue[] tupleValueResult = tuples[i].getValues();

					List<String> tupleValues = new LinkedList<String>();
					cooccurrenceModel.setValueList(tupleValues);
					for (int j = 0; j < tupleValueResult.length; j++) {
						String tupleValue = tupleValueResult[j].get(String.class);
						tupleValues.add(tupleValue);
					}

					long tupleCount = tuples[i].getCount();
					cooccurrenceModel.setCount(tupleCount);
				}

				request.setAttribute("categoryIngredientCooccurrenceModelList", categoryIngredientCooccurrenceModelList);
			}
			/***/

			/*** 8. アグリゲーション
			if (totalResult > 0) {
				ValuesDefinition vdef = queryManager.newValuesDefinition("price");
				vdef.setAggregate(new String[] { "count", "min", "max",
						"median", "avg", "sum", "stddev", "stddev-population",
						"variance", "variance-population" });

				vdef.setFrequency(ValuesDefinition.Frequency.ITEM);
				vdef.setDirection(ValuesDefinition.Direction.ASCENDING);
				vdef.setQueryDefinition((RawCombinedQueryDefinition)queryDef);

				ValuesHandle aggregateValue = queryManager.values(vdef, new ValuesHandle());

				List<AggregateResultModel> aggregatePriceModelList = new LinkedList<AggregateResultModel>();

				AggregateResult[] aggregateResult = aggregateValue.getAggregates();
				for (int i = 0; i < aggregateResult.length; i++) {
					String name = aggregateResult[i].getName();
					String value = aggregateResult[i].getValue();

					AggregateResultModel aggregatePriceModel = new AggregateResultModel();
					aggregatePriceModelList.add(aggregatePriceModel);

					aggregatePriceModel.setName(name);
					aggregatePriceModel.setValue(value);
				}

				request.setAttribute("aggregatePriceModelList", aggregatePriceModelList);
			}
			/***/

			/*** 9. メトリクス
			SearchMetrics metrics = searchHandle.getMetrics();
			long totalTime = metrics.getTotalTime();
			long queryResolutionTime = metrics.getQueryResolutionTime();
			long facetResolutionTime = metrics.getFacetResolutionTime();
			long snippetResolutionTime = metrics.getSnippetResolutionTime();

			SearchMetricsModel searchMetricsModel = new SearchMetricsModel();
			searchMetricsModel.setFacetResolutionTime(facetResolutionTime);
			searchMetricsModel.setQueryResolutionTime(queryResolutionTime);
			searchMetricsModel.setSnippetResolutionTime(snippetResolutionTime);
			searchMetricsModel.setTotalTime(totalTime);

			request.setAttribute("searchMetricsModel", searchMetricsModel);
			/***/

		} catch (JAXBException e) {
			throw new IllegalStateException(e);

		} catch (RuntimeException e) {
			e.printStackTrace();
			request.setAttribute("error", e);
			return new ActionForward("/WEB-INF/jsp/list.jsp", false);

		} finally {
			if (client != null) {
				client.release();
			}
		}

		return new ActionForward("/WEB-INF/jsp/list.jsp", false);
	}
}
