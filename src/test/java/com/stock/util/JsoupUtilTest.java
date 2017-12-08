package com.stock.util;

import java.io.IOException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.junit.Test;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class JsoupUtilTest {

	@Test
	public void test_1() throws IOException {
		String url = "http://data.eastmoney.com/DataCenter_V3/gdhs/GetList.ashx?reportdate=&market=&changerate==&range==&pagesize=50&page=1";
		Response resp = Jsoup.connect(url).execute();
		JSONObject obj1 = JSONObject.fromObject(resp.body());
		
		JSONArray jonArr = obj1.getJSONArray("data");
		JSONObject objItem = jonArr.getJSONObject(0);
		System.out.println(objItem);
		System.out.println(String.format("Code:%s  Name:%s  Rate:%s", 
				objItem.get("SecurityCode"),
				objItem.get("SecurityName"), 
				objItem.get("HolderNumChangeRate") ));
	}
}
