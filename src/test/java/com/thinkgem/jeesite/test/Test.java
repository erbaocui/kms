package com.thinkgem.jeesite.test;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.util.Collections;

public class Test {

	public static void main(String[] args) throws Exception{
		RestClient restClient = RestClient.builder(
				new HttpHost("169.254.100.12", 9200, "http"),
				new HttpHost("169.254.100.12", 9200, "http")).build();
		String method = "POST";
		String endpoint = "/kms/article/_search";
		HttpEntity entity = new NStringEntity("{\n" +
				"  \"from\" : 15,\n"+
				"  \"size\" : 15,\n "+
				"  \"query\": {\n" +
				"    \"match_all\": {}\n" +
				"  }\n" +
				"}", ContentType.APPLICATION_JSON);

		Response response = restClient.performRequest(method,endpoint, Collections.<String, String>emptyMap(),entity);
		System.out.println(EntityUtils.toString(response.getEntity()));

	}
}
