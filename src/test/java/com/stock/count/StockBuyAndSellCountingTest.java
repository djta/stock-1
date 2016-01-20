package com.stock.count;

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
public class StockBuyAndSellCountingTest {
	
	private static final String FILE_PATH = "file:D:\\new_tdx\\T0002\\export\\20091124-000919.txt";
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	
	@Autowired
	private StockBuyAndSellCounting countProcess;

	@Test
	public void countTest(){
		try {
			Resource resource = resolver.getResource(FILE_PATH);
			String content = IOUtils.toString(resource.getInputStream(), "gb2312");
			countProcess.count(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
