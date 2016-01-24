package com.stock.count;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

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
	
	private static final String FILE_PATH = "file:D:/new_tdx/T0002/export/*.txt";
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	
	@Autowired
	private StockBuyAndSellCounting countProcess;

	@Test
	public void countTest(){
		try {
			Resource[] res = resolver.getResources(FILE_PATH);
			Arrays.sort(res, new Comparator<Resource>() {

				@Override
				public int compare(Resource o1, Resource o2) {
					return o1.getFilename().compareTo(o2.getFilename());
				}
				
			});
			
			for (Resource resource : res) {
				System.out.println(resource.getFilename());
				String content = IOUtils.toString(resource.getInputStream(), "gb2312");
				countProcess.count(content);
				
				System.out.println("===============================");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
