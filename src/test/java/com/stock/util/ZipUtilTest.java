package com.stock.util;

import org.junit.Test;

public class ZipUtilTest {

	@Test
	public void zipTest() {
		String filePath = "E:/stock_data/pan_kou/2016-03-02";
		ZipUtil.zip(filePath);
	}
	
	@Test
	public void unZipTest() {
		String filePath = "E:/stock_data/pan_kou/2016-1_bak_1.zip";
		ZipUtil.unzip(filePath);
	}
}
