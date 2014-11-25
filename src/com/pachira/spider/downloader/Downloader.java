package com.pachira.spider.downloader;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import com.pachira.spider.spider.Request;

@SuppressWarnings({ "deprecation", "unused" })
public class Downloader {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		Request request = new Request("http://www.dytt.net");
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(new HttpGet(request.getUrl()));
		System.out.println(response.getStatusLine());
		byte[] contentBytes = IOUtils.toByteArray(response.getEntity().getContent());
		System.out.println(new String(contentBytes));
		System.out.println(request.getUrl());
	}
}
