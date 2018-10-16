/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.cms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.cms.dao.ArticleDao;
import com.thinkgem.jeesite.modules.cms.dao.ArticleDataDao;
import com.thinkgem.jeesite.modules.cms.utils.es.EsTransportClient;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.HtmlUtils;
import com.thinkgem.jeesite.modules.cms.constant.Index;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.entity.ArticleData;

import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
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
    @Autowired
	private ArticleDao articleDao;
	@Autowired
	private ArticleDataDao articleDataDao;


	private static final String PUT = "PUT";
	private static final String POST = "POST";
	private static final String GET = "GET";
	private static final String HEAD = "HEAD";
	private static final String DELETE = "DELETE";

	public void add(Article article)  {

		try {

			ArticleData articleData = article.getArticleData();
			Map<String, Object> json = new HashMap<String, Object>();
			json.put("title", article.getTitle());
			json.put("keywods", article.getKeywords());
			json.put("specialty", article.getSpecialty().toString());
			json.put("article_type",article.getType().toString());
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
		Page<Article> p=new Page<Article>();
		try {
			//int pageSize=page.getPageSize();
			//int pageNo=page.getPageNo()-1;
             int pageNo=1;
			int pageSize=10;

			//EsTransportClient client=new EsTransportClient();
			//client.buildClient();
			//esTransportClient.buildClient();
	EsTransportClient client=new EsTransportClient();
			client.buildClient();

			SearchRequestBuilder searchRequestBuilder= esTransportClient.getObject()
					.prepareSearch(Index.INDEX_NAME)
					.setTypes(Index.INDEX_TYPE_NAME);
		    // .setSearchType(SearchType.QUERY_THEN_FETCH);
	/*if(queryLog.getStatus()!=null&&(!queryLog.getStatus().equals(-1))){
				searchRequestBuilder=searchRequestBuilder.setQuery(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("status", queryLog.getStatus())));

			}*/

		    key="建筑";
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

	if(!StringUtils.isEmpty(key)) {
				queryBuilder.should(QueryBuilders.wildcardQuery("title", "*" + key + "*"));
				queryBuilder.should(QueryBuilders.wildcardQuery("keywods", "*" + key + "*"));
				queryBuilder.should(QueryBuilders.wildcardQuery("description", "*" + key + "*"));
				queryBuilder.should(QueryBuilders.wildcardQuery("content", "*" + key + "*"));
			}


		   queryBuilder.should(QueryBuilders.wildcardQuery("title", "*" + key + "*"));
			SearchResponse response =searchRequestBuilder
					.setQuery(queryBuilder)
					//.setQuery(QueryBuilders.matchAllQuery())
					.setFrom(pageNo*pageSize)
			        .setSize(pageSize)
					.setExplain(true)
					.execute()
					.actionGet();

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
ArticleData articleData=new ArticleData();
				articleData.setContent((String) document.get("content"));
				article.setArticleData( articleData);


				list.add(	article);
			}


			p.setPageNo(page.getPageNo());
			p.setPageSize(page.getPageSize());
			p.setList(list);
			p.setCount(total);
			p.initialize();
			//p.toString();


		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			return p;
		}
	}

	public Page<Article> restSearch(Page<Article> page,String key,Integer[] typeArray,Integer[] specialtyArray){
		Page<Article> p=page;
		RestClient restClient=null;
		key=key.toLowerCase();
		try {
		    int pageSize=page.getPageSize();
			int pageNo=page.getPageNo()-1;


			ArrayList<HttpHost> hostList = new ArrayList<HttpHost>();
			String[] nodes =Global.getConfig("elasticsearch.clusterNodes").split(";");
			for(String node:nodes){
				String[] data= node.split(":");
				String ip=data[0];
				int port= Integer.valueOf(data[1]);
				hostList.add(new HttpHost(ip,port, "http"));
			}
			restClient = RestClient.builder(
					hostList.toArray(new HttpHost[nodes.length])).build();
			String method = "POST";
			String endpoint = "/kms/article/_search";
			String  commonad="";
			String filter="";
			int typeLen=0;
			int  specialtyLen=0;
			if( typeArray!=null){
				typeLen=typeArray.length;
			}
			if( specialtyArray!=null){
				specialtyLen=specialtyArray.length;
			}
			if( (typeLen+ specialtyLen)>0){
				filter=",\"filter\": {\n"+
						"\"bool\": {\n"+
						"\"should\": [\n";
				if(typeArray!=null) {
					for (int i = 0; i < typeArray.length; i++) {
						filter += "{\"term\" : {\n " +
								"\"article_type\" : \"" + String.valueOf(typeArray[i]) + "\"" +
								"\n}" +
								"\n},";
					}
				}
				if( specialtyArray!=null) {
					for (int i = 0; i < specialtyArray.length; i++) {
						filter += "{\"term\" : {\n " +
								"\"specialty\" : \"" + String.valueOf(specialtyArray[i]) + "\"" +
								"\n}" +
								"\n},";
					}
				}
				filter=filter.substring(0,filter.length()-1);
				filter+="\n]" +
						"\n}" +
						"\n}";
			}


			if(!StringUtils.isEmpty(key)) {
				commonad="{\n" +
						"\"from\": " + pageNo * pageSize + ",\n" +
						"\"size\": " + pageSize + ",\n " +
						"\"min_score\": 1 ,\n" +
						"\"query\": {\n" +
						"             \"bool\":{\n" +
						"                        \"should\": [" +
						"{\"match_phrase\":{ \"title\": \"" + key + "\"}}," +
						"{\"match_phrase\":{ \"keywods\": \"" + key + "\"}}," +
						"{\"match_phrase\":{ \"description\": \"" + key + "\"}}," +
						"{\"match_phrase\":{ \"content\": \"" + key + "\"}}" +
						"                                     ]\n" +filter+
						"                          }\n" +
						"},\n"+
						//"},"+
				        "\"highlight\": {\n"+
					    "\"pre_tags\":[\n"+
					    "\"<mark>\"\n"+
				        "],\n"+
						"\"post_tags\": [\n"+
					    "\"</mark>\"\n"+
					    "]\n,"+
					    "\"fields\": {\n"+
						    "\"title\": {},\n"+
							"\"keywods\": {},\n"+
							"\"description\": {},\n"+
							"\"content\": {}\n"+
					    "},\n"+
						"\"fragment_size\": 2147483647\n"+
				        "}\n"+
				"}";

			}else{
				commonad="{\n" +
						"\"from\": " + pageNo * pageSize + ",\n" +
						"\"size\": " + pageSize + ",\n " +
						"\"min_score\": 1 ,\n" +
						"\"query\": {\n" +
								"\"match_all\": {} \n" +
						   "}"+filter+
				         "}";
			}
			HttpEntity entity = new NStringEntity(commonad, ContentType.APPLICATION_JSON);

			Response response = restClient.performRequest(method,endpoint, Collections.<String, String>emptyMap(),entity);
			//System.out.println(EntityUtils.toString(response.getEntity()));
			String  jsonStr = EntityUtils.toString( response .getEntity(), "UTF-8");

			JSONObject jsonObject = JSONObject.parseObject(jsonStr);
			//json对象转Map
			JSONObject hits = (JSONObject)jsonObject.getJSONObject("hits");
			JSONArray jsonArray=hits.getJSONArray("hits");
			List<Article> list=new ArrayList<Article>();
			Iterator iterator = jsonArray.iterator();
			while (iterator.hasNext()) {
				JSONObject  recordJson= (JSONObject) iterator.next();
				JSONObject  articleJson= (JSONObject)recordJson.getJSONObject("_source");
				Article article=new Article();
				String id=(String) recordJson.get("_id");
				article= articleDao.get(id);
				User user= UserUtils.get(article.getCreateBy().getId());
				article.setCreateBy(user);
				//article.setId((String) recordJson.get("_id"));*/
				//article.setTitle((String)  articleJson.get("title"));
//				article.setKeywords((String)  articleJson.get("keywods"));
//				article.setDescription((String)  articleJson.get("description"));
//				article.setTitle((String)  articleJson.get("content"));
				JSONObject  highlight= (JSONObject)recordJson.getJSONObject("highlight");
				JSONArray titleJSONArray=highlight.getJSONArray("title");
				if(titleJSONArray!=null) {
					List<String> tempList = JSONObject.parseArray(titleJSONArray.toJSONString(), String.class);

					if (null != tempList && tempList.size() > 0) {
						article.setTitle(tempList.get(0));
					}
				}
				JSONArray keywodsJSONArray=highlight.getJSONArray("keywods");
				if(keywodsJSONArray!=null) {
					List<String> tempList = JSONObject.parseArray(keywodsJSONArray.toJSONString(), String.class);

					if (null != tempList && tempList.size() > 0) {
						article.setKeywords(tempList.get(0));
					}
				}
				JSONArray descriptionJSONArray=highlight.getJSONArray("description");
				if(descriptionJSONArray!=null) {
					List<String> tempList = JSONObject.parseArray(descriptionJSONArray.toJSONString(), String.class);

					if (null != tempList && tempList.size() > 0) {
						article.setDescription(tempList.get(0));
					}
				}
				JSONArray contentJSONArray=highlight.getJSONArray("content");
				if(contentJSONArray!=null) {
					List<String> tempList = JSONObject.parseArray(contentJSONArray.toJSONString(), String.class);

					if (null != tempList && tempList.size() > 0) {
						ArticleData articleData=articleDataDao.get(article.getId());
						articleData.setContent(tempList.get(0));
						article.setArticleData(articleData);
					}
				}

				list.add(article);

			}

			Integer total=0;
			if(hits.get("total")!=null){
				total=(Integer)hits.get("total");
			}

			p=new Page(page.getPageNo(), page.getPageSize(), total,list);

			p.initialize();




		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				restClient.close();
			}catch (Exception e){

			}
			return p;
		}
	}

	public Boolean restDelete(String id){

		String rs = null;
		RestClient restClient=null;
		boolean result=false;
		try {
			restClient = RestClient.builder(
					new HttpHost("169.254.100.12", 9200, "http"),
					new HttpHost("169.254.100.13", 9200, "http")).build();
			String endpoint = "/kms/article/"+id;

			Response response = restClient.performRequest("DELETE", endpoint);
			rs = EntityUtils.toString(response.getEntity());
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				restClient.close();
			}catch (Exception e){

			}
			return result;
		}

	}

	public void restSave(Article article)  {
		RestClient restClient=null;
		boolean result=false;

		try {

			ArticleData articleData = article.getArticleData();
			restClient = RestClient.builder(
					new HttpHost("169.254.100.12", 9200, "http"),
					new HttpHost("169.254.100.13", 9200, "http")).build();
			String endpoint = "/kms/article/"+article.getId();
			String requestType = PUT;

			Map<String, Object> json = new HashMap<String, Object>();
			json.put("title", article.getTitle());
			json.put("keywods", article.getKeywords());
			json.put("specialty", article.getSpecialty().toString());
			json.put("article_type",article.getType().toString());
			json.put("description", article.getDescription());
			if (article.getArticleData().getContent() != null) {
				//json.put("content", HtmlUtils.removeHtmlTag(articleData.getContent()));
				json.put("content",articleData.getContent());
			}
			json.put("create_date", article.getCreateDate());

			HttpEntity entity = new NStringEntity(JSON.toJSONString(json), ContentType.APPLICATION_JSON);
			Response response = restClient.performRequest(requestType, endpoint, Collections.singletonMap("pretty", "true"),entity);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static  void main(String args[]){
		/*EsService esService=new EsService();
		//esService.search(new Page<Article>(),"");
		Page p=new Page<Article>();
		p.setPageNo(1);
		p.setPageSize(15);
		//esService.restSearch(p,null);*/
		String str="标准GB2312";
		System.out.println(str.toLowerCase());

	}
	

	
}
