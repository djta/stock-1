package com.stock.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUtil {

	public static void main(String... args) throws IOException {
		String url = "http://www.iwencai.com/stockpick/search?typed=1&preParams=&ts=1&f=1&qs=result_rewrite&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w=000975";
		Document document = Jsoup.connect(url).get();
		System.out.println(document);
		
		Elements eleSpans = document.select("span.fl a");
		for (Element ele : eleSpans) {
			System.out.println(ele.text());
		}
		
//		System.out.println("");
//		Elements hys = document.select("div.em a");
//		for (Element ele : hys) {
//			System.out.println(ele.text());
//		}
	}
}
