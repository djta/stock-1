package com.stock.count;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class StockTryTest {

private static final String FILE_PATH = "file:E:/BaiduYunDownload/exchange_*/*";
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	
	@Autowired
	private StockTry stockTry;

	@Test
	public void findTest(){
		try {
			Resource[] res = resolver.getResources(FILE_PATH);
			StringBuilder resBuilder = new StringBuilder();
			for (Resource resource : res) {
				String content = IOUtils.toString(resource.getInputStream(), "gb2312");
				String findStr = stockTry.find(content);
				if (findStr != null) {
					resBuilder.append(findStr);
					resBuilder.append("\n");
				}
			}
			System.out.println(resBuilder.toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
