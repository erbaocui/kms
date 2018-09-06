/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.cms.service;

import com.thinkgem.jeesite.common.es.EsTransportClient;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.HtmlUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.cms.constant.Index;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.entity.ArticleData;
import org.apache.commons.lang3.StringEscapeUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 索引Service
 * @author  cuijp
 * @version 2018-09-13
 */
@Service("esService")
@Transactional(readOnly = true)
public class EsService{


	@Autowired
	private EsTransportClient esTransportClient;
	

	public void add(Article article)  {

		try {

			ArticleData articleData = article.getArticleData();
			Map<String, Object> json = new HashMap<String, Object>();
			json.put("title", article.getTitle());
			json.put("keywods", article.getKeywords());
			//json.put("specialty",log.getModule());
			//json.put("article_type", log.getActionTime());
			json.put("description", article.getDescription());
			if (article.getArticleData().getContent() != null) {
				json.put("content", HtmlUtils.removeHtmlTag(articleData.getContent()));
			}
			json.put("create_date", article.getCreateDate());


			IndexResponse response = esTransportClient.getObject().prepareIndex(Index.INDEX_NAME, Index.INDEX_TYPE_NAME, article.getId()).setSource(json).execute().actionGet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteById(String id)  {

		try {
			DeleteResponse dResponse = esTransportClient.getObject().prepareDelete(Index.INDEX_NAME, Index.INDEX_TYPE_NAME,id).execute().actionGet();

			//是否查找并删除
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Page<Article> search(Page<Article> page,String key){
		try {
			int pageSize=page.getPageSize();
			int pageNo=page.getPageNo()-1;
			page.setPageNo(pageNo);
			page.setPageSize(pageSize);
		    SearchRequestBuilder searchRequestBuilder=esTransportClient.getObject().prepareSearch(Index.INDEX_NAME)
				.setTypes(Index.INDEX_TYPE_NAME);
		/*	if(queryLog.getStatus()!=null&&(!queryLog.getStatus().equals(-1))){
				searchRequestBuilder=searchRequestBuilder.setQuery(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("status", queryLog.getStatus())));

			}*/
		    key="文章";
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			if(!StringUtils.isEmpty(key)) {
				queryBuilder.should(QueryBuilders.wildcardQuery("title", "*" + key + "*"));
				queryBuilder.should(QueryBuilders.wildcardQuery("keywods", "*" + key + "*"));
				queryBuilder.should(QueryBuilders.wildcardQuery("description", "*" + key + "*"));
				queryBuilder.should(QueryBuilders.wildcardQuery("content", "*" + key + "*"));
			}

			SearchResponse response =searchRequestBuilder
					.setQuery(queryBuilder)

					.setFrom(pageNo*pageSize).setSize(pageSize)
					.get();

			SearchHit[] searchHits = response.getHits().getHits();
			Long total = response.getHits().getTotalHits();
			List<Article> list=new ArrayList<Article>();

			for(SearchHit hit:searchHits){
				Map document= hit.getSource();
				Article article=new Article();
				article.setId((String) document.get("id"));
				article.setTitle((String) document.get("title"));
				article.setKeywords((String) document.get("keywods"));
				article.setDescription((String) document.get("description"));
				/*ArticleData articleData=new ArticleData();
				articleData.setContent((String) document.get("content"));
				article.setArticleData( articleData);*/

				list.add(	article);
			}
			page.setList(list);
			page.setCount(total);
			page.initialize();


		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			return page;
		}
	}
	

	
}
