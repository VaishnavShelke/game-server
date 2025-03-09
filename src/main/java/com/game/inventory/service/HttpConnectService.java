package com.game.inventory.service;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpConnectService {

	private static final Logger logger = LoggerFactory.getLogger(HttpConnectService.class);
	
	public String postRequest(Map<String, String> headerMap, String payload, String url) {
		String responseJson = null;
		CloseableHttpClient httpClient =null;
		logger.info("**** Sending request to {}",url);
		logger.info("**** Payload :: {}",payload);
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		httpClient = clientBuilder.build();
		try {
			final HttpPost httpPost = new HttpPost(url);
		    final StringEntity entity = new StringEntity(payload);
		    httpPost.setEntity(entity);
		    if(headerMap != null) {
		    	for(Entry<String, String> entry : headerMap.entrySet())
	            {
	                httpPost.setHeader(entry.getKey(), entry.getValue());
	            }
		    }
		    CloseableHttpResponse response = httpClient.execute(httpPost);
		    HttpEntity responseEntity 	= response.getEntity();
		    if (responseEntity != null) {
                  responseJson = EntityUtils.toString(responseEntity);
            }
		}catch (Exception e) {
			logger.error("!!!!! Error in sending request {}",e.getMessage());
		}
		logger.info("**** Response :: {}",responseJson);
		return responseJson;
	}

}