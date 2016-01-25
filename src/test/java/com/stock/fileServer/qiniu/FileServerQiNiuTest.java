package com.stock.fileServer.qiniu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class FileServerQiNiuTest {

	@Autowired
	private FileServerQiNiu qiNiu;
	
	@Test
	public void uploadTest() {
		String fileName = "E:/stock_data/pan_kou/2016-1.zip";
		qiNiu.upload(fileName);
	}
}
